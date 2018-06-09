/**
 * Copyright (C) 2018 Axel Faust
 * Copyright (C) 2018 Order of the Bee
 * 
 * This file is part of Community Support Tools
 * 
 * Community Support Tools is free software: you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 * 
 * Community Support Tools is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with Community Support Tools. If not, see
 * <http://www.gnu.org/licenses/>.
 */
/*
 * Linked to Alfresco Copyright
 * (C) 2005-2018 Alfresco Software Limited.
 */

/* global json: false */

function getPermissionService()
{
    var ctxt, permissionService;
    ctxt = Packages.org.springframework.web.context.ContextLoader.getCurrentWebApplicationContext();
    permissionService = ctxt.getBean('PermissionService', Packages.org.alfresco.service.cmr.security.PermissionService);
    return permissionService;
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
    Packages.org.alfresco.repo.security.authentication.AuthenticationUtil.setRunAsUser(user);
    try
    {
        result = fn();
        // restore
        Packages.org.alfresco.repo.security.authentication.AuthenticationUtil.popAuthentication();
    }
    catch (e)
    {

        // restore
        Packages.org.alfresco.repo.security.authentication.AuthenticationUtil.popAuthentication();
        throw e;
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
            runAsUser(
                    function()
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
                                    .forEach(
                                            function(permission)
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
        case 'help': // no-op
            break;
        default:
            status.setCode(status.STATUS_NOT_FOUND, 'Command not found');
    }
}

main();
