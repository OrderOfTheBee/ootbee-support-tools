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
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.alfresco.repo.web.scripts.content.ContentStreamer;
import org.orderofthebee.addons.support.tools.repo.log.Log4jCompatibilityUtils;
import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptException;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;

/**
 * This web script uses the UUID of a temporary file appender created with the log4j-snapshot-create webscript to locate said
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

        final Optional<Path> logFilePathOpt = Log4jCompatibilityUtils.LOG4J_HELPER.closeSnapshotAppender(snapshotUUID);
        if (!logFilePathOpt.isPresent())
        {
            throw new WebScriptException(Status.STATUS_NOT_FOUND, "No snapshot Log4J appender found for UUID " + snapshotUUID);
        }
        final Path logFilePath = logFilePathOpt.get();
        if (!Files.exists(logFilePath) || !Files.isRegularFile(logFilePath))
        {
            throw new WebScriptException(Status.STATUS_NOT_FOUND, "Log file missing for snapshot Log4J appender with UUID " + snapshotUUID);
        }

        // stream the contents - log file will be automatically deleted by Alfresco's temporary file mechanisms
        final File file = logFilePath.toFile();
        this.delegate.streamContent(req, res, file, file.lastModified(), attach, file.getName(), model);
    }
}
