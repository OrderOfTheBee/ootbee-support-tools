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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.alfresco.repo.content.MimetypeMap;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptException;
import org.springframework.extensions.webscripts.WebScriptResponse;

/**
 * Stores the result of a script and template execution on the Javascript Console and
 * is used internally by the {@link ExecuteWebscript}.
 *
 * @author Florian Maul (fme AG)
 *
 */
public class JavascriptConsoleResult extends JavascriptConsoleResultBase
{

    private static final long serialVersionUID = 1988880899541060406L;

    private final List<String> printOutput = new ArrayList<>();

    private boolean statusResponseSent = false;

    private final List<Dump> dumpOutput = new ArrayList<>();

    public void setPrintOutput(final List<String> printOutput)
    {
        this.printOutput.clear();
        this.printOutput.addAll(printOutput);
    }

    public List<String> getPrintOutput()
    {
        return Collections.unmodifiableList(this.printOutput);
    }

    public void writeJson(final WebScriptResponse response) throws IOException
    {
        response.setContentEncoding("UTF-8");
        response.setContentType(MimetypeMap.MIMETYPE_JSON);

        try
        {
            final JSONObject jsonOutput = this.generateJsonOutput();
            response.getWriter().write(jsonOutput.toString());
        }
        catch (final JSONException e)
        {
            throw new WebScriptException(Status.STATUS_INTERNAL_SERVER_ERROR, "Error writing json response.", e);
        }
    }

    /**
     * Generates the execution summary / details of the handled JavaScript Console request.
     *
     * @return the JSON object structure of the execution summary / details
     * @throws JSONException
     *     if an error occurs preparing the execution summary / details JSON structure
     */
    public JSONObject generateJsonOutput() throws JSONException
    {
        final JSONObject jsonOutput = new JSONObject();
        jsonOutput.put("renderedTemplate", this.getRenderedTemplate());
        jsonOutput.put("printOutput", this.getPrintOutput());
        jsonOutput.put("dumpOutput", this.getDumpOutput());
        jsonOutput.put("spaceNodeRef", this.getSpaceNodeRef());
        jsonOutput.put("spacePath", this.getSpacePath());
        jsonOutput.put("result", new JSONArray());
        jsonOutput.put("scriptPerf", this.getScriptPerformance());
        jsonOutput.put("freemarkerPerf", this.getFreemarkerPerformance());
        jsonOutput.put("webscriptPerf", this.getWebscriptPerformance());
        jsonOutput.put("scriptOffset", this.getScriptPerformance());
        return jsonOutput;
    }

    public boolean isStatusResponseSent()
    {
        return this.statusResponseSent;
    }

    public void setStatusResponseSent(final boolean statusResponseSent)
    {
        this.statusResponseSent = statusResponseSent;
    }

    public void setDumpOutput(final List<Dump> dumpOutput)
    {
        this.dumpOutput.clear();
        this.dumpOutput.addAll(dumpOutput);
    }

    public List<Dump> getDumpOutput()
    {
        return Collections.unmodifiableList(this.dumpOutput);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + this.dumpOutput.hashCode();
        result = prime * result + this.printOutput.hashCode();
        result = prime * result + (this.statusResponseSent ? 1231 : 1237);
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
        if (!super.equals(obj))
        {
            return false;
        }
        if (this.getClass() != obj.getClass())
        {
            return false;
        }
        final JavascriptConsoleResult other = (JavascriptConsoleResult) obj;
        if (!this.dumpOutput.equals(other.dumpOutput))
        {
            return false;
        }
        if (!this.printOutput.equals(other.printOutput))
        {
            return false;
        }
        if (this.statusResponseSent != other.statusResponseSent)
        {
            return false;
        }
        return true;
    }

}
