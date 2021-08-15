/**
 * Copyright (C) 2016 - 2021 Order of the Bee
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
 * Copyright (C) 2005 - 2021 Alfresco Software Limited.
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

    private List<String> printOutput = new ArrayList<String>();

    private boolean statusResponseSent = false;

    private List<Dump> dumpOutput;

    public void setPrintOutput(List<String> printOutput)
    {
        this.printOutput = printOutput;
    }

    public List<String> getPrintOutput()
    {
        return this.printOutput;
    }

    public void writeJson(WebScriptResponse response) throws IOException
    {
        response.setContentEncoding("UTF-8");
        response.setContentType(MimetypeMap.MIMETYPE_JSON);

        try
        {
            JSONObject jsonOutput = generateJsonOutput();
            response.getWriter().write(jsonOutput.toString());
        }
        catch (JSONException e)
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
        JSONObject jsonOutput = new JSONObject();
        jsonOutput.put("renderedTemplate", getRenderedTemplate());
        jsonOutput.put("printOutput", getPrintOutput());
        jsonOutput.put("dumpOutput", this.dumpOutput);
        jsonOutput.put("spaceNodeRef", getSpaceNodeRef());
        jsonOutput.put("spacePath", getSpacePath());
        jsonOutput.put("result", new JSONArray());
        jsonOutput.put("scriptPerf", getScriptPerformance());
        jsonOutput.put("freemarkerPerf", getFreemarkerPerformance());
        jsonOutput.put("webscriptPerf", getWebscriptPerformance());
        jsonOutput.put("scriptOffset", getScriptPerformance());
        return jsonOutput;
    }

    public boolean isStatusResponseSent()
    {
        return statusResponseSent;
    }

    public void setStatusResponseSent(boolean statusResponseSent)
    {
        this.statusResponseSent = statusResponseSent;
    }

    public void setDumpOutput(List<Dump> dumpOutput)
    {
        this.dumpOutput = dumpOutput;
    }

}
