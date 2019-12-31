/**
 * Copyright (C) 2016 - 2020 Order of the Bee
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
 * Copyright (C) 2005 - 2020 Alfresco Software Limited.
 */

/* global logSettingTracker: false */

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

function processLoggerStateChangeFromJSONData(loggerName)
{
    var level;

    level = json.has('level') ? String(json.get('level')) : null;
    
    if (loggerName !== null && level !== null)
    {
        changeLoggerState(loggerName, level);
    }
    else
    {
        logger.warn('[log4j-logger.put.json.js] data missing in request');
        status.setCode(status.STATUS_BAD_REQUEST, 'Request incomplete');
    }
}

processLoggerStateChangeFromJSONData(String(url.templateArgs.logger).replace(/%dot%/g, '.'));