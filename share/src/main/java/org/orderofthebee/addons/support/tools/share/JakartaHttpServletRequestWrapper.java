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

import jakarta.servlet.http.HttpServletRequest;

/**
 * This class is used to wrap a (Java EE) {@link HttpServletRequest servlet request} for use in the {@link ContentStreamer} and
 * {@link LogFileHandler}.
 * 
 * @author Axel Faust
 */
public class JakartaHttpServletRequestWrapper implements Request
{

    protected final HttpServletRequest req;

    protected JakartaHttpServletRequestWrapper(final HttpServletRequest req)
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