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
package org.orderofthebee.addons.support.tools.share;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.SocketException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.alfresco.util.ParameterCheck;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Appender;
import org.apache.log4j.FileAppender;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggerRepository;
import org.springframework.extensions.surf.util.URLEncoder;
import org.springframework.extensions.webscripts.AbstractWebScript;
import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptException;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;

/**
 *
 * @author Axel Faust, <a href="http://acosix.de">Acosix GmbH</a>
 */
public abstract class AbstractLogFileWebScript extends AbstractWebScript
{

    private static final Logger LOGGER = Logger.getLogger(AbstractLogFileWebScript.class);

    // copied from Repository-tier contentStreamer
    private static final String HEADER_CONTENT_RANGE = "Content-Range";

    private static final String HEADER_CONTENT_LENGTH = "Content-Length";

    private static final String HEADER_ACCEPT_RANGES = "Accept-Ranges";

    private static final String HEADER_RANGE = "Range";

    private static final String HEADER_USER_AGENT = "User-Agent";

    // copied from Repository-tier HttpRangeProcessor
    private static final String HEADER_CONTENT_TYPE = "Content-Type";

    private static final String MULTIPART_BYTERANGES_BOUNDRY = "<ALF4558907921887235966L>";

    private static final String MULTIPART_BYTERANGES_BOUNDRY_SEP = "--" + MULTIPART_BYTERANGES_BOUNDRY;

    private static final int CHUNKSIZE = 64 * 1024;

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
                    final String file = ((FileAppender) appender).getFile();
                    final Path configuredFilePath = new File(file).toPath().toAbsolutePath().getParent();

                    pathAllowed = pathAllowed || (path.startsWith(configuredFilePath) && path.getFileName().toString().startsWith(file));
                }
            }
        }

        if (!pathAllowed)
        {
            throw new WebScriptException(Status.STATUS_FORBIDDEN,
                    "The log file path could not be resolved to a valid log file - access to any other file system contents is forbidden via this web script");
        }

        final File file = path.toFile();

        if (!file.exists())
        {
            throw new WebScriptException(Status.STATUS_NOT_FOUND, "The log file path could not be resolved to an existing log file");
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

        for (final Logger logger : allLoggers)
        {
            @SuppressWarnings("unchecked")
            final Enumeration<Appender> allAppenders = logger.getAllAppenders();
            while (allAppenders.hasMoreElements() && !allPathsAllowed)
            {
                final Appender appender = allAppenders.nextElement();
                if (appender instanceof FileAppender)
                {
                    final String file = ((FileAppender) appender).getFile();
                    final Path configuredFilePath = new File(file).toPath().toAbsolutePath().getParent();

                    for (final Path path : paths)
                    {
                        allPathsAllowed = allPathsAllowed && path.startsWith(configuredFilePath)
                                && path.getFileName().toString().startsWith(file);
                    }
                }
            }
        }

        if (!allPathsAllowed)
        {
            throw new WebScriptException(Status.STATUS_FORBIDDEN,
                    "A log file path could not be resolved to a valid log file - access to any other file system contents is forbidden via this web script");
        }

        final List<File> files = new ArrayList<>();
        for (final Path path : paths)
        {
            final File file = path.toFile();

            if (!file.exists())
            {
                throw new WebScriptException(Status.STATUS_NOT_FOUND, "A log file path could not be resolved to an existing log file");
            }

            files.add(file);
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

    // copied from Repo-tier ContentStreamer and base classes since they aren't available in Share
    protected void streamContent(final WebScriptRequest req, final WebScriptResponse res, final File file, final Long modifiedTime,
            final boolean attach, final String attachFileName, final Map<String, Object> model, final String mimetype) throws IOException
    {
        if (LOGGER.isDebugEnabled())
        {
            LOGGER.debug("Retrieving content from file " + file.getAbsolutePath() + " (attach: " + attach + ")");
        }

        // determine mimetype from file extension
        final long lastModified = modifiedTime == null ? file.lastModified() : modifiedTime;
        final Date lastModifiedDate = new Date(lastModified);

        this.streamContentImpl(req, res, file, attach, lastModifiedDate, String.valueOf(lastModifiedDate.getTime()), attachFileName, model,
                mimetype);
    }

    protected void streamContentImpl(final WebScriptRequest req, final WebScriptResponse res, final File file, final boolean attach,
            final Date modified, final String eTag, final String attachFileName, final Map<String, Object> model, final String mimetype)
            throws IOException
    {
        this.setAttachment(null, res, attach, attachFileName);

        res.setHeader(HEADER_ACCEPT_RANGES, "bytes");
        try
        {
            boolean processedRange = false;
            String range = req.getHeader(HEADER_CONTENT_RANGE);
            final long size = file.length();
            // the default encoding is Java's default encoding
            final String encoding = "UTF-8";

            if (range == null)
            {
                range = req.getHeader(HEADER_RANGE);
            }
            if (range != null)
            {
                if (LOGGER.isDebugEnabled())
                {
                    LOGGER.debug("Found content range header: " + range);
                }

                // ensure the range header is starts with "bytes=" and process the range(s)
                if (range.length() > 6)
                {
                    if (range.indexOf(',') != -1)
                    {
                        LOGGER.warn("Multi-range not supported");
                    }
                    else
                    {
                        processedRange = this.processRange(res, file, range.substring(6), mimetype, req.getHeader(HEADER_USER_AGENT));
                    }
                }
            }
            if (processedRange == false)
            {
                LOGGER.debug("Sending complete file content...");

                // set mimetype for the content and the character encoding for the stream
                res.setContentType(mimetype);
                res.setContentEncoding(encoding);

                // return the complete entity range
                res.setHeader(HEADER_CONTENT_RANGE, "bytes 0-" + Long.toString(size - 1L) + "/" + Long.toString(size));
                res.setHeader(HEADER_CONTENT_LENGTH, Long.toString(size));

                // set caching
                this.setResponseCache(res, modified, eTag, model);

                // get the content and stream directly to the response output stream
                // assuming the repository is capable of streaming in chunks, this should allow large files
                // to be streamed directly to the browser response stream.
                FileUtils.copyFile(file, res.getOutputStream());
            }
        }
        catch (final SocketException e1)
        {
            // the client cut the connection - our mission was accomplished apart from a little error message
            if (LOGGER.isInfoEnabled())
            {
                LOGGER.info("Client aborted stream read:\n\tfile: " + file);
            }
        }
    }

    protected void setAttachment(final WebScriptRequest req, final WebScriptResponse res, final boolean attach, final String attachFileName)
    {
        if (attach == true)
        {
            String headerValue = "attachment";
            if (attachFileName != null && attachFileName.length() > 0)
            {
                if (LOGGER.isDebugEnabled())
                {
                    LOGGER.debug("Attaching content using filename: " + attachFileName);
                }

                if (req == null)
                {
                    headerValue += "; filename*=UTF-8''" + encodeURL(attachFileName) + "; filename=\"" + attachFileName + "\"";
                }
                else
                {
                    final String userAgent = req.getHeader(HEADER_USER_AGENT);
                    final boolean isLegacy = (null != userAgent) && (userAgent.contains("MSIE 8") || userAgent.contains("MSIE 7"));
                    if (isLegacy)
                    {
                        headerValue += "; filename=\"" + encodeURL(attachFileName);
                    }
                    else
                    {
                        headerValue += "; filename=\"" + attachFileName + "\"; filename*=UTF-8''" + encodeURL(attachFileName);
                    }
                }
            }

            // set header based on filename - will force a Save As from the browse if it doesn't recognize it
            // this is better than the default response of the browser trying to display the contents
            res.setHeader("Content-Disposition", headerValue);
        }
    }

    protected void setResponseCache(final WebScriptResponse res, final Date modified, final String eTag, final Map<String, Object> model)
    {
        final Cache cache = new Cache();
        if (model == null || model.get("allowBrowserToCache") == null || ((String) model.get("allowBrowserToCache")).equals("false"))
        {
            cache.setNeverCache(false);
            cache.setMustRevalidate(true);
            cache.setMaxAge(0L);
            cache.setLastModified(modified);
            cache.setETag(eTag);
        }
        else
        {
            cache.setNeverCache(false);
            cache.setMustRevalidate(false);
            cache.setMaxAge(new Long(31536000));
            cache.setLastModified(modified);
            cache.setETag(eTag);
            res.setCache(cache);
        }
        res.setCache(cache);
    }

    // copied from Repo-tier HttpRangeProcessor
    protected boolean processRange(final WebScriptResponse res, final File file, final String range, final String mimetype,
            final String userAgent) throws IOException
    {
        // test for multiple byte ranges present in header
        return this.processSingleRange(res, file, range, mimetype);
    }

    protected boolean processSingleRange(final WebScriptResponse res, final File file, final String range, final String mimetype)
            throws IOException
    {
        // return the specific set of bytes as requested in the content-range header

        /*
         * Examples of byte-content-range-spec values, assuming that the entity contains total of 1234 bytes:
         * The first 500 bytes:
         * bytes 0-499/1234
         * The second 500 bytes:
         * bytes 500-999/1234
         * All except for the first 500 bytes:
         * bytes 500-1233/1234
         */
        /*
         * 'Range' header example:
         * bytes=10485760-20971519
         */

        boolean processedRange = false;
        Range r = null;
        try
        {
            r = Range.constructRange(range, mimetype, file.length());
        }
        catch (final IllegalArgumentException err)
        {
            if (LOGGER.isDebugEnabled())
            {
                LOGGER.debug("Failed to parse range header - returning 416 status code: " + err.getMessage());
            }

            res.setStatus(HttpServletResponse.SC_REQUESTED_RANGE_NOT_SATISFIABLE);
            res.setHeader(HEADER_CONTENT_RANGE, "\"*\"");
            res.getOutputStream().close();
            return true;
        }

        // set Partial Content status and range headers
        final String contentRange = "bytes " + Long.toString(r.start) + "-" + Long.toString(r.end) + "/" + Long.toString(file.length());

        res.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT);
        res.setContentType(mimetype);
        res.setHeader(HEADER_CONTENT_RANGE, contentRange);
        res.setHeader(HEADER_CONTENT_LENGTH, Long.toString((r.end - r.start) + 1L));

        if (LOGGER.isDebugEnabled())
        {
            LOGGER.debug("Processing: Content-Range: " + contentRange);
        }

        InputStream is = null;
        try
        {
            // output the binary data for the range
            OutputStream os = null;
            os = res.getOutputStream();
            is = new FileInputStream(file);

            this.streamRangeBytes(r, is, os, 0L);

            os.close();
            processedRange = true;
        }
        catch (final IOException err)
        {
            if (LOGGER.isDebugEnabled())
            {
                LOGGER.debug("Unable to process single range due to IO Exception: " + err.getMessage());
            }
            throw err;
        }
        finally
        {
            if (is != null)
            {
                is.close();
            }
        }

        return processedRange;
    }

    protected void streamRangeBytes(final Range r, final InputStream is, final OutputStream os, final long offset) throws IOException
    {
        if (r.start != 0L && r.start > offset)
        {
            final long skipped = offset + is.skip(r.start - offset);
            if (skipped < r.start)
            {
                // Nothing left to download!
                return;
            }
        }
        final long span = (r.end - r.start) + 1L;
        long bytesLeft = span;
        int read = 0;

        // Check that bytesLeft isn't greater than int can hold
        int bufSize;
        if (bytesLeft >= Integer.MAX_VALUE - 8)
        {
            bufSize = CHUNKSIZE;
        }
        else
        {
            bufSize = ((int) bytesLeft) < CHUNKSIZE ? (int) bytesLeft : CHUNKSIZE;
        }
        byte[] buf = new byte[bufSize];

        while ((read = is.read(buf)) > 0 && bytesLeft != 0L)
        {
            os.write(buf, 0, read);

            bytesLeft -= read;

            if (bytesLeft != 0L)
            {
                int resize;
                if (bytesLeft >= Integer.MAX_VALUE - 8)
                {
                    resize = CHUNKSIZE;
                }
                else
                {
                    resize = ((int) bytesLeft) < CHUNKSIZE ? (int) bytesLeft : CHUNKSIZE;
                }
                if (resize != buf.length)
                {
                    buf = new byte[resize];
                }
            }
            if (LOGGER.isTraceEnabled())
            {
                LOGGER.trace("...wrote " + read + " bytes, with " + bytesLeft + " to go...");
            }
        }
    }

    /**
     * Representation of a single byte range.
     */
    protected static class Range implements Comparable<Range>
    {

        private final long start;

        private final long end;

        private final long entityLength;

        private final String contentType;

        private String contentRange;

        /**
         * Constructor
         *
         * @param contentType
         *            Mimetype of the range content
         * @param start
         *            Start position in the parent entity
         * @param end
         *            End position in the parent entity
         * @param entityLength
         *            Length of the parent entity
         */
        Range(final String contentType, final long start, final long end, final long entityLength)
        {
            this.contentType = HEADER_CONTENT_TYPE + ": " + contentType;
            this.start = start;
            this.end = end;
            this.entityLength = entityLength;
        }

        /**
         * Factory method to construct a byte range from a range header value.
         *
         * @param range
         *            Range header value
         * @param contentType
         *            Mimetype of the range
         * @param entityLength
         *            Length of the parent entity
         *
         * @return Range
         *
         * @throws IllegalArgumentException
         *             for an invalid range
         */
        static Range constructRange(String range, final String contentType, final long entityLength)
        {
            if (range == null)
            {
                throw new IllegalArgumentException("Range argument is mandatory");
            }

            // strip total if present - it does not give us anything useful
            if (range.indexOf('/') != -1)
            {
                range = range.substring(0, range.indexOf('/'));
            }

            // find the separator
            final int separator = range.indexOf('-');
            if (separator == -1)
            {
                throw new IllegalArgumentException("Invalid range: " + range);
            }

            try
            {
                // split range and parse values
                long start = 0L;
                if (separator != 0)
                {
                    start = Long.parseLong(range.substring(0, separator));
                }
                long end = entityLength - 1L;
                if (separator != range.length() - 1)
                {
                    end = Long.parseLong(range.substring(separator + 1));
                }

                if (start > end)
                {
                    throw new IllegalArgumentException("Range start can not be less than range end: " + range);
                }
                // return object to represent the byte-range
                return new Range(contentType, start, end, entityLength);
            }
            catch (final NumberFormatException err)
            {
                throw new IllegalArgumentException("Unable to parse range value: " + range);
            }
        }

        /**
         * Output the header bytes for a multi-part byte range header
         */
        void outputHeader(final ServletOutputStream os) throws IOException
        {
            // output multi-part boundry separator
            os.println(MULTIPART_BYTERANGES_BOUNDRY_SEP);
            // output content type and range size sub-header for this part
            os.println(this.contentType);
            os.println(this.getContentRange());
            os.println();
        }

        /**
         * @return the length in bytes of the byte range content including the header bytes
         */
        int getLength()
        {
            // length in bytes of range plus it's header plus section marker and line feed bytes
            return MULTIPART_BYTERANGES_BOUNDRY_SEP.length() + 2 + this.contentType.length() + 2 + this.getContentRange().length() + 4
                    + (int) (this.end - this.start + 1L) + 2;
        }

        /**
         * @return the Content-Range header string value for this byte range
         */
        private String getContentRange()
        {
            if (this.contentRange == null)
            {
                this.contentRange = "Content-Range: bytes " + Long.toString(this.start) + "-" + Long.toString(this.end) + "/"
                        + Long.toString(this.entityLength);
            }
            return this.contentRange;
        }

        @Override
        public String toString()
        {
            return this.start + "-" + this.end;
        }

        /**
         * @see java.lang.Comparable#compareTo(java.lang.Object)
         */
        @Override
        public int compareTo(final Range o)
        {
            return this.start > o.start ? 1 : -1;
        }
    }

    // copied from Repo-tier WebDAVHelper
    protected final static String encodeURL(final String s)
    {
        return encodeURL(s, null);
    }

    protected final static String encodeURL(final String s, final String userAgent)
    {
        return URLEncoder.encode(s);
    }
}