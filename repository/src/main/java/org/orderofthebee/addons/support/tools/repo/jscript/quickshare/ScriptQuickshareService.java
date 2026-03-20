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
package org.orderofthebee.addons.support.tools.repo.jscript.quickshare;

import java.util.Map;

import org.alfresco.repo.jscript.BaseScopableProcessorExtension;
import org.alfresco.repo.jscript.ScriptNode;
import org.alfresco.service.cmr.quickshare.QuickShareService;

import com.google.common.base.Preconditions;

/**
 * utilizes the quickshareservice to share and unshare content.
 * it is possible to get the metadata of a shared document.
 */
public class ScriptQuickshareService extends BaseScopableProcessorExtension
{

    QuickShareService quickShareService;

    public void setQuickShareService(QuickShareService quickShareService)
    {
        this.quickShareService = quickShareService;
    }

    /**
     * share a content, so that others can
     *
     * @param node the document to share (no folders supported)
     * @return the quick share id
     */
    public String shareContent(ScriptNode node)
    {
        Preconditions.checkNotNull(node);
        return quickShareService.shareContent(node.getNodeRef()).getId();
    }

    /**
     * unshare a content, so that a previously shared content cannot be accessed anymore from anonymous users.
     *
     * @param shareId the shareid of the shared document
     *
     */
    public void unshareContent(String shareId)
    {
        Preconditions.checkNotNull(shareId);
        quickShareService.unshareContent(shareId);
    }

    /**
     * get metadata given by shareId
     *
     * @param shareId the shareid of the shared document
     * @return a map of metadata of the shared content
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> getMetadata(String shareId)
    {
        Preconditions.checkNotNull(shareId);
        return (Map<String,Object>) quickShareService.getMetaData(shareId).get("item");
    }

    /**
     * get metadata given by node
     *
     * @param node (not necessary to be shared!)
     * @return a map of metadata of the shared content or null
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> getMetadata(ScriptNode node)
    {
        Preconditions.checkNotNull(node);
        return (Map<String,Object>) quickShareService.getMetaData(node.getNodeRef()).get("item");
    }
}
