package de.fme.jsconsole;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.alfresco.repo.jscript.ScriptNode;
import org.alfresco.repo.jscript.ScriptNode.ScriptContentData;
import org.alfresco.repo.security.authority.script.ScriptGroup;
import org.alfresco.repo.security.authority.script.ScriptUser;
import org.alfresco.repo.site.script.Site;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.springframework.extensions.webscripts.ScriptContent;
import org.springframework.extensions.webscripts.ScriptValueConverter;

/**
 * Implements the 'jsconsole' Javascript extension object that is available in
 * the Javascript Console and is used internally for print output and
 * communication between Java code and Javascript by the
 * {@link ExecuteWebscript}.
 *
 * @author Florian Maul (fme AG)
 *
 */
public class JavascriptConsoleScriptObject {

	/** The print output. */
	private List<String> printOutput = new ArrayList<String>();

	/** The space. */
	private ScriptNode space = null;

	/** The Constant JSON_KEY_ENTRY_ID. */
	public static final String JSON_KEY_ENTRY_ID = "id";
	
	/** The Constant JSON_KEY_ENTRY_APPLICATION. */
	public static final String JSON_KEY_ENTRY_APPLICATION = "application";
	
	/** The Constant JSON_KEY_ENTRY_USER. */
	public static final String JSON_KEY_ENTRY_USER = "user";
	
	/** The Constant JSON_KEY_ENTRY_TIME. */
	public static final String JSON_KEY_ENTRY_TIME = "time";
	
	/** The Constant JSON_KEY_ENTRY_VALUES. */
	public static final String JSON_KEY_ENTRY_VALUES = "values";


	/**
	 * Default constructor with print output stored in an internal data structure.
	 */
	public JavascriptConsoleScriptObject()
	{
	    // NO-OP as default constructor
	}

	/**
	 * Alternative constructor that allows clients to provide a specific data structure for print output management.
	 *
	 * @param printOutput the print output
	 */
	public JavascriptConsoleScriptObject(List<String> printOutput)
    {
        this.printOutput = printOutput;
    }

	/**
	 * Gets the space.
	 *
	 * @return the space
	 */
	public ScriptNode getSpace() {
		return space;
	}

	/**
	 * Sets the space.
	 *
	 * @param space the new space
	 */
	public void setSpace(ScriptNode space) {
		this.space = space;
	}

	/**
	 * Gets the logger.
	 *
	 * @return the logger
	 */
	public JavascriptConsoleScriptLogger getLogger() {
		return new JavascriptConsoleScriptLogger(this);
	}

	/**
	 * Prints the.
	 *
	 * @param obj the obj
	 */
	public void print(Object obj) {

		if (obj != null) {

			Object value = ScriptValueConverter.unwrapValue(obj);

			if (value instanceof Collection<?>) {
				Collection<?> col = (Collection<?>) value;
				Iterator<?> colIter = col.iterator();
				int counter = 0;
				while (colIter.hasNext()) {
					printOutput.add("" + counter + " : " + formatValue(colIter.next()));
					counter++;
				}
			} else {
				printOutput.add(formatValue(value));
			}
		} else {
			printOutput.add("null");
		}

	}

	/**
	 * Format value.
	 *
	 * @param value the value
	 * @return the string
	 */
	@SuppressWarnings("unchecked")
	private String formatValue(Object value) {

		if (value == null) {
			return "null";
		}

		if (value instanceof ScriptNode) {
			return formatScriptNode((ScriptNode) value);
		} else if (value instanceof ScriptContent) {
			return formatScriptContent((ScriptContent) value);
		} else if (value instanceof ScriptGroup) {
			return formatScriptGroup((ScriptGroup) value);
		} else if (value instanceof ScriptUser) {
			return formatScriptUser((ScriptUser) value);
		} else if (value instanceof NodeRef) {
			return formatNodeRef((NodeRef) value);
		} else if (value instanceof ChildAssociationRef) {
			return formatChildAssoc((ChildAssociationRef) value);
		} else if (value instanceof ScriptContentData) {
			return formatScriptContentData((ScriptContentData) value);
		} else if (value instanceof Site) {
			return formatSite((Site) value);
		} else if (value instanceof Map) {
			return formatMap((Map<String, Object>) value);
		}
		return value.toString();
	}

	/**
	 * Format map.
	 *
	 * @param map the map
	 * @return the string
	 */
	private String formatMap(Map<String, Object> map) {
		StringBuffer buffer = new StringBuffer();
		for (Map.Entry<String, Object> entry : map.entrySet()) {
			if (entry != null) {
				buffer.append(formatValue(entry.getKey()));
				buffer.append(" : ");
				buffer.append(formatValue(entry.getValue()));
				buffer.append("\n");
			}
		}
		return buffer.toString();
	}

	/**
	 * Format script user.
	 *
	 * @param value the value
	 * @return the string
	 */
	private String formatScriptUser(ScriptUser value) {
		return "ScriptUser: " + value.getUserName() + " (" + value.getFullName() + ")";
	}

	/**
	 * Format site.
	 *
	 * @param site the site
	 * @return the string
	 */
	private String formatSite(Site site) {
		return "Site: " + site.getShortName() + " (" + site.getTitle() + ", " + site.getNode().getNodeRef() + ")";
	}

	/**
	 * Format script group.
	 *
	 * @param value the value
	 * @return the string
	 */
	private String formatScriptGroup(ScriptGroup value) {
		return "ScriptGroup: " + value.getShortName() + " (" + value.getFullName() + ")";
	}

	/**
	 * Format script content.
	 *
	 * @param value the value
	 * @return the string
	 */
	private String formatScriptContent(ScriptContent value) {
		return "ScriptContent: " + value.getPath();
	}

	/**
	 * Format child assoc.
	 *
	 * @param value the value
	 * @return the string
	 */
	private String formatChildAssoc(ChildAssociationRef value) {
		return "ChildAssociationRef: parent=" + value.getParentRef().toString() + ", child=" + value.getChildRef().toString();
	}

	/**
	 * Format node ref.
	 *
	 * @param value the value
	 * @return the string
	 */
	private String formatNodeRef(NodeRef value) {
		return "NodeRef: " + value.toString();
	}

	/**
	 * Format script content data.
	 *
	 * @param value the value
	 * @return the string
	 */
	private String formatScriptContentData(ScriptContentData value) {
		return "ScriptContentData: " + value.getMimetype() + " Size:" + value.getSize() + " URL:" + value.getUrl();
	}

	/**
	 * Format script node.
	 *
	 * @param value the value
	 * @return the string
	 */
	private String formatScriptNode(ScriptNode value) {
		return value.getName() + " (" + value.getNodeRef() + ")";
	}

	/**
	 * Gets the prints the output.
	 *
	 * @return the prints the output
	 */
	public List<String> getPrintOutput() {
	    // defensive copy
		return new ArrayList<String>(this.printOutput);
	}

}
