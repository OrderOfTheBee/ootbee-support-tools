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

import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.extensions.webscripts.Cache;

/**
 * This class is used to wrap a (Java EE) {@link HttpServletResponse servlet response} for use in the {@link ContentStreamer} and
 * {@link LogFileHandler}.
 * 
 * @author Axel Faust
 */
public class LegacyHttpServletResponseWrapper implements Response
{

    private static final Logger LOGGER = LoggerFactory.getLogger(LegacyHttpServletResponseWrapper.class);

    private final HttpServletResponse res;

    public LegacyHttpServletResponseWrapper(final HttpServletResponse res)
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

    /**
     * 
     * {@inheritDoc}
     */
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
            cacheControl.append(ContentStreamer.NO_CACHE);
            pragma = ContentStreamer.NO_CACHE;
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
            LOGGER.debug("Cache - set response header Cache-Control: {}", cacheControl);
            // special case for IE Ajax request handling
            if (ContentStreamer.NO_CACHE.equals(cacheControlValue))
            {
                this.res.setHeader("Expires", "Thu, 01 Jan 1970 00:00:00 GMT");
            }
        }
        if (pragma.length() > 0)
        {
            this.res.setHeader("Pragma", pragma);
            LOGGER.debug("Cache - set response header Pragma: {}", pragma);
        }

        // set ETag
        if (cache.getETag() != null)
        {
            final String eTag = "\"" + cache.getETag() + "\"";
            this.res.setHeader("ETag", eTag);
            LOGGER.debug("Cache - set response header ETag: {}", eTag);
        }

        // set Last Modified
        if (cache.getLastModified() != null)
        {
            this.res.setDateHeader("Last-Modified", cache.getLastModified().getTime());
            if (LOGGER.isDebugEnabled())
            {
                final SimpleDateFormat formatter = ContentStreamer.getHTTPDateFormat();
                final String lastModified = formatter.format(cache.getLastModified());
                LOGGER.debug("Cache - set response header Last-Modified: {}", lastModified);
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

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isCommitted()
    {
        return res.isCommitted();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void reset()
    {
        res.reset();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void sendError(int status, String message) throws IOException
    {
        res.sendError(status, message);
    }

}