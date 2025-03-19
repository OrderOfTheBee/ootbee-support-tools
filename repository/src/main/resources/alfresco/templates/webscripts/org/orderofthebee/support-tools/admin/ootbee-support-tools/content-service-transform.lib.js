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

/* exported supportsContentServiceTransformers */
function supportsContentServiceTransformers()
{
    var ctxt, contentService, supported;

    ctxt = Packages.org.springframework.web.context.ContextLoader.getCurrentWebApplicationContext();
    contentService = ctxt.getBean('ContentService', Packages.org.alfresco.service.cmr.repository.ContentService);
    supported = typeof contentService.getTransformer === 'function';

    return supported;
}

function buildTransformerNames()
{
    var ctxt, transformerDebug, transformers, transformerNames, i, transformerName, ArrayList; 

    ctxt = Packages.org.springframework.web.context.ContextLoader.getCurrentWebApplicationContext();
    transformerDebug = ctxt.getBean('transformerDebug', Packages.org.alfresco.repo.content.transform.TransformerDebug);

    ArrayList = Packages.java.util.ArrayList;
    transformers = new ArrayList(transformerDebug.sortTransformersByName(null));
    transformerNames = [];

    for (i = 0; i < transformers.size(); i++)
    {
        transformerName = String(transformers.get(i).name);
        if (/^transformer\..+/.test(transformerName))
        {
            transformerName = transformerName.substring(12);
        }
        transformerNames.push(transformerName);
    }

    model.transformerNames = transformerNames;
}

/* exported buildExtensionsAndMimetypes */
function buildExtensionsAndMimetypes()
{
    var ctxt, mimetypeService, extensionsAndMimetypes, mimetypes, mimetype, i;

    ctxt = Packages.org.springframework.web.context.ContextLoader.getCurrentWebApplicationContext();
    mimetypeService = ctxt.getBean('MimetypeService', Packages.org.alfresco.service.cmr.repository.MimetypeService);

    extensionsAndMimetypes = [];
    mimetypes = mimetypeService.getMimetypes(null);
    for (i = 0; i < mimetypes.size(); i++)
    {
        mimetype = mimetypes.get(i);
        extensionsAndMimetypes.push({
            mimetype : String(mimetype),
            extension : String(mimetypeService.getExtension(mimetype))
        });
    }
    extensionsAndMimetypes.sort(function(a, b)
    {
        var result = a.extension.localeCompare(b.extension);
        return result;
    });

    model.extensionsAndMimetypes = extensionsAndMimetypes;
}

/* exported getProperties */
function getProperties()
{
    var ctxt, transformerConfig, changesOnly;
    
    ctxt = Packages.org.springframework.web.context.ContextLoader.getCurrentWebApplicationContext();
    transformerConfig = ctxt.getBean('transformerConfig', Packages.org.alfresco.repo.content.transform.TransformerConfig);
    
    changesOnly = args.arg0 === 'true';
    
    model.message = transformerConfig.getProperties(changesOnly);
    model.header = msg.get('test-transform.detail.getProperties.heading', [msg.get(changesOnly ? 'boolean.yes' : 'boolean.no')]);
}

/* exported setProperties */
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
    
    try
    {
        model.message = transformerConfig.setProperties(input);
    }
    catch (e)
    {
        model.message = e.message;
    }
    model.headerKey = 'setProperties.heading';
}

/* exported removeProperties */
function removeProperties()
{
    var ctxt, transformerConfig, input;
    
    ctxt = Packages.org.springframework.web.context.ContextLoader.getCurrentWebApplicationContext();
    transformerConfig = ctxt.getBean('transformerConfig', Packages.org.alfresco.repo.content.transform.TransformerConfig);
    
    input = args.arg0;
    
    if (input !== null && String(input).length > 0)
    {
        try
        {
            model.message = transformerConfig.removeProperties(input);
        }
        catch (e)
        {
            model.message = e.message;
        }
    }
    else
    {
        model.messageKey = 'removeProperties.message';
    }
    model.header = msg.get('test-transform.detail.removeProperties.heading', [input]);
}

function getLogEntries(logName)
{
    var ctxt, TransformersSubsystemContextFactory, transformerDebugLog, logEntries, i, log = '';
    
    ctxt = Packages.org.springframework.web.context.ContextLoader.getCurrentWebApplicationContext();
    
    // unfortunately the regular subsystem proxy is exposed with a too narrow interface
    // need to lookup the actual bean directly
    TransformersSubsystemContextFactory = ctxt.getBean('Transformers', Packages.org.alfresco.repo.management.subsystems.ApplicationContextFactory);
    transformerDebugLog = TransformersSubsystemContextFactory.applicationContext.getBean(logName, Packages.org.alfresco.repo.content.transform.LogEntries);
    
    logEntries = transformerDebugLog.getEntries(100);
    for (i = 0; i < logEntries.length; i++)
    {
        if (log.length > 0)
        {
           log += '\n'; 
        }
        log += logEntries[i];
    }
    
    return log;
}

/* exported getTransformationDebugLog */
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

/* exported getTransformationLog */
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

/* exported getTransformerNames */
function getTransformerNames()
{
    var i, transformerNames;
    
    buildTransformerNames();
    
    transformerNames = '';
    
    for (i = 0; i < model.transformerNames.length; i++)
    {
        if (transformerNames.length > 0)
        {
            transformerNames += '\n';
        }
        transformerNames += model.transformerNames[i];
    }
    
    model.message = transformerNames;
    model.headerKey = 'getTransformerNames.heading';
}

function getEffectiveTransformerName(input, transformerRegistry)
{
    var transformerName = input;
    if (transformerName !== null)
    {
        if (transformerName === '')
        {
            transformerName = null;
        }
        else if (!/^transformer\..+$/.test(transformerName))
        {
            transformerName = 'transformer.' + transformerName;
        }
    }
    transformerRegistry.getTransformer(transformerName);
    return transformerName;
}

function getEffectiveExtension(input)
{
    var extension = input;
    if (extension !== null)
    {
        if (extension === '')
        {
            extension = null;
        }
        else
        {
            extension = extension.toLowerCase();
        }
    }
    
    return extension;
}

// we can't access TransformerConfigMBeanImpl here since that isn't exposed from the subsystem (and may in the future be Enterprise only)
// getTransformationStatistics functions effectively duplicate the code

function getTransformationStatisticsLowLevel(sb, transformer, sourceMimetype, targetMimetype, counter, includeSystemWideSummary,
        transformerConfig)
{
    var statistics, count;

    statistics = transformerConfig.getStatistics(transformer, sourceMimetype, targetMimetype, false);
    if (statistics !== null)
    {
        count = statistics.count;
        if (count > 0)
        {
            if (sb.length() > 0)
            {
                sb.append('\n');
            }

            if (counter.incrementAndGet() === 1 && includeSystemWideSummary)
            {
                sb.append('\n');
            }

            sb.append(statistics.getTransformerName());
            sb.append(' ');
            sb.append(statistics.getSourceExt());
            sb.append(' ');
            sb.append(statistics.getTargetExt());
            sb.append(" count=");
            sb.append(count);
            sb.append(" errors=");
            sb.append(statistics.getErrorCount());
            sb.append(" averageTime=");
            sb.append(statistics.getAverageTime());
            sb.append(" ms");
        }
    }
}

function getTransformationStatisticsHighLevel(sourceExtension, targetExtension, sb, transformer, sourceMimetypes, targetMimetypes,
        includeSystemWideSummary, transformerConfig)
{
    var AtomicInteger, StringBuilder, counter, lengthAtStart, sb2, i, j, sourceMimetype, targetMimetype;

    AtomicInteger = Packages.java.util.concurrent.atomic.AtomicInteger;
    StringBuilder = Packages.java.lang.StringBuilder;
    counter = new AtomicInteger(0);
    lengthAtStart = sb.length();

    for (i = 0; i < sourceMimetypes.length; i++)
    {
        sourceMimetype = sourceMimetypes[i];
        for (j = 0; j < targetMimetypes.length; j++)
        {
            targetMimetype = targetMimetypes[j];

            getTransformationStatisticsLowLevel(sb, transformer, sourceMimetype, targetMimetype, counter, includeSystemWideSummary,
                    transformerConfig);
        }
    }

    if (sourceExtension === null && targetExtension === null && counter.get() > 1)
    {
        sb2 = new StringBuilder();
        getTransformationStatisticsLowLevel(sb2, transformer, null, null, counter, includeSystemWideSummary, transformerConfig);
        sb2.append('\n');
        sb.insert(lengthAtStart === 0 ? 0 : lengthAtStart + 2, sb2);
    }
}

/* exported getTransformationStatistics */
function getTransformationStatistics()
{
    var ctxt, transformerDebug, transformerConfig, transformerRegistry, ArrayList, StringBuilder, transformerName, sourceExtension, targetExtension, transformers, sourceMimetypes, targetMimetypes, includeSystemWideSummary, sb, i;

    ctxt = Packages.org.springframework.web.context.ContextLoader.getCurrentWebApplicationContext();
    transformerDebug = ctxt.getBean('transformerDebug', Packages.org.alfresco.repo.content.transform.TransformerDebug);
    transformerConfig = ctxt.getBean('transformerConfig', Packages.org.alfresco.repo.content.transform.TransformerConfig);
    transformerRegistry = ctxt.getBean('contentTransformerRegistry', Packages.org.alfresco.repo.content.transform.ContentTransformerRegistry);
    ArrayList = Packages.java.util.ArrayList;
    StringBuilder = Packages.java.lang.StringBuilder;

    transformerName = getEffectiveTransformerName(args.arg0, transformerRegistry);
    sourceExtension = getEffectiveExtension(args.arg1);
    targetExtension = getEffectiveExtension(args.arg2);

    try
    {
        transformers = new ArrayList(transformerDebug.sortTransformersByName(transformerName));
        sourceMimetypes = new ArrayList(transformerDebug.getSourceMimetypes(sourceExtension));
        targetMimetypes = new ArrayList(transformerDebug.getTargetMimetypes(sourceExtension, targetExtension, sourceMimetypes));
    
        sb = new StringBuilder();
        includeSystemWideSummary = transformerName === null;
        if (includeSystemWideSummary)
        {
            getTransformationStatisticsHighLevel(sourceExtension, targetExtension, sb, null, sourceMimetypes, targetMimetypes, false,
                    transformerConfig);
        }
    		
    		
        for (i = 0; i < transformers.length; i++)
        {
            getTransformationStatisticsHighLevel(sourceExtension, targetExtension, sb, transformers[i], sourceMimetypes, targetMimetypes,
                    includeSystemWideSummary, transformerConfig);
        }

        if (sb.length() === 0)
        {
            model.messageKey = 'getTransformationStatistics.noEntries';
        }
        else
        {
            model.message = sb.toString();
        }
    }
    catch (e)
    {
        model.message = e.message;
    }
    model.header = msg.get('test-transform.detail.getTransformationStatistics.heading', [transformerName, sourceExtension, targetExtension]);
}

/* exported testTransform */
function testTransform()
{
    var ctxt, transformerDebug, transformerRegistry, transformerName, sourceExtension, targetExtension, use;

    ctxt = Packages.org.springframework.web.context.ContextLoader.getCurrentWebApplicationContext();
    transformerDebug = ctxt.getBean('transformerDebug', Packages.org.alfresco.repo.content.transform.TransformerDebug);
    transformerRegistry = ctxt.getBean('contentTransformerRegistry',
            Packages.org.alfresco.repo.content.transform.ContentTransformerRegistry);

    try
    {
        transformerName = getEffectiveTransformerName(args.arg0, transformerRegistry);
        sourceExtension = getEffectiveExtension(args.arg1);
        targetExtension = getEffectiveExtension(args.arg2);
        use = args.arg3 === '' ? null : args.arg3;

        model.message = transformerName === null ? transformerDebug.testTransform(sourceExtension, targetExtension, use) : transformerDebug
                .testTransform(transformerName, sourceExtension, targetExtension, use);
    }
    catch (e)
    {
        model.message = e.message;
    }
    model.header = msg.get('test-transform.detail.testTransform.heading', [ args.arg0, args.arg1, args.arg2, args.arg3 ]);
}

/* exported getTransformationsByExtension */
function getTransformationsByExtension()
{
    var ctxt, transformerDebug, sourceExtension, targetExtension, use;

    ctxt = Packages.org.springframework.web.context.ContextLoader.getCurrentWebApplicationContext();
    transformerDebug = ctxt.getBean('transformerDebug', Packages.org.alfresco.repo.content.transform.TransformerDebug);

    sourceExtension = getEffectiveExtension(args.arg0);
    targetExtension = getEffectiveExtension(args.arg1);
    use = args.arg2 === '' ? null : args.arg2;

    try
    {
        model.message = transformerDebug.transformationsByExtension(sourceExtension, targetExtension, true, true, false, use);
    }
    catch (e)
    {
        model.message = e.message;
    }
    model.header = msg.get('test-transform.detail.getTransformationsByExtension.heading', [ sourceExtension, targetExtension, use ]);
}

/* exported getTransformationsByTransformer */
function getTransformationsByTransformer()
{
    var ctxt, transformerDebug, transformerName, use, transformerRegistry;

    ctxt = Packages.org.springframework.web.context.ContextLoader.getCurrentWebApplicationContext();
    transformerDebug = ctxt.getBean('transformerDebug', Packages.org.alfresco.repo.content.transform.TransformerDebug);
    transformerRegistry = ctxt.getBean('contentTransformerRegistry',
            Packages.org.alfresco.repo.content.transform.ContentTransformerRegistry);

    use = args.arg1 === '' ? null : args.arg1;

    try
    {
        transformerName = getEffectiveTransformerName(args.arg0, transformerRegistry);
        model.message = transformerDebug.transformationsByTransformer(transformerName, true, true, use);
    }
    catch (e)
    {
        model.message = e.message;
    }
    model.header = msg.get('test-transform.detail.getTransformationsByTransformer.heading', [ args.arg0, use ]);
}