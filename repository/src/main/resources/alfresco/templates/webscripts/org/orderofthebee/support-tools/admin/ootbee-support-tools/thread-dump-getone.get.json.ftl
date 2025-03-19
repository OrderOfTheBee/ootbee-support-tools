<#compress>
<#-- 
Copyright (C) 2016 - 2025 Order of the Bee

This file is part of OOTBee Support Tools

OOTBee Support Tools is free software: you can redistribute it and/or modify
it under the terms of the GNU Lesser General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

OOTBee Support Tools is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public License
along with OOTBee Support Tools. If not, see <http://www.gnu.org/licenses/>.

Linked to Alfresco
Copyright (C) 2005 - 2025 Alfresco Software Limited.
 
  -->

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
</#compress>