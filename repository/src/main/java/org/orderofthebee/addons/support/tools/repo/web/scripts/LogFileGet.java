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
package org.orderofthebee.addons.support.tools.repo.web.scripts;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.alfresco.repo.web.scripts.content.ContentStreamer;
import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;

/**
 * @author Axel Faust
 */
public class LogFileGet extends AbstractLogFileWebScript
{

    protected ContentStreamer delegate;

    /**
     * @param delegate
     *            ContentStreamer
     */
    public void setDelegate(final ContentStreamer delegate)
    {
        this.delegate = delegate;
    }

    /**
     *
     * {@inheritDoc}
     */
    @Override
    public void execute(final WebScriptRequest req, final WebScriptResponse res) throws IOException
    {
        final String filePath = this.getFilePath(req);

        final Map<String, Object> model = new HashMap<>();
        final Status status = new Status();
        final Cache cache = new Cache(this.getDescription().getRequiredCache());
        model.put("status", status);
        model.put("cache", cache);

        final String attachParam = req.getParameter("a");
        final boolean attach = attachParam != null && Boolean.parseBoolean(attachParam);

        final File file = this.validateFilePath(filePath);
        this.delegate.streamContent(req, res, file, file.lastModified(), attach, file.getName(), model);
    }

}
