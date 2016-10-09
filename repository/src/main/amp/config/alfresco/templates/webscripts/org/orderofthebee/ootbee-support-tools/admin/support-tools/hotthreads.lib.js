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

function buildHotthreadInformation() 
{
	var hotThreads = [];

    function format(thisValue) 
    {
        thisValue = "00" + thisValue;
        return thisValue.substr(-2);
    }

	var runtimeBean = Packages.java.lang.management.ManagementFactory.getRuntimeMXBean();
    
    var now = new Date();
    var myDate = now.getFullYear() + "-" + format(now.getMonth() + 1) + "-" + format(now.getDate()) + " " + format(now.getHours()) + ":" + format(now.getMinutes()) + ":" + format(now.getSeconds());

    model.myDate = myDate;    
    model.vmName = runtimeBean.vmName;
	model.vmVersion = runtimeBean.vmVersion;

	var threadInfo = {};
	var threadBean = Packages.java.lang.management.ManagementFactory.getThreadMXBean();
	
	var info, cpu;
	
    for each (var tix in threadBean.allThreadIds) 
	{
        cpu = threadBean.getThreadCpuTime(tix);
		info = threadBean.getThreadInfo(tix);
        threadInfo[tix] = new MyThreadInfo(cpu, info);
    }
   
	var threadPackage = Packages.java.lang.Thread;
	threadPackage.sleep(999);
    
    for each (var tiy in threadBean.allThreadIds) 
	{
		cpu = threadBean.getThreadCpuTime(tiy);
		info = threadBean.getThreadInfo(tiy);
        threadInfo[tiy].deltaDone=true;
		threadInfo[tiy].cpuTime=cpu-threadInfo[tiy].cpuTime;
    }
    
	var threadDump = threadBean.dumpAllThreads(true, true);
    var threads = new Array(); // new sortable array to store all values

    for (var n = threadDump.length -1; n >= 0; n--)
    {
        var threadId = threadDump[n].threadId;
        if (threadInfo[threadId] !== undefined && threadInfo[threadId] !== null)
        {
            threadInfo[threadId].info=threadDump[n];
            threads.push(threadInfo[threadId]);
        }
    }
    
	threads = threads.sort(compareCpuTime);

    // Show the 5 hottest threads
    for (var ht = 0 ; ht < 5 ; ht++) 
    {				
        var thread = threads[ht].info;
        var thisCpuTime = threads[ht].cpuTime / 10000000;

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
        
		//if (thisStackTrace.indexOf("hotthreads-getone.get.js") < 1)
		if (!(thisStackTrace.indexOf("hotthreads-getone.get.js") > 1))
		
		{
			hotThreads[ht].stackTrace = thisStackTrace;
		}
		
        var lockedSynchronizers = thread.lockedSynchronizers;
        if (lockedSynchronizers && lockedSynchronizers.length > 0) 
		{
    		hotThreads[ht].lockedSynchronizers = [];
    		
            for (var i = 0; i < lockedSynchronizers.length; i++) 
			{
            	hotThreads[ht].lockedSynchronizers[i] = {};
            	hotThreads[ht].lockedSynchronizers[i].identityHashCode = toHex(lockedSynchronizers[i].identityHashCode, 16);
            	hotThreads[ht].lockedSynchronizers[i].className = lockedSynchronizers[i].className;
            }
        } 
    }

    var deadLockedThreads = 0;
    
    if (threadBean.findDeadlockedThreads()) 
	{
        deadLockedThreads = threadBean.findDeadlockedThreads();
    }
    
    model.numberOfThreads = threads.length;
    model.deadlockedThreads = deadLockedThreads;
    model.hotThreads = hotThreads;

}
