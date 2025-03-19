/**
 * Copyright (C) 2016 - 2025 Order of the Bee
 *
 * This file is part of OOTBee Support Tools
 *
 * OOTBee Support Tools is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OOTBee Support Tools is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with OOTBee Support Tools. If not, see <http://www.gnu.org/licenses/>.
 */
/*
 * Linked to Alfresco
 * Copyright (C) 2005 - 2021 Alfresco Software Limited.
 */

/* exported supportsRenditionService2 */
function supportsRenditionService2()
{
    var ctxt, supported;

    ctxt = Packages.org.springframework.web.context.ContextLoader.getCurrentWebApplicationContext();
    supported = ctxt.containsBean('RenditionService2');

    return supported;
}

// supported/known mimetypes are relevant for various tool drop downs
/* exported buildMimetypesModel */
function buildMimetypesModel()
{
    var ctxt, mimetypeService, jMimetypes, mimetypes, idx, max;

    ctxt = Packages.org.springframework.web.context.ContextLoader.getCurrentWebApplicationContext();
    mimetypeService = ctxt.getBean('mimetypeService', Packages.org.alfresco.service.cmr.repository.MimetypeService);

    // Alfresco generally returns lists by reference - we don't want to modify internal state by sorting
    // can't use simple [].concat(mimetypeService.mimetypes) as Rhino versions in 6.x don't seem to handle that as well as ACS 7.x
    jMimetypes = mimetypeService.mimetypes;
    mimetypes = [];
    for (idx = 0, max = jMimetypes.size(); idx < max; idx++) {
        mimetypes.push(jMimetypes.get(idx));
    }
    // cannot rely on mimetypes being internally sorted lexicographically
    mimetypes.sort(function(a, b)
    {
        return a.localeCompare(b);
    });
    model.mimetypes = mimetypes;
}

function buildPropertyGetter(ctxt)
{
    var globalProperties, placeholderHelper, propertyGetter;

    globalProperties = ctxt.getBean('global-properties', Packages.java.util.Properties);
    placeholderHelper = new Packages.org.springframework.util.PropertyPlaceholderHelper('${', '}', ':', true);

    propertyGetter = function(propertyName, defaultValue)
    {
        var propertyValue;

        propertyValue = globalProperties[propertyName];
        if (propertyValue)
        {
            propertyValue = placeholderHelper.replacePlaceholders(propertyValue, globalProperties);
        }

        // native JS strings are always preferrable
        if (propertyValue !== undefined && propertyValue !== null)
        {
            propertyValue = String(propertyValue);
        }
        else if (defaultValue !== undefined)
        {
            propertyValue = defaultValue;
        }
        
        return propertyValue;
    };

    return propertyGetter;
}

/* exported buildRenditionService2Model */
function buildRenditionService2Model()
{
    var ctxt, renditionDefinitionRegistry, renditionDefinitions, renditionNames, renditionName, propertyGetter;

    ctxt = Packages.org.springframework.web.context.ContextLoader.getCurrentWebApplicationContext();
    renditionDefinitionRegistry = ctxt.getBean('renditionDefinitionRegistry2', Packages.org.alfresco.repo.rendition2.RenditionDefinitionRegistry2);

    renditionDefinitions = {};
    // Set is weird to handle in JS
    renditionNames = renditionDefinitionRegistry.renditionNames.iterator();
    while (renditionNames.hasNext())
    {
        renditionName = renditionNames.next();
        renditionDefinitions[renditionName] = renditionDefinitionRegistry.getRenditionDefinition(renditionName);
    }
    model.renditionDefinitions = renditionDefinitions;

    propertyGetter = buildPropertyGetter(ctxt);

    // in ACS 6.x there is actually a switching facade
    model.hasLocalTransformClient = ctxt.containsBean('localTransformClientImpl') || ctxt.containsBean('localTransformClient');
    model.localTransformEnabled = propertyGetter('local.transform.service.enabled', 'false') === 'true';
    model.hasLegacyTransformClient = ctxt.containsBean('legacyTransformClient');
    model.legacyTransformEnabled = propertyGetter('legacy.transform.service.enabled', 'false') === 'true';
    model.hasSynchronousTransformClient = ctxt.containsBean('synchronousTransformClient');
    model.hasRemoteTransformClient = ctxt.containsBean('remoteTransformClient');
    model.remoteTransformEnabled = propertyGetter('transform.service.enabled', 'false') === 'true';
}

function toTransformOptionModel(optionGroup)
{
    var model, options, option;

    if (optionGroup && optionGroup.transformOptions)
    {
        model = {
            required: optionGroup.required,
            groupElements: []
        };

        options = optionGroup.transformOptions.iterator();
        while (options.hasNext())
        {
            option = options.next();
            if (option.name)
            {
                model.groupElements.push({
                    name: option.name,
                    required: option.required
                });
            }
            else if (option.transformOptions)
            {
                model.groupElements.push(toTransformOptionModel(option));
            }
        }
    }
    else
    {
        model = null;
    }

    return model;
}

function buildTransformServiceRegistryModel(registry)
{
    var model, data, transformCountsByTransformer, transformsByTransformer, optionsByTransformer, extractableTypes, embeddableTypes, nonPassthroughSourceTypes, nonPassthroughTargetTypes, transformsBySourceAndTarget, transformsByTarget, transforms, transform, sourceMimetype, targetMimetype, idx, transformerName, sorter;

    data = registry.data;

    transformCountsByTransformer = {};
    transformsByTransformer = {};
    optionsByTransformer = {};

    extractableTypes = {};
    embeddableTypes = {};
    nonPassthroughSourceTypes = {};
    nonPassthroughTargetTypes = {};

    transformsBySourceAndTarget = data.transforms;
    for (sourceMimetype in transformsBySourceAndTarget)
    {
        // Maps are weird to handle in JS (cannot use containsKey, typeof, Object.hasOwnProperty etc)
        if (transformsBySourceAndTarget[sourceMimetype] !== null)
        {
            transformsByTarget = transformsBySourceAndTarget[sourceMimetype];
            for (targetMimetype in transformsByTarget)
            {
                if (transformsByTarget[targetMimetype] !== null)
                {
                    transforms = transformsByTarget[targetMimetype];
                    for (idx = 0; idx < transforms.size(); idx++)
                    {
                        transform = transforms.get(idx);
                        transformerName = String(transform.name);
                        if (String(targetMimetype) === 'alfresco-metadata-extract')
                        {
                            extractableTypes[sourceMimetype] = true;
                        }
                        else if (String(targetMimetype) === 'alfresco-metadata-embed')
                        {
                            embeddableTypes[sourceMimetype] = true;
                        }
                        else if (transformerName !== 'PassThrough')
                        {
                            if (!transformCountsByTransformer.hasOwnProperty(transformerName))
                            {
                                transformCountsByTransformer[transformerName] = 0;
                                transformsByTransformer[transformerName] = {};
                            }

                            transformCountsByTransformer[transformerName] += 1;
                            transformsByTransformer[transformerName][sourceMimetype] = transformsByTransformer[transformerName][sourceMimetype] || [];
                            transformsByTransformer[transformerName][sourceMimetype].push({
                                sourceMimetype: sourceMimetype,
                                targetMimetype: targetMimetype,
                                priority: transform.priority,
                                maxSourceSizeBytes: transform.maxSourceSizeBytes
                            });

                            nonPassthroughSourceTypes[sourceMimetype] = true;
                            nonPassthroughTargetTypes[targetMimetype] = true;

                            if (!optionsByTransformer.hasOwnProperty(transformerName))
                            {
                                optionsByTransformer[transformerName] = toTransformOptionModel(transform.transformOptions);
                            }
                        }
                    }
                }
            }
        }
    }

    model = {
        extractableMimetypes: [].concat(Object.keys(extractableTypes)),
        embeddableMimetypes: [].concat(Object.keys(embeddableTypes)),
        transformSourceMimetypes: [].concat(Object.keys(nonPassthroughSourceTypes)),
        transformTargetMimetypes: [].concat(Object.keys(nonPassthroughTargetTypes)),
        transformCountsByTransformer: transformCountsByTransformer,
        transformsByTransformer: transformsByTransformer,
        transformerNames: [].concat(Object.keys(transformsByTransformer)),
        optionsByTransformer: optionsByTransformer
    };

    sorter = function(a, b)
    {
        return a.localeCompare(b);
    };
    model.extractableMimetypes.sort(sorter);
    model.embeddableMimetypes.sort(sorter);
    model.transformSourceMimetypes.sort(sorter);
    model.transformTargetMimetypes.sort(sorter);
    model.transformerNames.sort(sorter);

    return model;
}

/* exported buildTransformServiceRegistryModels */
function buildTransformServiceRegistryModels()
{
    var ctxt, globalProperties, placeholderHelper, propertyName, localTransformServiceRegistry, localTransformKey, localTransformUrl, remoteTransformServiceRegistry, remoteTransformUrl;

    ctxt = Packages.org.springframework.web.context.ContextLoader.getCurrentWebApplicationContext();
    globalProperties = ctxt.getBean('global-properties', Packages.java.util.Properties);
    placeholderHelper = new Packages.org.springframework.util.PropertyPlaceholderHelper('${', '}', ':', true);

    // in ACS 6.x there is actually a switching facade and we need the real thing
    if (ctxt.containsBean('localTransformServiceRegistryImpl') || ctxt.containsBean('localTransformServiceRegistry'))
    {
        if (ctxt.containsBean('localTransformServiceRegistryImpl')) {
            localTransformServiceRegistry = ctxt.getBean('localTransformServiceRegistryImpl', Packages.org.alfresco.transform.client.registry.TransformServiceRegistry);
        } else  {
            localTransformServiceRegistry = ctxt.getBean('localTransformServiceRegistry', Packages.org.alfresco.transform.client.registry.TransformServiceRegistry);
        }

        // in Alfresco 6.1 Community this is actually a LegacyLocalTransformServiceRegistry without any data
        if (localTransformServiceRegistry.data)
        {
            model.localTransformServiceRegistryModel = buildTransformServiceRegistryModel(localTransformServiceRegistry);

            for (propertyName in globalProperties)
            {
                if (/^localTransform\..+\.url$/.test(propertyName))
                {
                    localTransformUrl = globalProperties[propertyName];
                    if (localTransformUrl)
                    {
                        localTransformUrl = placeholderHelper.replacePlaceholders(localTransformUrl, globalProperties);
                    }

                    if (localTransformUrl)
                    {
                        model.localTransformServiceRegistryModel.remoteUrls = model.localTransformServiceRegistryModel.remoteUrls || {};
                        localTransformKey = propertyName.substring(15, propertyName.length - 4);
                        model.localTransformServiceRegistryModel.remoteUrls[localTransformKey] = localTransformUrl;
                    }
                }
            }
        }
    }

    if (ctxt.containsBean('remoteTransformServiceRegistry'))
    {
        remoteTransformServiceRegistry = ctxt.getBean('remoteTransformServiceRegistry', Packages.org.alfresco.transform.client.registry.TransformServiceRegistry);
        // ACS Community ships with a DummyTransformServiceRegistry for some reason
        // (well, "some reason" = "Alfresco fails to cleanly structure + separate Community + Enterprise")
        if (remoteTransformServiceRegistry.data)
        {
            model.remoteTransformServiceRegistryModel = buildTransformServiceRegistryModel(remoteTransformServiceRegistry);

            remoteTransformUrl = globalProperties['transform.service.url'];
            if (remoteTransformUrl)
            {
                remoteTransformUrl = placeholderHelper.replacePlaceholders(remoteTransformUrl, globalProperties);
            }

            if (remoteTransformUrl)
            {
                model.remoteTransformServiceRegistryModel.remoteUrl = remoteTransformUrl;
            }
        }
    }
}

function getRegistryForKey(registryKey)
{
    var ctxt, registry;

    if (typeof registryKey !== 'string')
    {
        status.setCode(400, 'Registry key must be a string');
    }
    else
    {
        ctxt = Packages.org.springframework.web.context.ContextLoader.getCurrentWebApplicationContext();

        switch (registryKey)
        {
            case 'localTransform':
                // in ACS 6.x there is actually a switching facade and we need the real thing
                registryKey = ctxt.containsBean('localTransformServiceRegistryImpl') ? 'localTransformServiceRegistryImpl' : 'localTransformServiceRegistry';
                break;
            case 'remoteTransform':
                registryKey = 'remoteTransformServiceRegistry';
                break;
        }
        
        if (ctxt.containsBean(registryKey))
        {
            registry = ctxt.getBean(registryKey, Packages.org.alfresco.transform.client.registry.TransformServiceRegistry);
        }
        else
        {
            status.setCode(400, 'Invalid registry key value');
        }
    }
    return registry;
}

/* exported renditionService2findApplicableRenditions */
function renditionService2findApplicableRenditions()
{
    var reqBody, sourceMimetype, sourceSize, ctxt, renditionDefinitionRegistry;

    if (json)
    {
        // web script json is (unwieldly) org.json.JSONObject
        reqBody = JSON.parse(json.toString());
        sourceMimetype = reqBody.sourceMimetype;
        sourceSize = reqBody.sourceSize;

        if (typeof sourceMimetype !== 'string')
        {
            status.setCode(400, 'Source mimetype must be a string');
        }
        else if (typeof sourceSize !== 'number' && !(typeof sourceSize === 'string' && /^-?\d+$/.test(sourceSize)))
        {
            status.setCode(400, 'Source size must be a number or digit-only string');
        }
        else
        {
            ctxt = Packages.org.springframework.web.context.ContextLoader.getCurrentWebApplicationContext();
            renditionDefinitionRegistry = ctxt.getBean('renditionDefinitionRegistry2', Packages.org.alfresco.repo.rendition2.RenditionDefinitionRegistry2);
            model.applicableRenditions = renditionDefinitionRegistry.getRenditionNamesFrom(sourceMimetype, sourceSize);
        }
    }
    else
    {
        status.setCode(400, 'Operation requires JSON request body');
    }
}

/* exported renditionService2findTransformDetails */
function renditionService2findTransformDetails()
{
    var reqBody, registryKey, sourceMimetype, sourceSize, targetMimetype, options, registry;

    if (json)
    {
        // web script json is (unwieldly) org.json.JSONObject
        reqBody = JSON.parse(json.toString());
        registryKey = reqBody.registryKey;
        sourceMimetype = reqBody.sourceMimetype;
        sourceSize = reqBody.sourceSize || -1;
        targetMimetype = reqBody.targetMimetype;
        options = reqBody.options;

        if (typeof sourceMimetype !== 'string')
        {
            status.setCode(400, 'Source mimetype must be a string');
        }
        else if (typeof sourceSize !== 'number' && !(typeof sourceSize === 'string' && /^-?\d+$/.test(sourceSize)))
        {
            status.setCode(400, 'Source size must be a number or digit-only string');
        }
        else if (typeof targetMimetype !== 'string')
        {
            status.setCode(400, 'Target mimetype must be a string');
        }
        else if (typeof options !== 'undefined' && options !== null && typeof options !== 'object')
        {
            status.setCode(400, 'Options must be an object');
        }
        else
        {
            registry = getRegistryForKey(registryKey);
            if (registry)
            {
                // not available in all ACS 6.x
                if (typeof registry.findTransformers === 'function') {
                    model.transformers = registry.findTransformers(sourceMimetype, targetMimetype, options, null);
                }
                model.maxSourceSizeBytes = registry.findMaxSize(sourceMimetype, targetMimetype, options, null);
                model.transformerName = registry.findTransformerName(sourceMimetype, parseInt(sourceSize), targetMimetype, options, null);
            }
        }
    }
    else
    {
        status.setCode(400, 'Operation requires JSON request body');
    }
}

function renditionService2callProbeLocalTransform(localTransformName, liveProbe)
{
    var ctxt, globalProperties, placeholderHelper, localTransformUrl, httpGet, httpClient, httpResponse, httpResponseEntity;

    ctxt = Packages.org.springframework.web.context.ContextLoader.getCurrentWebApplicationContext();
    globalProperties = ctxt.getBean('global-properties', Packages.java.util.Properties);
    placeholderHelper = new Packages.org.springframework.util.PropertyPlaceholderHelper('${', '}', ':', true);
    
    localTransformUrl = globalProperties['localTransform.' + localTransformName + '.url'];
    if (localTransformUrl)
    {
        localTransformUrl = placeholderHelper.replacePlaceholders(localTransformUrl, globalProperties);
    }

    if (localTransformUrl)
    {
        if (localTransformUrl.lastIndexOf('/') !== localTransformUrl.length - 1)
        {
            localTransformUrl += '/';
        }
        model.localTransformName = localTransformName;
        model.localTransformUrl = localTransformUrl;
        try
        {
            // Note: Cannot use RemoteTransformerClient for checking availability via its check() operation
            // once successfully checked, RemoteTransformerClient forever considers transformers to be available
            // unless an actual transformation request encounters an arbitrary IO error
            // This means we cannot run explicit probes via the client outside of that default availability check
            // Also, client does not perform an actual probe - it only loads the version without checking if (test) transformations actually work

            httpGet = Packages.org.apache.http.client.methods.HttpGet(localTransformUrl + (liveProbe ? 'live' : 'ready'));
            httpClient = Packages.org.apache.http.impl.client.HttpClients.createDefault();
            httpResponse = httpClient.execute(httpGet);
            httpResponseEntity = httpResponse.entity;

            model.probeSuccessful = httpResponse.statusLine.statusCode === 200;
            model.probeResponse = Packages.org.apache.http.util.EntityUtils.toString(httpResponseEntity);
        }
        catch (e)
        {
            model.probeSuccessful = false;
            model.errorMessage = e.message;
        }
        
        if (httpResponseEntity)
        {
            try
            {
                Packages.org.apache.http.util.EntityUtils.consume(httpResponseEntity);
            }
            catch (ignore)
            {
                // NO-OP - just prevent cleanup issues from failing whole operation
            }
        }

        if (httpResponse)
        {
            try
            {
                httpResponse.close();
            }
            catch (ignore)
            {
                // NO-OP - just prevent cleanup issues from failing whole operation
            }
        }

        if (httpClient)
        {
            try
            {
                httpClient.close();
            }
            catch (ignore)
            {
                // NO-OP - just prevent cleanup issues from failing whole operation
            }
        }
    }
    else
    {
        status.setCode(404, 'No URL was configured for local transform ' + localTransformName);
    }
}

/* exported renditionService2probeLocalTransform */
function renditionService2probeLocalTransform()
{
    var reqBody, localTransformName, live;

    if (json)
    {
        // web script json is (unwieldly) org.json.JSONObject
        reqBody = JSON.parse(json.toString());
        localTransformName = reqBody.localTransformName;
        live = reqBody.live;

        if (typeof localTransformName !== 'string')
        {
            status.setCode(400, 'Name of local transform must be a string');
        }
        else
        {
            renditionService2callProbeLocalTransform(localTransformName, live === true || live === 'true');
        }
        
    }
}

/* exported renditionService2retrieveLocalTransformLogs */
function renditionService2retrieveLocalTransformLogs(localTransformName)
{
    var ctxt, globalProperties, placeholderHelper, localTransformUrl, httpGet, httpClient, httpResponse, httpResponseEntity, logResponse, tableStartIdx, tableEndIdx;

    ctxt = Packages.org.springframework.web.context.ContextLoader.getCurrentWebApplicationContext();
    globalProperties = ctxt.getBean('global-properties', Packages.java.util.Properties);
    placeholderHelper = new Packages.org.springframework.util.PropertyPlaceholderHelper('${', '}', ':', true);
    
    localTransformUrl = globalProperties['localTransform.' + localTransformName + '.url'];
    if (localTransformUrl)
    {
        localTransformUrl = placeholderHelper.replacePlaceholders(localTransformUrl, globalProperties);
    }

    if (localTransformUrl)
    {
        if (localTransformUrl.lastIndexOf('/') !== localTransformUrl.length - 1)
        {
            localTransformUrl += '/';
        }
        model.localTransformName = localTransformName;
        model.localTransformUrl = localTransformUrl;
        try
        {
            httpGet = Packages.org.apache.http.client.methods.HttpGet(localTransformUrl + '/log');
            httpClient = Packages.org.apache.http.impl.client.HttpClients.createDefault();
            httpResponse = httpClient.execute(httpGet);
            httpResponseEntity = httpResponse.entity;

            if (httpResponse.statusLine.statusCode === 200)
            {
                logResponse = Packages.org.apache.http.util.EntityUtils.toString(httpResponseEntity);
                tableStartIdx = logResponse.indexOf('<table>');
                tableEndIdx = logResponse.indexOf('</table>', tableStartIdx);

                if (tableStartIdx !== -1 && tableEndIdx !== -1)
                {
                    model.logTable = logResponse.substring(tableStartIdx, tableEndIdx);
                }
            }
        }
        catch (e)
        {
            model.errorMessage = e.message;
        }
        
        if (httpResponseEntity)
        {
            try
            {
                Packages.org.apache.http.util.EntityUtils.consume(httpResponseEntity);
            }
            catch (ignore)
            {
                // NO-OP - just prevent cleanup issues from failing whole operation
            }
        }

        if (httpResponse)
        {
            try
            {
                httpResponse.close();
            }
            catch (ignore)
            {
                // NO-OP - just prevent cleanup issues from failing whole operation
            }
        }

        if (httpClient)
        {
            try
            {
                httpClient.close();
            }
            catch (ignore)
            {
                // NO-OP - just prevent cleanup issues from failing whole operation
            }
        }
    }
    else
    {
        status.setCode(404, 'No URL was configured for local transform ' + localTransformName);
    }
}