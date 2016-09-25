/**
 * Copyright (C) 2016 Axel Faust / Markus Joos
 * Copyright (C) 2016 Order of the Bee
 *
 * This file is part of Community Support Tools
 *
 * Community Support Tools is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Community Support Tools is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Community Support Tools. If not, see <http://www.gnu.org/licenses/>.
 */
/*
 * Linked to Alfresco
 * Copyright (C) 2005-2016 Alfresco Software Limited.
 */

function buildTransformerNames()
{
    var ctxt, transformerDebug, transformerNames, transformers, idx, transformerName, ArrayList; 
    
    ctxt = Packages.org.springframework.web.context.ContextLoader.getCurrentWebApplicationContext();
    transformerDebug = ctxt.getBean('transformerDebug', Packages.org.alfresco.repo.content.transform.TransformerDebug);
    
    ArrayList = Packages.java.util.ArrayList;
    transformerNames = new ArrayList(transformerDebug.sortTransformersByName(null));
    for (idx = 0; idx < transformerNames.length; idx++)
    {
        transformerName = String(transformerNames.get(idx));
        if (/^transformer\..+/.test(transformerName))
        {
            transformerName = transformerName.substring(12);
            transformerNames.set(idx, transformerName);
        }
    }
    
    model.transformerNames = transformerNames;
}

function buildExtensionsAndMimetypes()
{
    var ctxt, mimetypeService, extensionsByMimetype, mimetypes, mimetype, idx;
    
    ctxt = Packages.org.springframework.web.context.ContextLoader.getCurrentWebApplicationContext();
    mimetypeService = ctxt.getBean('MimetypeService', Packages.org.alfresco.service.cmr.repository.MimetypeService);
    
    extensionsByMimetypes = {};
    mimetypes = mimetypeService.getMimetypes(null);
    for (idx = 0; idx < mimetypes.size(); idx++)
    {
        mimetype = mimetypes.get(idx);
        extensionsByMimetypes[mimetype] = mimetypeService.getExtension(mimetype);
    }
    
    model.extensionsByMimetype = extensionsByMimetypes;
}

function getProperties()
{
    var ctxt, transformerConfig, changesOnly;
    
    ctxt = Packages.org.springframework.web.context.ContextLoader.getCurrentWebApplicationContext();
    transformerConfig = ctxt.getBean('transformerConfig', Packages.org.alfresco.repo.content.transform.TransformerConfig);
    
    changesOnly = args.arg0 == 'true';
    
    model.message = transformerConfig.getProperties(changesOnly);
    model.header = msg.get('test-transform.details.getProperties.heading', [msg.get(changesOnly ? 'boolean.yes' : 'boolean.no')]);
}

function setProperties()
{
    var ctxt, transformerConfig, input;
    
    ctxt = Packages.org.springframework.web.context.ContextLoader.getCurrentWebApplicationContext();
    transformerConfig = ctxt.getBean('transformerConfig', Packages.org.alfresco.repo.content.transform.TransformerConfig);
    
    input = args.arg0;
    // for some reason the parameter may not be URI-decoded
    if (input.indexOf('%3D') !== -1 || input.indexOf('%3d') !== -1)
    {
        input = decodeURIComponent(input);
    }
    
    model.message = transformerConfig.setProperties(input);
    model.header = 'setProperties.heading';
}

function removeProperties()
{
    var ctxt, transformerConfig, input;
    
    ctxt = Packages.org.springframework.web.context.ContextLoader.getCurrentWebApplicationContext();
    transformerConfig = ctxt.getBean('transformerConfig', Packages.org.alfresco.repo.content.transform.TransformerConfig);
    
    input = args.arg0;
    
    if (input !== null && String(input).length > 0)
    {
        model.message = transformerConfig.removeProperties(input);
    }
    else
    {
        model.messageKey = 'removeProperties.message';
    }
    model.headerKey = 'removeProperties.heading';
}

function getLogEntries(logName)
{
    var ctxt, transformerDebugLog, logEntries, idx, log = '';
    
    ctxt = Packages.org.springframework.web.context.ContextLoader.getCurrentWebApplicationContext();
    transformerDebugLog = ctxt.getBean('logName', Packages.org.alfresco.repo.content.transform.TransformerLogger);
    
    logEntries = transformerDebugLog.getEntries(100);
    for (idx = 0; idx < logEntries.length; idx++)
    {
        if (log.length > 0)
        {
           log += '\n'; 
        }
        log += logEntries[idx];
    }
    
    return log;
}

function getTransformationDebugLog()
{
    var log = getLogEntries('transformerDebugLog');
    
    if (log.length > 0)
    {
        model.message = log;
    }
    else
    {
        model.messageKey = 'getTransformationDebugLog.noEntries';
    }
    model.headerKey = 'getTransformationDebugLog.heading';
}

function getTransformationLog()
{
    var log = getLogEntries('transformerLog');
    
    if (log.length > 0)
    {
        model.message = log;
    }
    else
    {
        model.messageKey = 'getTransformationLog.noEntries';
    }
    model.headerKey = 'getTransformationLog.heading';
}

function getTransformerNames()
{
    var idx, transformerNames;
    
    buildTransformerNames();
    
    transformerNames = '';
    for (idx = 0; idx < model.transfomerNames.length; idx++)
    {
        if (transformerNames.length > 0)
        {
            transformerNames += '\n';
        }
        transformerNames += model.transformerNames[idx];
    }
    
    model.message = transformerNames;
    model.headerKey = 'getTransformerNames.heading';
}