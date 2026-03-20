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
package org.orderofthebee.addons.support.tools.repo.jscript.links;

import com.google.common.base.Preconditions;
import org.alfresco.repo.jscript.BaseScopableProcessorExtension;
import org.alfresco.repo.jscript.ScriptNode;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.repository.DeleteLinksStatusReport;
import org.alfresco.service.cmr.repository.DocumentLinkService;
import org.alfresco.service.cmr.repository.NodeRef;

import java.util.List;
import org.alfresco.service.cmr.repository.ChildAssociationRef;

/**
 * encapsulates the documentlinkservice which allows to create links for
 * documents into other folders,
 * delete all links for a document and get the original document of a given
 * link.
 */
public class ScriptLinkService extends BaseScopableProcessorExtension
{

    DocumentLinkService documentLinkService;
    ServiceRegistry services;

    public void setDocumentLinkService(DocumentLinkService documentLinkService)
    {
        this.documentLinkService = documentLinkService;
    }

    /**
     * Sets the service registry
     *
     * @param services the service registry
     */
    public void setServiceRegistry(ServiceRegistry services)
    {
        this.services = services;
    }

    /**
     * creates a link from the source to the target
     *
     * @param source       the document to link in another folder
     * @param targetFolder folder the document should be linked to.
     * @return the created link as script node
     */
    public ScriptNode createLink(ScriptNode source, ScriptNode targetFolder)
    {
        Preconditions.checkNotNull(source);
        Preconditions.checkNotNull(targetFolder);

        List<ChildAssociationRef> children = services.getNodeService().getChildAssocs(targetFolder.getNodeRef());
        for (ChildAssociationRef child : children)
        {
            NodeRef childNode = child.getChildRef();
            try
            {
                NodeRef dest = documentLinkService.getLinkDestination(childNode);
                if (dest != null && dest.equals(source.getNodeRef()))
                {
                    return new ScriptNode(childNode, services);
                }
            }
            catch (Exception e)
            {
                // Not a link or permission issue, ignore
            }
        }

        return new ScriptNode(documentLinkService.createDocumentLink(source.getNodeRef(),
                              targetFolder.getNodeRef()), services);
    }

    /**
     * delete all links of the given document.
     *
     * @param source - document or folder to delete all links for.
     * @return the deletelinkstatusreport object which holds information about the
     *         deletion
     */
    public DeleteLinksStatusReport deleteLinks(ScriptNode source)
    {
        Preconditions.checkNotNull(source);
        return documentLinkService.deleteLinksToDocument(source.getNodeRef());
    }

    /**
     * get the original document for a linked node.
     *
     * @param link the document to get the source document for.
     * @return the source document or null (if the given parameter is not a link)
     */
    public ScriptNode getSource(ScriptNode link)
    {
        Preconditions.checkNotNull(link);
        NodeRef sourceDocument = documentLinkService.getLinkDestination(link.getNodeRef());
        if (sourceDocument != null)
        {
            return new ScriptNode(sourceDocument, services);
        }
        else
        {
            return null;
        }
    }

}
