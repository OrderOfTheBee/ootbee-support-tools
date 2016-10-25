/**
 * Copyright (C) 2016 Axel Faust / Markus Joos
 * Copyright (C) 2016 Order of the Bee
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
 * Linked to Alfresco Copyright (C) 2005-2016 Alfresco Software Limited.
 */

/* global formdata: false */

function buildLoggerState(logger)
{
    var RootLogger, loggerState, allAppenders, appender;

    RootLogger = Packages.org.apache.log4j.spi.RootLogger;
    
    loggerState = {
        name : logger.name,
        isRoot : logger instanceof RootLogger,
        parent : logger.parent !== null ? logger.parent.name : null,
        parentIsRoot : logger.parent instanceof RootLogger,
        level : logger.level !== null ? String(logger.level) : null,
        effectiveLevel : String(logger.effectiveLevel),
        additivity : logger.additivity,
        appenders : []
    };

    allAppenders = logger.allAppenders;
    while (allAppenders.hasMoreElements())
    {
        appender = allAppenders.nextElement();

        loggerState.appenders.push({
            name : appender.name
        });
    }

    return loggerState;
}

/* exported buildLoggerStates */
function buildLoggerStates(showUnconfiguredLoggers)
{
    var loggerRepository, loggerStates, loggerState, currentLoggers, logger;

    loggerRepository = Packages.org.apache.log4j.LogManager.getLoggerRepository();

    loggerStates = [];

    currentLoggers = loggerRepository.currentLoggers;
    while (currentLoggers.hasMoreElements())
    {
        logger = currentLoggers.nextElement();

        if (logger.level !== null || showUnconfiguredLoggers)
        {
            loggerState = buildLoggerState(logger);
            loggerStates.push(loggerState);
        }
    }

    loggerStates.sort(function(a, b)
    {
        return a.name.localeCompare(b.name);
    });

    loggerState = buildLoggerState(loggerRepository.rootLogger);
    loggerStates.splice(0, 0, loggerState);

    model.loggerStates = loggerStates;
}

function changeLoggerState(loggerName, level)
{
    var logger;
    
    if (loggerName === '-root-')
    {
        logger = Packages.org.apache.log4j.Logger.getRootLogger();
    }
    else
    {
        logger = Packages.org.apache.log4j.Logger.getLogger(loggerName);
    }
    
    if (String(level) === '' || String(level) === 'UNSET')
    {
        logger.setLevel(null);
    }
    else
    {
        logger.setLevel(Packages.org.apache.log4j.Level.toLevel(level));
    }
}

/* exported processLoggerStateChangeFromFormData */
function processLoggerStateChangeFromFormData()
{
    var fields, field, i, loggerName, level, showUnconfiguredLoggers;

    fields = formdata.fields;
    for (i = 0; i < fields.length; i++)
    {
        field = fields[i];
        switch (String(field.name))
        {
            case 'logger':
                loggerName = String(field.value);
                break;
            case 'level':
                level = String(field.value);
                break;
            case 'showUnconfiguredLoggers':
                showUnconfiguredLoggers = String(field.value);
                break;
            default:
                logger.debug('Unknown field: ' + field.name);
        }
    }

    changeLoggerState(loggerName, level);

    return showUnconfiguredLoggers;
}

/* exported registerTailingAppender */
function registerTailingAppender()
{
    var uuid, appender, rootLogger;

    uuid = String(Packages.java.util.UUID.randomUUID());
    appender = new Packages.org.orderofthebee.addons.support.tools.repo.LimitedListAppender(uuid, 10000);
    rootLogger = Packages.org.apache.log4j.Logger.getRootLogger();
    appender.registerAsAppender(rootLogger);

    model.uuid = uuid;
}

/* exported retrieveTailingEvents */
function retrieveTailingEvents()
{
    var uuid, rootLogger, appender;

    uuid = String(args.uuid || '');

    if (uuid !== '')
    {
        rootLogger = Packages.org.apache.log4j.Logger.getRootLogger();
        appender = rootLogger.getAppender(uuid);

        if (appender !== null)
        {
            model.events = appender.retrieveLogEvents();
        }
    }
}

/**
 * Builds a generic script model from a logger-/appender-related Java object.
 * 
 * @param javaObject the javaObject to transform into a script model
 * @returns the script model for the javaObject
 */
function buildGenericJavaObjectModel(javaObject)
{
    var key, value, model;
    
    model = {};
    
    if (javaObject.name !== undefined)
    {
        model.name = javaObject.name;
    }
    
    if (javaObject.class !== undefined)
    {
        model.class = javaObject.class.name;
    }
    
    for (key in javaObject)
    {
        if (javaObject[key] !== undefined && javaObject[key] !== null && typeof javaObject[key] !== 'function')
        {
            key = String(key);
            if (key !== 'name' && key !== 'class')
            {
                value = javaObject[key];
                // TODO Add cases for other "expected" complex Java types for recursion
                if (value instanceof Packages.org.apache.log4j.Layout)
                {
                    model[key] = buildGenericJavaObjectModel(value);
                }
                else
                {
                    model[key] = value;
                }
            }
        }
    }
    
    return model;
}

function buidAppenderModelsForLogger(logger, appenders)
{
    var allAppenders;
    
    if (logger.additivity && logger.parent !== null)
    {
        buidAppenderModelsForLogger(logger.parent, appenders);
    }
    
    allAppenders = logger.allAppenders;
    while (allAppenders.hasMoreElements())
    {
        appenders.push(buildGenericJavaObjectModel(allAppenders.nextElement()));
    }
}

/* exported buildAppenderModel */
function buildAppenderModel(loggerName)
{
    var logger, appenders;
    
    if (loggerName === '-root-')
    {
        logger = Packages.org.apache.log4j.Logger.getRootLogger();
    }
    else
    {
        logger = Packages.org.apache.log4j.Logger.getLogger(loggerName);
    }
    
    appenders = [];
    buidAppenderModelsForLogger(logger, appenders);
    
    model.logger = loggerName;
    model.appenders = appenders;
}