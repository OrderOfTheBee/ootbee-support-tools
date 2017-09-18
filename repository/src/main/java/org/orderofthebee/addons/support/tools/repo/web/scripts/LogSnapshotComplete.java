/**
 * Copyright (C) 2017 Ana Gouveia / Bindu Wavell
 * Copyright (C) 2017 Order of the Bee
 *
 * This file is part of Community Support Tools
 *
 * Community Support Tools is free software: you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 *
 * Community Support Tools is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser
 * General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Community Support Tools. If not, see
 * <http://www.gnu.org/licenses/>.
 */
package org.orderofthebee.addons.support.tools.repo.web.scripts;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import org.alfresco.repo.web.scripts.content.ContentStreamer;
import org.alfresco.util.EqualsHelper;
import org.apache.log4j.Appender;
import org.apache.log4j.Logger;
import org.orderofthebee.addons.support.tools.repo.TemporaryFileAppender;
import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptException;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;

/**
 * This web script uses the UUID of a {@link TemporaryFileAppender} created with the log4j-snapshot-create webscript to locate said
 * instance, deregisters it from all loggers it was registered with, and closes it. The contents of the associated log file will be streamed
 * as the response to the caller. If the UUID does not match any of the appenders, this web script will fail accordingly with a
 * {@link WebScriptException}.
 *
 * @author Ana Gouveia
 * @author Bindu Wavell <a href="mailto:bindu@ziaconsulting.com">bindu@ziaconsulting.com</a>
 */
public class LogSnapshotComplete extends AbstractLogFileWebScript
{

    protected ContentStreamer delegate;

    public void setDelegate(final ContentStreamer delegate)
    {
        this.delegate = delegate;
    }

    /**
     * <p>
     * Gets the appender, removes it from all the loggers and streams the content back.
     * </p>
     *
     * {@inheritDoc}
     */
    @Override
    public void execute(final WebScriptRequest req, final WebScriptResponse res) throws IOException
    {
        final Map<String, String> templateVars = req.getServiceMatch().getTemplateVars();
        final String snapshotUUID = templateVars.get("snapshotUUID");

        if (snapshotUUID == null || snapshotUUID.trim().isEmpty())
        {
            throw new WebScriptException(Status.STATUS_BAD_REQUEST, "UUID for log snapshot missing in request");
        }

        final Map<String, Object> model = new HashMap<>();
        final Status status = new Status();
        final Cache cache = new Cache(this.getDescription().getRequiredCache());
        model.put("status", status);
        model.put("cache", cache);

        final String attachParam = req.getParameter("a");
        final boolean attach = attachParam != null && Boolean.parseBoolean(attachParam);

        @SuppressWarnings("unchecked") // Log4J API is old and unaware of generics
        final Enumeration<Appender> appenders = Logger.getRootLogger().getAllAppenders();
        TemporaryFileAppender snapshotAppender = null;

        // Find the TemporaryFileAppender that writes to this file
        while (appenders.hasMoreElements())
        {
            final Appender appender = appenders.nextElement();
            if (appender instanceof TemporaryFileAppender)
            {
                final TemporaryFileAppender fileAppender = (TemporaryFileAppender) appender;
                if (EqualsHelper.nullSafeEquals(snapshotUUID, fileAppender.getAppenderUUID()))
                {
                    snapshotAppender = fileAppender;
                    break;
                }
            }
        }

        if (snapshotAppender == null)
        {
            throw new WebScriptException(Status.STATUS_NOT_FOUND, "No snapshot Log4J appender found for UUID " + snapshotUUID);
        }

        final File file = Paths.get(snapshotAppender.getFile()).toFile();
        if (!file.exists())
        {
            throw new WebScriptException(Status.STATUS_NOT_FOUND, "Log file missing for snapshot Log4J appender with UUID " + snapshotUUID);
        }

        // Remove the appender from all loggers and close it
        snapshotAppender.removeAppenderFromLoggers();
        snapshotAppender.close();

        // stream the contents - log file will be automatically deleted by Alfresco's temporary file mechanisms
        this.delegate.streamContent(req, res, file, file.lastModified(), attach, file.getName(), model);
    }
}
