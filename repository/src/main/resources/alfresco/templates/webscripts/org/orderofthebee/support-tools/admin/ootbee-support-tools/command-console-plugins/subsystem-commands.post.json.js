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

function loadBeansByClass(cls)
{
    var ctxt, beans, idx, result;
    ctxt = Packages.org.springframework.web.context.ContextLoader.getCurrentWebApplicationContext();
    beans = ctxt.getBeansOfType(cls, true, false);

    // turn into a proper JS array
    result = [];
    for (idx = 0; idx < beans.length; idx++)
    {
        result.push(beans[idx]);
    }

    return result;
}

function toInstanceId(instanceIds)
{
    var id, idx;

    id = '';
    for (idx = 0; idx < instanceIds.size(); idx++)
    {
        if (id.length > 0)
        {
            id += ';';
        }
        // there should be no ; in any ID
        id += String(instanceIds.get(idx)).replace(/\\/g, '\\\\').replace(/;/g, '\\;');
    }

    return id;
}

function fromInstanceId(instanceId)
{
    var pattern, instanceIds, lastIndex, match, idx;

    pattern = /.;/g;
    instanceIds = [];
    lastIndex = 0;

    while ((match = pattern.exec(instanceId)) !== null)
    {
        if (match.indexOf('\\') !== 0)
        {
            instanceIds.push(instanceId.substring(lastIndex, match.index + 1));
            lastIndex = match.index + 2;
        }
    }
    instanceIds.push(instanceId.substring(lastIndex));

    for (idx = 0; idx < instanceIds.length; idx++)
    {
        instanceIds[idx] = instanceIds[idx].replace(/\\;/g, ';').replace(/\\\\/g, '\\');
    }

    return instanceIds;
}

function matchIds(instanceId, requestedId)
{
    var result, idx;

    result = instanceId.size() === requestedId.length;
    for (idx = 0; idx < instanceId.size() && idx < requestedId.length && result; idx++)
    {
        result = String(instanceId.get(idx)) === requestedId[idx];
    }

    return result;
}

function getSensitiveKeys()
{
    var ctxt, globalProperties, sensitiveKeys;
    ctxt = Packages.org.springframework.web.context.ContextLoader.getCurrentWebApplicationContext();
    globalProperties = ctxt.getBean('global-properties', Packages.java.util.Properties);
    sensitiveKeys = globalProperties["ootbee-support-tools.systeminformation.sensitiveKeys"].split(',');
    return sensitiveKeys;
}

function getChildApplicationContextFactoryForManager(childApplicationContextManager, instanceId)
{
    var factory, cls, stateGetter, state, contextFactoryGetter;

    if (typeof childApplicationContextManager.getChildApplicationContextFactory === 'function')
    {
        factory = childApplicationContextManager.getChildApplicationContextFactory(instanceId);
    }
    else
    {
        // deal with Alfresco versions before 5.2 which did not provide a decent public getter

        cls = Packages.java.lang.Class.forName('org.alfresco.repo.management.subsystems.AbstractPropertyBackedBean');
        stateGetter = cls.getDeclaredMethod('getState', Packages.java.lang.Boolean.TYPE);
        stateGetter.setAccessible(true);
        state = stateGetter.invoke(childApplicationContextManager, Packages.java.lang.Boolean.FALSE);

        cls = Packages.java.lang.Class.forName('org.alfresco.repo.management.subsystems.DefaultChildApplicationContextManager$ApplicationContextManagerState');
        contextFactoryGetter = cls.getDeclaredMethod('getApplicationContextFactory', Packages.java.lang.Class.forName('java.lang.String'));
        contextFactoryGetter.setAccessible(true);
        factory = contextFactoryGetter.invoke(state, instanceId);
    }

    return factory;
}

function resolveSubsystemInstance(instanceId)
{
    var match, requestedId, simpleFactories, instanceIdx, factory, managers, managerIdx, manager, instanceIds;

    requestedId = fromInstanceId(instanceId);

    simpleFactories = loadBeansByClass(Packages.org.alfresco.repo.management.subsystems.ApplicationContextFactory);
    for (instanceIdx = 0; match === undefined && instanceIdx < simpleFactories.length; instanceIdx++)
    {
        factory = simpleFactories[instanceIdx];
        if (matchIds(factory.id, requestedId))
        {
            match = factory;
        }
    }

    if (match === undefined)
    {
        managers = loadBeansByClass(Packages.org.alfresco.repo.management.subsystems.ChildApplicationContextManager);
        for (managerIdx = 0; match === undefined && managerIdx < managers.length; managerIdx++)
        {
            manager = managers[managerIdx];
            if (matchIds(manager.id, requestedId))
            {
                match = manager;
            }

            if (match === undefined)
            {
                instanceIds = manager.instanceIds;
                for (instanceIdx = 0; match === undefined && instanceIdx < instanceIds.size(); instanceIdx++)
                {
                    instanceId = instanceIds.get(instanceIdx);
                    factory = getChildApplicationContextFactoryForManager(manager, instanceId);

                    if (matchIds(factory.id, requestedId))
                    {
                        match = factory;
                    }
                }
            }
        }
    }

    return match;
}

function toInstance(factoryOrManager)
{
    var instance;

    instance = {
        id : toInstanceId(factoryOrManager.id),
        // getCategory unfortunately is protected / inaccessible, but always first element in ID list
        category : factoryOrManager.id.get(0),
        typeName : factoryOrManager.typeName ? factoryOrManager.typeName : null
    };

    if (factoryOrManager.currentSourceBeanName)
    {
        instance.currentSourceBean = factoryOrManager.currentSourceBeanName;
    }

    return instance;
}

function listInstances()
{
    var instances, simpleFactories, instanceIdx, factory, managers, managerIdx, manager, instanceIds, instanceId;

    instances = [];
    simpleFactories = loadBeansByClass(Packages.org.alfresco.repo.management.subsystems.ApplicationContextFactory);

    for (instanceIdx = 0; instanceIdx < simpleFactories.length; instanceIdx++)
    {
        factory = simpleFactories[instanceIdx];
        instances.push(toInstance(factory));
    }

    managers = loadBeansByClass(Packages.org.alfresco.repo.management.subsystems.ChildApplicationContextManager);
    for (managerIdx = 0; managerIdx < managers.length; managerIdx++)
    {
        manager = managers[managerIdx];
        instances.push(toInstance(manager));

        instanceIds = manager.instanceIds;
        for (instanceIdx = 0; instanceIdx < instanceIds.size(); instanceIdx++)
        {
            instanceId = instanceIds.get(instanceIdx);
            factory = getChildApplicationContextFactoryForManager(manager, instanceId);
            instances.push(toInstance(factory));
        }
    }

    model.subsystemInstances = instances;
}

function listProperties(reqArgs)
{
    var withSensitiveValues, sensitiveKeys, factoryOrManager, propertyNameIterator, mutableProperties, immutableProperties, propertyName, property, keySensitive, keyIdx, key;

    if (reqArgs.length >= 1)
    {
        model.requestedInstanceId = reqArgs[0];
        withSensitiveValues = (reqArgs[1] || '') === 'withSensitiveValues';
        sensitiveKeys = getSensitiveKeys();
        factoryOrManager = resolveSubsystemInstance(model.requestedInstanceId);

        if (factoryOrManager)
        {
            model.subsystemInstance = toInstance(factoryOrManager);

            propertyNameIterator = factoryOrManager.propertyNames.iterator();
            mutableProperties = [];
            immutableProperties = [];
            while (propertyNameIterator.hasNext())
            {
                propertyName = propertyNameIterator.next();

                property = {
                    key : propertyName,
                    value : factoryOrManager.getProperty(propertyName),
                    description : factoryOrManager.getDescription(propertyName)
                };

                if (!withSensitiveValues)
                {
                    keySensitive = false;
                    for (keyIdx = 0; !keySensitive && keyIdx < sensitiveKeys.length; keyIdx++)
                    {
                        key = sensitiveKeys[keyIdx];
                        if (key.trim().length > 0)
                        {
                            keySensitive = property.key.toLowerCase().endsWith(key.trim().toLowerCase());
                        }
                    }

                    if (keySensitive)
                    {
                        property.value = '***';
                    }
                }

                if (factoryOrManager.isUpdateable(propertyName))
                {
                    mutableProperties.push(property);
                }
                else
                {
                    immutableProperties.push(property);
                }
            }

            model.mutableProperties = mutableProperties;
            model.immutableProperties = immutableProperties;
        }
        else
        {
            status.code = 400;
            status.redirect = false;
        }
    }
    else
    {
        status.code = 400;
        status.redirect = false;
    }
}

function setProperty(reqArgs)
{
    var factoryOrManager, arg, separatorIdx, propertyValue;

    if (reqArgs.length >= 1)
    {
        model.requestedInstanceId = reqArgs[0];
        factoryOrManager = resolveSubsystemInstance(model.requestedInstanceId);

        if (factoryOrManager)
        {
            model.subsystemInstance = toInstance(factoryOrManager);

            if (reqArgs.length >= 2)
            {
                arg = String(reqArgs[1]);
                separatorIdx = arg.indexOf('=');
                if (separatorIdx !== -1)
                {
                    model.propertyName = arg.substring(0, separatorIdx);
                    propertyValue = arg.substring(separatorIdx + 1);

                    factoryOrManager.setProperty(model.propertyName, propertyValue);

                    model.property = {
                        key : model.propertyName,
                        value : factoryOrManager.getProperty(model.propertyName),
                        description : factoryOrManager.getDescription(model.propertyName)
                    };
                }
            }
        }
    }
}

function setProperties(reqArgs)
{
    var factoryOrManager, argIdx, arg, separatorIdx, propertyName, propertyValue, propertiesToSet, idx, property;

    if (reqArgs.length >= 1)
    {
        model.requestedInstanceId = reqArgs[0];
        factoryOrManager = resolveSubsystemInstance(model.requestedInstanceId);

        if (factoryOrManager)
        {
            model.subsystemInstance = toInstance(factoryOrManager);

            if (reqArgs.length >= 2)
            {
                model.propertyNames = [];
                propertiesToSet = new Packages.java.util.HashMap();
                for (argIdx = 1; argIdx < reqArgs.length; argIdx++)
                {
                    arg = reqArgs[argIdx];
                    separatorIdx = arg.indexOf('=');
                    if (separatorIdx !== -1)
                    {
                        propertyName = arg.substring(0, separatorIdx);
                        propertyValue = arg.substring(separatorIdx + 1);

                        propertiesToSet[propertyName] = propertyValue;
                        model.propertyNames.push(propertyName);
                    }
                }

                factoryOrManager.setProperties(propertiesToSet);

                model.properties = [];
                for (idx = 0; idx < model.propertyNames.length; idx++)
                {
                    propertyName = model.propertyNames[idx];
                    property = {
                        key : propertyName,
                        value : factoryOrManager.getProperty(propertyName),
                        description : factoryOrManager.getDescription(propertyName)
                    };
                    model.properties.push(property);
                }
            }
        }
    }
}

function removeProperties(reqArgs)
{
    var factoryOrManager, propertiesToRemove, argIdx, idx, propertyName, property;

    if (reqArgs.length >= 1)
    {
        model.requestedInstanceId = reqArgs[0];
        factoryOrManager = resolveSubsystemInstance(model.requestedInstanceId);

        if (factoryOrManager)
        {
            model.subsystemInstance = toInstance(factoryOrManager);

            if (reqArgs.length >= 2)
            {
                model.propertyNames = [];
                propertiesToRemove = new Packages.java.util.HashSet();
                for (argIdx = 1; argIdx < reqArgs.length; argIdx++)
                {
                    model.propertyNames.push(reqArgs[argIdx]);
                    propertiesToRemove.add(reqArgs[argIdx]);
                }

                factoryOrManager.removeProperties(propertiesToRemove);

                model.properties = [];
                for (idx = 0; idx < model.propertyNames.length; idx++)
                {
                    propertyName = model.propertyNames[idx];
                    property = {
                        key : propertyName,
                        value : factoryOrManager.getProperty(propertyName),
                        description : factoryOrManager.getDescription(propertyName)
                    };
                    model.properties.push(property);
                }
            }
        }
    }
}

function revert(reqArgs)
{
    var factoryOrManager;

    if (reqArgs.length >= 1)
    {
        model.requestedInstanceId = reqArgs[0];
        factoryOrManager = resolveSubsystemInstance(model.requestedInstanceId);

        if (factoryOrManager)
        {
            model.subsystemInstance = toInstance(factoryOrManager);
            factoryOrManager.revert();
        }
    }
}

function stop(reqArgs)
{
    var factoryOrManager;

    if (reqArgs.length >= 1)
    {
        model.requestedInstanceId = reqArgs[0];
        factoryOrManager = resolveSubsystemInstance(model.requestedInstanceId);

        if (factoryOrManager)
        {
            model.subsystemInstance = toInstance(factoryOrManager);
            factoryOrManager.stop();
        }
    }
}

function start(reqArgs)
{
    var factoryOrManager;

    if (reqArgs.length >= 1)
    {
    	model.requestedInstanceId = reqArgs[0]; 
        factoryOrManager = resolveSubsystemInstance(model.requestedInstanceId);

        if (factoryOrManager)
        {
            model.subsystemInstance = toInstance(factoryOrManager);
            factoryOrManager.start();
        }
    }
}

function restart(reqArgs)
{
    var factoryOrManager;

    if (reqArgs.length >= 1)
    {
        model.requestedInstanceId = reqArgs[0];
        factoryOrManager = resolveSubsystemInstance(model.requestedInstanceId);

        if (factoryOrManager)
        {
            model.subsystemInstance = toInstance(factoryOrManager);
            factoryOrManager.stop();
            factoryOrManager.start();
        }
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
        case 'listInstances':
            listInstances();
            break;
        case 'listProperties':
            listProperties(reqArgs);
            break;
        case 'setProperty':
            setProperty(reqArgs);
            break;
        case 'setProperties':
            setProperties(reqArgs);
            break;
        case 'removeProperties':
            removeProperties(reqArgs);
            break;
        case 'revert':
            revert(reqArgs);
            break;
        case 'stop':
            stop(reqArgs);
            break;
        case 'start':
            start(reqArgs);
            break;
        case 'restart':
            restart(reqArgs);
            break;
        case 'help': // no-op
            break;
        default:
            status.setCode(status.STATUS_NOT_FOUND, 'Command not found');
    }
}

main();
