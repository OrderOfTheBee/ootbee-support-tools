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
/*
 * Copyright (C) 2008-2015 Citeck LLC.
 *
 * This file is part of Citeck EcoS
 *
 * Citeck EcoS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Citeck EcoS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Citeck EcoS. If not, see <http://www.gnu.org/licenses/>.
 */
package org.orderofthebee.addons.support.tools.repo.jscript.permission;

import java.util.Set;

import org.alfresco.repo.jscript.BaseScopableProcessorExtension;
import org.alfresco.repo.jscript.ScriptNode;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.cmr.security.AccessPermission;
import org.alfresco.service.cmr.security.AccessStatus;
import org.alfresco.service.cmr.security.PermissionService;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;

import com.google.common.base.Preconditions;

/**
 * encapsulates some permissionService methods related to nodes.
 * <p>
 * copied from https://github.com/Citeck/ecos-community/blob/166a54543d770caf8153297f4fbe1ff6868f9f2a/
 * idocs-repo/source/java/ru/citeck/ecos/security/ScriptPermissionService.java
 */
public class ScriptPermissionService extends BaseScopableProcessorExtension
{

    private PermissionService permissionService;




    public boolean hasReadPermission(String nodeRef)
    {
        Preconditions.checkNotNull(nodeRef);
        return permissionService.hasReadPermission(new NodeRef(nodeRef)) == AccessStatus.ALLOWED;
    }

    public boolean hasPermission(String nodeRef, String permission)
    {
        Preconditions.checkNotNull(nodeRef);
        Preconditions.checkNotNull(permission);
        return permissionService.hasPermission(new NodeRef(nodeRef), permission) == AccessStatus.ALLOWED;
    }

    public boolean hasPermission(String nodeRef, String permission, String authority)
    {
        Preconditions.checkNotNull(nodeRef);
        Preconditions.checkNotNull(permission);
        Preconditions.checkNotNull(authority);

        return AuthenticationUtil.runAs(
                   () -> permissionService.hasPermission(new NodeRef(nodeRef), permission) == AccessStatus.ALLOWED,
                   authority);
    }

    public boolean hasReadPermission(ScriptNode node)
    {
        Preconditions.checkNotNull(node);
        return permissionService.hasReadPermission(node.getNodeRef()) == AccessStatus.ALLOWED;
    }

    public boolean hasPermission(ScriptNode node, String permission)
    {
        Preconditions.checkNotNull(node);
        Preconditions.checkNotNull(permission);
        return permissionService.hasPermission(node.getNodeRef(), permission) == AccessStatus.ALLOWED;
    }


    public boolean hasPermission(ScriptNode node, String permission, ScriptNode authority)
    {
        Preconditions.checkNotNull(node);
        Preconditions.checkNotNull(permission);
        Preconditions.checkNotNull(authority);

        return AuthenticationUtil.runAs(
                   () -> permissionService.hasPermission(node.getNodeRef(), permission) == AccessStatus.ALLOWED,
                   (String) authority.getProperties().get("userName"));
    }



    /**
     * sets a new permission for a certain authority on a certain node by specifying if the permission is allowed or denied.
     *
     * @param nodeRefId the node to set the permission on
     * @param permission the name of the permission
     *            (see https://github.com/Alfresco/community-edition/blob/master/projects/repository/config/alfresco/
     *            model/permissionDefinitions.xml for the names)
     * @param authority the name of the authority- a username or a group name!
     * @param allow true if the permission should be allowed, false if denied
     */
    public void setPermission(
        String nodeRefId,
        String permission,
        String authority,
        boolean allow)
    {

        Preconditions.checkNotNull(nodeRefId);
        Preconditions.checkNotNull(permission);
        Preconditions.checkNotNull(authority);

        NodeRef nodeRef = new NodeRef(nodeRefId);
        permissionService.setPermission(
            nodeRef,
            authority,
            permission,
            allow
        );
    }

    /**
     * get the permissions of a node as native javascript array of type AccessPermission
     *
     * @param nodeRef the node to get the permissions for
     */
    public Scriptable getPermissions(String nodeRef)
    {
        Preconditions.checkNotNull(nodeRef);
        Set<AccessPermission> permissions = permissionService.getPermissions(new NodeRef(nodeRef));
        Object[] permissionsArray = permissions.toArray(new Object[permissions.size()]);

        return Context.getCurrentContext().newArray(getScope(), permissionsArray);

    }

    /**
     * get the current user permissions of a node as native javascript array of type AccessPermission
     *
     * @param node the scriptnode
     */
    public Scriptable getPermissionsOfCurrentUser(ScriptNode node)
    {
        Preconditions.checkNotNull(node);
        return getPermissions(node.getNodeRef().toString());
    }

    /**
     * get all permissions of a node as native javascript array of type AccessPermission
     *
     * @param node the scriptnode
     */
    public Scriptable getAllPermissions(ScriptNode node)
    {
        Preconditions.checkNotNull(node);
        Set<AccessPermission> permissions = permissionService.getAllSetPermissions(node.getNodeRef());

        Object[] permissionsArray = permissions.toArray(new Object[permissions.size()]);
        return Context.getCurrentContext().newArray(getScope(), permissionsArray);
    }

    /**
     * delete a certain permission for a certain authority on a certain node.
     *
     * @param nodeRefId the node to delete the permission from
     * @param permission the name of the permission
     *            (see https://github.com/Alfresco/community-edition/blob/master/projects/repository/config/alfresco/
     *            model/permissionDefinitions.xml for the names)
     * @param authority the name of the authority- a username or a group name!
     */

    public void deletePermission(
        final String nodeRefId,
        final String permission,
        final String authority)
    {

        Preconditions.checkNotNull(nodeRefId);
        Preconditions.checkNotNull(permission);
        Preconditions.checkNotNull(authority);

        NodeRef nodeRef = new NodeRef(nodeRefId);
        permissionService.deletePermission(
            nodeRef,
            authority,
            permission
        );
    }

    /**
     * delete all permissions on a certain node.
     *
     * @param nodeRefId the noderef - cannot be null
     */
    public void deletePermissions(String nodeRefId)
    {
        Preconditions.checkNotNull(nodeRefId);
        NodeRef nodeRef = new NodeRef(nodeRefId);
        permissionService.deletePermissions(
            nodeRef);
    }

    /**
     * removes all permissions of an authority on a certain node.
     *
     * @param nodeRefId the node
     * @param authority the authority which must exist
     */

    public void clearPermissions(
        final String nodeRefId,
        final String authority)
    {
        Preconditions.checkNotNull(nodeRefId);
        Preconditions.checkNotNull(authority);

        NodeRef nodeRef = new NodeRef(nodeRefId);
        permissionService.clearPermission(
            nodeRef,
            authority
        );
    }


    public void deleteStorePermissions(
        final String storeProtocol,
        final String storeId)
    {
        StoreRef storeRef = new StoreRef(storeProtocol, storeId);
        permissionService.deletePermissions(
            storeRef
        );
    }

    public void clearStorePermission(
        final String storeProtocol,
        final String storeId,
        final String authority)
    {
        StoreRef storeRef = new StoreRef(storeProtocol, storeId);
        permissionService.clearPermission(
            storeRef,
            authority
        );
    }


    public void setInheritParentPermissions(
        final String nodeRefId,
        final String inheritParentPermissions)
    {
        NodeRef nodeRef = new NodeRef(nodeRefId);
        permissionService.setInheritParentPermissions(
            nodeRef,
            new Boolean(inheritParentPermissions)
        );
    }

    public boolean isInheritParentPermissions(final String nodeRefId)
    {
        NodeRef nodeRef = new NodeRef(nodeRefId);
        Boolean result = permissionService.getInheritParentPermissions(nodeRef);
        return result.booleanValue();
    }

    public void setPermissionService(PermissionService permissionService)
    {
        this.permissionService = permissionService;
    }
}
