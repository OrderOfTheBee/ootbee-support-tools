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

/**
 * This interface is used to abstract the actual request away for our {@link ContentStreamer} and {@link LogFileHandler}.
 * 
 * @author Axel Faust
 */
public interface Request
{

    /**
     * Returns the value of the specified request header as a <code>String</code>. If the request did not include a header of the
     * specified name, this method returns <code>null</code>. If there are multiple headers with the same name, this method returns the
     * first head in the request. The header name is case insensitive. You can use this method with any request header.
     *
     * @param name
     *     a <code>String</code> specifying the
     *     header name
     *
     * @return a <code>String</code> containing the
     * value of the requested
     * header, or <code>null</code>
     * if the request does not
     * have a header of that name
     *
     */
    String getHeader(String name);
}