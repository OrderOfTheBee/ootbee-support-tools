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
package org.orderofthebee.addons.support.tools.repo.jscript.tenant;

import org.alfresco.repo.processor.BaseProcessorExtension;
import org.alfresco.repo.tenant.TenantInterpreter;
import org.springframework.extensions.webscripts.annotation.ScriptClass;
import org.springframework.extensions.webscripts.annotation.ScriptClassType;
import org.springframework.extensions.webscripts.annotation.ScriptMethod;
import org.springframework.extensions.webscripts.annotation.ScriptMethodType;

import java.io.IOException;

/**
 * script object for using the de.jgoldhammer.alfresco.jscript.tenant interpreter
 *
 * @author Jens Goldhammer (fme AG)
 */

@ScriptClass(types=ScriptClassType.JavaScriptRootObject, code="tenantAdmin", help="the root object for tenant " +
             "interpreter used in the tenantadmin console. Allows to run commands to show tenants, create new tenants " +
             "and delete tenants...")
public class ScriptTenantAdminConsole extends BaseProcessorExtension
{

    TenantInterpreter tenantInterpreter;

    public void setTenantInterpreter(TenantInterpreter tenantInterpreter)
    {
        this.tenantInterpreter = tenantInterpreter;
    }

    @ScriptMethod(
        help="using the tenantinterpreter to run commands similar to the de.jgoldhammer.alfresco.jscript.tenant console",
        output="String",
        code="tenants.exec('help')",
        type=ScriptMethodType.WRITE)
    public String exec(String command) throws IOException
    {
        return tenantInterpreter.interpretCommand(command);
    }


}
