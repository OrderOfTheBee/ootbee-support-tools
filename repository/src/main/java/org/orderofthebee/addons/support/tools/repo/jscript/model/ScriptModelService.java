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
package org.orderofthebee.addons.support.tools.repo.jscript.model;

import java.util.List;

import org.alfresco.query.PagingRequest;
import org.alfresco.repo.jscript.BaseScopableProcessorExtension;
import org.alfresco.repo.jscript.ScriptNode;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.dictionary.CustomModelDefinition;
import org.alfresco.service.cmr.dictionary.CustomModelService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;

/**
 * Created by jgoldhammer on 17.09.16.
 */
public class ScriptModelService extends BaseScopableProcessorExtension
{

    CustomModelService customModelService;
    ServiceRegistry serviceRegistry;

    public void setServiceRegistry(ServiceRegistry serviceRegistry)
    {
        this.serviceRegistry = serviceRegistry;
    }

    public void setCustomModelService(CustomModelService customModelService)
    {
        this.customModelService = customModelService;
    }

    /**
     * checks whether the current user is a model admin...
     * @return true if the user is a model admin or super admin, false if not
     */
    public boolean isModelAdmin()
    {
        return customModelService.isModelAdmin(AuthenticationUtil.getRunAsUser());
    }

    /**
     * activates a custom model by name
     *
     * @param model name of the model
     */
    public void activateModel(String model)
    {
        customModelService.activateCustomModel(model);
    }

    public void deactivateModel(String model)
    {
        customModelService.deactivateCustomModel(model);
    }

    public void deleteModel(String model)
    {
        customModelService.deleteCustomModel(model);
    }

    public ScriptNode getModelNode(String model)
    {
        NodeRef customModelRef = customModelService.getModelNodeRef(model);
        if(customModelRef!=null)
        {
            return new ScriptNode(customModelRef, serviceRegistry);
        }
        else
        {
            return null;
        }
    }

    public Scriptable getCustomModels(int start, int end)
    {
        List<CustomModelDefinition> customModels = customModelService.getCustomModels(new PagingRequest(start, end)).getPage();
        return Context.getCurrentContext().newArray(getScope(),customModels.toArray(new Object[customModels.size()]));
    }


}
