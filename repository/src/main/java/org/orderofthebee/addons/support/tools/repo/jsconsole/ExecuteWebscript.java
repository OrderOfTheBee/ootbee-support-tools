/**
 * Copyright (C) 2016 - 2025 Order of the Bee
 *
 * This file is part of OOTBee Support Tools
 *
 * OOTBee Support Tools is free software: you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 *
 * OOTBee Support Tools is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser
 * General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with OOTBee Support Tools. If not, see
 * <http://www.gnu.org/licenses/>.
 *
 * Linked to Alfresco
 * Copyright (C) 2005 - 2025 Alfresco Software Limited.
 *
 * This file is part of code forked from the JavaScript Console project
 * which was licensed under the Apache License, Version 2.0 at the time.
 * In accordance with that license, the modifications / derivative work
 * is now being licensed under the LGPL as part of the OOTBee Support Tools
 * addon.
 */
package org.orderofthebee.addons.support.tools.repo.jsconsole;

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
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.cache.SimpleCache;
import org.alfresco.repo.content.MimetypeMap;
import org.alfresco.repo.jscript.RhinoScriptProcessor;
import org.alfresco.repo.jscript.ScriptNode;
import org.alfresco.repo.jscript.ScriptUtils;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.security.permissions.AccessDeniedException;
import org.alfresco.scripts.ScriptResourceHelper;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.security.PermissionService;
import org.alfresco.service.transaction.TransactionService;
import org.alfresco.util.MD5;
import org.alfresco.util.Pair;
import org.apache.commons.logging.LogFactory;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
public class ExecuteWebscript extends AbstractWebScript
{

    private static final Logger LOGGER = LoggerFactory.getLogger(ExecuteWebscript.class);

    private ScriptUtils scriptUtils;

    private TransactionService transactionService;

    private NodeService nodeService;

    private PermissionService permissionService;

    private String postRollScript = "";

    private org.alfresco.service.cmr.repository.ScriptProcessor jsProcessor;

    private DumpService dumpService;

    public void setDumpService(final DumpService dumpService)
    {
        this.dumpService = dumpService;
    }

    private SimpleCache<Pair<String, Integer>, List<String>> printOutputCache;

    private SimpleCache<String, JavascriptConsoleResultBase> resultCache;

    private int printOutputChunkSize = 5;

    private String preRollScriptClasspath;

    private String postRollScriptClasspath;
    
    private boolean allowUnrestrictedScripts;

    /**
     *
     * {@inheritDoc}
     */
    @Override
    public void init(final Container container, final Description description)
    {
        super.init(container, description);
        try
        {
            this.postRollScript = this.readScriptFromClasspath(this.postRollScriptClasspath);
        }
        catch (final IOException e)
        {
            LOGGER.error("Could not read pre-roll script", e);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see org.springframework.extensions.webscripts.WebScript#execute(org.
     * springframework.extensions.webscripts.WebScriptRequest,
     * org.springframework.extensions.webscripts.WebScriptResponse)
     */
    @Override
    public void execute(final WebScriptRequest request, final WebScriptResponse response) throws IOException
    {
        int scriptOffset = 0;

        JavascriptConsoleResult result = null;
        try
        {
            // this isn't very precise since there is some bit of processing until here that we can't measure
            final PerfLog webscriptPerf = new PerfLog().start();
            final JavascriptConsoleRequest jsreq = JavascriptConsoleRequest.readJson(request);

            // Note: Need to use import here so the user-supplied script may also import scripts
            final String script = "<import resource=\"classpath:" + this.preRollScriptClasspath + "\">\n" + jsreq.script;

            final ScriptContent scriptContent = new StringScriptContent(script + this.postRollScript, this.allowUnrestrictedScripts);

            final int providedScriptLength = this.countScriptLines(jsreq.script, false);
            final int resolvedScriptLength = this.countScriptLines(script, true);
            scriptOffset = providedScriptLength - resolvedScriptLength;

            try
            {
                result = this.runScriptWithTransactionAndAuthentication(request, response, jsreq, scriptContent);

                result.setScriptOffset(scriptOffset);

                // this won't be very precise since there is still some post-processing, but we can't delay it any longer
                result.setWebscriptPerformance(
                        String.valueOf(webscriptPerf.stop("Took {} ms to execute webscript with {} - result: {} ", jsreq, result)));

                if (!result.isStatusResponseSent())
                {
                    result.writeJson(response);
                }
            }
            finally
            {
                if (jsreq.resultChannel != null && ExecuteWebscript.this.resultCache != null)
                {
                    if (result != null)
                    {
                        ExecuteWebscript.this.resultCache.put(jsreq.resultChannel, result.toBaseResult());
                    }
                    else
                    {
                        // dummy response as indicator for "error"
                        ExecuteWebscript.this.resultCache.put(jsreq.resultChannel, new JavascriptConsoleResultBase());
                    }

                }
            }

        }
        catch (final WebScriptException e)
        {
            response.setStatus(500);
            response.setContentEncoding("UTF-8");
            response.setContentType(MimetypeMap.MIMETYPE_JSON);

            this.writeErrorInfosAsJson(response, result, scriptOffset, e);
        }
    }

    private int countScriptLines(final String script, final boolean attemptImportResolution)
    {
        String scriptSource;

        if (attemptImportResolution && this.jsProcessor instanceof RhinoScriptProcessor)
        {
            // resolve any imports
            scriptSource = ScriptResourceHelper.resolveScriptImports(script, (RhinoScriptProcessor) this.jsProcessor,
                    LogFactory.getLog(ExecuteWebscript.class));
        }
        else
        {
            // assume this is the literal source
            scriptSource = script;
        }

        // EOL is not only dependent on the current system but on the environment of the script author, so check for any known EOL styles
        final String[] scriptLines = scriptSource.split("(\\r?\\n\\r?)|(\\r)");
        return scriptLines.length;
    }

    /**
     * used our own json reponse for errors because you cannot pass your own
     * parameters to the built-in alfresco status templates.
     *
     * @param response
     * @param result
     * @param scriptOffset
     * @param e
     *     the occured exception
     * @throws IOException
     */
    private void writeErrorInfosAsJson(final WebScriptResponse response, final JavascriptConsoleResult result, final int scriptOffset,
            final WebScriptException e) throws IOException
    {
        try
        {
            final JSONObject jsonOutput = new JSONObject();

            // set some common stuff like
            final JSONObject status = new JSONObject();
            status.put("code", 500);
            status.put("name", "Internal Error");
            status.put("description", "An error inside the HTTP server which prevented it from fulfilling the request.");
            jsonOutput.put("status", status);

            // find out the closest error message which is helpful for the
            // user...
            String errorMessage = e.getMessage();
            if (e.getCause() != null)
            {
                errorMessage = e.getCause().getMessage();
                if (e.getCause().getCause() != null)
                {
                    errorMessage = e.getCause().getCause().getMessage();
                }
            }
            jsonOutput.put("message", errorMessage);

            // print the stacktrace into the callstack variable...
            final Writer writer = new StringWriter();
            final PrintWriter printWriter = new PrintWriter(writer);
            e.printStackTrace(printWriter);
            final String s = writer.toString();
            jsonOutput.put("callstack", s);

            // always print the result into the error stream because we want to have all outputs before the exceptions occurs
            if (result != null)
            {
                jsonOutput.put("result", result.generateJsonOutput().toString());
            }

            // scriptOffset is useful to determine the correct line in case of an error
            // (if you use preroll-scripts or imports in javascript input)
            jsonOutput.put("scriptOffset", scriptOffset);

            response.getWriter().write(jsonOutput.toString(5));

        }
        catch (final JSONException ex)
        {
            throw new WebScriptException(Status.STATUS_INTERNAL_SERVER_ERROR, "Error writing json error response.", ex);
        }
    }

    private String readScriptFromClasspath(final String resourceName) throws IOException
    {
        final StringBuilder script = new StringBuilder();
        final InputStream resource = this.getClass().getClassLoader().getResourceAsStream(resourceName);
        if (resource != null)
        {
            try (BufferedReader bfr = new BufferedReader(new InputStreamReader(resource, StandardCharsets.UTF_8)))
            {
                String line;
                while ((line = bfr.readLine()) != null)
                {
                    script.append(line);
                }
            }
        }
        else
        {
            throw new IllegalStateException("Unable to find classpath script resource " + resourceName);
        }
        return script.toString();
    }

    private JavascriptConsoleResult runScriptWithTransactionAndAuthentication(final WebScriptRequest request,
            final WebScriptResponse response, final JavascriptConsoleRequest jsreq, final ScriptContent scriptContent)
    {

        LOGGER.debug("running script as user {}", jsreq.runas);

        if (jsreq.runas != null && !jsreq.runas.trim().isEmpty())
        {
            return AuthenticationUtil.runAs(() -> this.runWithTransactionIfNeeded(request, response, jsreq, scriptContent), jsreq.runas);
        }
        else
        {
            return this.runWithTransactionIfNeeded(request, response, jsreq, scriptContent);
        }
    }

    private JavascriptConsoleResult runWithTransactionIfNeeded(final WebScriptRequest request, final WebScriptResponse response,
            final JavascriptConsoleRequest jsreq, final ScriptContent scriptContent)
    {

        final List<String> printOutput;
        if (jsreq.resultChannel != null && this.printOutputCache != null)
        {
            printOutput = new CacheBackedChunkedList<>(this.printOutputCache, jsreq.resultChannel, this.printOutputChunkSize);
        }
        else
        {
            printOutput = null;
        }

        JavascriptConsoleResult result = null;

        if (jsreq.useTransaction)
        {
            LOGGER.debug("Using transction to execute script: {}", jsreq.transactionReadOnly ? "readonly" : "readwrite");
            result = this.transactionService.getRetryingTransactionHelper().doInTransaction(() -> {
                // clear due to potential retry
                if (printOutput != null)
                {
                    printOutput.clear();
                }
                return this.executeScriptContent(request, response, scriptContent, jsreq.template, jsreq.spaceNodeRef, jsreq.urlargs,
                        jsreq.documentNodeRef, printOutput);
            }, jsreq.transactionReadOnly);
        }
        else
        {
            LOGGER.debug("Executing script script without transaction");
            result = this.executeScriptContent(request, response, scriptContent, jsreq.template, jsreq.spaceNodeRef, jsreq.urlargs,
                    jsreq.documentNodeRef, printOutput);
        }
        return result;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.alfresco.web.scripts.WebScript#execute(org.alfresco.web.scripts.
     * WebScriptRequest, org.alfresco.web.scripts.WebScriptResponse)
     */
    private JavascriptConsoleResult executeScriptContent(final WebScriptRequest req, final WebScriptResponse res,
            final ScriptContent scriptContent, final String template, final String spaceNodeRef, final Map<String, String> urlargs,
            final String documentNodeRef, final List<String> printOutput)
    {
        final JavascriptConsoleResult output = new JavascriptConsoleResult();

        // retrieve requested format
        final String format = req.getFormat();

        try
        {
            // construct model for script / template
            final Status status = new Status();
            final Cache cache = new Cache(this.getDescription().getRequiredCache());
            final Map<String, Object> model = new HashMap<>(8, 1.0f);
            model.put("status", status);
            model.put("cache", cache);

            final Map<String, Object> scriptModel = this.createScriptParameters(req, res, null, model);

            this.augmentScriptModelArgs(scriptModel, urlargs);

            // add return model allowing script to add items to template model
            final Map<String, Object> returnModel = new HashMap<>(8, 1.0f);
            scriptModel.put("model", returnModel);

            final JavascriptConsoleScriptObject javascriptConsole = printOutput == null ? new JavascriptConsoleScriptObject()
                    : new JavascriptConsoleScriptObject(printOutput);
            scriptModel.put("jsconsole", javascriptConsole);

            if (spaceNodeRef != null && !spaceNodeRef.trim().isEmpty())
            {
                javascriptConsole.setSpace(this.scriptUtils.getNodeFromString(spaceNodeRef));
            }
            else
            {
                final Object ch = scriptModel.get("companyhome");
                if (ch instanceof NodeRef)
                {
                    javascriptConsole.setSpace(this.scriptUtils.getNodeFromString(ch.toString()));
                }
                else
                {
                    javascriptConsole.setSpace((ScriptNode) ch);
                }
            }
            scriptModel.put("space", javascriptConsole.getSpace());

            ScriptNode documentNode = null;
            if (documentNodeRef != null && !documentNodeRef.trim().isEmpty())
            {
                documentNode = this.scriptUtils.getNodeFromString(documentNodeRef);
                scriptModel.put("document", documentNode);
            }
            scriptModel.put("dumpService", this.dumpService);

            final PerfLog jsPerf = new PerfLog().start();
            try
            {
                final ScriptProcessor scriptProcessor = this.getContainer().getScriptProcessorRegistry()
                        .getScriptProcessorByExtension("js");
                scriptProcessor.executeScript(scriptContent, scriptModel);
            }
            finally
            {
                output.setScriptPerformance(
                        String.valueOf(jsPerf.stop("Took {} ms to execute script in {} with model {}", scriptContent, scriptModel)));
                output.setPrintOutput(javascriptConsole.getPrintOutput());
                if (documentNode != null)
                {
                    output.setDumpOutput(this.dumpService.addDump(documentNode));
                }
            }

            final ScriptNode newSpace = javascriptConsole.getSpace();
            final NodeRef newSpaceRef = newSpace.getNodeRef();
            output.setSpaceNodeRef(newSpaceRef.toString());
            try
            {
                output.setSpacePath(this.nodeService.getPath(newSpaceRef).toDisplayPath(this.nodeService, this.permissionService) + "/"
                        + this.nodeService.getProperty(newSpaceRef, ContentModel.PROP_NAME));
            }
            catch (final AccessDeniedException ade)
            {
                output.setSpacePath("/");
            }

            this.mergeScriptModelIntoTemplateModel(scriptContent, returnModel, model);

            // create model for template rendering
            final Map<String, Object> templateModel = this.createTemplateParameters(req, res, model);

            // is a redirect to a status specific template required?
            if (status.getRedirect())
            {
                this.sendStatus(req, res, status, cache, format, templateModel);
                output.setStatusResponseSent(true);
            }
            else
            {
                // apply location
                final String location = status.getLocation();
                if (location != null && location.length() > 0)
                {
                    LOGGER.debug("Setting location to {}", location);
                    res.setHeader(WebScriptResponse.HEADER_LOCATION, location);
                }

                if (template != null && !template.trim().isEmpty())
                {
                    final PerfLog freemarkerPerf = new PerfLog().start();
                    final TemplateProcessor templateProcessor = this.getContainer().getTemplateProcessorRegistry()
                            .getTemplateProcessorByExtension("ftl");
                    final StringWriter sw = new StringWriter();
                    templateProcessor.processString(template, templateModel, sw);
                    final String templateResult = sw.toString();
                    output.setFreemarkerPerformance(String.valueOf(freemarkerPerf.stop(
                            "Took {} ms to execute template {} with model {} with result {}", template, templateModel, templateResult)));
                    output.setRenderedTemplate(templateResult);
                }
            }
        }
        catch (final Exception e)
        {
            LOGGER.debug("Caught exception; decorating with appropriate status template", e);
            throw this.createStatusException(e, req, res);
        }
        return output;
    }

    private void augmentScriptModelArgs(final Map<String, Object> scriptModel, final Map<String, String> additionalUrlArgs)
    {
        @SuppressWarnings("unchecked")
        final Map<String, String> args = (Map<String, String>) scriptModel.get("args");

        args.putAll(additionalUrlArgs);
    }

    /**
     * Merge script generated model into template-ready model
     *
     * @param scriptContent
     *     script content
     * @param scriptModel
     *     script model
     * @param templateModel
     *     template model
     */
    private final void mergeScriptModelIntoTemplateModel(final ScriptContent scriptContent, final Map<String, Object> scriptModel,
            final Map<String, Object> templateModel)
    {
        // determine script processor
        final ScriptProcessor scriptProcessor = this.getContainer().getScriptProcessorRegistry().getScriptProcessor(scriptContent);
        if (scriptProcessor != null)
        {
            for (final Map.Entry<String, Object> entry : scriptModel.entrySet())
            {
                // retrieve script model value
                final Object value = entry.getValue();
                final Object templateValue = scriptProcessor.unwrapValue(value);
                templateModel.put(entry.getKey(), templateValue);
            }
        }
    }

    /**
     * Render a template (of given format) to the Web Script Response
     *
     * @param format
     *     template format (null, default format)
     * @param model
     *     data model to render
     * @param writer
     *     where to output
     */
    protected final void renderFormatTemplate(String format, final Map<String, Object> model, final Writer writer)
    {
        format = (format == null) ? "" : format;

        final String templatePath = this.getDescription().getId() + "." + format;

        LOGGER.debug("Rendering template {}", templatePath);

        this.renderTemplate(templatePath, model, writer);
    }

    public void setScriptUtils(final ScriptUtils scriptUtils)
    {
        this.scriptUtils = scriptUtils;
    }

    public void setTransactionService(final TransactionService transactionService)
    {
        this.transactionService = transactionService;
    }

    public void setNodeService(final NodeService nodeService)
    {
        this.nodeService = nodeService;
    }

    public void setPermissionService(final PermissionService permissionService)
    {
        this.permissionService = permissionService;
    }

    public void setJsProcessor(final org.alfresco.service.cmr.repository.ScriptProcessor jsProcessor)
    {
        this.jsProcessor = jsProcessor;
    }

    public final void setPrintOutputCache(final SimpleCache<Pair<String, Integer>, List<String>> printOutputCache)
    {
        this.printOutputCache = printOutputCache;
    }

    public final void setResultCache(final SimpleCache<String, JavascriptConsoleResultBase> resultCache)
    {
        this.resultCache = resultCache;
    }

    public final void setPrintOutputChunkSize(final int printOutputChunkSize)
    {
        this.printOutputChunkSize = printOutputChunkSize;
    }

    public final void setPreRollScriptClasspath(final String preRollScriptClasspath)
    {
        this.preRollScriptClasspath = preRollScriptClasspath;
    }

    public final void setPostRollScriptClasspath(final String postRollScriptClasspath)
    {
        this.postRollScriptClasspath = postRollScriptClasspath;
    }
    
    public final void setAllowUnrestrictedScripts(boolean allowUnrestrictedScripts)
    {
      this.allowUnrestrictedScripts = allowUnrestrictedScripts;
    }

    private static class StringScriptContent implements ScriptContent
    {

        private final String content;
                
        private final boolean secure;

        public StringScriptContent(final String content, final boolean secure)
        {
            this.content = content;
            this.secure = secure;
        }

        @Override
        public InputStream getInputStream()
        {
            return new ByteArrayInputStream(this.content.getBytes(Charset.forName("UTF-8")));
        }

        @Override
        public String getPath()
        {
            return MD5.Digest(this.content.getBytes()) + ".js";
        }

        @Override
        public String getPathDescription()
        {
            return "Javascript Console Script";
        }

        @Override
        public Reader getReader()
        {
            return new StringReader(this.content);
        }

        @Override
        public boolean isCachable()
        {
            return false;
        }

        @Override
        public boolean isSecure()
        {
            return this.secure;
        }
    }

}
