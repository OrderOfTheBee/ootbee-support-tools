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

import org.alfresco.repo.web.scripts.content.ContentStreamer;
import org.apache.log4j.Appender;
import org.apache.log4j.Logger;
import org.orderofthebee.addons.support.tools.repo.TemporaryFileAppender;
import org.springframework.extensions.webscripts.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * Given the path to a temporary log file created with the log4j-snapshot-create webscript, we
 * attempt to locate an associated {@link TemporaryFileAppender}. If we find it, it is
 * deregistered from all loggers it was added to and then closed. If the file at the provided
 * path exists and is a valid log snapshot file, we stream the contents out otherwise we
 * throw a WebScriptException.
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
    * <p>Gets the appender, removes it from all the loggers and streams the content back.</p>
    * 
    * {@inheritDoc}
    */
    @Override
    public void execute(WebScriptRequest req, WebScriptResponse res) throws IOException
    {
        final String servicePath = req.getServicePath();
        final String matchPath = req.getServiceMatch().getPath();
        final String fileAppenderPathStr = servicePath.substring(servicePath.indexOf(matchPath) + matchPath.length());
        final Path fileAppenderPath = Paths.get(fileAppenderPathStr);

        final Map<String, Object> model = new HashMap<String, Object>();
        final Status status = new Status();
        final Cache cache = new Cache(this.getDescription().getRequiredCache());
        model.put("status", status);
        model.put("cache", cache);

        final String attachParam = req.getParameter("a");
        final boolean attach = attachParam != null && Boolean.parseBoolean(attachParam);

        Enumeration appenders = Logger.getRootLogger().getAllAppenders();
        TemporaryFileAppender snapshotAppender = null;

        // Find the TemporaryFileAppender that writes to this file
        while (appenders.hasMoreElements())
        {
            Appender appender = (Appender) appenders.nextElement();
            if (appender instanceof TemporaryFileAppender)
            {
                TemporaryFileAppender fileAppender = (TemporaryFileAppender) appender;
                if (fileAppender.getFile().equals(fileAppenderPathStr))
                {
                    snapshotAppender = fileAppender;
                }
            }
        }


        // If found, remove the appender from all loggers and close it
        if (snapshotAppender != null)
        {
            snapshotAppender.removeAppenderFromLoggers();
            snapshotAppender.close();
        }

        // In any event, if the file is valid we can stream it out (if not, a WebScriptException will be thrown.)
        final String fileName = fileAppenderPath.getFileName().toString();
        if (!fileName.matches("^ootbee-support-tools-snapshot.*\\.log$"))
        {
            throw new WebScriptException(Status.STATUS_BAD_REQUEST,
                    "The path " + fileAppenderPathStr + " is invalid for a snapshot log file.");
        }
        final File file = fileAppenderPath.toFile();
        if (!file.exists())
        {
            throw new WebScriptException(Status.STATUS_NOT_FOUND,
                    "There is no file at " + fileAppenderPathStr + ".");
        }
        // TODO(bwavell): Consider if we should create a temp file and validate the paths are the same.

        this.delegate.streamContent(req, res, file, file.lastModified(), attach, file.getName(), model);
    }
}
