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

function buildThreaddumpInformation()
{
	var modelThreads = [];
   
	function format(thisvalue)
	{
		thisvalue="00" + thisvalue;
		return thisvalue.substr(-2);
	}

	var runtimeBean = Packages.java.lang.management.ManagementFactory.getRuntimeMXBean();
  
	var now = new Date();
	var myDate = now.getFullYear() + "-" + format(now.getMonth()+1) + "-" + format(now.getDate()) + " " + format(now.getHours()) + ":" + format(now.getMinutes()) + ":" +format(now.getSeconds());

    model.myDate = myDate;    
    model.vmName = runtimeBean.vmName;
	model.vmVersion = runtimeBean.vmVersion;

   	var threadBean = Packages.java.lang.management.ManagementFactory.getThreadMXBean();
   
   	var threads = threadBean.dumpAllThreads(true, true);
  
   	for (var n = threads.length -1; n >= 0; n--)
   	{
    	var thread = threads[n];
      	var keys = threads[n].dataKeys;

      	modelThreads[n] = {};
      	modelThreads[n].threadName = thread.threadName;
      	modelThreads[n].threadId = thread.threadId;
      	modelThreads[n].blockedCount = thread.blockedCount;
      	modelThreads[n].waitedCount = thread.waitedCount;
      	modelThreads[n].waitedTime = thread.waitedTime;
      	modelThreads[n].threadState = thread.threadState;
      	modelThreads[n].stackTrace = stackTrace(thread.stackTrace, thread.lockedMonitors, thread);
      
      var lockedSynchronizers=thread.lockedSynchronizers;
      if (lockedSynchronizers && lockedSynchronizers.length>0)	
      {
         modelThreads[n].lockedSynchronizers = [];
         
         for (var i = 0; i < lockedSynchronizers.length; i++)
         {
            modelThreads[n].lockedSynchronizers[i] = {};
            modelThreads[n].lockedSynchronizers[i].identityHashCode = toHex(lockedSynchronizers[i].identityHashCode, 16);
            modelThreads[n].lockedSynchronizers[i].className = lockedSynchronizers[i].className;
         }
      }
   }
   
   var deadLockedThreads = 0;
   
   if (threadBean.findDeadlockedThreads()) 
   {
      deadLockedThreads = threadBean.findDeadlockedThreads;
   }

   model.numberOfThreads = threads.length;
   model.deadlockedThreads = deadLockedThreads;
   model.modelThreads = modelThreads;
}
