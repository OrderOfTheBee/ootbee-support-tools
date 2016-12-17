/*
 * Copyright (C) 2005-2010 Alfresco Software Limited.
 *
 * This file is part of Alfresco
 *
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */
package org.orderofthebee.addons.support.tools.repo.security;

import org.alfresco.repo.security.authentication.AuthenticationException;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.security.permissions.AccessDeniedException;
import org.alfresco.repo.tenant.TenantDisabledException;
import org.alfresco.service.transaction.ReadOnlyServerException;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.dao.TransientDataAccessResourceException;

/**
 * https://issues.alfresco.com/jira/browse/ACE-4001
 * <p>
 * Interceptor to translate and possibly I18Nize exceptions thrown by service calls.
 */
public class ExtendedExceptionTranslatorMethodInterceptor implements MethodInterceptor {
    private static final String MSG_ACCESS_DENIED = "permissions.err_access_denied";

    private static Log LOG = LogFactory.getLog(ExtendedExceptionTranslatorMethodInterceptor.class);

    public ExtendedExceptionTranslatorMethodInterceptor() {
        super();
    }

    public Object invoke(MethodInvocation mi) throws Throwable {
        try {
            return mi.proceed();
        } catch (net.sf.acegisecurity.AccessDeniedException ade) {
            if (LOG.isDebugEnabled()) {
                logReasonWithArguments(mi);
            }
            throw new AccessDeniedException(MSG_ACCESS_DENIED, ade);
        } catch (ReadOnlyServerException e) {
            throw new AccessDeniedException(ReadOnlyServerException.MSG_READ_ONLY, e);
        } catch (TransientDataAccessResourceException e) {
            // this usually occurs when the server is in read-only mode
            throw new AccessDeniedException(ReadOnlyServerException.MSG_READ_ONLY, e);
        } catch (InvalidDataAccessApiUsageException e) {
            // this usually occurs when the server is in read-only mode
            throw new AccessDeniedException(ReadOnlyServerException.MSG_READ_ONLY, e);
        } catch (TenantDisabledException e) {
            // the cause already has an I18Nd message
            throw new AuthenticationException(e.getMessage(), e);
        }
    }

    /**
     * Writes the currently invoked method, with arguments and current user context to the debug log.
     *
     * @param mi The current method invocation.
     */
    private void logReasonWithArguments(MethodInvocation mi) {
        LOG.debug("AccessDeniedException occured in");
        LOG.debug(mi.getMethod().toString());

        Object[] args = mi.getArguments();
        for (int i = 0; i < args.length; i++) {
            LOG.debug("with argument " + i + ": " + args[i]);
        }

        LOG.debug("for current user: " + AuthenticationUtil.getFullyAuthenticatedUser());
    }
}
