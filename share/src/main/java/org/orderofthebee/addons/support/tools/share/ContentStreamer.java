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
import java.io.InputStream;
import java.io.OutputStream;
import java.net.SocketException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.TimeZone;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.springframework.extensions.surf.util.URLEncoder;
import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;

/**
 * @author Axel Faust, <a href="http://acosix.de">Acosix GmbH</a>
 */
public class ContentStreamer
{

    private static final Logger LOGGER = Logger.getLogger(ContentStreamer.class);

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

    // copied from WebScriptServletResponseImpl
    protected static final String NO_CACHE = "no-cache";

    protected static ThreadLocal<SimpleDateFormat> s_dateFormat = new ThreadLocal<>();

    // Request/Response abstractions to simplify our code
    protected static interface Request
    {

        /**
         * Returns the value of the specified request header as a <code>String</code>. If the request did not include a header of the
         * specified name, this method returns <code>null</code>. If there are multiple headers with the same name, this method returns the
         * first head in the request. The header name is case insensitive. You can use this method with any request header.
         *
         * @param name
         *            a <code>String</code> specifying the
         *            header name
         *
         * @return a <code>String</code> containing the
         *         value of the requested
         *         header, or <code>null</code>
         *         if the request does not
         *         have a header of that name
         *
         * @see HttpServletRequest#getHeader(String)
         * @see WebScriptRequest#getHeader(String)
         *
         */
        String getHeader(String name);
    }

    protected static interface Response
    {

        /**
         * <p>
         * Sets the status code for this response. This method is used to set the return status code when there is no error (for example,
         * for the status codes SC_OK or SC_MOVED_TEMPORARILY). If there is an error, and the caller wishes to invoke an error-page defined
         * in the web application, the <code>sendError</code> method should be used instead.
         * </p>
         *
         * <p>
         * The container clears the buffer and sets the Location header, preserving cookies and other headers.
         * </p>
         *
         * @param status
         *            the status code
         *
         * @see HttpServletResponse#setStatus(int)
         * @see WebScriptResponse#setStatus(int)
         */
        void setStatus(int status);

        /**
         * Sets a response header with the given name and value.If the header had already been set, the new value overwrites the previous
         * one. The <code>containsHeader</code> method can be used to test for the presence of a header before setting its value.
         *
         * @param name
         *            the name of the header
         * @param value
         *            the header value If it contains octet string,
         *            it should be encoded according to RFC 2047
         *            (http://www.ietf.org/rfc/rfc2047.txt)
         *
         * @see HttpServletResponse#setHeader(String, String)
         * @see WebScriptResponse#setHeader(String, String)
         *
         */
        void setHeader(String name, String value);

        /**
         * Sets the Content Type
         *
         * @param contentType
         *            String
         *
         * @see HttpServletResponse#setContentType(String)
         * @see WebScriptResponse#setContentType(String)
         */
        void setContentType(String contentType);

        /**
         * Sets the Content Encoding
         *
         * @param contentEncoding
         *            String
         *
         * @see WebScriptResponse#setContentEncoding(String)
         */
        void setContentEncoding(String contentEncoding);

        /**
         * Sets the Cache control
         *
         * @param cache
         *            cache control
         *
         * @see WebScriptResponse#setCache(Cache)
         */
        void setCache(Cache cache);

        /**
         * Returns a {@link OutputStream} suitable for writing binary data in the response. The servlet container does not encode the binary
         * data.
         *
         * @return a {@link OutputStream} for writing binary data
         *
         * @exception IOException
         *                if an input or output exception occurred
         *
         * @see HttpServletResponse#getOutputStream()
         * @see WebScriptResponse#getOutputStream()
         */
        OutputStream getOutputStream() throws IOException;
    }

    protected static class WebScriptRequestWrapper implements Request
    {

        protected final WebScriptRequest req;

        protected WebScriptRequestWrapper(final WebScriptRequest req)
        {
            this.req = req;
        }

        /**
         *
         * {@inheritDoc}
         */
        @Override
        public String getHeader(final String name)
        {
            return this.req.getHeader(name);
        }
    }

    protected static class HttpServletRequestWrapper implements Request
    {

        protected final HttpServletRequest req;

        protected HttpServletRequestWrapper(final HttpServletRequest req)
        {
            this.req = req;
        }

        /**
         *
         * {@inheritDoc}
         */
        @Override
        public String getHeader(final String name)
        {
            return this.req.getHeader(name);
        }
    }

    protected static class WebScriptResponseWrapper implements Response
    {

        protected final WebScriptResponse res;

        protected WebScriptResponseWrapper(final WebScriptResponse res)
        {
            this.res = res;
        }

        /**
         *
         * {@inheritDoc}
         */
        @Override
        public void setStatus(final int status)
        {
            this.res.setStatus(status);
        }

        /**
         *
         * {@inheritDoc}
         */
        @Override
        public void setHeader(final String name, final String value)
        {
            this.res.setHeader(name, value);
        }

        /**
         *
         * {@inheritDoc}
         */
        @Override
        public void setContentType(final String contentType)
        {
            this.res.setContentType(contentType);
        }

        /**
         *
         * {@inheritDoc}
         */
        @Override
        public void setContentEncoding(final String contentEncoding)
        {
            this.res.setContentEncoding(contentEncoding);
        }

        /**
         *
         * {@inheritDoc}
         */
        @Override
        public void setCache(final Cache cache)
        {
            this.res.setCache(cache);
        }

        /**
         *
         * {@inheritDoc}
         */
        @Override
        public OutputStream getOutputStream() throws IOException
        {
            return this.res.getOutputStream();
        }

    }

    protected static class HttpServletResponseWrapper implements Response
    {

        protected final HttpServletResponse res;

        protected HttpServletResponseWrapper(final HttpServletResponse res)
        {
            this.res = res;
        }

        /**
         *
         * {@inheritDoc}
         */
        @Override
        public void setStatus(final int status)
        {
            this.res.setStatus(status);
        }

        /**
         *
         * {@inheritDoc}
         */
        @Override
        public void setHeader(final String name, final String value)
        {
            this.res.setHeader(name, value);
        }

        /**
         *
         * {@inheritDoc}
         */
        @Override
        public void setContentType(final String contentType)
        {
            this.res.setContentType(contentType);
        }

        /**
         *
         * {@inheritDoc}
         */
        @Override
        public void setContentEncoding(final String contentEncoding)
        {
            this.res.setCharacterEncoding(contentEncoding);
        }

        @Override
        public void setCache(final Cache cache)
        {
            // copied from WebScriptServletResponseImpl and adapted to avoid +-concatenation
            // set Cache-Control
            final StringBuilder cacheControl = new StringBuilder(64);
            String pragma = "";
            if (cache.getIsPublic())
            {
                cacheControl.append("public");
            }
            if (cache.getNeverCache())
            {
                if (cacheControl.length() > 0)
                {
                    cacheControl.append(", ");
                }
                cacheControl.append(NO_CACHE);
                pragma = NO_CACHE;
            }
            if (cache.getMaxAge() != null && cache.getNeverCache() == false)
            {
                if (cacheControl.length() > 0)
                {
                    cacheControl.append(", ");
                }
                cacheControl.append("max-age=").append(cache.getMaxAge());
            }
            if (cache.getMustRevalidate() && cache.getNeverCache() == false)
            {
                if (cacheControl.length() > 0)
                {
                    cacheControl.append(", ");
                }
                cacheControl.append("must-revalidate");
            }
            if (cacheControl.length() > 0)
            {
                final String cacheControlValue = cacheControl.toString();
                this.res.setHeader("Cache-Control", cacheControlValue);
                if (LOGGER.isDebugEnabled())
                {
                    LOGGER.debug("Cache - set response header Cache-Control: " + cacheControl);
                }
                // special case for IE Ajax request handling
                if (NO_CACHE.equals(cacheControlValue))
                {
                    this.res.setHeader("Expires", "Thu, 01 Jan 1970 00:00:00 GMT");
                }
            }
            if (pragma.length() > 0)
            {
                this.res.setHeader("Pragma", pragma);
                if (LOGGER.isDebugEnabled())
                {
                    LOGGER.debug("Cache - set response header Pragma: " + pragma);
                }
            }

            // set ETag
            if (cache.getETag() != null)
            {
                final String eTag = "\"" + cache.getETag() + "\"";
                this.res.setHeader("ETag", eTag);
                if (LOGGER.isDebugEnabled())
                {
                    LOGGER.debug("Cache - set response header ETag: " + eTag);
                }
            }

            // set Last Modified
            if (cache.getLastModified() != null)
            {
                this.res.setDateHeader("Last-Modified", cache.getLastModified().getTime());
                if (LOGGER.isDebugEnabled())
                {
                    final SimpleDateFormat formatter = getHTTPDateFormat();
                    final String lastModified = formatter.format(cache.getLastModified());
                    LOGGER.debug("Cache - set response header Last-Modified: " + lastModified);
                }
            }
        }

        /**
         *
         * {@inheritDoc}
         */
        @Override
        public OutputStream getOutputStream() throws IOException
        {
            return this.res.getOutputStream();
        }

    }

    // provide method variants for specific request / response types
    protected void streamContent(final WebScriptRequest req, final WebScriptResponse res, final File file, final Long modifiedTime,
            final boolean attach, final String attachFileName, final Map<String, Object> model, final String mimetype) throws IOException
    {
        final WebScriptRequestWrapper reqWrapper = new WebScriptRequestWrapper(req);
        final WebScriptResponseWrapper resWrapper = new WebScriptResponseWrapper(res);
        this.streamContent(reqWrapper, resWrapper, file, modifiedTime, attach, attachFileName, model, mimetype);
    }

    protected void streamContent(final HttpServletRequest req, final HttpServletResponse res, final File file, final Long modifiedTime,
            final boolean attach, final String attachFileName, final Map<String, Object> model, final String mimetype) throws IOException
    {
        final HttpServletRequestWrapper reqWrapper = new HttpServletRequestWrapper(req);
        final HttpServletResponseWrapper resWrapper = new HttpServletResponseWrapper(res);
        this.streamContent(reqWrapper, resWrapper, file, modifiedTime, attach, attachFileName, model, mimetype);
    }

    // copied from Repo-tier ContentStreamer and base classes since they aren't available in Share
    protected void streamContent(final Request req, final Response res, final File file, final Long modifiedTime, final boolean attach,
            final String attachFileName, final Map<String, Object> model, final String mimetype) throws IOException
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

    protected void streamContentImpl(final Request req, final Response res, final File file, final boolean attach, final Date modified,
            final String eTag, final String attachFileName, final Map<String, Object> model, final String mimetype) throws IOException
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

    protected void setAttachment(final Request req, final Response res, final boolean attach, final String attachFileName)
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

    protected void setResponseCache(final Response res, final Date modified, final String eTag, final Map<String, Object> model)
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
    protected boolean processRange(final Response res, final File file, final String range, final String mimetype, final String userAgent)
            throws IOException
    {
        // test for multiple byte ranges present in header
        return this.processSingleRange(res, file, range, mimetype);
    }

    protected boolean processSingleRange(final Response res, final File file, final String range, final String mimetype) throws IOException
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

    // copied from WebScriptServletResponseImpl
    protected static SimpleDateFormat getHTTPDateFormat()
    {
        if (s_dateFormat.get() != null)
        {
            return s_dateFormat.get();
        }

        final SimpleDateFormat formatter = new SimpleDateFormat("EEE, dd MMM yyyy kk:mm:ss zzz");
        formatter.setLenient(false);
        formatter.setTimeZone(TimeZone.getTimeZone("GMT"));
        s_dateFormat.set(formatter);
        return s_dateFormat.get();
    }
}
