package de.fme.jsconsole;

import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.springframework.extensions.surf.util.Content;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptException;
import org.springframework.extensions.webscripts.WebScriptRequest;

/**
 * Parses and stores the input data for the Javascript Console {@link ExecuteWebscript} and contains
 * the logic to decode the request body JSON data.
 *
 * @author Florian Maul (fme AG)
 *
 */
public class JavascriptConsoleRequest {

	/** The Constant DEFAULT_DUMP_LIMIT. */
	private static final int DEFAULT_DUMP_LIMIT = 10;

	/** The script. */
	public final String script;
	
	/** The template. */
	public final String template;
	
	/** The space node ref. */
	public final String spaceNodeRef;
	
	/** The runas. */
	public final String runas;
	
	/** The use transaction. */
	public final boolean useTransaction;
	
	/** The transaction read only. */
	public final boolean transactionReadOnly;
	
	/** The urlargs. */
	public final Map<String, String> urlargs;
	
	/** The document node ref. */
	public final String documentNodeRef;
	
	/** The dump limit. */
	public final Integer dumpLimit;

	/** The result channel. */
	public final String resultChannel;

	/**
	 * Instantiates a new javascript console request.
	 *
	 * @param script the script
	 * @param template the template
	 * @param spaceNodeRef the space node ref
	 * @param transaction the transaction
	 * @param runas the runas
	 * @param urlargs the urlargs
	 * @param documentNodeRef the document node ref
	 * @param dumpLimit the dump limit
	 * @param resultChannel the result channel
	 */
	private JavascriptConsoleRequest(String script, String template,
            String spaceNodeRef, String transaction, String runas, String urlargs, String documentNodeRef, Integer dumpLimit, String resultChannel) {
        super();
        this.script = script;
        this.template = template;
        this.spaceNodeRef = spaceNodeRef;
        this.documentNodeRef = documentNodeRef;
		this.dumpLimit = dumpLimit;
        this.urlargs = parseQueryString(urlargs);
        this.transactionReadOnly = "readonly".equalsIgnoreCase(transaction);
        this.useTransaction = transactionReadOnly || "readwrite".equalsIgnoreCase(transaction);
        this.runas = runas;
        this.resultChannel = resultChannel;
    }

	/**
	 * parses the query string
	 * is used because HttpUtils.parseQueryString is deprecated
	 *
	 * @param queryString the query string
	 * @return the map
	 */
    protected static Map<String, String> parseQueryString(String queryString) {
        Map<String, String> map = new HashMap<String, String>();

        String[] parameters = queryString.split("&");
        for(int i = 0; i < parameters.length; i++) {
            String[] keyAndValue = parameters[i].split("=");
            if(keyAndValue.length != 2) {
                // "invalid url parameter " + parameters[i]);
                continue;
            }
            String key = keyAndValue[0];
            String value = keyAndValue[1];
            map.put(key, value);
        }

        return map;
    }

	/**
	 * Read json.
	 *
	 * @param request the request
	 * @return the javascript console request
	 */
	public static JavascriptConsoleRequest readJson(WebScriptRequest request) {
		Content content = request.getContent();

		InputStreamReader br = new InputStreamReader(content.getInputStream(),
				Charset.forName("UTF-8"));
		JSONTokener jsonTokener = new JSONTokener(br);
		try {
			JSONObject jsonInput = new JSONObject(jsonTokener);

			String script = jsonInput.getString("script");
			String template = jsonInput.getString("template");
			String spaceNodeRef = jsonInput.getString("spaceNodeRef");
			String transaction = jsonInput.getString("transaction");
			String urlargs = jsonInput.getString("urlargs");
			String documentNodeRef = jsonInput.getString("documentNodeRef");
			int dumpLimit = DEFAULT_DUMP_LIMIT;
			if(jsonInput.has("dumpLimit")){
				dumpLimit = jsonInput.getInt("dumpLimit");
			}
			String logOutputChannel = jsonInput.has("printOutputChannel") ? jsonInput.getString("printOutputChannel") : null;
			String resultChannel = jsonInput.has("resultChannel") ? jsonInput.getString("resultChannel") : null;

			String runas = jsonInput.getString("runas");
			if (runas == null) {
				runas = "";
			}
			
			return new JavascriptConsoleRequest(script, template, spaceNodeRef, transaction, runas, urlargs, documentNodeRef, dumpLimit, resultChannel);
			
		} catch (JSONException e) {
			throw new WebScriptException(Status.STATUS_INTERNAL_SERVER_ERROR,
					"Error reading json request body.", e);
		}
	}

	/**
	 * To string.
	 *
	 * @return the string
	 */
	@Override
	public String toString() {
		return "JavascriptConsoleRequest [script=" + script + ", template=" + template + ", spaceNodeRef=" + spaceNodeRef
				+ ", runas=" + runas + ", useTransaction=" + useTransaction + ", transactionReadOnly=" + transactionReadOnly
				+ ", urlargs=" + urlargs + ", documentNodeRef=" + documentNodeRef + ", dumpLimit=" + dumpLimit + ", resultChannel=" + resultChannel + "]";
	}

}
