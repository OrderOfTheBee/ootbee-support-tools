/**
 * Copyright (C) 2017 Axel Faust / Markus Joos
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
package org.orderofthebee.addons.support.tools.share;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.alfresco.util.ParameterCheck;
import org.alfresco.util.TempFileProvider;
import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.apache.log4j.Appender;
import org.apache.log4j.FileAppender;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggerRepository;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptException;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;

/**
 * This class is used to consolidate the log file handling logic that may need to be shared between web script and servlet based log file
 * handling components since <a href="https://issues.alfresco.com/jira/browse/ALF-21949">ALF-21949</a> breaks streaming use cases for web
 * scripts.
 *
 * @author Axel Faust, <a href="http://acosix.de">Acosix GmbH</a>
 */
public class LogFileHandler
{

    private static final Logger LOGGER = Logger.getLogger(LogFileHandler.class);

    protected ContentStreamer contentStreamer;

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
    protected static File validateFilePath(final String filePath)
    {
        ParameterCheck.mandatoryString("filePath", filePath);

        final Path path = Paths.get(filePath);
        boolean pathAllowed = false;
        final List<Logger> allLoggers = LogFileHandler.getAllLoggers();

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
    protected static List<File> validateFilePaths(final List<String> filePaths)
    {
        ParameterCheck.mandatoryCollection("filePaths", filePaths);

        final List<Path> paths = new ArrayList<>();
        for (final String filePath : filePaths)
        {
            paths.add(Paths.get(filePath));
        }

        boolean allPathsAllowed = true;
        final List<Logger> allLoggers = LogFileHandler.getAllLoggers();
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

    protected static List<Logger> getAllLoggers()
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

    protected static String determineMimetypeFromFileName(final File file)
    {
        // chances are log file is text/plain but might also be a compressed file (i.e. via logrotate)
        String mimetype = "application/octet-stream";
        final String name = file.getName();
        if (name.matches("^.+\\.(log|txt)(\\.\\d{4}-\\d{2}-\\d{2})?$"))
        {
            mimetype = "text/plain";
        }
        else if (name.matches("^.+\\.zip$"))
        {
            mimetype = "application/zip";
        }
        else if (name.matches("^.+\\.gz$"))
        {
            mimetype = "application/gzip";
        }
        else if (name.matches("^.+\\.bz$"))
        {
            mimetype = "application/bzip";
        }
        else if (name.matches("^.+\\.bz2$"))
        {
            mimetype = "application/bzip2";
        }
        else if (name.matches("^.+\\.tar$"))
        {
            mimetype = "application/tar";
        }
        return mimetype;
    }

    /**
     * @param contentStreamer
     *            the contentStreamer to set
     */
    public void setContentStreamer(final ContentStreamer contentStreamer)
    {
        this.contentStreamer = contentStreamer;
    }

    protected void handleLogFileRequest(final String filePath, final boolean attach, final WebScriptRequest req,
            final WebScriptResponse res, final Map<String, Object> model) throws IOException
    {
        final File file = validateFilePath(filePath);

        final String mimetype = determineMimetypeFromFileName(file);
        this.contentStreamer.streamContent(req, res, file, file.lastModified(), attach, file.getName(), model, mimetype);
    }

    protected void handleLogFileRequest(final String filePath, final boolean attach, final HttpServletRequest req,
            final HttpServletResponse res, final Map<String, Object> model) throws IOException
    {
        try
        {
            final File file = validateFilePath(filePath);

            final String mimetype = determineMimetypeFromFileName(file);
            this.contentStreamer.streamContent(req, res, file, file.lastModified(), attach, file.getName(), model, mimetype);
        }
        catch (final WebScriptException wsex)
        {
            if (!res.isCommitted())
            {
                res.reset();
                res.sendError(wsex.getStatus(), wsex.getMessage());
            }
            else
            {
                LOGGER.info("Could not send error via committed response", wsex);
            }
        }
    }

    protected void handleLogZipRequest(final List<String> filePaths, final WebScriptRequest req, final WebScriptResponse res,
            final Map<String, Object> model) throws IOException
    {
        final List<File> files = LogFileHandler.validateFilePaths(filePaths);
        final File logFileZip = TempFileProvider.createTempFile("ootbee-support-tools-logFiles", "zip");
        try
        {
            this.createZip(files, logFileZip);

            this.contentStreamer.streamContent(req, res, logFileZip, logFileZip.lastModified(), false, "log-files.zip", model,
                    "application/zip");
        }
        finally
        {
            // eager cleanup
            if (!logFileZip.delete())
            {
                logFileZip.deleteOnExit();
            }
        }
    }

    protected void handleLogZipRequest(final List<String> filePaths, final HttpServletRequest req, final HttpServletResponse res,
            final Map<String, Object> model) throws IOException
    {
        try
        {
            final List<File> files = LogFileHandler.validateFilePaths(filePaths);
            final File logFileZip = TempFileProvider.createTempFile("ootbee-support-tools-logFiles", "zip");
            try
            {
                this.createZip(files, logFileZip);

                this.contentStreamer.streamContent(req, res, logFileZip, logFileZip.lastModified(), false, "log-files.zip", model,
                        "application/zip");
            }
            finally
            {
                // eager cleanup
                if (!logFileZip.delete())
                {
                    logFileZip.deleteOnExit();
                }
            }
        }
        catch (final WebScriptException wsex)
        {
            if (!res.isCommitted())
            {
                res.reset();
                res.sendError(wsex.getStatus(), wsex.getMessage());
            }
            else
            {
                LOGGER.info("Could not send error via committed response", wsex);
            }
        }
    }

    protected void createZip(final List<File> files, final File logFileZip)
    {
        try
        {
            final ZipArchiveOutputStream zipOS = new ZipArchiveOutputStream(logFileZip);
            try
            {
                for (final File logFile : files)
                {
                    final ArchiveEntry archiveEntry = zipOS.createArchiveEntry(logFile, logFile.getName());
                    zipOS.putArchiveEntry(archiveEntry);

                    final FileInputStream fis = new FileInputStream(logFile);
                    try
                    {
                        final byte[] buf = new byte[10240];
                        while (fis.read(buf) != -1)
                        {
                            zipOS.write(buf);
                        }
                    }
                    finally
                    {
                        fis.close();
                    }

                    zipOS.closeArchiveEntry();
                }
            }
            finally
            {
                zipOS.close();
            }
        }
        catch (final IOException ioEx)
        {
            throw new WebScriptException(Status.STATUS_INTERNAL_SERVER_ERROR, "Error creating ZIP file", ioEx);
        }
    }
}
