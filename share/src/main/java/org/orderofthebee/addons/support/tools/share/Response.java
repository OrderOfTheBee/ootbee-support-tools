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

/**
 * This interface is used to abstract the actual response away for our {@link ContentStreamer} and {@link LogFileHandler}.
 * 
 * @author Axel Faust
 */
public interface Response
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
     *     the status code
     *
     */
    void setStatus(int status);

    /**
     * Sets a response header with the given name and value.If the header had already been set, the new value overwrites the previous
     * one. The <code>containsHeader</code> method can be used to test for the presence of a header before setting its value.
     *
     * @param name
     *     the name of the header
     * @param value
     *     the header value If it contains octet string,
     *     it should be encoded according to RFC 2047
     *     (http://www.ietf.org/rfc/rfc2047.txt)
     *
     */
    void setHeader(String name, String value);

    /**
     * Sets the Content Type
     *
     * @param contentType
     *     String
     *
     */
    void setContentType(String contentType);

    /**
     * Sets the Content Encoding
     *
     * @param contentEncoding
     *     String
     *
     */
    void setContentEncoding(String contentEncoding);

    /**
     * Sets the Cache control
     *
     * @param cache
     *     cache control
     *
     */
    void setCache(Cache cache);

    /**
     * Returns a {@link OutputStream} suitable for writing binary data in the response. The servlet container does not encode the binary
     * data.
     *
     * @return a {@link OutputStream} for writing binary data
     *
     * @exception IOException
     *     if an input or output exception occurred
     *
     */
    OutputStream getOutputStream() throws IOException;

    /**
     * Retrieves the flag denoting whether the response was already committed.
     * 
     * @return {@code true} if the response was already committed
     */
    boolean isCommitted();

    /**
     * Reset the response.
     */
    void reset();

    /**
     * Send an error status.
     * 
     * @param status
     *     the status code
     * @param message
     *     the error message
     */
    void sendError(int status, String message) throws IOException;
}