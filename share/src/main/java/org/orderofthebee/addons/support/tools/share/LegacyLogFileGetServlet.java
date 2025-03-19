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
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.ApplicationContext;
import org.springframework.extensions.surf.FrameworkBean;
import org.springframework.extensions.surf.LinkBuilder;
import org.springframework.extensions.surf.LinkBuilderFactory;
import org.springframework.extensions.surf.RequestContext;
import org.springframework.extensions.surf.UserFactory;
import org.springframework.extensions.surf.WebFrameworkServiceRegistry;
import org.springframework.extensions.surf.exception.UserFactoryException;
import org.springframework.extensions.surf.support.ServletRequestContext;
import org.springframework.extensions.surf.util.URLDecoder;
import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.connector.User;
import org.springframework.web.context.support.WebApplicationContextUtils;

/**
 * @author Axel Faust
 */
@WebServlet(name = "OOTBee Support Tools - Log File Download", urlPatterns = { "/ootbee-support-tools/log4j-log-file/*" })
public class LegacyLogFileGetServlet extends HttpServlet
{

    private static final long serialVersionUID = 5162376729083905078L;

    protected LogFileHandler logFileHandler;

    protected WebFrameworkServiceRegistry serviceRegistry;

    protected FrameworkBean frameworkBean;

    protected LinkBuilder linkBuilder;

    protected UserFactory userFactory;

    /**
     *
     * {@inheritDoc}
     */
    @Override
    public void init() throws ServletException
    {
        super.init();
        final ApplicationContext context = WebApplicationContextUtils.getRequiredWebApplicationContext(this.getServletContext());
        this.logFileHandler = context.getBean(LogFileHandler.class);
        this.serviceRegistry = context.getBean("webframework.service.registry", WebFrameworkServiceRegistry.class);
        this.frameworkBean = context.getBean("framework.utils", FrameworkBean.class);
        this.linkBuilder = context.getBean("webframework.factory.linkbuilder.servlet", LinkBuilderFactory.class).newInstance();
        this.userFactory = context.getBean("user.factory", UserFactory.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doGet(final HttpServletRequest req, final HttpServletResponse res) throws ServletException, IOException
    {
        if (req.getCharacterEncoding() == null)
        {
            req.setCharacterEncoding("UTF-8");
        }

        User user = null;

        final ServletRequestContext context = new ServletRequestContext(this.serviceRegistry, this.frameworkBean, this.linkBuilder);
        final String userEndpointId = (String) context.getAttribute(RequestContext.USER_ENDPOINT);
        try
        {
            user = this.userFactory.initialiseUser(context, req, userEndpointId);
        }
        catch (final UserFactoryException ufex)
        {
            res.sendError(Status.STATUS_INTERNAL_SERVER_ERROR, ufex.getMessage());
        }

        if (user != null && user.isAdmin())
        {
            final String requestURI = req.getRequestURI();
            String filePath = URLDecoder.decode(requestURI.substring(req.getContextPath().length() + req.getServletPath().length() + 1));
            if (!filePath.startsWith("/"))
            {
                filePath = "/" + filePath;
            }

            final Map<String, Object> model = new HashMap<>();
            final Status status = new Status();
            final Cache cache = new Cache();
            cache.setNeverCache(true);
            model.put("status", status);
            model.put("cache", cache);

            final String attachParam = req.getParameter("a");
            final boolean attach = attachParam != null && Boolean.parseBoolean(attachParam);

            final LegacyHttpServletRequestWrapper reqW = new LegacyHttpServletRequestWrapper(req);
            final LegacyHttpServletResponseWrapper resW = new LegacyHttpServletResponseWrapper(res);
            this.logFileHandler.handleLogFileRequest(filePath, attach, () -> reqW, () -> resW, model);
        }
        else
        {
            res.sendError(Status.STATUS_FORBIDDEN);
        }
    }

}
