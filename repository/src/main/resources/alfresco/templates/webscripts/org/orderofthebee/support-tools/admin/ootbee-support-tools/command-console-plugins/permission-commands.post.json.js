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
 */
/*
 * Linked to Alfresco
 * Copyright (C) 2005 - 2025 Alfresco Software Limited.
 */

/* global json: false */

function getAuthenticationComponent()
{
    var ctxt, authenticationComponent;
    ctxt = Packages.org.springframework.web.context.ContextLoader.getCurrentWebApplicationContext();
    authenticationComponent = ctxt.getBean('AuthenticationComponent',
            Packages.org.alfresco.repo.security.authentication.AuthenticationComponent);
    return authenticationComponent;
}

function getPermissionService()
{
    var ctxt, permissionService;
    ctxt = Packages.org.springframework.web.context.ContextLoader.getCurrentWebApplicationContext();
    permissionService = ctxt.getBean('PermissionService', Packages.org.alfresco.service.cmr.security.PermissionService);
    return permissionService;
}

function getTenantService()
{
    var ctxt, tenantService;
    ctxt = Packages.org.springframework.web.context.ContextLoader.getCurrentWebApplicationContext();
    tenantService = ctxt.getBean('tenantService', Packages.org.alfresco.repo.tenant.TenantService);
    return tenantService;
}

function getPermissionServiceImpl()
{
    var ctxt, permissionService;
    ctxt = Packages.org.springframework.web.context.ContextLoader.getCurrentWebApplicationContext();
    permissionService = ctxt.getBean('permissionServiceImpl', Packages.org.alfresco.repo.security.permissions.impl.PermissionServiceImpl);
    return permissionService;
}

function getEffectiveDynamicAuthorities(node, user)
{
    var permissionService, tenantService, permissionServiceClass, dynamicAuthorityField, dynamicAuthorities, effectiveDynamicAuthorities, nodeRef, idx, dynamicAuthority;

    permissionService = getPermissionServiceImpl();
    tenantService = getTenantService();

    permissionServiceClass = Packages.java.lang.Class.forName('org.alfresco.repo.security.permissions.impl.PermissionServiceImpl');
    dynamicAuthorityField = permissionServiceClass.getDeclaredField('dynamicAuthorities');
    dynamicAuthorityField.setAccessible(true);

    dynamicAuthorities = dynamicAuthorityField.get(permissionService);
    effectiveDynamicAuthorities = [];

    nodeRef = tenantService.getName(node.nodeRef);
    for (idx = 0; idx < dynamicAuthorities.size(); idx++)
    {
        dynamicAuthority = dynamicAuthorities.get(idx);
        if (dynamicAuthority.hasAuthority(nodeRef, user))
        {
            effectiveDynamicAuthorities.push(dynamicAuthority.getAuthority());
        }
    }

    return effectiveDynamicAuthorities;
}

function getAllSettablePermissions(node)
{
    var permissions, permissionsArr, permissionsIter;

    permissions = getPermissionService().getSettablePermissions(node.nodeRef);
    permissionsArr = [];
    permissionsIter = permissions.iterator();
    while (permissionsIter.hasNext())
    {
        permissionsArr.push(String(permissionsIter.next()));
    }
    return permissionsArr;
}

function runAsUser(fn, user)
{
    var result;
    Packages.org.alfresco.repo.security.authentication.AuthenticationUtil.pushAuthentication();
    try
    {
        // use full authentication instead of just runAs - depending on authentication subsystem, this may affect implicitly granted
        // authorities
        getAuthenticationComponent().setCurrentUser(user,
                Packages.org.alfresco.repo.security.authentication.AuthenticationComponent.UserNameValidationMode.CHECK);

        result = fn();

        // restore
        Packages.org.alfresco.repo.security.authentication.AuthenticationUtil.popAuthentication();
    }
    catch (e)
    {
        // restore
        Packages.org.alfresco.repo.security.authentication.AuthenticationUtil.popAuthentication();

        if (e instanceof Packages.org.alfresco.repo.security.authentication.AuthenticationException)
        {
            status.setCode(status.STATUS_BAD_REQUEST, 'User ' + user + ' does not exist');
        }
        else
        {
            throw e;
        }
    }
    return result;
}

function executeEffectivePermission(args, settable)
{
    var userArg, nodeArg, permissionArg, argIdx, node;

    for (argIdx = 0; argIdx < args.length; argIdx++)
    {
        switch (args[argIdx])
        {
            case 'user':
                userArg = (args.length > argIdx + 1) ? args[++argIdx] : null;
                break;
            case 'permission':
                permissionArg = (args.length > argIdx + 1) ? args[++argIdx] : null;
                break;
            case 'node':
                nodeArg = (args.length > argIdx + 1) ? args[++argIdx] : null;
                break;
        }
    }

    if (userArg && nodeArg && (settable === true || permissionArg))
    {
        node = search.findNode(nodeArg);
        if (node !== null)
        {
            if (node.hasAspect('smf:smartFolderChild'))
            {
                if (node.properties['smf:actualNodeRef'])
                {
                    // translate to the actual node
                    node = search.findNode(node.properties['smf:actualNodeRef']);
                }
                else
                {
                    status.setCode(status.STATUS_BAD_REQUEST, 'Node ' + nodeArg + ' is a smart-folder child without a property denoting the actual NodeRef');
                }
            }
            else if (node.hasAspect('smf:smartFolder'))
            {
                status.setCode(status.STATUS_BAD_REQUEST, 'Node ' + nodeArg + ' is a smart-folder and cannot be evaluated for permissions');
            }

            if (status.code === status.STATUS_OK)
            {
                runAsUser(function()
                {
                    var permissionService = getPermissionService();
                    if (permissionArg && settable !== true)
                    {
                        model.checkedPermissions = [ {
                            user : userArg,
                            permission : permissionArg,
                            node : node,
                            allowed : permissionService.hasPermission(node.nodeRef, permissionArg) === Packages.org.alfresco.service.cmr.security.AccessStatus.ALLOWED
                        } ];
                    }
                    else
                    {
                        model.checkedPermissions = [];
                        getAllSettablePermissions(node)
                                .forEach(function(permission)
                                {
                                    model.checkedPermissions
                                            .push({
                                                user : userArg,
                                                permission : permission,
                                                node : node,
                                                allowed : permissionService.hasPermission(node.nodeRef, permission) === Packages.org.alfresco.service.cmr.security.AccessStatus.ALLOWED
                                            });
                                });
                    }
                }, userArg);
            }
        }
        else
        {
            status.setCode(status.STATUS_BAD_REQUEST, 'Node ' + nodeArg + ' does not exist');
        }
    }
    else
    {
        status.setCode(status.STATUS_BAD_REQUEST, 'Missing arguments - command requires "user" and "node"');
    }
}

function executeEffectiveAuthorisations(args)
{
    var userArg, nodeArg, argIdx, permissionService, effectiveAuthorisations, authorisations, authIter, dynamicAuthorities, node, authIdx;

    for (argIdx = 0; argIdx < args.length; argIdx++)
    {
        switch (args[argIdx])
        {
            case 'user':
                userArg = (args.length > argIdx + 1) ? args[++argIdx] : null;
                break;
            case 'node':
                nodeArg = (args.length > argIdx + 1) ? args[++argIdx] : null;
                break;
        }
    }

    // for some reason, getAuthorisations on the public PermissionService is denied to all (even admin), so we have to use the impl bean
    permissionService = getPermissionServiceImpl();
    effectiveAuthorisations = {};

    if (nodeArg)
    {
        node = search.findNode(nodeArg);
        if (node !== null)
        {
            if (node.hasAspect('smf:smartFolderChild'))
            {
                if (node.properties['smf:actualNodeRef'])
                {
                    // translate to the actual node
                    node = search.findNode(node.properties['smf:actualNodeRef']);
                }
                else
                {
                    status.setCode(status.STATUS_BAD_REQUEST, 'Node ' + nodeArg + ' is a smart-folder child without a property denoting the actual NodeRef');
                }
            }
            else if (node.hasAspect('smf:smartFolder'))
            {
                status.setCode(status.STATUS_BAD_REQUEST, 'Node ' + nodeArg + ' is a smart-folder and cannot be evaluated for authorisations');
            }

            if (status.code === status.STATUS_OK)
            {
                if (userArg)
                {
                    runAsUser(function()
                    {
                        model.user = userArg;
                        authorisations = permissionService.getAuthorisations();
                        dynamicAuthorities = getEffectiveDynamicAuthorities(node, userArg);
                    }, userArg);
                }
                else
                {
                    model.user = person.properties.userName;
                    authorisations = permissionService.getAuthorisations();
                    dynamicAuthorities = getEffectiveDynamicAuthorities(node, person.properties.userName);
                }
            }
        }
        else
        {
            status.setCode(status.STATUS_BAD_REQUEST, 'Node ' + nodeArg + ' does not exist');
        }
    }
    else
    {
        if (userArg)
        {
            runAsUser(function()
            {
                model.user = userArg;
                authorisations = permissionService.getAuthorisations();
            }, userArg);
        }
        else
        {
            model.user = person.properties.userName;
            authorisations = permissionService.getAuthorisations();
        }
    }

    if (authorisations)
    {
        authIter = authorisations.iterator();
        while (authIter.hasNext())
        {
            effectiveAuthorisations[authIter.next()] = true;
        }
    }

    if (dynamicAuthorities)
    {
        for (authIdx = 0; authIdx < dynamicAuthorities.length; authIdx++)
        {
            effectiveAuthorisations[dynamicAuthorities[authIdx]] = true;
        }
    }

    model.authorisations = Object.keys(effectiveAuthorisations);
}

function main()
{
    var service, reqBody, reqArgs, argIdx;

    service = String(url.service);
    model.command = service.substring(service.lastIndexOf('/') + 1);

    // web script json is (unwieldly) org.json.JSONObject
    reqBody = JSON.parse(json.toString());
    reqArgs = [];
    if (reqBody.arguments && Array.isArray(reqBody.arguments))
    {
        for (argIdx = 0; argIdx < reqBody.arguments.length; argIdx++)
        {
            reqArgs[argIdx] = reqBody.arguments[argIdx];
        }
    }

    switch (model.command)
    {
        case 'effectivePermission':
            executeEffectivePermission(reqArgs, false);
            break;
        case 'effectivePermissions':
            executeEffectivePermission(reqArgs, true);
            break;
        case 'effectiveAuthorisations':
            executeEffectiveAuthorisations(reqArgs);
            break;
        case 'help': // no-op
            break;
        default:
            status.setCode(status.STATUS_NOT_FOUND, 'Command not found');
    }
}

main();
