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

/* global toHex: false, stackTrace: false */
/* exported buildThreadDumpInformation */
function buildThreadDumpInformation()
{
    var modelThreads, runtimeBean, threadBean, threads, n, thread, lockedSynchronizers, i, deadLockedThreads;

    modelThreads = [];

    runtimeBean = Packages.java.lang.management.ManagementFactory.getRuntimeMXBean();

    model.myDate = (new Date()).toISOString();
    model.vmName = runtimeBean.vmName;
    model.vmVersion = runtimeBean.vmVersion;

    threadBean = Packages.java.lang.management.ManagementFactory.getThreadMXBean();

    threads = threadBean.dumpAllThreads(true, true);

    for (n = threads.length - 1; n >= 0; n--)
    {
        thread = threads[n];

        modelThreads[n] = {};
        modelThreads[n].threadName = thread.threadName;
        modelThreads[n].threadId = thread.threadId;
        modelThreads[n].blockedCount = thread.blockedCount;
        modelThreads[n].waitedCount = thread.waitedCount;
        modelThreads[n].waitedTime = thread.waitedTime;
        modelThreads[n].threadState = thread.threadState;
        modelThreads[n].stackTrace = stackTrace(thread.stackTrace, thread.lockedMonitors, thread);

        lockedSynchronizers = thread.lockedSynchronizers;
        if (lockedSynchronizers && lockedSynchronizers.length > 0)
        {
            modelThreads[n].lockedSynchronizers = [];

            for (i = 0; i < lockedSynchronizers.length; i++)
            {
                modelThreads[n].lockedSynchronizers[i] = {};
                modelThreads[n].lockedSynchronizers[i].identityHashCode = toHex(lockedSynchronizers[i].identityHashCode, 16);
                modelThreads[n].lockedSynchronizers[i].className = lockedSynchronizers[i].className;
            }
        }
    }

    deadLockedThreads = 0;

    if (threadBean.findDeadlockedThreads())
    {
        deadLockedThreads = threadBean.findDeadlockedThreads;
    }

    model.numberOfThreads = threads.length;
    model.deadlockedThreads = deadLockedThreads;
    model.modelThreads = modelThreads;
}
