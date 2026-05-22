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
package org.orderofthebee.addons.support.tools.repo.jscript.policy;

import org.alfresco.repo.jscript.ScriptNode;
import org.alfresco.repo.policy.BehaviourFilter;
import org.alfresco.repo.processor.BaseProcessorExtension;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.springframework.extensions.webscripts.annotation.ScriptClass;
import org.springframework.extensions.webscripts.annotation.ScriptClassType;
import org.springframework.extensions.webscripts.annotation.ScriptMethod;
import org.springframework.extensions.webscripts.annotation.ScriptMethodType;

/**
 * script object for handling the behaviourFilter
 *
 * @author Jens Goldhammer (fme AG)
 */

@ScriptClass(types = ScriptClassType.JavaScriptRootObject, code = "de/jgoldhammer/alfresco/jscript/policy",
             help = "the root object for the de.jgoldhammer.alfresco.jscript.policy/behaviourFilter")
public class ScriptPolicies extends BaseProcessorExtension
{
    private BehaviourFilter behaviourFilter;
    private NamespaceService namespaceService;

    public void setBehaviourFilter(BehaviourFilter behaviourFilter)
    {
        this.behaviourFilter = behaviourFilter;
    }

    public void setNamespaceService(NamespaceService namespaceService)
    {
        this.namespaceService = namespaceService;
    }

    @ScriptMethod(help = "eanbles the behaviour for the given scriptnode", output = "void",
                  code = "de.jgoldhammer.alfresco.jscript.policy.enableFor(node);", type = ScriptMethodType.READ)
    public void enableForNode(ScriptNode node)
    {
        behaviourFilter.enableBehaviour(node.getNodeRef());
    }

    @ScriptMethod(help = "eanbles the behaviour for the given scriptnode", output = "void",
                  code = "de.jgoldhammer.alfresco.jscript.policy.enableFor(node);", type = ScriptMethodType.READ)
    public void enableForTypeOrAspect(String shortQName)
    {
        if (shortQName == null)
        {
            throw new IllegalArgumentException("shortQName cannot be null");
        }
        if (namespaceService == null)
        {
            throw new IllegalStateException("namespaceService is not initialized");
        }
        QName k = QName.resolveToQName(namespaceService, shortQName);
        if (k == null)
        {
            throw new IllegalStateException("Could not resolve QName: " + shortQName);
        }
        behaviourFilter.enableBehaviour(k);
    }

    @ScriptMethod(help = "eanbles the behaviour for the given scriptnode", output = "void",
                  code = "de.jgoldhammer.alfresco.jscript.policy.enableFor(node);", type = ScriptMethodType.READ)

    public void disableForTypeOrAspect(String shortQName)
    {
        if (shortQName == null)
        {
            throw new IllegalArgumentException("shortQName cannot be null");
        }
        if (namespaceService == null)
        {
            throw new IllegalStateException("namespaceService is not initialized");
        }
        QName k = QName.resolveToQName(namespaceService, shortQName);
        if (k == null)
        {
            throw new IllegalStateException("Could not resolve QName: " + shortQName);
        }
        behaviourFilter.disableBehaviour(k);
    }

    @ScriptMethod(help = "eanbles all behaviours for the current transaction ", output = "void", code = "policy.enableAll;",
                  type = ScriptMethodType.READ)

    public void enableAll()
    {
        behaviourFilter.enableBehaviour();
    }

    @ScriptMethod(help = "disables all behaviour for the given scriptnode", output = "void",
                  code = "de.jgoldhammer.alfresco.jscript.policy.disableFor(node);", type = ScriptMethodType.READ)
    public void disableForNode(ScriptNode node)
    {
        behaviourFilter.disableBehaviour(node.getNodeRef());
    }

    @ScriptMethod(help = "disables all behaviour for the given scriptnode", output = "void",
                  code = "de.jgoldhammer.alfresco.jscript.policy.disableFor(node);", type = ScriptMethodType.READ)
    public void isAltered()
    {
        behaviourFilter.isActivated();
    }

}
