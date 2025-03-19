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
 */
package org.orderofthebee.addons.support.tools.share;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptException;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;
import org.springframework.extensions.webscripts.servlet.FormData;

/**
 * @author Axel Faust
 */
public class LogFilesZIPPost extends AbstractLogFileWebScript
{

    /**
     *
     * {@inheritDoc}
     */
    @Override
    public void execute(final WebScriptRequest req, final WebScriptResponse res) throws IOException
    {
        final Map<String, Object> model = new HashMap<>();
        final Status status = new Status();
        final Cache cache = new Cache(this.getDescription().getRequiredCache());
        model.put("status", status);
        model.put("cache", cache);

        final Object parsedContent = req.parseContent();
        if (!(parsedContent instanceof FormData))
        {
            throw new WebScriptException(Status.STATUS_BAD_REQUEST, "No or invalid request data provided - only form data is supported");
        }

        final FormData rqData = (FormData) parsedContent;
        final List<String> filePaths = new ArrayList<>();
        final String[] paths = rqData.getParameters().get("paths");
        filePaths.addAll(Arrays.asList(paths));

        final WebScriptRequestWrapper reqW = new WebScriptRequestWrapper(req);
        final WebScriptResponseWrapper resW = new WebScriptResponseWrapper(res);
        this.logFileHandler.handleLogZipRequest(filePaths, () -> reqW, () -> resW, model);
    }

}
