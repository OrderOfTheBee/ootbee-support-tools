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
package org.orderofthebee.addons.support.tools.repo.jscript.hidden;

import org.alfresco.repo.jscript.BaseScopableProcessorExtension;
import org.alfresco.repo.jscript.ScriptNode;
import org.alfresco.repo.model.filefolder.HiddenAspect;
import org.alfresco.repo.model.filefolder.HiddenFileInfo;

import com.google.common.base.Preconditions;

/**
 * wraps the api to hide nodes for certain clients or completely...
 */
public class ScriptHiddenAspect extends BaseScopableProcessorExtension
{

    HiddenAspect hiddenAspect;

    public void setHiddenAspect(HiddenAspect hiddenAspect)
    {
        this.hiddenAspect = hiddenAspect;
    }

    public void hideNodeExplicit(ScriptNode scriptNode)
    {
        Preconditions.checkNotNull(scriptNode);
        hiddenAspect.hideNodeExplicit(scriptNode.getNodeRef());
    }

    public void unhideNode(ScriptNode scriptNode)
    {
        Preconditions.checkNotNull(scriptNode);
        hiddenAspect.unhideExplicit(scriptNode.getNodeRef());
    }

    public boolean hasHiddenAspect(ScriptNode scriptNode)
    {
        Preconditions.checkNotNull(scriptNode);
        return hiddenAspect.hasHiddenAspect(scriptNode.getNodeRef());
    }

    public void removeHiddenAspect(ScriptNode scriptNode)
    {
        Preconditions.checkNotNull(scriptNode);
        hiddenAspect.removeHiddenAspect(scriptNode.getNodeRef());
    }

    public HiddenFileInfo onHiddenPath(ScriptNode scriptNode)
    {
        Preconditions.checkNotNull(scriptNode);
        return hiddenAspect.onHiddenPath(scriptNode.getNodeRef());
    }

}
