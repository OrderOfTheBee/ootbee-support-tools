<import resource="classpath:alfresco/templates/webscripts/org/alfresco/repository/admin/admin-common.lib.js">
<import resource="classpath:alfresco/templates/webscripts/org/orderofthebee/ootbee-support-tools/admin/support-tools/threads-common.lib.js">

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
	
    for each (var ti in threadBean.allThreadIds) 
	{
        cpu = threadBean.getThreadCpuTime(ti);
		info = threadBean.getThreadInfo(ti);
        threadInfo[ti] = new MyThreadInfo(cpu, info);
    }

	sleep(999);

    for each (var ti in threadBean.allThreadIds) 
	{
		cpu = threadBean.getThreadCpuTime(ti);
		info = threadBean.getThreadInfo(ti);
        threadInfo[ti] = new MyThreadInfo(cpu, info);
        threadInfo[ti].deltaDone=true;
		threadInfo[ti].cpuTime=cpu-threadInfo[ti].cpuTime;
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
    
	threads=threads.sort(compareCpuTime);

    // Show the 5 hottest threads
    for (var n = 0 ; n <= 5 ; n++) 
    {				
        var thread = threads[n].info;
        var keys = threads[n].info.dataKeys;
        var thisCpuTime = threads[n].cpuTime / 10000000;

        // TODO: change to logger.info?
		logger.warn("+++Hotthreads "+thread.threadName+" tid=" + thread.threadId +" CPU TIME=" + thisCpuTime +"% ("+threads[n].cpuTime+")");


		hotThreads[n] = {};
		hotThreads[n].cpuTime = thisCpuTime;
        
        hotThreads[n].threadName = thread.threadName;
        hotThreads[n].threadId = thread.threadId;
        hotThreads[n].blockedCount = thread.blockedCount;
        hotThreads[n].waitedCount = thread.waitedCount;
        hotThreads[n].waitedTime = thread.waitedTime;
        hotThreads[n].threadState = thread.threadState;

        thisStackTrace=stackTrace(thread.stackTrace, thread.lockedMonitors, thread);
        
		if (!(thisStackTrace.indexOf("hotthreads-getone.get.js") > 1))
		{
			hotThreads[n].stackTrace = thisStackTrace;
		}
		
        var lockedSynchronizers = thread.lockedSynchronizers;
        if (lockedSynchronizers && lockedSynchronizers.length > 0) 
		{
    		hotThreads[n].lockedSynchronizers = [];
    		
            for (var i = 0; i < lockedSynchronizers.length; i++) 
			{
            	hotThreads[n].lockedSynchronizers[i] = {};
            	hotThreads[n].lockedSynchronizers[i].identityHashCode = toHex(lockedSynchronizers[i].identityHashCode, 16);
            	hotThreads[n].lockedSynchronizers[i].className = lockedSynchronizers[i].className;
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

function sleep(delay) 
{
    var start = new Date().getTime();
    while (new Date().getTime() < start + delay);
}

