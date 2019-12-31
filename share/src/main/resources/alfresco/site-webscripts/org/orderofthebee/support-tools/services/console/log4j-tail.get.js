/**
 * Copyright (C) 2016 - 2020 Order of the Bee
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
 * Copyright (C) 2005 - 2020 Alfresco Software Limited.
 */

function registerTailingAppender(uuidParam)
{
    var uuid, appender, rootLogger;

    uuid = uuidParam || String(Packages.java.util.UUID.randomUUID());
    appender = new Packages.org.orderofthebee.addons.support.tools.share.LimitedListAppender(uuid, 10000);
    rootLogger = Packages.org.apache.log4j.Logger.getRootLogger();
    appender.registerAsAppender(rootLogger);

    model.uuid = uuid;
    
    return appender;
}

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

retrieveTailingEvents();