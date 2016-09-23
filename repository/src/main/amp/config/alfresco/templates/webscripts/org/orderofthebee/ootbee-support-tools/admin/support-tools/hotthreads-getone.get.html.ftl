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