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
public class JavascriptConsoleRequest
{

    private static final int DEFAULT_DUMP_LIMIT = 10;

    public final String script;

    public final String template;

    public final String spaceNodeRef;

    public final String runas;

    public final boolean useTransaction;

    public final boolean transactionReadOnly;

    public final Map<String, String> urlargs;

    public final String documentNodeRef;

    public final Integer dumpLimit;

    public final String resultChannel;

    private JavascriptConsoleRequest(String script, String template, String spaceNodeRef, String transaction, String runas, String urlargs,
            String documentNodeRef, Integer dumpLimit, String resultChannel)
    {
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
     * Parses the query string of a JavaScript Console request
     * 
     * @param queryString
     *     the query string to parse
     * @return the map of parsed URL parameters
     */
    protected static Map<String, String> parseQueryString(String queryString)
    {
        Map<String, String> map = new HashMap<String, String>();

        String[] parameters = queryString.split("&");
        for (int i = 0; i < parameters.length; i++)
        {
            String[] keyAndValue = parameters[i].split("=");
            if (keyAndValue.length != 2)
            {
                // "invalid url parameter " + parameters[i]);
                continue;
            }
            String key = keyAndValue[0];
            String value = keyAndValue[1];
            map.put(key, value);
        }

        return map;
    }

    public static JavascriptConsoleRequest readJson(WebScriptRequest request)
    {
        Content content = request.getContent();

        InputStreamReader br = new InputStreamReader(content.getInputStream(), Charset.forName("UTF-8"));
        JSONTokener jsonTokener = new JSONTokener(br);
        try
        {
            JSONObject jsonInput = new JSONObject(jsonTokener);

            String script = jsonInput.getString("script");
            String template = jsonInput.getString("template");
            String spaceNodeRef = jsonInput.getString("spaceNodeRef");
            String transaction = jsonInput.getString("transaction");
            String urlargs = jsonInput.getString("urlargs");
            String documentNodeRef = jsonInput.getString("documentNodeRef");
            int dumpLimit = DEFAULT_DUMP_LIMIT;
            if (jsonInput.has("dumpLimit"))
            {
                dumpLimit = jsonInput.getInt("dumpLimit");
            }
            String resultChannel = jsonInput.has("resultChannel") ? jsonInput.getString("resultChannel") : null;

            String runas = jsonInput.getString("runas");
            if (runas == null)
            {
                runas = "";
            }

            return new JavascriptConsoleRequest(script, template, spaceNodeRef, transaction, runas, urlargs, documentNodeRef, dumpLimit,
                    resultChannel);

        }
        catch (JSONException e)
        {
            throw new WebScriptException(Status.STATUS_INTERNAL_SERVER_ERROR, "Error reading json request body.", e);
        }
    }

    /**
     */
    @Override
    public String toString()
    {
        return "JavascriptConsoleRequest [script=" + script + ", template=" + template + ", spaceNodeRef=" + spaceNodeRef + ", runas="
                + runas + ", useTransaction=" + useTransaction + ", transactionReadOnly=" + transactionReadOnly + ", urlargs=" + urlargs
                + ", documentNodeRef=" + documentNodeRef + ", dumpLimit=" + dumpLimit + ", resultChannel=" + resultChannel + "]";
    }

}
