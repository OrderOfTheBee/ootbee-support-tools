/**
 * Copyright (C) 2016 Axel Faust / Markus Joos
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
package org.orderofthebee.addons.support.tools.repo;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.alfresco.repo.web.scripts.content.ContentStreamer;
import org.apache.log4j.Appender;
import org.apache.log4j.FileAppender;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggerRepository;
import org.springframework.extensions.webscripts.AbstractWebScript;
import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptException;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;

/**
 * @author Axel Faust, <a href="http://acosix.de">Acosix GmbH</a>
 */
public class LogFileGet extends AbstractWebScript
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
        final String logFile = req.getParameter("logFile");
        final String filePath = req.getParameter("path");

        final Path path = Paths.get(filePath, logFile);

        boolean pathAllowed = false;
        final List<Logger> allLoggers = this.getAllLoggers();

        for (final Logger logger : allLoggers)
        {
            @SuppressWarnings("unchecked")
            final Enumeration<Appender> allAppenders = logger.getAllAppenders();
            while (allAppenders.hasMoreElements() && !pathAllowed)
            {
                final Appender appender = allAppenders.nextElement();
                if (appender instanceof FileAppender)
                {
                    final String file = ((FileAppender) appender).getFile();
                    final Path configuredFilePath = new File(file).toPath().toAbsolutePath().getParent();

                    pathAllowed = pathAllowed || (path.startsWith(configuredFilePath) && path.getFileName().toString().startsWith(file));
                }
            }
        }

        if (!pathAllowed)
        {
            throw new WebScriptException(Status.STATUS_FORBIDDEN,
                    "The log file and path could not be resolved to a valid log file - access to any other file system contents is forbidden via this web script");
        }

        final Map<String, Object> model = new HashMap<>();
        final Status status = new Status();
        final Cache cache = new Cache(this.getDescription().getRequiredCache());
        model.put("status", status);
        model.put("cache", cache);

        final File file = path.toFile();
        this.delegate.streamContent(req, res, file, file.lastModified(), true, file.getName(), model);
    }

    protected List<Logger> getAllLoggers()
    {
        final LoggerRepository loggerRepository = LogManager.getLoggerRepository();
        final List<Logger> loggers = new ArrayList<>();

        loggers.add(loggerRepository.getRootLogger());
        @SuppressWarnings("unchecked")
        final Enumeration<Logger> currentLoggers = loggerRepository.getCurrentLoggers();
        while (currentLoggers.hasMoreElements())
        {
            loggers.add(currentLoggers.nextElement());
        }
        return loggers;
    }

}
