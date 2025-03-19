<#compress>
<#escape x as jsonUtils.encodeJSONString(x)>
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

{
    "events" : [
        <#if events??><#list events as event>{
            "level" : "${event.level?string}",
            "loggerName" : "${event.loggerName}",
            "loggerSimpleName" : "${event.loggerName?substring(event.loggerName?last_index_of(".") + 1)}",
            "loggerCompressedName" : "${compressName(event.loggerName)}",
            "message": "${event.renderedMessage}",
            "timestamp" : {
                "raw" : "${event.timeStamp?c}",
                "iso8601" : "${xmldate(event.timeStamp?number_to_datetime)}",
                "nice" : "${event.timeStamp?number_to_datetime?string("yyyy-MM-dd HH:mm:ss:SSS")}"
            }
        }<#if event_has_next>,</#if>
        </#list></#if>
    ]
}
</#escape>
</#compress>

<#function compressName loggerName subCall = false>
    <#local loggerCompressedName = "" />
    <#if loggerName?contains('$')>
        <#local loggerCompressedName = compressName(loggerName?substring(0, loggerName?index_of('$')), true) + loggerName?substring(loggerName?index_of('$')) />
    <#else>
        <#local fragments = loggerName?split(".") />
        <#list fragments as loggerNameFragment>
            <#if loggerNameFragment_index != 0>
                <#local loggerCompressedName = loggerCompressedName + "." />
            </#if>
            <#if loggerNameFragment_index &lt; fragments?size - 2 || (subCall && loggerNameFragment_index &lt; fragments?size - 1)>
                <#local loggerCompressedName = loggerCompressedName + loggerNameFragment?substring(0, 1) />
            <#else>
                <#local loggerCompressedName = loggerCompressedName + loggerNameFragment />
            </#if>
        </#list>
    </#if>
    <#return loggerCompressedName />
</#function>