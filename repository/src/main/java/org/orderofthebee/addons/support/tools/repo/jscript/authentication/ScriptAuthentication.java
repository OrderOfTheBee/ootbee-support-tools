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
 *
 * This file is part of code forked from the alfresco-jscript-extensions project
 * by Jens Goldhammer, which was licensed under the Apache License, Version 2.0.
 * In accordance with that license, the modifications / derivative work
 * is now being licensed under the LGPL as part of the OOTBee Support Tools
 * addon.
 */
/**
 *
 */
package org.orderofthebee.addons.support.tools.repo.jscript.authentication;

import org.alfresco.repo.processor.BaseProcessorExtension;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.service.cmr.security.AuthorityService;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.extensions.webscripts.annotation.ScriptClass;
import org.springframework.extensions.webscripts.annotation.ScriptClassType;
import org.springframework.extensions.webscripts.annotation.ScriptMethod;
import org.springframework.extensions.webscripts.annotation.ScriptMethodType;

/**
 * Script object for handling authentication context switching.
 * Only admin users are allowed to switch authentication context.
 *
 * @author Jens Goldhammer (fme AG)
 * @author Order of the Bee
 */
@ScriptClass(types = ScriptClassType.JavaScriptRootObject, code = "auth",
             help = "Root object for authentication utilities to switch the authenticated user")
public class ScriptAuthentication extends BaseProcessorExtension implements ApplicationContextAware
{

    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException
    {
        this.applicationContext = applicationContext;
    }

    private void checkAdminAuthority()
    {
        AuthorityService authorityService = (AuthorityService) applicationContext.getBean("authorityService");
        if (!authorityService.isAdminAuthority(AuthenticationUtil.getFullyAuthenticatedUser()))
        {
            throw new RuntimeException("Only admin users are allowed to use the runAs methods");
        }
    }

    @ScriptMethod(help = "Set the system user as the currently running user for authentication purposes. Requires admin authority.",
                  output = "void", code = "auth.runAsSystem()", type = ScriptMethodType.READ)
    public void runAsSystem()
    {
        checkAdminAuthority();
        AuthenticationUtil.setRunAsUserSystem();
    }

    @ScriptMethod(help =
                      "Switch to the given user for all authenticated operations. Requires admin authority. "
                      + "The original user can still be found using auth.getFullyAuthenticatedUser()",
                  output = "void", code = "auth.runAs('user1');", type = ScriptMethodType.READ)
    public void runAs(String userName)
    {
        checkAdminAuthority();
        AuthenticationUtil.setRunAsUser(userName);
    }

    @ScriptMethod(help =
                      "Get the user that is currently in effect for purposes of authentication. "
                      + "This includes any overlays introduced by auth.runAs().",
                  output = "String", code = "auth.getRunAsUser()", type = ScriptMethodType.READ)
    public String getRunAsUser()
    {
        return AuthenticationUtil.getRunAsUser();
    }

    @ScriptMethod(help =
                      "Get the fully authenticated user. Returns the name of the user that last authenticated, "
                      + "excluding any overlay authentication.",
                  output = "String", code = "auth.getFullyAuthenticatedUser();", type = ScriptMethodType.READ)
    public String getFullyAuthenticatedUser()
    {
        return AuthenticationUtil.getFullyAuthenticatedUser();
    }

    @ScriptMethod(help = "Authenticate as the given user. Requires admin authority. All operations will run in the context of this user.",
                  output = "void", code = "auth.runAsFullyAuthenticatedUser('user1');", type = ScriptMethodType.READ)
    public void runAsFullyAuthenticatedUser(String userName)
    {
        checkAdminAuthority();
        AuthenticationUtil.setFullyAuthenticatedUser(userName);
    }

    @ScriptMethod(help = "Get the name of the system user",
                  output = "String",
                  code = "auth.getSystemUserName()",
                  type = ScriptMethodType.READ)
    public String getSystemUserName()
    {
        return AuthenticationUtil.getSystemUserName();
    }

    @ScriptMethod(help = "Get the name of the admin user",
                  output = "String",
                  code = "auth.getAdminUserName();",
                  type = ScriptMethodType.READ)
    public String getAdminUserName()
    {
        return AuthenticationUtil.getAdminUserName();
    }
}
