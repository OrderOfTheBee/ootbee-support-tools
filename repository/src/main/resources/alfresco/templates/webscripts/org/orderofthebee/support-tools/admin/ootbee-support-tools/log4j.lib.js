/**
 * Copyright (C) 2017 Axel Faust / Markus Joos / Bindu Wavell
 * Copyright (C) 2017 Order of the Bee
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
 * Copyright (C) 2005-2017 Alfresco Software Limited.
 */

/* global logSettingTracker: false */

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
        canBeReset : logSettingTracker.canBeReset(logger),
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
function buildLoggerStates(showUnconfiguredLoggers, loggerNamePattern, skipCount, maxItems)
{
    var loggerRepository, loggerStates, loggerState, currentLoggers, effectiveLoggerNamePattern, logger;

    loggerRepository = Packages.org.apache.log4j.LogManager.getLoggerRepository();

    loggerStates = [];

    currentLoggers = loggerRepository.currentLoggers;
    
    effectiveLoggerNamePattern = null;
    if (loggerNamePattern !== null && String(loggerNamePattern) !== '')
    {
        effectiveLoggerNamePattern = new RegExp(String(loggerNamePattern).replace(/\./g, '\\.').replace(/\*/g, '.+'), 'i');
    }
    
    while (currentLoggers.hasMoreElements())
    {
        logger = currentLoggers.nextElement();

        if ((effectiveLoggerNamePattern === null || effectiveLoggerNamePattern.test(logger.name))
                && (logger.level !== null || showUnconfiguredLoggers))
        {
            loggerState = buildLoggerState(logger);
            loggerStates.push(loggerState);
        }
    }

    loggerStates.sort(function(a, b)
    {
        return a.name.localeCompare(b.name);
    });

    if (effectiveLoggerNamePattern === null)
    {
        loggerState = buildLoggerState(loggerRepository.rootLogger);
        loggerStates.splice(0, 0, loggerState);
    }
    
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
    var logger, newLevel;

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
        newLevel = null;
    }
    else
    {
        newLevel = Packages.org.apache.log4j.Level.toLevel(level);
    }
    
    logSettingTracker.recordChange(logger, logger.level, newLevel);
    logger.level = newLevel;
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
    var logger;
    
    if (loggerName !== undefined && loggerName !== null)
    {
        if (String(loggerName) === '-root-')
        {
            logger = Packages.org.apache.log4j.Logger.getRootLogger();
        }
        else
        {
            logger = Packages.org.apache.log4j.Logger.getLogger(loggerName);
        }
    }
    
    if (logger === undefined)
    {
        logSettingTracker.resetToDefault();
    }
    else
    {
        logSettingTracker.resetToDefault(logger);
    }
}

/* exported registerTailingAppender */
function registerTailingAppender(uuidParam)
{
    var uuid, appender, rootLogger;

    uuid = uuidParam || String(Packages.java.util.UUID.randomUUID());
    appender = new Packages.org.orderofthebee.addons.support.tools.repo.LimitedListAppender(uuid, 10000);
    rootLogger = Packages.org.apache.log4j.Logger.getRootLogger();
    appender.registerAsAppender(rootLogger);

    model.uuid = uuid;

    return appender;
}

/* exported retrieveTailingEvents */
function retrieveTailingEvents()
{
    var uuid, rootLogger, appender;

    uuid = String(args.uuid || '');
    rootLogger = Packages.org.apache.log4j.Logger.getRootLogger();
    appender = rootLogger.getAppender(uuid);

    if (appender === null)
    {
        appender = registerTailingAppender(uuid);
    }

    model.events = appender.retrieveLogEvents();
}

/**
 * Builds a generic script model from a logger-/appender-related Java object.
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

function collectLogFilePatterns(allLoggerAppenders, logFilePatterns)
{
    var loggerRepository, loggers, logger, allAppenders, appender;
    
    if (allLoggerAppenders)
    {
        loggerRepository = Packages.org.apache.log4j.LogManager.getLoggerRepository();
        loggers = loggerRepository.currentLoggers;
        while (loggers.hasMoreElements())
        {
            logger = loggers.nextElement();
            allAppenders = logger.allAppenders;
            while (allAppenders.hasMoreElements())
            {
                appender = allAppenders.nextElement();
                if (appender.file !== undefined && appender.file !== null)
                {
                    logFilePatterns[String(appender.file)] = true;
                }
            }
        }
    }
    
    // root logger is not container in currentLoggers for some reason
    logger = Packages.org.apache.log4j.Logger.getRootLogger();
    allAppenders = logger.allAppenders;
    while (allAppenders.hasMoreElements())
    {
        appender = allAppenders.nextElement();
        if (appender.file !== undefined && appender.file !== null)
        {
            logFilePatterns[String(appender.file)] = true;
        }
    }
}

/* exported buildLogFilesModel */
function buildLogFilesModel(useAllLoggerAppenders)
{
    var filePatterns, logFiles, matcherFn, filePattern, path, file, dirStream;

    filePatterns = {};
    logFiles = [];

    collectLogFilePatterns(useAllLoggerAppenders, filePatterns);

    matcherFn = function(path)
    {
        var logFileCandidate = path.toFile();
        if (logFileCandidate.isFile())
        {
            logFiles.push({
                name : String(logFileCandidate.name),
                // standardize paths
                directoryPath : String(logFileCandidate.parentFile.toPath()).replace(/\\/g, '/'),
                path : String(logFileCandidate.toPath()).replace(/\\/g, '/'),
                size : logFileCandidate.length(),
                lastModified : logFileCandidate.lastModified()
            });
        }
    };

    for (filePattern in filePatterns)
    {
        if (filePatterns.hasOwnProperty(filePattern) && filePatterns[filePattern] === true)
        {
            file = new Packages.java.io.File(filePattern);
            path = Packages.java.nio.file.Paths.get(file.toURI()).getParent();
            dirStream = Packages.java.nio.file.Files
                    .newDirectoryStream(path, filePattern.substring(filePattern.lastIndexOf('/') + 1) + '*');
            // Rhino does not support conversion of function to SAM type
            dirStream.forEach({
                accept : matcherFn
            });
        }
    }

    model.logFiles = logFiles;
    model.locale = Packages.org.springframework.extensions.surf.util.I18NUtil.getLocale().toString();
}

function getLoggersToSnapshot()
{
    var logger, loggers, currentLoggers, loggerRepository;
    
    logger = Packages.org.apache.log4j.Logger.getRootLogger();
    loggers = [logger];
    
    loggerRepository = Packages.org.apache.log4j.LogManager.getLoggerRepository();
    currentLoggers = loggerRepository.currentLoggers;
    while (currentLoggers.hasMoreElements())
    {
    	logger = currentLoggers.nextElement();
    	if (!logger.additivity)
    	{
    		loggers.push(logger);
    	}
    }
    return loggers;
}

/* exported createSnapshot */
function createSnapshot()
{
	var snapshotAppender, loggers;
	
	snapshotAppender = new Packages.org.orderofthebee.addons.support.tools.repo.TemporaryFileAppender('ootbee-support-tools-snapshot-');
	loggers = getLoggersToSnapshot();
	loggers.forEach(
	    function createSnapshot_connectLoggerAndAppender(logger)
        {
            snapshotAppender.registerAsAppender(logger);
	    }
    );
	
	return snapshotAppender.appenderUUID;
}

/* exported logSnapshotLapMessage */
function logSnapshotLapMessage(message) {
    var lapLogger, level;

    // Fake logger that does not correspond to any class-based logger
    lapLogger = Packages.org.apache.log4j.Logger.getLogger('org.orderofthebee.addons.support.tools.repo.logSnapshotLap');

    // ensure level is enabled (in case someone reconfigured logger) and log
    level = Packages.org.apache.log4j.Level.INFO;
    lapLogger.setLevel(level);
    lapLogger.log(level, message);
}