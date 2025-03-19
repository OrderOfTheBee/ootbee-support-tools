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

import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.WebScriptResponse;

/**
 * This class is used to wrap a {@link WebScriptResponse web script response} for use in the {@link ContentStreamer} and
 * {@link LogFileHandler}.
 * 
 * @author Axel Faust
 */
public class WebScriptResponseWrapper implements Response
{

    private final WebScriptResponse res;

    public WebScriptResponseWrapper(final WebScriptResponse res)
    {
        this.res = res;
    }

    /**
     * @return the res
     */
    public WebScriptResponse getWebScriptResponse()
    {
        return res;
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

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isCommitted()
    {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void reset()
    {
        // NO-OP
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void sendError(int status, String message)
    {
        // NO-OP
    }
}