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
import java.util.List;

import org.alfresco.repo.cache.SimpleCache;
import org.alfresco.repo.content.MimetypeMap;
import org.alfresco.util.Pair;
import org.alfresco.util.PropertyCheck;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.extensions.webscripts.AbstractWebScript;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptException;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;

/**
 * Web script to retrieve the result of a web script execution or - in case the web script has not run to completion yet - the intermediary
 * log output.
 *
 * @author Axel Faust
 */
public class ExecutionResultGet extends AbstractWebScript implements InitializingBean
{

    private SimpleCache<Pair<String, Integer>, List<String>> printOutputCache;

    private SimpleCache<String, JavascriptConsoleResultBase> resultCache;

    /**
     *
     * {@inheritDoc}
     */
    @Override
    public void afterPropertiesSet()
    {
        PropertyCheck.mandatory(this, "printOutputCache", this.printOutputCache);
        PropertyCheck.mandatory(this, "resultCache", this.resultCache);
    }

    /**
     * @param printOutputCache
     *     the printOutputCache to set
     */
    public final void setPrintOutputCache(final SimpleCache<Pair<String, Integer>, List<String>> printOutputCache)
    {
        this.printOutputCache = printOutputCache;
    }

    /**
     * @param resultCache
     *     the resultCache to set
     */
    public final void setResultCache(final SimpleCache<String, JavascriptConsoleResultBase> resultCache)
    {
        this.resultCache = resultCache;
    }

    /**
     *
     * {@inheritDoc}
     */
    @Override
    public void execute(final WebScriptRequest request, final WebScriptResponse response) throws IOException
    {
        final String resultChannel = request.getServiceMatch().getTemplateVars().get("resultChannel");

        if (resultChannel != null && resultChannel.trim().length() > 0)
        {

            final JavascriptConsoleResultBase result = this.resultCache.get(resultChannel);
            final List<String> printOutput = new ArrayList<>();
            try
            {
                for (int chunk = 0; chunk < Integer.MAX_VALUE; chunk++)
                {
                    final Pair<String, Integer> chunkKey = new Pair<>(resultChannel, Integer.valueOf(chunk));
                    final List<String> chunkOutput = this.printOutputCache.get(chunkKey);
                    if (chunkOutput != null)
                    {
                        printOutput.addAll(chunkOutput);
                    }
                    else
                    {
                        break;
                    }
                }

            }
            finally
            {
                if (result != null)
                {
                    // check for dummy error result
                    if (result.equals(new JavascriptConsoleResultBase()))
                    {
                        response.setContentEncoding("UTF-8");
                        response.setContentType(MimetypeMap.MIMETYPE_JSON);

                        try
                        {
                            final JSONObject jsonOutput = new JSONObject();
                            jsonOutput.put("printOutput", printOutput);
                            jsonOutput.put("error", Boolean.TRUE);

                            response.getWriter().write(jsonOutput.toString());

                        }
                        catch (final JSONException e)
                        {
                            throw new WebScriptException(Status.STATUS_INTERNAL_SERVER_ERROR, "Error writing json response.", e);
                        }
                    }
                    else
                    {
                        result.writeJson(response, printOutput);
                    }

                    // clear all data
                    this.resultCache.remove(resultChannel);
                    for (int chunk = 0; chunk < Integer.MAX_VALUE; chunk++)
                    {
                        final Pair<String, Integer> chunkKey = new Pair<>(resultChannel, Integer.valueOf(chunk));
                        if (this.printOutputCache.contains(chunkKey))
                        {
                            this.printOutputCache.remove(chunkKey);
                        }
                        else
                        {
                            break;
                        }
                    }
                }
                else
                {
                    response.setContentEncoding("UTF-8");
                    response.setContentType(MimetypeMap.MIMETYPE_JSON);

                    try
                    {
                        final JSONObject jsonOutput = new JSONObject();
                        jsonOutput.put("printOutput", printOutput);

                        response.getWriter().write(jsonOutput.toString());

                    }
                    catch (final JSONException e)
                    {
                        throw new WebScriptException(Status.STATUS_INTERNAL_SERVER_ERROR, "Error writing json response.", e);
                    }
                }
            }
        }
        else
        {
            throw new WebScriptException(Status.STATUS_BAD_REQUEST, "The print output channel has not been specified");
        }
    }
}
