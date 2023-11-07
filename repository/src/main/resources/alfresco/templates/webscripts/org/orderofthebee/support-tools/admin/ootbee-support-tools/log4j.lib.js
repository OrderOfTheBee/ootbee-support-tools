/**
 * Copyright (C) 2016 - 2023 Order of the Bee
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
 * Copyright (C) 2005 - 2023 Alfresco Software Limited.
 */

function buildLoggerState(logger)
{
    var loggerState, appenderNames, idx;

    loggerState = {
        name : logger.name,
        isRoot : logger.root,
        parent : logger.parentName,
        parentIsRoot : logger.parentRoot,
        level : logger.level,
        effectiveLevel : logger.effectiveLevel,
        additivity : logger.additivity,
        canBeReset : Packages.org.orderofthebee.addons.support.tools.repo.log.Log4jCompatibilityUtils.canBeReset(logger.name),
        appenders : []
    };

    appenderNames = logger.appenderNames;
    for (idx = 0; idx < appenderNames.size(); idx++)
    {
        loggerState.appenders.push({
            name : appenderNames.get(idx)
        });
    }

    return loggerState;
}

/* exported buildLoggerStates */
function buildLoggerStates(showUnconfiguredLoggers, loggerNamePattern, skipCount, maxItems)
{
    var loggers, loggerStates, idx, loggerState;

    loggers = Packages.org.orderofthebee.addons.support.tools.repo.log.Log4jCompatibilityUtils.LOG4J_HELPER.getLoggers(loggerNamePattern, showUnconfiguredLoggers);
    loggerStates = [];

    for (idx = 0; idx < loggers.size(); idx++)
    {
        loggerState = buildLoggerState(loggers.get(idx));
        loggerStates.push(loggerState);
    }

    loggerStates.sort(function(a, b)
    {
        return a.name.localeCompare(b.name);
    });
    
    model.totalRecords = loggerStates.length;
    model.startIndex = 0;
    if (skipCount)
    {
        loggerStates.splice(0, parseInt(skipCount, 10));
        model.startIndex = parseInt(skipCount, 10);
    }
    
    if (maxItems && parseInt(maxItems, 10) < loggerStates.length)
    {
        loggerStates.splice(parseInt(maxItems, 10), loggerStates.length - parseInt(maxItems, 10));
    }

    model.loggerStates = loggerStates;
}

function changeLoggerState(loggerName, level)
{
    var effectiveLoggerName, newLevel;

    effectiveLoggerName = loggerName;
    if (loggerName === '-root-')
    {
        effectiveLoggerName = Packages.org.orderofthebee.addons.support.tools.repo.log.Log4jCompatibilityUtils.LOG4J_HELPER.rootLoggerName;
    }

    if (String(level) === '' || String(level) === 'UNSET')
    {
        newLevel = null;
    }
    else
    {
        newLevel = String(level);
    }

    Packages.org.orderofthebee.addons.support.tools.repo.log.Log4jCompatibilityUtils.LOG4J_HELPER.setLevel(effectiveLoggerName, newLevel);
}

/* exported processLoggerStateChangeFromJSONData */
function processLoggerStateChangeFromJSONData(loggerName)
{
    var level;

    loggerName = loggerName ? loggerName : (json.has('logger') ? String(json.get('logger')) : null);
    level = json.has('level') ? String(json.get('level')) : null;
    
    if (loggerName !== null && level !== null)
    {
        changeLoggerState(loggerName, level);
    }
    else
    {
        logger.warn('Data missing in request');
        status.setCode(status.STATUS_BAD_REQUEST, 'Request incomplete');
    }
}

/* exported resetLoggerSetting */
function resetLoggerSetting(loggerName)
{
    var effectiveLoggerName;
    
    if (loggerName !== undefined && loggerName !== null)
    {
        if (String(loggerName) === '-root-')
        {
            effectiveLoggerName = Packages.org.orderofthebee.addons.support.tools.repo.log.Log4jCompatibilityUtils.LOG4J_HELPER.rootLoggerName;
        }
        else
        {
            effectiveLoggerName = String(loggerName);
        }
    }
    
    if (effectiveLoggerName === undefined)
    {
        Packages.org.orderofthebee.addons.support.tools.repo.log.Log4jCompatibilityUtils.resetToDefault();
    }
    else
    {
        Packages.org.orderofthebee.addons.support.tools.repo.log.Log4jCompatibilityUtils.resetToDefault(effectiveLoggerName);
    }
}

/* exported registerTailingAppender */
function registerTailingAppender()
{
    var uuid;

    uuid = Packages.org.orderofthebee.addons.support.tools.repo.log.Log4jCompatibilityUtils.LOG4J_HELPER.createTailingAppender();
    model.uuid = uuid;

    return uuid;
}

/* exported retrieveTailingEvents */
function retrieveTailingEvents()
{
    var uuid;

    uuid = args.uuid ? String(args.uuid) : registerTailingAppender();
    model.events = Packages.org.orderofthebee.addons.support.tools.repo.log.Log4jCompatibilityUtils.LOG4J_HELPER.retrieveTailingAppenderEvents(uuid);
}

/**
 * Builds a generic script model from an appender-related Java object.
 *
 * @param javaObject
 *            the javaObject to transform into a script model
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
                // TODO Add cases for other "expected" complex config properties for recursion
                if (key === 'layout' || key === 'manager')
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
    var parentLogger, appenderNames, idx, appender;

    if (logger.additivity && logger.parentName !== null && !logger.root)
    {
        parentLogger = Packages.org.orderofthebee.addons.support.tools.repo.log.Log4jCompatibilityUtils.LOG4J_HELPER.getLogger(logger.parentName);
        buidAppenderModelsForLogger(parentLogger, appenders);
    }

    appenderNames = logger.appenderNames;
    for (idx = 0; idx < appenderNames.size(); idx++)
    {
        appender = Packages.org.orderofthebee.addons.support.tools.repo.log.Log4jCompatibilityUtils.LOG4J_HELPER.getAppender(appenderNames.get(idx), logger.name);
        if (appender !== null)
        {
            appenders.push(buildGenericJavaObjectModel(appender));
        }
    }
}

/* exported buildAppenderModel */
function buildAppenderModel(loggerName)
{
    var effectiveLoggerName, appenders, logger;


    if (String(loggerName) === '-root-')
    {
        effectiveLoggerName = Packages.org.orderofthebee.addons.support.tools.repo.log.Log4jCompatibilityUtils.LOG4J_HELPER.rootLoggerName;
    }
    else
    {
        effectiveLoggerName = String(loggerName);
    }
    logger = Packages.org.orderofthebee.addons.support.tools.repo.log.Log4jCompatibilityUtils.LOG4J_HELPER.getLogger(effectiveLoggerName);

    appenders = [];
    buidAppenderModelsForLogger(logger, appenders);

    model.logger = loggerName;
    model.appenders = appenders;
}

/* exported buildLogFilesModel */
function buildLogFilesModel(useAllLoggerAppenders)
{
    var logFiles, filePaths, idx, filePath, file;

    logFiles = [];
    filePaths = Packages.org.orderofthebee.addons.support.tools.repo.log.Log4jCompatibilityUtils.LOG4J_HELPER.collectLogFilePaths(useAllLoggerAppenders);

    for (idx = 0; idx < filePaths.size(); idx++)
    {
        filePath = filePaths.get(idx);
        file = filePath.toFile();
        logFiles.push({
            name : String(file.name),
            // standardize paths
            directoryPath : String(file.parentFile.toPath()).replace(/\\/g, '/'),
            path : String(filePath).replace(/\\/g, '/'),
            size : file.length(),
            lastModified : file.lastModified()
        });
    }

    model.logFiles = logFiles;
    model.locale = Packages.org.springframework.extensions.surf.util.I18NUtil.getLocale().toString();
}

/* exported logSnapshotLapMessage */
function logSnapshotLapMessage(message)
{
    var lapLogger;

    // Fake logger that does not correspond to any class-based logger
    Packages.org.orderofthebee.addons.support.tools.repo.log.Log4jCompatibilityUtils.LOG4J_HELPER.setLevel('org.orderofthebee.addons.support.tools.repo.logSnapshotLap', 'INFO');

    lapLogger = Packages.org.slf4j.LoggerFactory.getLogger('org.orderofthebee.addons.support.tools.repo.logSnapshotLap');
    lapLogger.info(message);
}