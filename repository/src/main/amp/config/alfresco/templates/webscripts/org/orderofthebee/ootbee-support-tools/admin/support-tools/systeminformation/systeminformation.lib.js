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

/* exported buildSystemInformation */
function buildSystemInformation(){
    var ctxt, managementFactory, runtime, globalProperties ;

    ctxt = Packages.org.springframework.web.context.ContextLoader.getCurrentWebApplicationContext();
    globalProperties = ctxt.getBean('global-properties', Packages.java.util.Properties);
    model.globalProperties = globalProperties;
    model.sensitiveKeys = Packages.com.google.common.collect.Lists.newArrayList(Packages.com.google.common.base.Splitter
                                   .on(',')
                                   .trimResults()
                                   .omitEmptyStrings()
                                   .split(globalProperties["support-tools.systeminformation.sensitiveKeys"]));

    model.environmentProperties = java.lang.System.getenv();
    model.systemProperties = java.lang.System.getProperties();

    managementFactory = java.lang.management.ManagementFactory;
    runtime = managementFactory.getRuntimeMXBean();

    if (runtime.isBootClassPathSupported()){
        model.bootClassPath = runtime.getBootClassPath().split(java.io.File.pathSeparator);
    }

    model.javaArguments = runtime.getInputArguments();

    var duration = Packages.net.time4j.Duration.of(runtime.getUptime(), Packages.net.time4j.ClockUnit.MILLIS).with(Packages.net.time4j.Duration.STD_PERIOD);
    var prettyUptime = Packages.net.time4j.PrettyTime.of(Packages.org.springframework.extensions.surf.util.I18NUtil.getLocale()).print(duration, Packages.net.time4j.format.TextWidth.WIDE);
    model.upTime = prettyUptime;
    model.startTime = runtime.getStartTime();

}
