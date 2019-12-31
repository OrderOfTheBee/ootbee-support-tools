/**
 * Copyright (C) 2016 - 2020 Order of the Bee
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
 * Copyright (C) 2005 - 2020 Alfresco Software Limited.
 */
package org.orderofthebee.addons.support.tools.repo.web.scripts;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import org.alfresco.util.ParameterCheck;
import org.apache.log4j.Appender;
import org.apache.log4j.FileAppender;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggerRepository;
import org.springframework.extensions.webscripts.AbstractWebScript;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptException;
import org.springframework.extensions.webscripts.WebScriptRequest;

/**
 *
 * @author Axel Faust, <a href="http://acosix.de">Acosix GmbH</a>
 */
public abstract class AbstractLogFileWebScript extends AbstractWebScript
{

    /**
     * Validates a single log file path and resolves it to a file handle.
     *
     * @param filePath
     *            the file path to validate
     * @return the resolved file handle if the file path is valid and allowed to be accessed
     *
     * @throws WebScriptException
     *             if access to the log file is prohibited
     */
    protected File validateFilePath(final String filePath)
    {
        ParameterCheck.mandatoryString("filePath", filePath);

        final Path path = Paths.get(filePath);
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
                    final String appenderFile = ((FileAppender) appender).getFile();
                    final File configuredFile = new File(appenderFile);
                    final Path configuredFilePath = configuredFile.toPath().toAbsolutePath().getParent();
                    pathAllowed = pathAllowed
                            || (path.startsWith(configuredFilePath) && path.getFileName().toString().startsWith(configuredFile.getName()));
                }
            }
        }

        if (!pathAllowed)
        {
            throw new WebScriptException(Status.STATUS_FORBIDDEN, "The log file path " + filePath
                    + " could not be resolved to a valid log file - access to any other file system contents is forbidden via this web script");
        }

        final File file = path.toFile();

        if (!file.exists())
        {
            throw new WebScriptException(Status.STATUS_NOT_FOUND,
                    "The log file path " + filePath + " could not be resolved to an existing log file");
        }

        return file;
    }

    /**
     * Validates a log file paths and resolves them to file handles.
     *
     * @param filePaths
     *            the file paths to validate
     * @return the resolved file handles if the file paths are valid and allowed to be accessed
     *
     * @throws WebScriptException
     *             if access to any log file is prohibited
     */
    protected List<File> validateFilePaths(final List<String> filePaths)
    {
        ParameterCheck.mandatoryCollection("filePaths", filePaths);

        final List<Path> paths = new ArrayList<>();
        for (final String filePath : filePaths)
        {
            paths.add(Paths.get(filePath));
        }

        boolean allPathsAllowed = true;
        final List<Logger> allLoggers = this.getAllLoggers();
        final List<File> files = new ArrayList<>();

        for (final Logger logger : allLoggers)
        {
            @SuppressWarnings("unchecked")
            final Enumeration<Appender> allAppenders = logger.getAllAppenders();
            while (allAppenders.hasMoreElements() && allPathsAllowed)
            {
                final Appender appender = allAppenders.nextElement();
                if (appender instanceof FileAppender)
                {
                    final String appenderFile = ((FileAppender) appender).getFile();
                    final File configuredFile = new File(appenderFile);
                    final Path configuredFilePath = configuredFile.toPath().toAbsolutePath().getParent();

                    for (final Path path : paths)
                    {
                        allPathsAllowed = allPathsAllowed && path.startsWith(configuredFilePath)
                                && path.getFileName().toString().startsWith(configuredFile.getName());

                        if (!allPathsAllowed)
                        {
                            throw new WebScriptException(Status.STATUS_FORBIDDEN, "The log file path " + path
                                    + " could not be resolved to a valid log file - access to any other file system contents is forbidden via this web script");
                        }

                        final File file = path.toFile();
                        if (!file.exists())
                        {
                            throw new WebScriptException(Status.STATUS_NOT_FOUND,
                                    "The log file path " + path + " could not be resolved to an existing log file");
                        }

                        files.add(file);
                    }
                }
            }
        }

        return files;
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

    protected String getFilePath(final WebScriptRequest req) {

        final String servicePath = req.getServicePath();
        final String matchPath = req.getServiceMatch().getPath();
        String filePath = servicePath.substring(servicePath.indexOf(matchPath) + matchPath.length());

        if(!filePath.startsWith("/")) {
            filePath = "/" + filePath;
        }

        return filePath;

    }

}