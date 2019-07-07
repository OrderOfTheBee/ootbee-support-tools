<#compress>
<#-- 
Copyright (C) 2016 - 2018 Axel Faust
Copyright (C) 2016 - 2018 Order of the Bee

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
Copyright (C) 2005-2018 Alfresco Software Limited.
 
  -->
<#escape x as jsonUtils.encodeJSONString(x)>
{
    "caches": [
    <#list cacheInfos as cacheInfo>
        {
            "name": "${cacheInfo.name}",
            "definedType": "${(cacheInfo.definedType!msg('caches.typeNotSet'))}",
            "type": "${cacheInfo.type}",
            "shortType": "${compressName(cacheInfo.type)}",
            "size": ${cacheInfo.size?c},
            <#if cacheInfo.maxSize &gt; 0>"maxSize": ${cacheInfo.maxSize?c},</#if>
            <#if cacheInfo.cacheGets &gt;= 0>"cacheGets": ${cacheInfo.cacheGets?c},</#if>
            <#if cacheInfo.cacheHits &gt;= 0>"cacheHits": ${cacheInfo.cacheHits?c},</#if>
            <#if cacheInfo.cacheHitRate &gt;= 0>"cacheHitRate": ${cacheInfo.cacheHitRate?c},</#if>
            <#if cacheInfo.cacheMisses &gt;= 0>"cacheMisses": ${cacheInfo.cacheMisses?c},</#if>
            <#if cacheInfo.cacheMissRate &gt;= 0>"cacheMissRate": ${cacheInfo.cacheMissRate?c},</#if>
            <#if cacheInfo.cacheEvictions &gt;= 0>"cacheEvictions": ${cacheInfo.cacheEvictions?c},</#if>
            "clearable": ${cacheInfo.clearable?string}
        }<#if cacheInfo_has_next>,</#if>
    </#list>
    ]
}
</#escape>
</#compress>

<#function compressName className subCall = false>
    <#local compressedName = "" />
    <#if className?contains('$')>
        <#local compressedName = compressName(className?substring(0, className?index_of('$')), true) + className?substring(className?index_of('$')) />
    <#else>
        <#local fragments = className?split(".") />
        <#list fragments as classNameFragment>
            <#if classNameFragment_index != 0>
                <#local compressedName = compressedName + "." />
            </#if>
            <#if classNameFragment_index &lt; fragments?size - 2 || (subCall && classNameFragment_index &lt; fragments?size - 1)>
                <#local compressedName = compressedName + classNameFragment?substring(0, 1) />
            <#else>
                <#local compressedName = compressedName + classNameFragment />
            </#if>
        </#list>
    </#if>
    <#return compressedName />
</#function>