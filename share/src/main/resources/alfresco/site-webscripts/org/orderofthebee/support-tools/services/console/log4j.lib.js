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
 * Copyright (C) 2005 - 2025 Alfresco Software Limited.
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
        canBeReset : Packages.org.orderofthebee.addons.support.tools.share.log.Log4jCompatibilityUtils.canBeReset(logger.name),
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

    loggers = Packages.org.orderofthebee.addons.support.tools.share.log.Log4jCompatibilityUtils.LOG4J_HELPER.getLoggers(loggerNamePattern, showUnconfiguredLoggers);
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
        effectiveLoggerName = Packages.org.orderofthebee.addons.support.tools.share.log.Log4jCompatibilityUtils.LOG4J_HELPER.rootLoggerName;
    }

    if (String(level) === '' || String(level) === 'UNSET')
    {
        newLevel = null;
    }
    else
    {
        newLevel = String(level);
    }

    Packages.org.orderofthebee.addons.support.tools.share.log.Log4jCompatibilityUtils.LOG4J_HELPER.setLevel(effectiveLoggerName, newLevel);
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
            effectiveLoggerName = Packages.org.orderofthebee.addons.support.tools.share.log.Log4jCompatibilityUtils.LOG4J_HELPER.rootLoggerName;
        }
        else
        {
            effectiveLoggerName = String(loggerName);
        }
    }
    
    if (effectiveLoggerName === undefined)
    {
        Packages.org.orderofthebee.addons.support.tools.share.log.Log4jCompatibilityUtils.resetToDefault();
    }
    else
    {
        Packages.org.orderofthebee.addons.support.tools.share.log.Log4jCompatibilityUtils.resetToDefault(effectiveLoggerName);
    }
}

/* exported registerTailingAppender */
function registerTailingAppender()
{
    var uuid;

    uuid = Packages.org.orderofthebee.addons.support.tools.share.log.Log4jCompatibilityUtils.LOG4J_HELPER.createTailingAppender();
    model.uuid = uuid;

    return uuid;
}

/* exported retrieveTailingEvents */
function retrieveTailingEvents()
{
    var uuid;

    uuid = args.uuid ? String(args.uuid) : registerTailingAppender();
    model.events = Packages.org.orderofthebee.addons.support.tools.share.log.Log4jCompatibilityUtils.LOG4J_HELPER.retrieveTailingAppenderEvents(uuid);
}

/* exported buildLogFilesModel */
function buildLogFilesModel(useAllLoggerAppenders)
{
    var logFiles, filePaths, idx, filePath, file;

    logFiles = [];
    filePaths = Packages.org.orderofthebee.addons.support.tools.share.log.Log4jCompatibilityUtils.LOG4J_HELPER.collectLogFilePaths(useAllLoggerAppenders);

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