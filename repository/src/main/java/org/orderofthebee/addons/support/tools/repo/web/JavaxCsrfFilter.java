/**
 * Copyright (C) 2016 - 2026 Order of the Bee
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
 * Copyright (C) 2005 - 2026 Alfresco Software Limited.
 */
package org.orderofthebee.addons.support.tools.repo.web;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;

/**
 * This filter extends coverage of Alfresco CSRF handling to admin console web scripts of this addon.
 *
 * @author Axel Faust
 */
@WebFilter(filterName = "OOTBee CRSF Token Filter", urlPatterns = { "/service/ootbee/admin/*", "/s/ootbee/admin/*",
        "/wcservice/ootbee/admin/*", "/wcs/ootbee/admin/*" })
public class JavaxCsrfFilter implements Filter
{

    private Filter actualFilter;

    /**
     * {@inheritDoc}
     */
    @Override
    public void init(final FilterConfig filterConfig) throws ServletException
    {
        try
        {
            final Class<?> cls = Class.forName("org.springframework.extensions.webscripts.servlet.CSRFFilter");
            this.actualFilter = (Filter) cls.newInstance();
        }
        catch (final ClassNotFoundException | InstantiationException | IllegalAccessException | ClassCastException e)
        {
            throw new ServletException("Failed to instantiate actual filter", e);
        }
        this.actualFilter.init(filterConfig);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain) throws IOException, ServletException
    {
        this.actualFilter.doFilter(request, response, chain);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void destroy()
    {
        this.actualFilter.destroy();
    }

}
