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

function roundToMebibyte(value)
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
            heapInit : roundToMebibyte(heapUsage.init),
            heapFree : roundToMebibyte(heapUsage.max - heapUsage.used),
            heapUsed : roundToMebibyte(heapUsage.used),
            heapCommitted : roundToMebibyte(heapUsage.committed),
            heapMax : roundToMebibyte(heapUsage.max),
            nonHeapInit : roundToMebibyte(nonHeapUsage.init),
            nonHeapFree : roundToMebibyte(nonHeapUsage.max - nonHeapUsage.used),
            nonHeapUsed : roundToMebibyte(nonHeapUsage.used),
            nonHeapCommitted : roundToMebibyte(nonHeapUsage.committed),
            nonHeapMax : roundToMebibyte(nonHeapUsage.max),
            freeMemory : roundToMebibyte(Runtime.freeMemory()),
            totalMemory : roundToMebibyte(Runtime.totalMemory()),
            maxMemory : roundToMebibyte(Runtime.maxMemory())
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

/* exported buildMetrics */
function buildMetrics()
{
    buildMemoryMetrics();
    buildThreadMetrics();
    buildCPUMetrics();
}
