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

function MyThreadInfo(cpuTime, info) 
{
    this.deltaDone = false;
    this.info = info;
    this.cpuTime = cpuTime;
}
    
function compareCpuTime(o1, o2) 
{
	return (o2.cpuTime - o1.cpuTime);
}

/* exported buildHotThreadInformation */
function buildHotThreadInformation() 
{
    var hotThreads, runtimeBean, threadInfo, threadBean, info, cpu, tix, threadPackage, tiy, threadDump, threads, n, threadId, ht, thread, thisCpuTime, thisStackTrace, lockedSynchronizers, i, deadLockedThreads;
    
	hotThreads = [];

	runtimeBean = Packages.java.lang.management.ManagementFactory.getRuntimeMXBean();
    
    model.myDate = (new Date()).toISOString();    
    model.vmName = runtimeBean.vmName;
	model.vmVersion = runtimeBean.vmVersion;

	threadInfo = {};
	threadBean = Packages.java.lang.management.ManagementFactory.getThreadMXBean();
	
	/* jshint forin: false */
    for each (tix in threadBean.allThreadIds) 
	{
        cpu = threadBean.getThreadCpuTime(tix);
		info = threadBean.getThreadInfo(tix);
        threadInfo[tix] = new MyThreadInfo(cpu, info);
    }
   
	threadPackage = Packages.java.lang.Thread;
	threadPackage.sleep(999);
    
    for each (tiy in threadBean.allThreadIds) 
	{
		cpu = threadBean.getThreadCpuTime(tiy);
		info = threadBean.getThreadInfo(tiy);
        threadInfo[tiy].deltaDone=true;
		threadInfo[tiy].cpuTime=cpu-threadInfo[tiy].cpuTime;
    }
    /* jshint forin: true */
    
	threadDump = threadBean.dumpAllThreads(true, true);
    threads = []; // new sortable array to store all values

    for (n = threadDump.length -1; n >= 0; n--)
    {
        threadId = threadDump[n].threadId;
        if (threadInfo[threadId] !== undefined && threadInfo[threadId] !== null)
        {
            threadInfo[threadId].info=threadDump[n];
            threads.push(threadInfo[threadId]);
        }
    }
    
	threads = threads.sort(compareCpuTime);

    // Show the 5 hottest threads
    for (ht = 0 ; ht < 5 ; ht++) 
    {				
        thread = threads[ht].info;
        thisCpuTime = threads[ht].cpuTime / 10000000;

		logger.warn("+++Hotthreads "+thread.threadName+" tid=" + thread.threadId +" CPU TIME=" + thisCpuTime +"% ("+threads[ht].cpuTime+")");

		hotThreads[ht] = {};
		hotThreads[ht].cpuTime = thisCpuTime;
        
        hotThreads[ht].threadName = thread.threadName;
        hotThreads[ht].threadId = thread.threadId;
        hotThreads[ht].blockedCount = thread.blockedCount;
        hotThreads[ht].waitedCount = thread.waitedCount;
        hotThreads[ht].waitedTime = thread.waitedTime;
        hotThreads[ht].threadState = thread.threadState;

        thisStackTrace = stackTrace(thread.stackTrace, thread.lockedMonitors, thread);
        
		if (thisStackTrace.indexOf("hotthreads-getone.get.js") <= 1)
		
		{
			hotThreads[ht].stackTrace = thisStackTrace;
		}
		
        lockedSynchronizers = thread.lockedSynchronizers;
        if (lockedSynchronizers && lockedSynchronizers.length > 0) 
		{
    		hotThreads[ht].lockedSynchronizers = [];
    		
            for (i = 0; i < lockedSynchronizers.length; i++) 
			{
            	hotThreads[ht].lockedSynchronizers[i] = {};
            	hotThreads[ht].lockedSynchronizers[i].identityHashCode = toHex(lockedSynchronizers[i].identityHashCode, 16);
            	hotThreads[ht].lockedSynchronizers[i].className = lockedSynchronizers[i].className;
            }
        } 
    }

    deadLockedThreads = 0;
    
    if (threadBean.findDeadlockedThreads()) 
	{
        deadLockedThreads = threadBean.findDeadlockedThreads();
    }
    
    model.numberOfThreads = threads.length;
    model.deadlockedThreads = deadLockedThreads;
    model.hotThreads = hotThreads;

}
