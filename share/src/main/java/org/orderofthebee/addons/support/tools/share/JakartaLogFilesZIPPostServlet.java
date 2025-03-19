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
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.context.ApplicationContext;
import org.springframework.extensions.surf.FrameworkBean;
import org.springframework.extensions.surf.LinkBuilder;
import org.springframework.extensions.surf.LinkBuilderFactory;
import org.springframework.extensions.surf.RequestContext;
import org.springframework.extensions.surf.UserFactory;
import org.springframework.extensions.surf.WebFrameworkServiceRegistry;
import org.springframework.extensions.surf.support.ServletRequestContext;
import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.connector.User;
import org.springframework.extensions.webscripts.servlet.FormData;
import org.springframework.web.context.support.WebApplicationContextUtils;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * @author Axel Faust
 */
@WebServlet(name = "OOTBee Support Tools - Log ZIP File Download", urlPatterns = { "/ootbee-support-tools/log4j-log-files.zip" })
public class JakartaLogFilesZIPPostServlet extends HttpServlet
{

    private static final long serialVersionUID = 6594146886154738251L;

    protected LogFileHandler logFileHandler;

    protected WebFrameworkServiceRegistry serviceRegistry;

    protected FrameworkBean frameworkBean;

    protected LinkBuilder linkBuilder;

    protected UserFactory userFactory;

    protected Method userFactoryInitialiseUserMethod;

    protected Constructor<FormData> formDataCtor;

    /**
     *
     * {@inheritDoc}
     */
    @Override
    public void init() throws ServletException
    {
        super.init();
        try
        {
            final Method getWebApplicationContextMethod = WebApplicationContextUtils.class.getMethod("getRequiredWebApplicationContext",
                    ServletContext.class);
            final ApplicationContext context = (ApplicationContext) getWebApplicationContextMethod.invoke(null, this.getServletContext());
            this.logFileHandler = context.getBean(LogFileHandler.class);
            this.serviceRegistry = context.getBean("webframework.service.registry", WebFrameworkServiceRegistry.class);
            this.frameworkBean = context.getBean("framework.utils", FrameworkBean.class);
            this.linkBuilder = context.getBean("webframework.factory.linkbuilder.servlet", LinkBuilderFactory.class).newInstance();
            this.userFactory = context.getBean("user.factory", UserFactory.class);

            this.userFactoryInitialiseUserMethod = UserFactory.class.getMethod("initialiseUser", RequestContext.class,
                    HttpServletRequest.class, String.class);
            this.formDataCtor = FormData.class.getConstructor(HttpServletRequest.class);
        }
        catch (NoSuchMethodException | SecurityException | InvocationTargetException | IllegalAccessException e)
        {
            throw new ServletException("Unable to init servlet", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doPost(final HttpServletRequest req, final HttpServletResponse res) throws ServletException, IOException
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
            user = (User) this.userFactoryInitialiseUserMethod.invoke(this.userFactory, context, req, userEndpointId);
        }
        catch (InvocationTargetException e)
        {
            res.sendError(Status.STATUS_INTERNAL_SERVER_ERROR, e.getCause().getMessage());
        }
        catch (final IllegalAccessException e)
        {
            res.sendError(Status.STATUS_INTERNAL_SERVER_ERROR, e.getMessage());
        }

        if (user != null && user.isAdmin())
        {
            final Map<String, Object> model = new HashMap<>();
            final Status status = new Status();
            final Cache cache = new Cache();
            cache.setNeverCache(true);
            model.put("status", status);
            model.put("cache", cache);

            final FormData rqData;
            try
            {
                rqData = this.formDataCtor.newInstance(req);
            }
            catch (InvocationTargetException | InstantiationException | IllegalAccessException e)
            {
                res.sendError(Status.STATUS_INTERNAL_SERVER_ERROR, e.getMessage());
                return;
            }

            final List<String> filePaths = new ArrayList<>();
            final String[] paths = rqData.getParameters().get("paths");
            filePaths.addAll(Arrays.asList(paths));

            final JakartaHttpServletRequestWrapper reqW = new JakartaHttpServletRequestWrapper(req);
            final JakartaHttpServletResponseWrapper resW = new JakartaHttpServletResponseWrapper(res);
            this.logFileHandler.handleLogZipRequest(filePaths, () -> reqW, () -> resW, model);
        }
        else
        {
            res.sendError(Status.STATUS_FORBIDDEN);
        }
    }

}
