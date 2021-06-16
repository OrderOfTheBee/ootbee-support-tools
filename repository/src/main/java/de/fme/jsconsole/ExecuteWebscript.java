package de.fme.jsconsole;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.alfresco.repo.cache.SimpleCache;
import org.alfresco.repo.content.MimetypeMap;
import org.alfresco.repo.jscript.RhinoScriptProcessor;
import org.alfresco.repo.jscript.ScriptNode;
import org.alfresco.repo.jscript.ScriptUtils;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.security.permissions.AccessDeniedException;
import org.alfresco.repo.transaction.RetryingTransactionHelper.RetryingTransactionCallback;
import org.alfresco.scripts.ScriptResourceHelper;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.transaction.TransactionService;
import org.alfresco.util.MD5;
import org.alfresco.util.Pair;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.extensions.webscripts.AbstractWebScript;
import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.Container;
import org.springframework.extensions.webscripts.DeclarativeWebScript;
import org.springframework.extensions.webscripts.Description;
import org.springframework.extensions.webscripts.ScriptContent;
import org.springframework.extensions.webscripts.ScriptProcessor;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.TemplateProcessor;
import org.springframework.extensions.webscripts.WebScriptException;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;

/**
 * Implements a webscript that is used to execute arbitrary scripts and
 * freemarker templates the same way a {@link DeclarativeWebScript} would do.
 *
 * @author Florian Maul (fme AG)
 * @version 1.0
 *
 */
public class ExecuteWebscript extends AbstractWebScript {

	/** The Constant LOG. */
	private static final Log LOG = LogFactory.getLog(ExecuteWebscript.class);

	/** The Constant PRE_ROLL_SCRIPT_RESOURCE_NAME. */
	private static final String PRE_ROLL_SCRIPT_RESOURCE_NAME = "jsconsole-pre-roll-script.js";
	
	/** The Constant PRE_ROLL_SCRIPT_RESOURCE. */
	private static final String PRE_ROLL_SCRIPT_RESOURCE = "/de/fme/jsconsole/" + PRE_ROLL_SCRIPT_RESOURCE_NAME;
	
	/** The Constant POST_ROLL_SCRIPT_RESOURCE_NAME. */
	private static final String POST_ROLL_SCRIPT_RESOURCE_NAME = "jsconsole-post-roll-script.js";

	/** The script utils. */
	private ScriptUtils scriptUtils;

	/** The transaction service. */
	private TransactionService transactionService;

	/** The post roll script. */
	private String postRollScript = "";

	/** The js processor. */
	private org.alfresco.service.cmr.repository.ScriptProcessor jsProcessor;

	/** The dump service. */
	private DumpService dumpService;

	/**
	 * Sets the dump service.
	 *
	 * @param dumpService the new dump service
	 */
	public void setDumpService(DumpService dumpService) {
		this.dumpService = dumpService;
	}

	/** The print output cache. */
	private SimpleCache<Pair<String, Integer>, List<String>> printOutputCache;

	/** The result cache. */
	private SimpleCache<String, JavascriptConsoleResultBase> resultCache;

	/** The print output chunk size. */
	private int printOutputChunkSize = 5;

	/**
	 * Inits the.
	 *
	 * @param container the container
	 * @param description the description
	 */
	@Override
	public void init(Container container, Description description) {
		super.init(container, description);
		try {
			postRollScript = readScriptFromResource(POST_ROLL_SCRIPT_RESOURCE_NAME, false);
		} catch (IOException e) {
			LOG.error("Could not read base import script.");
		}
	}

	/**
	 * Execute.
	 *
	 * @param request the request
	 * @param response the response
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	/*
	 * (non-Javadoc)
	 *
	 * @see org.springframework.extensions.webscripts.WebScript#execute(org.
	 * springframework.extensions.webscripts.WebScriptRequest,
	 * org.springframework.extensions.webscripts.WebScriptResponse)
	 */
	@Override
	public void execute(WebScriptRequest request, WebScriptResponse response) throws IOException {
		int scriptOffset = 0;

		JavascriptConsoleResult result = null;
		try {
		    // this isn't very precise since there is some bit of processing until here that we can't measure
			PerfLog webscriptPerf = new PerfLog().start();
			JavascriptConsoleRequest jsreq = JavascriptConsoleRequest.readJson(request);

			// Note: Need to use import here so the user-supplied script may also import scripts
			String script = "<import resource=\"classpath:" + PRE_ROLL_SCRIPT_RESOURCE + "\">\n" + jsreq.script;

			ScriptContent scriptContent = new StringScriptContent(script + this.postRollScript);

			int providedScriptLength = countScriptLines(jsreq.script, false);
			int resolvedScriptLength = countScriptLines(script, true);
			scriptOffset = providedScriptLength - resolvedScriptLength;

			try {
			    result = runScriptWithTransactionAndAuthentication(request, response, jsreq, scriptContent);

			    result.setScriptOffset(scriptOffset);

			    // this won't be very precise since there is still some post-processing, but we can't delay it any longer
			    result.setWebscriptPerformance(String.valueOf(webscriptPerf.stop("Execute Webscript with {0} - result: {1} ",
                        jsreq, result)));

    			if (!result.isStatusResponseSent()) {
    			    result.writeJson(response);
    			}
			} finally {
			    if (jsreq.resultChannel != null && ExecuteWebscript.this.resultCache != null) {
			        if (result != null) {
			            ExecuteWebscript.this.resultCache.put(jsreq.resultChannel, result.toBaseResult());
			        } else {
			            // dummy response as indicator for "error"
			            ExecuteWebscript.this.resultCache.put(jsreq.resultChannel, new JavascriptConsoleResultBase());
			        }

			    }
			}

		} catch (WebScriptException e) {
			response.setStatus(500);
			response.setContentEncoding("UTF-8");
			response.setContentType(MimetypeMap.MIMETYPE_JSON);

			writeErrorInfosAsJson(response, result, scriptOffset, e);
		}
	}

	/**
	 * Count script lines.
	 *
	 * @param script the script
	 * @param attemptImportResolution the attempt import resolution
	 * @return the int
	 */
	private int countScriptLines(String script, boolean attemptImportResolution)
	{
		String scriptSource;

		if (attemptImportResolution && this.jsProcessor instanceof RhinoScriptProcessor) {
			// resolve any imports
			scriptSource = ScriptResourceHelper.resolveScriptImports(script, (RhinoScriptProcessor)this.jsProcessor, LOG);
		} else {
			// assume this is the literal source
			scriptSource = script;
		}

		// EOL is not only dependent on the current system but on the environment of the script author, so check for any known EOL styles
		String[] scriptLines = scriptSource.split("(\\r?\\n\\r?)|(\\r)");
		return scriptLines.length;
	}

	/**
	 * used our own json reponse for errors because you cannot pass your own
	 * parameters to the built-in alfresco status templates.
	 *
	 * @param response the response
	 * @param result the result
	 * @param scriptOffset the script offset
	 * @param e            the occured exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	private void writeErrorInfosAsJson(WebScriptResponse response, JavascriptConsoleResult result, int scriptOffset, WebScriptException e) throws IOException {
		try {
			JSONObject jsonOutput = new JSONObject();

			// set some common stuff like
			JSONObject status = new JSONObject();
			status.put("code", 500);
			status.put("name", "Internal Error");
			status.put("description", "An error inside the HTTP server which prevented it from fulfilling the request.");
			jsonOutput.put("status", status);

			// find out the closest error message which is helpful for the
			// user...
			String errorMessage = e.getMessage();
			if (e.getCause() != null) {
				errorMessage = e.getCause().getMessage();
				if (e.getCause().getCause() != null) {
					errorMessage = e.getCause().getCause().getMessage();
				}
			}
			jsonOutput.put("message", errorMessage);

			// print the stacktrace into the callstack variable...
			Writer writer = new StringWriter();
			PrintWriter printWriter = new PrintWriter(writer);
			e.printStackTrace(printWriter);
			String s = writer.toString();
			jsonOutput.put("callstack", s);

			// always print the result into the error stream because we want to have all outputs before the exceptions occurs
			if(result!=null){
				jsonOutput.put("result", result.generateJsonOutput().toString());
			}

			// scriptoffset is useful to determine the correct line in case of
			// an error (if you use preroll-scripts or imports in javascript
			// input)
			jsonOutput.put("scriptOffset", scriptOffset);

			response.getWriter().write(jsonOutput.toString(5));

		} catch (JSONException ex) {
			throw new WebScriptException(Status.STATUS_INTERNAL_SERVER_ERROR, "Error writing json error response.", ex);
		}
	}

	/**
	 * Read script from resource.
	 *
	 * @param resource the resource
	 * @param unwrapLines the unwrap lines
	 * @return the string
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	private String readScriptFromResource(String resource, boolean unwrapLines) throws IOException {
	    BufferedReader reader = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream(resource)));
	    List<String> lines = new ArrayList<>(128);
	    try {
	      String line = null;
	      while ((line = reader.readLine()) != null) {
	        lines.add(line);
	      }
	    } finally {
	      reader.close();
	    }

		StringBuilder script = new StringBuilder();
		for (String line : lines) {
			if (unwrapLines) {
				script.append(line.replace("\n", ""));
			} else {
				script.append(line);
			}
		}
		return script.toString();
	}

	/**
	 * Run script with transaction and authentication.
	 *
	 * @param request the request
	 * @param response the response
	 * @param jsreq the jsreq
	 * @param scriptContent the script content
	 * @return the javascript console result
	 */
	private JavascriptConsoleResult runScriptWithTransactionAndAuthentication(final WebScriptRequest request,
			final WebScriptResponse response, final JavascriptConsoleRequest jsreq, final ScriptContent scriptContent) {

		LOG.debug("running script as user " + jsreq.runas);

		if (jsreq.runas != null && !jsreq.runas.trim().isEmpty()) {
			return AuthenticationUtil.runAs(new AuthenticationUtil.RunAsWork<JavascriptConsoleResult>() {
				@Override
				public JavascriptConsoleResult doWork() {
					return runWithTransactionIfNeeded(request, response, jsreq, scriptContent);
				}
			}, jsreq.runas);
		} else {
			return runWithTransactionIfNeeded(request, response, jsreq, scriptContent);
		}
	}

	/**
	 * Run with transaction if needed.
	 *
	 * @param request the request
	 * @param response the response
	 * @param jsreq the jsreq
	 * @param scriptContent the script content
	 * @return the javascript console result
	 */
	private JavascriptConsoleResult runWithTransactionIfNeeded(final WebScriptRequest request, final WebScriptResponse response,
			final JavascriptConsoleRequest jsreq, final ScriptContent scriptContent) {

	    final List<String> printOutput;
	    if (jsreq.resultChannel != null && this.printOutputCache != null) {
	        printOutput = new CacheBackedChunkedList<String, String>(this.printOutputCache, jsreq.resultChannel, this.printOutputChunkSize);
	    } else {
	        printOutput = null;
	    }

	    JavascriptConsoleResult result = null;

		if (jsreq.useTransaction) {
			LOG.debug("Using transction to execute script: " + (jsreq.transactionReadOnly ? "readonly" : "readwrite"));
			result = this.transactionService.getRetryingTransactionHelper().doInTransaction(
					new RetryingTransactionCallback<JavascriptConsoleResult>() {
						@Override
						public JavascriptConsoleResult execute() throws Exception {
						    // clear due to potential retry
		                    if (printOutput != null) {
		                        printOutput.clear();
		                    }
							return executeScriptContent(request, response, scriptContent, jsreq.template, jsreq.spaceNodeRef,
									jsreq.urlargs, jsreq.documentNodeRef, printOutput);
						}
					}, jsreq.transactionReadOnly);
			return result;
		}

		LOG.debug("Executing script script without transaction.");
		result = executeScriptContent(request, response, scriptContent, jsreq.template, jsreq.spaceNodeRef, jsreq.urlargs,
				jsreq.documentNodeRef, printOutput);
		return result;
	}

	/**
	 * Execute script content.
	 *
	 * @param req the req
	 * @param res the res
	 * @param scriptContent the script content
	 * @param template the template
	 * @param spaceNodeRef the space node ref
	 * @param urlargs the urlargs
	 * @param documentNodeRef the document node ref
	 * @param printOutput the print output
	 * @return the javascript console result
	 */
	/*
	 * (non-Javadoc)
	 *
	 * @see org.alfresco.web.scripts.WebScript#execute(org.alfresco.web.scripts.
	 * WebScriptRequest, org.alfresco.web.scripts.WebScriptResponse)
	 */
	private JavascriptConsoleResult executeScriptContent(WebScriptRequest req, WebScriptResponse res,
			ScriptContent scriptContent, String template, String spaceNodeRef, Map<String, String> urlargs, String documentNodeRef,
			List<String> printOutput) {
		JavascriptConsoleResult output = new JavascriptConsoleResult();

		// retrieve requested format
		String format = req.getFormat();

		try {
			// construct model for script / template
			Status status = new Status();
			Cache cache = new Cache(getDescription().getRequiredCache());
			Map<String, Object> model = new HashMap<String, Object>(8, 1.0f);
			model.put("status", status);
			model.put("cache", cache);

			Map<String, Object> scriptModel = createScriptParameters(req, res, null, model);

			augmentScriptModelArgs(scriptModel, urlargs);

			// add return model allowing script to add items to template model
			Map<String, Object> returnModel = new HashMap<String, Object>(8, 1.0f);
			scriptModel.put("model", returnModel);

			JavascriptConsoleScriptObject javascriptConsole = printOutput == null ? new JavascriptConsoleScriptObject() : new JavascriptConsoleScriptObject(printOutput);
			scriptModel.put("jsconsole", javascriptConsole);

			if (spaceNodeRef != null && !spaceNodeRef.trim().isEmpty()) {
				javascriptConsole.setSpace(this.scriptUtils.getNodeFromString(spaceNodeRef));
			} else {
				Object ch = scriptModel.get("companyhome");
				if (ch instanceof NodeRef) {
					javascriptConsole.setSpace(this.scriptUtils.getNodeFromString(ch.toString()));
				} else {
					javascriptConsole.setSpace((ScriptNode) ch);
				}
			}
			scriptModel.put("space", javascriptConsole.getSpace());

			ScriptNode documentNode = null;
			if (documentNodeRef != null && !documentNodeRef.trim().isEmpty()) {
				documentNode = this.scriptUtils.getNodeFromString(documentNodeRef);
				scriptModel.put("document", documentNode);
			}
			scriptModel.put("dumpService", dumpService);

			PerfLog jsPerf = new PerfLog(LOG).start();
			try {
				ScriptProcessor scriptProcessor = getContainer().getScriptProcessorRegistry().getScriptProcessorByExtension("js");
				scriptProcessor.executeScript(scriptContent, scriptModel);
			} finally {
				output.setScriptPerformance(String.valueOf(jsPerf.stop("Executed the script {0} with model {1}", scriptContent,
						scriptModel)));
				output.setPrintOutput(javascriptConsole.getPrintOutput());
				if (documentNode != null) {
					output.setDumpOutput(dumpService.addDump(documentNode));
				}
			}


			ScriptNode newSpace = javascriptConsole.getSpace();
			output.setSpaceNodeRef(newSpace.getNodeRef().toString());
			try {
				output.setSpacePath(newSpace.getDisplayPath() + "/" + newSpace.getName());
			} catch (AccessDeniedException ade) {
				output.setSpacePath("/");
			}

			mergeScriptModelIntoTemplateModel(scriptContent, returnModel, model);

			// create model for template rendering
			Map<String, Object> templateModel = createTemplateParameters(req, res, model);

			// is a redirect to a status specific template required?
			if (status.getRedirect()) {
				sendStatus(req, res, status, cache, format, templateModel);
				output.setStatusResponseSent(true);
			} else {
				// apply location
				String location = status.getLocation();
				if (location != null && location.length() > 0) {
					if (LOG.isDebugEnabled()) {
						LOG.debug("Setting location to " + location);
					}
					res.setHeader(WebScriptResponse.HEADER_LOCATION, location);
				}

				if (template != null && !template.trim().isEmpty()) {
					PerfLog freemarkerPerf = new PerfLog(LOG).start();
					TemplateProcessor templateProcessor = getContainer().getTemplateProcessorRegistry()
							.getTemplateProcessorByExtension("ftl");
					StringWriter sw = new StringWriter();
					templateProcessor.processString(template, templateModel, sw);
					String templateResult = sw.toString();
					output.setFreemarkerPerformance(String.valueOf(freemarkerPerf.stop(
							"Executed the template {0} with model {1} with result {2}", template, templateModel, templateResult)));
					output.setRenderedTemplate(templateResult);
				}
			}
		} catch (Throwable e) {
			if (LOG.isDebugEnabled()) {
				StringWriter stack = new StringWriter();
				e.printStackTrace(new PrintWriter(stack));
				LOG.debug("Caught exception; decorating with appropriate status template : " + stack.toString());
			}

			throw createStatusException(e, req, res);
		}
		return output;
	}

	/**
	 * Augment script model args.
	 *
	 * @param scriptModel the script model
	 * @param additionalUrlArgs the additional url args
	 */
	private void augmentScriptModelArgs(Map<String, Object> scriptModel, Map<String, String> additionalUrlArgs) {
		@SuppressWarnings("unchecked")
		Map<String, String> args = (Map<String, String>) scriptModel.get("args");

		args.putAll(additionalUrlArgs);
	}

	/**
	 * Merge script generated model into template-ready model.
	 *
	 * @param scriptContent            script content
	 * @param scriptModel            script model
	 * @param templateModel            template model
	 */
	private final void mergeScriptModelIntoTemplateModel(ScriptContent scriptContent, Map<String, Object> scriptModel,
			Map<String, Object> templateModel) {
		// determine script processor
		ScriptProcessor scriptProcessor = getContainer().getScriptProcessorRegistry().getScriptProcessor(scriptContent);
		if (scriptProcessor != null) {
			for (Map.Entry<String, Object> entry : scriptModel.entrySet()) {
				// retrieve script model value
				Object value = entry.getValue();
				Object templateValue = scriptProcessor.unwrapValue(value);
				templateModel.put(entry.getKey(), templateValue);
			}
		}
	}

	/**
	 * Render a template (of given format) to the Web Script Response.
	 *
	 * @param format            template format (null, default format)
	 * @param model            data model to render
	 * @param writer            where to output
	 */
	protected final void renderFormatTemplate(String format, Map<String, Object> model, Writer writer) {
		format = (format == null) ? "" : format;

		String templatePath = getDescription().getId() + "." + format;

		if (LOG.isDebugEnabled()) {
			LOG.debug("Rendering template '" + templatePath + "'");
		}

		renderTemplate(templatePath, model, writer);
	}

	/**
	 * Sets the script utils.
	 *
	 * @param scriptUtils the new script utils
	 */
	public void setScriptUtils(ScriptUtils scriptUtils) {
		this.scriptUtils = scriptUtils;
	}

	/**
	 * Sets the transaction service.
	 *
	 * @param transactionService the new transaction service
	 */
	public void setTransactionService(TransactionService transactionService) {
		this.transactionService = transactionService;
	}

	/**
	 * Sets the js processor.
	 *
	 * @param jsProcessor the new js processor
	 */
	public void setJsProcessor(org.alfresco.service.cmr.repository.ScriptProcessor jsProcessor) {
		this.jsProcessor = jsProcessor;
	}

	/**
	 * Sets the print output cache.
	 *
	 * @param printOutputCache the print output cache
	 */
	public final void setPrintOutputCache(SimpleCache<Pair<String, Integer>, List<String>> printOutputCache) {
        this.printOutputCache = printOutputCache;
    }

	/**
	 * Sets the result cache.
	 *
	 * @param resultCache the result cache
	 */
	public final void setResultCache(SimpleCache<String, JavascriptConsoleResultBase> resultCache) {
        this.resultCache = resultCache;
    }

    /**
     * Sets the prints the output chunk size.
     *
     * @param printOutputChunkSize the new prints the output chunk size
     */
    public final void setPrintOutputChunkSize(int printOutputChunkSize) {
        this.printOutputChunkSize = printOutputChunkSize;
    }

    /**
     * The Class StringScriptContent.
     */
    private static class StringScriptContent implements ScriptContent {
		
		/** The content. */
		private final String content;

		/**
		 * Instantiates a new string script content.
		 *
		 * @param content the content
		 */
		public StringScriptContent(String content) {
			this.content = content;
		}

		/**
		 * Gets the input stream.
		 *
		 * @return the input stream
		 */
		@Override
		public InputStream getInputStream() {
			return new ByteArrayInputStream(content.getBytes(Charset.forName("UTF-8")));
		}

		/**
		 * Gets the path.
		 *
		 * @return the path
		 */
		@Override
		public String getPath() {
			return MD5.Digest(content.getBytes()) + ".js";
		}

		/**
		 * Gets the path description.
		 *
		 * @return the path description
		 */
		@Override
		public String getPathDescription() {
			return "Javascript Console Script";
		}

		/**
		 * Gets the reader.
		 *
		 * @return the reader
		 */
		@Override
		public Reader getReader() {
			return new StringReader(content);
		}

		/**
		 * Checks if is cachable.
		 *
		 * @return true, if is cachable
		 */
		@Override
		public boolean isCachable() {
			return false;
		}

		/**
		 * Checks if is secure.
		 *
		 * @return true, if is secure
		 */
		@Override
		public boolean isSecure() {
			return true;
		}
	}

}
