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
package org.orderofthebee.addons.support.tools.repo.jscript.content;

import org.alfresco.repo.jscript.BaseScopableProcessorExtension;
import org.alfresco.service.cmr.model.FileFolderService;
import org.alfresco.service.cmr.model.FileInfo;
import org.alfresco.service.cmr.repository.NodeRef;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

import com.google.common.base.Preconditions;

/**
 * helps to get the content url of a node.
 *
 * https://github.com/magnus-larsson/my-alfresco/blob/97538c73268e3ca77e39e8cc37d6f61f8f90b4c5/
 * repo/src/main/java/se/vgregion/alfresco/repo/scripts/ContentUrlResolver.java
 *
 */
public class ScriptContentUrlResolver extends BaseScopableProcessorExtension implements InitializingBean
{

    private FileFolderService fileFolderService;

    public void setFileFolderService(final FileFolderService fileFolderService)
    {
        this.fileFolderService = fileFolderService;
    }

    public String getContentUrl(final String node)
    {
        Preconditions.checkNotNull(node);
        NodeRef nodeRef = new NodeRef(node);

        final FileInfo fileInfo = fileFolderService.getFileInfo(nodeRef);

        if (fileInfo == null || fileInfo.getContentData() == null)
        {
            throw new IllegalArgumentException("Cannot get the content date for this node");
        }
        return fileFolderService.getFileInfo(nodeRef).getContentData().getContentUrl();
    }

    @Override
    public void afterPropertiesSet() throws Exception
    {
        Assert.notNull(fileFolderService, "FileFolderService must not be null");
    }

}
