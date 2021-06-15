package de.fme.jsconsole;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;

import org.alfresco.repo.content.MimetypeMap;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptException;
import org.springframework.extensions.webscripts.WebScriptResponse;

/**
 * Stores the base result of a script and template execution on the Javascript Console and
 * is used internally by the {@link ExecuteWebscript}.
 *
 * @author Florian Maul (fme AG)
 *
 */
public class JavascriptConsoleResultBase implements Serializable {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 4149990179052751784L;

    /** The rendered template. */
    private String renderedTemplate = "";

	/** The space node ref. */
	private String spaceNodeRef = "";

	/** The space path. */
	private String spacePath = "";

	/** The script performance. */
	private String scriptPerformance;

	/** The freemarker performance. */
	private String freemarkerPerformance;

	/** The webscript performance. */
	private String webscriptPerformance;

	/** The script offset. */
	private int scriptOffset;

	/**
	 * Sets the webscript performance.
	 *
	 * @param webscriptPerformance the new webscript performance
	 */
	public void setWebscriptPerformance(String webscriptPerformance) {
		this.webscriptPerformance = webscriptPerformance;
	}

	/**
	 * Sets the script performance.
	 *
	 * @param scriptPerformance the new script performance
	 */
	public void setScriptPerformance(String scriptPerformance) {
		this.scriptPerformance = scriptPerformance;
	}

	/**
	 * Sets the freemarker performance.
	 *
	 * @param freemarkerPerformance the new freemarker performance
	 */
	public void setFreemarkerPerformance(String freemarkerPerformance) {
		this.freemarkerPerformance = freemarkerPerformance;
	}

	/**
	 * Sets the rendered template.
	 *
	 * @param renderedTemplate the new rendered template
	 */
	public void setRenderedTemplate(String renderedTemplate) {
		this.renderedTemplate = renderedTemplate;
	}

	/**
	 * Sets the space node ref.
	 *
	 * @param spaceNodeRef the new space node ref
	 */
	public void setSpaceNodeRef(String spaceNodeRef) {
		this.spaceNodeRef = spaceNodeRef;
	}

	/**
	 * Sets the space path.
	 *
	 * @param spacePath the new space path
	 */
	public void setSpacePath(String spacePath) {
		this.spacePath = spacePath;
	}
	
	/**
	 * Gets the webscript performance.
	 *
	 * @return the webscript performance
	 */
	public String getWebscriptPerformance() {
	    return this.webscriptPerformance;
	}
	
	/**
	 * Gets the script performance.
	 *
	 * @return the script performance
	 */
	public String getScriptPerformance() {
        return this.scriptPerformance;
    }
	
	/**
	 * Gets the freemarker performance.
	 *
	 * @return the freemarker performance
	 */
	public String getFreemarkerPerformance() {
        return this.freemarkerPerformance;
    }

	/**
	 * Gets the rendered template.
	 *
	 * @return the rendered template
	 */
	public String getRenderedTemplate() {
		return renderedTemplate;
	}

	/**
	 * Gets the space node ref.
	 *
	 * @return the space node ref
	 */
	public String getSpaceNodeRef() {
		return spaceNodeRef;
	}

	/**
	 * Gets the space path.
	 *
	 * @return the space path
	 */
	public String getSpacePath() {
		return spacePath;
	}

	/**
	 * Write json.
	 *
	 * @param response the response
	 * @param printOutput the print output
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public void writeJson(WebScriptResponse response, List<String> printOutput) throws IOException {
		response.setContentEncoding("UTF-8");
		response.setContentType(MimetypeMap.MIMETYPE_JSON);

		try {
			JSONObject jsonOutput = new JSONObject();
			jsonOutput.put("renderedTemplate", getRenderedTemplate());
			jsonOutput.put("printOutput", printOutput);
			jsonOutput.put("spaceNodeRef", getSpaceNodeRef());
			jsonOutput.put("spacePath", getSpacePath());
			jsonOutput.put("result", new JSONArray());
			jsonOutput.put("scriptPerf", this.scriptPerformance);
			jsonOutput.put("freemarkerPerf", this.freemarkerPerformance);
			jsonOutput.put("webscriptPerf", this.webscriptPerformance);
			jsonOutput.put("scriptOffset", this.scriptOffset);

			response.getWriter().write(jsonOutput.toString());

		} catch (JSONException e) {
			throw new WebScriptException(Status.STATUS_INTERNAL_SERVER_ERROR,
					"Error writing json response.", e);
		}
	}
	
	/**
	 * To base result.
	 *
	 * @return the javascript console result base
	 */
	public JavascriptConsoleResultBase toBaseResult() {
	    final JavascriptConsoleResultBase base = new JavascriptConsoleResultBase();
	    base.setFreemarkerPerformance(this.freemarkerPerformance);
	    base.setRenderedTemplate(this.renderedTemplate);
	    base.setScriptOffset(this.scriptOffset);
	    base.setScriptPerformance(this.scriptPerformance);
	    base.setSpaceNodeRef(this.spaceNodeRef);
	    base.setSpacePath(this.spacePath);
	    base.setWebscriptPerformance(this.webscriptPerformance);
	    
	    return base;
	}

	/**
	 * To string.
	 *
	 * @return the string
	 */
	@Override
	public String toString() {
		return "JavascriptConsoleResultBase [renderedTemplate=" + this.renderedTemplate
				+ ", spaceNodeRef=" + this.spaceNodeRef + ", spacePath=" + this.spacePath
				+ ", scriptPerformance=" + this.scriptPerformance + ", freemarkerPerformance=" + this.freemarkerPerformance + "]";
	}

	/**
	 * Sets the script offset.
	 *
	 * @param scriptOffset the new script offset
	 */
	public void setScriptOffset(int scriptOffset) {
		this.scriptOffset = scriptOffset;
	}

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((this.freemarkerPerformance == null) ? 0 : this.freemarkerPerformance.hashCode());
        result = prime * result + ((this.renderedTemplate == null) ? 0 : this.renderedTemplate.hashCode());
        result = prime * result + this.scriptOffset;
        result = prime * result + ((this.scriptPerformance == null) ? 0 : this.scriptPerformance.hashCode());
        result = prime * result + ((this.spaceNodeRef == null) ? 0 : this.spaceNodeRef.hashCode());
        result = prime * result + ((this.spacePath == null) ? 0 : this.spacePath.hashCode());
        result = prime * result + ((this.webscriptPerformance == null) ? 0 : this.webscriptPerformance.hashCode());
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
        {
            return true;
        }
        if (obj == null)
        {
            return false;
        }
        if (!(obj instanceof JavascriptConsoleResultBase))
        {
            return false;
        }
        JavascriptConsoleResultBase other = (JavascriptConsoleResultBase) obj;
        if (this.freemarkerPerformance == null)
        {
            if (other.freemarkerPerformance != null)
            {
                return false;
            }
        }
        else if (!this.freemarkerPerformance.equals(other.freemarkerPerformance))
        {
            return false;
        }
        if (this.renderedTemplate == null)
        {
            if (other.renderedTemplate != null)
            {
                return false;
            }
        }
        else if (!this.renderedTemplate.equals(other.renderedTemplate))
        {
            return false;
        }
        if (this.scriptOffset != other.scriptOffset)
        {
            return false;
        }
        if (this.scriptPerformance == null)
        {
            if (other.scriptPerformance != null)
            {
                return false;
            }
        }
        else if (!this.scriptPerformance.equals(other.scriptPerformance))
        {
            return false;
        }
        if (this.spaceNodeRef == null)
        {
            if (other.spaceNodeRef != null)
            {
                return false;
            }
        }
        else if (!this.spaceNodeRef.equals(other.spaceNodeRef))
        {
            return false;
        }
        if (this.spacePath == null)
        {
            if (other.spacePath != null)
            {
                return false;
            }
        }
        else if (!this.spacePath.equals(other.spacePath))
        {
            return false;
        }
        if (this.webscriptPerformance == null)
        {
            if (other.webscriptPerformance != null)
            {
                return false;
            }
        }
        else if (!this.webscriptPerformance.equals(other.webscriptPerformance))
        {
            return false;
        }
        return true;
    }

}
