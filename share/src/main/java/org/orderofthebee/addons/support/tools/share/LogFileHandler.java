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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

import org.alfresco.util.ParameterCheck;
import org.alfresco.util.TempFileProvider;
import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.orderofthebee.addons.support.tools.share.log.Log4jCompatibilityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptException;
import org.springframework.extensions.webscripts.WebScriptRequest;

/**
 * This class is used to consolidate the log file handling logic that may need to be shared between web script and servlet based log file
 * handling components since <a href="https://issues.alfresco.com/jira/browse/ALF-21949">ALF-21949</a> breaks streaming use cases for web
 * scripts.
 *
 * @author Axel Faust
 */
public class LogFileHandler
{

    private static final Logger LOGGER = LoggerFactory.getLogger(LogFileHandler.class);

    private static final String LOG_FILE_PATH_MSG_PREFIX = "The log file path ";

    private static final String LOG_FILE_RESOLUTION_ERROR_MSG_SUFFIX = " could not be resolved to a valid log file - access to any other file system contents is forbidden via this web script";

    private static final String LOG_FILE_EXIST_ERROR_MSG_SUFFIX = " could not be resolved to an existing log file";

    protected ContentStreamer contentStreamer;

    /**
     * Validates a single log file path and resolves it to a file handle.
     *
     * @param filePath
     *     the file path to validate
     * @return the resolved file handle if the file path is valid and allowed to be accessed
     *
     * @throws WebScriptException
     *     if access to the log file is prohibited
     */
    protected static File validateFilePath(final String filePath)
    {
        ParameterCheck.mandatoryString("filePath", filePath);

        final AtomicReference<File> file = new AtomicReference<>();
        Log4jCompatibilityUtils.LOG4J_HELPER.validateFilePath(filePath, s -> {
            throw new WebScriptException(Status.STATUS_FORBIDDEN, LOG_FILE_PATH_MSG_PREFIX + s + LOG_FILE_RESOLUTION_ERROR_MSG_SUFFIX);
        }, p -> {
            final File f = p.toFile();
            if (!f.exists())
            {
                throw new WebScriptException(Status.STATUS_NOT_FOUND, LOG_FILE_PATH_MSG_PREFIX + p + LOG_FILE_EXIST_ERROR_MSG_SUFFIX);
            }
            file.set(f);
        });

        return file.get();
    }

    /**
     * Validates a log file paths and resolves them to file handles.
     *
     * @param filePaths
     *     the file paths to validate
     * @return the resolved file handles if the file paths are valid and allowed to be accessed
     *
     * @throws WebScriptException
     *     if access to any log file is prohibited
     */
    protected static List<File> validateFilePaths(final List<String> filePaths)
    {
        ParameterCheck.mandatoryCollection("filePaths", filePaths);

        final List<File> files = new ArrayList<>();
        Log4jCompatibilityUtils.LOG4J_HELPER.validateFilePath(filePaths, s -> {
            throw new WebScriptException(Status.STATUS_FORBIDDEN, LOG_FILE_PATH_MSG_PREFIX + s + LOG_FILE_RESOLUTION_ERROR_MSG_SUFFIX);
        }, p -> {
            final File f = p.toFile();
            if (!f.exists())
            {
                throw new WebScriptException(Status.STATUS_NOT_FOUND, LOG_FILE_PATH_MSG_PREFIX + p + LOG_FILE_EXIST_ERROR_MSG_SUFFIX);
            }
            files.add(f);
        });

        return files;
    }

    protected static String getFilePath(final WebScriptRequest req)
    {
        final String servicePath = req.getServicePath();
        final String matchPath = req.getServiceMatch().getPath();
        String filePath = servicePath.substring(servicePath.indexOf(matchPath) + matchPath.length());

        if (!filePath.startsWith("/"))
        {
            filePath = "/" + filePath;
        }

        return filePath;
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
     *     the contentStreamer to set
     */
    public void setContentStreamer(final ContentStreamer contentStreamer)
    {
        this.contentStreamer = contentStreamer;
    }

    protected void handleLogFileRequest(final String filePath, final boolean attach, final Supplier<Request> req,
            final Supplier<Response> res, final Map<String, Object> model) throws IOException
    {
        try
        {
            final File file = validateFilePath(filePath);

            final String mimetype = determineMimetypeFromFileName(file);
            this.contentStreamer.streamContent(req, res, file, file.lastModified(), attach, file.getName(), model, mimetype);
        }
        catch (final WebScriptException wsex)
        {
            Response rres = res.get();
            if (!rres.isCommitted())
            {
                rres.reset();
                rres.sendError(wsex.getStatus(), wsex.getMessage());
            }
            else if (!(rres instanceof WebScriptResponseWrapper))
            {
                LOGGER.info("Could not send error via committed response", wsex);
            }
            else
            {
                throw wsex;
            }
        }
    }

    protected void handleLogZipRequest(final List<String> filePaths, final Supplier<Request> req, final Supplier<Response> res,
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
                if (!Files.deleteIfExists(logFileZip.toPath()))
                {
                    logFileZip.deleteOnExit();
                }
            }
        }
        catch (final WebScriptException wsex)
        {
            Response rres = res.get();
            if (!rres.isCommitted())
            {
                rres.reset();
                rres.sendError(wsex.getStatus(), wsex.getMessage());
            }
            else if (!(rres instanceof WebScriptResponseWrapper))
            {
                LOGGER.info("Could not send error via committed response", wsex);
            }
            else
            {
                throw wsex;
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
