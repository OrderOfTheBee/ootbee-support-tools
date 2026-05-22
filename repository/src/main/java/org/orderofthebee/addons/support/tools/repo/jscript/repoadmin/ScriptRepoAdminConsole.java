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
package org.orderofthebee.addons.support.tools.repo.jscript.repoadmin;

import org.alfresco.repo.admin.RepoAdminInterpreter;
import org.alfresco.repo.processor.BaseProcessorExtension;
import org.springframework.extensions.webscripts.annotation.ScriptClass;
import org.springframework.extensions.webscripts.annotation.ScriptClassType;
import org.springframework.extensions.webscripts.annotation.ScriptMethod;
import org.springframework.extensions.webscripts.annotation.ScriptMethodType;

import java.io.IOException;

/**
 * script object for using the repoadmin interpreter.
 *
 * @author Jens Goldhammer (fme AG)
 */

@ScriptClass(types=ScriptClassType.JavaScriptRootObject, code="repoAdmin",
             help="the root object for the repo admin interpreter used in the de.jgoldhammer.alfresco.jscript.repoadmin "
                  + "console. Allows to run commands to deploy messages, models in the backend.")
public class ScriptRepoAdminConsole extends BaseProcessorExtension
{

    RepoAdminInterpreter repoAdminInterpreter;

    public void setRepoAdminInterpreter(RepoAdminInterpreter repoAdminInterpreter)
    {
        this.repoAdminInterpreter = repoAdminInterpreter;
    }

    @ScriptMethod(
        help="using the repoAdminInterpreter to run commands similar to the repo admin console",
        output="String",
        code="repoAdmin.exec('help')",
        type=ScriptMethodType.WRITE)
    public String exec(String command) throws IOException
    {
        return repoAdminInterpreter.interpretCommand(command);
    }


}
