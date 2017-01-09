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

/* global logSettingTracker: false */

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

if (url.templateArgs.logger !== null)
{
    resetLoggerSetting(String(url.templateArgs.logger).replace(/%dot%/g, '.'));
}
else
{
    resetLoggerSetting();
}