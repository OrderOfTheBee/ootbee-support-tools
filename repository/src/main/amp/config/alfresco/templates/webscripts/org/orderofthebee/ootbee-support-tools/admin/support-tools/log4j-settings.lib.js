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

function buildLoggerState(logger)
{
    var loggerState, allAppenders, appender;

    loggerState = {
        name : logger.name,
        parent : logger.parent != null ? logger.parent.name : null,
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

function buildLoggerStates(showUnconfiguredLoggers)
{
    var loggerRepository, loggerStates, currentLoggers, logger;

    loggerRepository = Packages.org.apache.log4j.LogManager.getLoggerRepository();

    loggerStates = [];

    currentLoggers = loggerRepository.currentLoggers;
    while (currentLoggers.hasMoreElements())
    {
        logger = currentLoggers.nextElement();

        if (logger.level !== null || showUnconfiguredLoggers)
        {
            loggerStates.push(buildLoggerState(logger));
        }
    }

    loggerStates.sort(function(a, b)
    {
        return a.name.localeCompare(b.name);
    });

    loggerStates.splice(0, 0, buildLoggerState(loggerRepository.rootLogger));

    model.loggerStates = loggerStates;
}

function changeLoggerState(loggerName, level)
{
    var logger = Packages.org.apache.log4j.Logger.getLogger(loggerName);
    logger.setLevel(Packages.org.apache.log4j.Level.toLevel(level));
}

function processLoggerStateChangeFromFormData()
{
    var fields, field, i, loggerName, level, showUnconfiguredLoggers;
    
    fields = formdata.fields;
    for (i = 0; i < fields.length; i++)
    {
        field = fields[i];
        switch(String(field.name))
        {
            case 'logger': loggerName = String(field.value); break;
            case 'level': level = String(field.value); break;
            case 'showUnconfiguredLoggers': showUnconfiguredLoggers = String(field.value); break;
            default: logger.debug('Unknown field: ' + field.name);
        }
    }
    
    changeLoggerState(loggerName, level);
    
    return showUnconfiguredLoggers;
}