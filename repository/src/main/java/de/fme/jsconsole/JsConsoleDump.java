package de.fme.jsconsole;

/**
 * The Class JsConsoleDump.
 */
public class JsConsoleDump {

	/** The json. */
	private String json;
	
	/** The node ref. */
	private String nodeRef;

	/**
	 * Instantiates a new js console dump.
	 *
	 * @param nodeRef the node ref
	 * @param json the json
	 */
	public JsConsoleDump(String nodeRef, String json) {
		this.nodeRef = nodeRef;
		this.json = json;
	}

	/**
	 * Gets the node ref.
	 *
	 * @return the node ref
	 */
	public String getNodeRef() {
		return nodeRef;
	}

	/**
	 * Gets the json.
	 *
	 * @return the json
	 */
	public String getJson() {
		return json;
	}

	/**
	 * To string.
	 *
	 * @return the string
	 */
	@Override
	public String toString() {
		return json;
	}

}
