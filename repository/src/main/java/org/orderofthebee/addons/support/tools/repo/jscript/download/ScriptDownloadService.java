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
package org.orderofthebee.addons.support.tools.repo.jscript.download;

import org.alfresco.repo.jscript.BaseScopableProcessorExtension;
import org.alfresco.repo.jscript.ScriptNode;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.download.DownloadService;
import org.alfresco.service.cmr.download.DownloadStatus;
import org.alfresco.service.cmr.repository.NodeRef;

import com.google.common.base.Preconditions;

/**
 * ScriptDownloadService wraps the downloadservice.
 *
 */
public class ScriptDownloadService extends BaseScopableProcessorExtension
{

    DownloadService downloadService;
    ServiceRegistry serviceRegistry;

    public void setServiceRegistry(ServiceRegistry serviceRegistry)
    {
        this.serviceRegistry = serviceRegistry;
    }

    public void setDownloadService(DownloadService downloadService)
    {
        this.downloadService = downloadService;
    }

    public ScriptNode createByNodeRefs(String[] nodeRefs, boolean recursive)
    {
        Preconditions.checkNotNull(nodeRefs);

        NodeRef[] nodeRefsParam = new NodeRef[nodeRefs.length];
        for (int i=0; i<nodeRefs.length; i++)
        {
            nodeRefsParam[i] = new NodeRef(nodeRefs[i]);
        }

        return new ScriptNode(downloadService.createDownload(nodeRefsParam, recursive), serviceRegistry);
    }

    public ScriptNode createByNodeRef(String nodeRef, boolean recursive)
    {
        Preconditions.checkNotNull(nodeRef);
        NodeRef[] nodeRefsParam = {new NodeRef(nodeRef)};

        return new ScriptNode(downloadService.createDownload(nodeRefsParam, recursive), serviceRegistry);
    }

    public ScriptNode create(ScriptNode[] nodes, boolean recursive)
    {
        Preconditions.checkNotNull(nodes);

        NodeRef[] nodeRefsParam = new NodeRef[nodes.length];
        for (int i=0; i<nodes.length; i++)
        {
            nodeRefsParam[i] = nodes[i].getNodeRef();
        }

        return new ScriptNode(downloadService.createDownload(nodeRefsParam, recursive), serviceRegistry);
    }

    public ScriptNode create(ScriptNode node, boolean recursive)
    {
        Preconditions.checkNotNull(node);
        NodeRef[] nodeRefsParam = {node.getNodeRef()};
        return new ScriptNode(downloadService.createDownload(nodeRefsParam, recursive), serviceRegistry);
    }

    public void cancel(ScriptNode downloadRequest)
    {
        Preconditions.checkNotNull(downloadRequest,"downloadRequest cannot be null here");
        downloadService.cancelDownload(downloadRequest.getNodeRef());
    }

    public void cancel(String downloadRequestNodeRef)
    {
        Preconditions.checkNotNull(downloadRequestNodeRef,"downloadRequest cannot be null here");
        downloadService.cancelDownload(new NodeRef(downloadRequestNodeRef));
    }

    public DownloadStatus getStatus(ScriptNode download)
    {
        return downloadService.getDownloadStatus(download.getNodeRef());
    }

    public DownloadStatus getStatus(String downloadNodeRef)
    {
        return downloadService.getDownloadStatus(new NodeRef(downloadNodeRef));
    }
}
