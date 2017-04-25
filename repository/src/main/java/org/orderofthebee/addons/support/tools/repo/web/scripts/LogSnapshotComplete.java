/**
 * Copyright (C) 2016 Order of the Bee
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
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import org.alfresco.repo.web.scripts.content.ContentStreamer;
import org.apache.log4j.Appender;
import org.apache.log4j.FileAppender;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;

/**
 * @author Ana Gouveia
 */
public class LogSnapshotComplete extends AbstractLogFileWebScript
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
    * Gets the appender, removes it from all the loggers and streams the content back.
    * 
    * {@inheritDoc}
    */
    @Override
    @SuppressWarnings("rawtypes")
    public void execute(WebScriptRequest req, WebScriptResponse res) throws IOException
    {
        final String servicePath = req.getServicePath();
        final String matchPath = req.getServiceMatch().getPath();
        final String fileAppenderPath = servicePath.substring(servicePath.indexOf(matchPath) + matchPath.length());

        final Map<String, Object> model = new HashMap<String, Object>();
        final Status status = new Status();
        final Cache cache = new Cache(this.getDescription().getRequiredCache());
        model.put("status", status);
        model.put("cache", cache);

        final String attachParam = req.getParameter("a");
        final boolean attach = attachParam != null && Boolean.parseBoolean(attachParam);

        Enumeration appenders = Logger.getRootLogger().getAllAppenders();
        FileAppender snapshotAppender = null;

        while (appenders.hasMoreElements())
        {
            Appender appender = (Appender) appenders.nextElement();
            if (appender instanceof FileAppender)
            {
                FileAppender fileAppender = (FileAppender) appender;
                if (fileAppender.getFile().equals(fileAppenderPath))
                {
                    snapshotAppender = fileAppender;
                }
            }
        }

        if (snapshotAppender != null)
        {
            final File file = this.validateFilePath(snapshotAppender.getFile());
            this.delegate.streamContent(req, res, file, file.lastModified(), attach, file.getName(), model);
            
            Enumeration loggers = LogManager.getCurrentLoggers();
            while (loggers.hasMoreElements())
            {
                Logger logger = (Logger) loggers.nextElement();
                Enumeration logAppenders = logger.getAllAppenders();
                while (logAppenders.hasMoreElements())
                {
                    Appender appender = (Appender) logAppenders.nextElement();
                    if (appender instanceof FileAppender && ((FileAppender) appender).getFile().equals(fileAppenderPath))
                    {
                        logger.removeAppender(appender);
                    }
                }
            }
            Logger.getRootLogger().removeAppender(snapshotAppender);
        }
    }
}
