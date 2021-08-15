/**
 * Copyright (C) 2016 - 2021 Order of the Bee
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
 * Copyright (C) 2005 - 2021 Alfresco Software Limited.
 * 
 * This file is part of code forked from the JavaScript Console project
 * which was licensed under the Apache License, Version 2.0 at the time.
 * In accordance with that license, the modifications / derivative work
 * is now being licensed under the LGPL as part of the OOTBee Support Tools
 * addon.
 */

(function(global, model)
{
    var hiddenMethods, isValidMethodName, result, objectName, methodName;

    hiddenMethods = 'setServiceRegistry getServiceRegistry serviceRegistry exec setPreferenceService setAuthenticationService'
            + ' setTenantService setAuthorityService setPersonService setNodeDAO equals hashCode class'
            + ' getClass getExtensionName setExtensionName extensionName notify update wait setScope getScope scope '
            + ' setProcessor';

    isValidMethodName = function(name)
    {
        return typeof name === 'string' && name.length > 0 && hiddenMethods.indexOf(name) === -1;
    };

    result = {
        global : [],
        methods : {},
        node : []
    };

    for (objectName in global)
    {
        if (typeof global[objectName] !== 'undefined' && global[objectName] !== null)
        {
            result.global.push(objectName);
            result.methods[objectName] = [];

            for (methodName in global[objectName])
            {
                if (typeof global[objectName][methodName] !== 'undefined'
                    && global[objectName][methodName] !== null && isValidMethodName(methodName))
                {
                    result.methods[objectName].push(methodName);
                }
            }

            result.methods[objectName].sort();
        }
    }

    // add global scriptNodes document and space modelled by the companyhome
    result.global.push('document');
    result.global.push('space');
    result.global.push('script');
    result.methods.document = result.methods.companyhome;
    result.methods.space = result.methods.companyhome;

    // add json for json based webscripts
    // (http://wiki.alfresco.com/wiki/Web_Scripts#json)
    result.global.push('json');
    result.methods.json = [];
    result.methods.json.push('get(fieldName)');
    result.methods.json.push('getJSONArray(fieldName)');
    result.methods.json.push('getJSONObject(index)');
    result.methods.json.push('has(fieldName)');
    result.methods.json.push('isNull(fieldName)');
    result.methods.json.push('length()');

    result.global.push('formdata');

    // JavaScript Console specific commands
    result.global.push('print');
    result.global.push('dump');
    result.global.push('recurse');
    result.methods.logger.push('getLevel');
    result.methods.logger.push('setLevel');

    // Rhino specific commands
    result.global.push('java');
    result.global.push('Packages');

    result.global.sort();
    model.json = jsonUtils.toJSONString(result);

})(this, model);