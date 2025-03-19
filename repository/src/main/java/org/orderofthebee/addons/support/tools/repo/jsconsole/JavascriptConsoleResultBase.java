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
public class JavascriptConsoleResultBase implements Serializable
{

    private static final long serialVersionUID = 4149990179052751784L;

    private String renderedTemplate = "";

    private String spaceNodeRef = "";

    private String spacePath = "";

    private String scriptPerformance;

    private String freemarkerPerformance;

    private String webscriptPerformance;

    private int scriptOffset;

    public void setWebscriptPerformance(final String webscriptPerformance)
    {
        this.webscriptPerformance = webscriptPerformance;
    }

    public void setScriptPerformance(final String scriptPerformance)
    {
        this.scriptPerformance = scriptPerformance;
    }

    public void setFreemarkerPerformance(final String freemarkerPerformance)
    {
        this.freemarkerPerformance = freemarkerPerformance;
    }

    public void setRenderedTemplate(final String renderedTemplate)
    {
        this.renderedTemplate = renderedTemplate;
    }

    public void setSpaceNodeRef(final String spaceNodeRef)
    {
        this.spaceNodeRef = spaceNodeRef;
    }

    public void setSpacePath(final String spacePath)
    {
        this.spacePath = spacePath;
    }

    public String getWebscriptPerformance()
    {
        return this.webscriptPerformance;
    }

    public String getScriptPerformance()
    {
        return this.scriptPerformance;
    }

    public String getFreemarkerPerformance()
    {
        return this.freemarkerPerformance;
    }

    public String getRenderedTemplate()
    {
        return this.renderedTemplate;
    }

    public String getSpaceNodeRef()
    {
        return this.spaceNodeRef;
    }

    public String getSpacePath()
    {
        return this.spacePath;
    }

    public void writeJson(final WebScriptResponse response, final List<String> printOutput) throws IOException
    {
        response.setContentEncoding("UTF-8");
        response.setContentType(MimetypeMap.MIMETYPE_JSON);

        try
        {
            final JSONObject jsonOutput = new JSONObject();
            jsonOutput.put("renderedTemplate", this.getRenderedTemplate());
            jsonOutput.put("printOutput", printOutput);
            jsonOutput.put("dumpOutput", new JSONArray());
            jsonOutput.put("spaceNodeRef", this.getSpaceNodeRef());
            jsonOutput.put("spacePath", this.getSpacePath());
            jsonOutput.put("result", new JSONArray());
            jsonOutput.put("scriptPerf", this.scriptPerformance);
            jsonOutput.put("freemarkerPerf", this.freemarkerPerformance);
            jsonOutput.put("webscriptPerf", this.webscriptPerformance);
            jsonOutput.put("scriptOffset", this.scriptOffset);

            response.getWriter().write(jsonOutput.toString());

        }
        catch (final JSONException e)
        {
            throw new WebScriptException(Status.STATUS_INTERNAL_SERVER_ERROR, "Error writing json response.", e);
        }
    }

    public JavascriptConsoleResultBase toBaseResult()
    {
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

    @Override
    public String toString()
    {
        return "JavascriptConsoleResultBase [renderedTemplate=" + this.renderedTemplate + ", spaceNodeRef=" + this.spaceNodeRef
                + ", spacePath=" + this.spacePath + ", scriptPerformance=" + this.scriptPerformance + ", freemarkerPerformance="
                + this.freemarkerPerformance + "]";
    }

    public void setScriptOffset(final int scriptOffset)
    {
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
    public boolean equals(final Object obj)
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
        final JavascriptConsoleResultBase other = (JavascriptConsoleResultBase) obj;
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
