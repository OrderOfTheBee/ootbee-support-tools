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

function roundToMegabyte(value)
{
    var result = Math.round(value / 1024 / 1024);
    return result;
}

function buildMemoryMetrics()
{
    var memoryMetrics, memoryMXBean, Runtime, heapUsage, nonHeapUsage;

    memoryMXBean = Packages.java.lang.management.ManagementFactory.getMemoryMXBean();
    Runtime = Packages.java.lang.Runtime.getRuntime();
    
    heapUsage = memoryMXBean.heapMemoryUsage;
    nonHeapUsage = memoryMXBean.nonHeapMemoryUsage;

    memoryMetrics = {
            heapInit : roundToMegabyte(heapUsage.init),
            heapFree : roundToMegabyte(heapUsage.max - heapUsage.used),
            heapUsed : roundToMegabyte(heapUsage.used),
            heapCommitted : roundToMegabyte(heapUsage.committed),
            heapMax : roundToMegabyte(heapUsage.max),
            nonHeapInit : roundToMegabyte(nonHeapUsage.init),
            nonHeapFree : roundToMegabyte(nonHeapUsage.max - nonHeapUsage.used),
            nonHeapUsed : roundToMegabyte(nonHeapUsage.used),
            nonHeapCommitted : roundToMegabyte(nonHeapUsage.committed),
            nonHeapMax : roundToMegabyte(nonHeapUsage.max),
            freeMemory : roundToMegabyte(Runtime.freeMemory()),
            totalMemory : roundToMegabyte(Runtime.totalMemory()),
            maxMemory : roundToMegabyte(Runtime.maxMemory())
    };

    model.memoryMetrics = memoryMetrics;
}

function buildThreadMetrics()
{
    var threadMetrics, threadMXBean;
    
    threadMXBean = Packages.java.lang.management.ManagementFactory.getThreadMXBean();
    threadMetrics = {
            threadCount : threadMXBean.threadCount,
            peakThreadCount : threadMXBean.peakThreadCount,
            startedThreadCount : threadMXBean.totalStartedThreadCount,
            daemonThreadCount : threadMXBean.daemonThreadCount
    };
    
    model.threadMetrics = threadMetrics;
}

function buildCPUMetrics()
{
    var cpuMetrics, osMXBean;
    
    osMXBean = Packages.java.lang.management.ManagementFactory.getOperatingSystemMXBean();
    cpuMetrics = {
            systemCPULoad : Math.round(osMXBean.systemCpuLoad * 100),
            processCPULoad : Math.round(osMXBean.processCpuLoad * 100),
            processCPUTime : osMXBean.systemCpuTime
    };
    
    model.cpuMetrics = cpuMetrics;
}

function buildMetrics()
{
    buildMemoryMetrics();
    buildThreadMetrics();
    buildCPUMetrics();
}
