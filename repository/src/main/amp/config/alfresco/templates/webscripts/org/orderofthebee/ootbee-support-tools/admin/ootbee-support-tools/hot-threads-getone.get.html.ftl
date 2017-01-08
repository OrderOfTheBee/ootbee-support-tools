<#-- 
Copyright (C) 2016 Axel Faust / Markus Joos
Copyright (C) 2016 Order of the Bee

This file is part of Community Support Tools

Community Support Tools is free software: you can redistribute it and/or modify
it under the terms of the GNU Lesser General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

Community Support Tools is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public License
along with Community Support Tools. If not, see <http://www.gnu.org/licenses/>.

Linked to Alfresco
Copyright (C) 2005-2016 Alfresco Software Limited.
 
  -->
  
<pre id="__id__" class="__class__">
<span id="date" class="highlight">${myDate}</span>
<span class="highlight">Hot Threads Report on ${vmName} (${vmVersion})</span>

<#if hotThreads?? && hotThreads?size gt 0>
    <#list hotThreads as hotThread>
<span class="highlight"> CPU TIME=${hotThread.cpuTime}% </span>
<span class="highlight">"${hotThread.threadName}" tid=${hotThread.threadId} Total_Blocked=${hotThread.blockedCount} Total_Waited=${hotThread.waitedCount} Waited_Time=${hotThread.waitedTime} ${hotThread.threadState}</span>
   java.lang.Thread.State: ${hotThread.threadState}
            <#if hotThread.stackTrace??>
${hotThread.stackTrace}   Locked ownable synchronizers:<#else>   ***Ingore this thread: this is the running process to Obtain HOTTHREADS
   Locked ownable synchronizers:</#if>
            <#if hotThread.lockedSynchronizers?? && hotThread.lockedSynchronizers?size gt 0>
                <#list hotThread.lockedSynchronizers as lockedSynchronizer>
	<${lockedSynchronizer.identityHashCode}> (a ${lockedSynchronizer.className})
                </#list>
            <#else>
	- None
            </#if>

    </#list>
Number of Threads = ${numberOfThreads}
Deadlocked Threads = ${deadlockedThreads}
</#if>
</pre>