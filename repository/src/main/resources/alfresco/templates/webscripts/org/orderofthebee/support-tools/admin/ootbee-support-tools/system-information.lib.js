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

/* exported buildSystemInformation */
/**
 * @author Jens Goldhammer
 */
function buildSystemInformation()
{
    var ctxt, managementFactory, runtime, globalProperties, duration, prettyUptime;

    ctxt = Packages.org.springframework.web.context.ContextLoader.getCurrentWebApplicationContext();
    globalProperties = ctxt.getBean('global-properties', Packages.java.util.Properties);
    model.globalProperties = globalProperties;
    model.sensitiveKeys = globalProperties["ootbee-support-tools.systeminformation.sensitiveKeys"].split(',');

    model.environmentProperties = Packages.java.lang.System.getenv();
    model.systemProperties = Packages.java.lang.System.getProperties();

    managementFactory = Packages.java.lang.management.ManagementFactory;
    runtime = managementFactory.getRuntimeMXBean();

    if (runtime.isBootClassPathSupported())
    {
        model.bootClassPath = runtime.getBootClassPath().split(Packages.java.io.File.pathSeparator);
    }
    else
    {
        model.bootClassPath = [];
    }

    model.javaArguments = runtime.getInputArguments();

    duration = Packages.net.time4j.Duration.of(runtime.getUptime(), Packages.net.time4j.ClockUnit.MILLIS).with(Packages.net.time4j.Duration.STD_PERIOD);
    prettyUptime = Packages.net.time4j.PrettyTime.of(Packages.org.springframework.extensions.surf.util.I18NUtil.getLocale()).print(duration, Packages.net.time4j.format.TextWidth.WIDE);
    model.upTime = prettyUptime;
    model.startTime = Packages.java.text.DateFormat.getDateTimeInstance(3, 3, Packages.org.springframework.extensions.surf.util.I18NUtil.getLocale()).format(new Packages.java.util.Date(runtime.getStartTime()));
}