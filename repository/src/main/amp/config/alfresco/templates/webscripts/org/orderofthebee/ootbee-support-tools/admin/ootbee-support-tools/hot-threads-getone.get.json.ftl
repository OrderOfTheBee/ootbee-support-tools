<#compress>
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

<#escape x as jsonUtils.encodeJSONString(x)>
{
   "myDate": "${myDate}",
   "vmName": "${vmName}",
   "vmVersion": "${vmVersion}",
   
    <#if hotThreads?? && hotThreads?size gt 0>
    "hotThreads": [
        <#list hotThreads as hotThreadsEntry>
        {
            "cpuTime": "${hotThreadsEntry.cpuTime}",
            "threadName": "${hotThreadsEntry.threadName}",
            "threadId": "${hotThreadsEntry.threadId}",
            "blockedCount": "${hotThreadsEntry.blockedCount}",
            "waitedCount": "${hotThreadsEntry.waitedCount}", 
            "waitedTime": "${hotThreadsEntry.waitedTime}",
            "threadState": "${hotThreadsEntry.threadState}",
            "stackTrace": 
            <#if hotThreadsEntry.stackTrace??>
                "${hotThreadsEntry.stackTrace}"
            <#else>
                "***Ingore this thread: this is the running process to Obtain HOTTHREADS"
            </#if>
            <#if hotThreadsEntry.lockedSynchronizers??>
            , "lockedOwnableSynchronizers" : [
                <#list hotThreadsEntry.lockedSynchronizers as lockedSynchronizersEntry>
                    {"identityHashCode": "${lockedSynchronizersEntry.identityHashCode}", "className": "${lockedSynchronizersEntry.className}"}
                    <#if lockedSynchronizersEntry_index lt hotThreadsEntry.lockedSynchronizers?size - 1>,</#if>
                </#list>
            ]
            </#if>
        }
        <#if hotThreadsEntry_index lt hotThreads?size - 1>,</#if>
        </#list>
    ],
    </#if>

   "numberOfThreads": "${numberOfThreads}",
   "deadlockedThreads": "${deadlockedThreads}"
}
</#escape>
</#compress>