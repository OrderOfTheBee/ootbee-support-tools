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
<#list cacheInfos as cacheInfo>
# HELP alfresco_cache_${cacheInfo.name} Cache metrics for the ${cacheInfo.name} (${cacheInfo.type})
# TYPE alfresco_active_${cacheInfo.name} gauge
alfresco_active_${cacheInfo.name}{type="size"} ${cacheInfo.size?c}}
alfresco_active_${cacheInfo.name}{type="maxSize"} <#if cacheInfo.maxSize &gt; 0>${cacheInfo.maxSize?c}<#else>NaN</#if>
alfresco_active_${cacheInfo.name}{type="cacheGets"} <#if cacheInfo.cacheGets &gt;= 0>${cacheInfo.cacheGets?c}<#else>NaN</#if>
alfresco_active_${cacheInfo.name}{type="cacheHits"} <#if cacheInfo.cacheHits &gt;= 0>${cacheInfo.cacheHits?c}<#else>NaN</#if>
alfresco_active_${cacheInfo.name}{type="cacheHitRate"} <#if cacheInfo.cacheHitRate &gt;= 0>${cacheInfo.cacheHitRate?string["0.#"]}<#else>NaN</#if>
alfresco_active_${cacheInfo.name}{type="cacheMisses"} <#if cacheInfo.cacheMisses &gt;= 0>${cacheInfo.cacheMisses?c}<#else>NaN</#if>
alfresco_active_${cacheInfo.name}{type="cacheMissRate"} <#if cacheInfo.cacheMissRate &gt;= 0>${cacheInfo.cacheMissRate?string["0.#"]}<#else>NaN</#if>
alfresco_active_${cacheInfo.name}{type="cacheEvictions"} <#if cacheInfo.cacheEvictions &gt;= 0>${cacheInfo.cacheEvictions?c}<#else>NaN</#if>
</#list>
</#compress>
