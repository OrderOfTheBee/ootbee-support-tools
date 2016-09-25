<#escape x as jsonUtils.encodeJSONString(x)>
{
   "myDate": "${myDate}",
   "vmName": "${vmName}",
   "vmVersion": "${vmVersion}",
   
    <#if modelThreads?? && modelThreads?size gt 0>
    "modelThreads": [
        <#list modelThreads as modelThread>
        {
            "threadName": "${modelThread.threadName}",
            "threadId": "${modelThread.threadId}",
            "blockedCount": "${modelThread.blockedCount}",
            "waitedCount": "${modelThread.waitedCount}", 
            "waitedTime": "${modelThread.waitedTime}",
            "threadState": "${modelThread.threadState}",
            "stackTrace": 
            <#if modelThread.stackTrace??>
                "${modelThread.stackTrace}"
            <#else>
                ""
            </#if>
            <#if modelThread.lockedSynchronizers??>
            , "lockedOwnableSynchronizers" : [
                <#list modelThread.lockedSynchronizers as lockedSynchronizer>
                    {"identityHashCode": "${lockedSynchronizer.identityHashCode}", "className": "${lockedSynchronizer.className}"}
                    <#if lockedSynchronizer_index lt modelThread.lockedSynchronizers?size - 1>,</#if>
                </#list>
            ]
            </#if>
        }
        <#if modelThread_index lt modelThreads?size - 1>,</#if>
        </#list>
    ],
    </#if>

   "numberOfThreads": "${numberOfThreads}",
   "deadlockedThreads": "${deadlockedThreads}"
}
</#escape>