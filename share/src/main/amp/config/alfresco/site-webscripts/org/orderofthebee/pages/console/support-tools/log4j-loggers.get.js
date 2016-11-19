/**
 * Copyright (C) 2016 Axel Faust
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

function buildLoggerStates(showUnconfiguredLoggers, loggerNamePattern)
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

    loggerState = buildLoggerState(loggerRepository.rootLogger);
    loggerStates.splice(0, 0, loggerState);

    model.loggerStates = loggerStates;
}

buildLoggerStates(String(args.showUnconfiguredLoggers) === 'true', args.loggerName);