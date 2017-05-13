<#-- 
Copyright (C) 2016, 2017 Axel Faust
Copyright (C) 2016, 2017 Order of the Bee

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
Copyright (C) 2005-2017 Alfresco Software Limited.
 
  -->

<#include "../admin-template.ftl" />

<@page title=msg("caches.title") readonly=true customCSSFiles=["ootbee-support-tools/css/jquery.dataTables.css"]
    customJSFiles=["ootbee-support-tools/js/jquery-2.2.3.js", "ootbee-support-tools/js/jquery.dataTables.js", "ootbee-support-tools/js/caches.js"]>

<script type="text/javascript">//<![CDATA[
    AdminCA.setServiceContext('${url.serviceContext}');
//]]></script>

    <div class="column-full">
        <p class="intro">${msg("caches.intro")?html}</p>      

        <div class="control">
            <table id="caches-table" class="data results" width="100%">
                <thead>
                    <tr>
                        <th>${msg("caches.attr.name")?html}</th>
                        <th>${msg("caches.attr.type.alfresco")?html}</th>
                        <th>${msg("caches.attr.type.class")?html}</th>

                        <th>${msg("caches.attr.size")?html}</th>
                        <th>${msg("caches.attr.maxSize")?html}</th>

                        <th>${msg("caches.attr.cacheGets")?html}</th>
                        <th>${msg("caches.attr.cacheHits")?html}</th>
                        <th>${msg("caches.attr.cacheHitPercentage")?html}</th>
                        <th>${msg("caches.attr.cacheMisses")?html}</th>
                        <th>${msg("caches.attr.cacheMissPercentage")?html}</th>

                        <th>${msg("caches.attr.cacheEvictions")?html}</th>
                        <th></th>
                    </tr>
                </thead>
                <tbody>
                    <#list cacheInfos as cacheInfo>
                        <tr>
                            <td>${cacheInfo.name?html}</td>
                            <td>${(cacheInfo.definedType!msg('caches.typeNotSet'))?html}</td>
                            <td>${cacheInfo.type?html}</td>

                            <td class="numericalCellValue">${cacheInfo.size?c}</td>
                            <td class="numericalCellValue"><#if cacheInfo.maxSize &gt; 0>${cacheInfo.maxSize?c}</#if></td>

                            <td class="numericalCellValue"><#if cacheInfo.cacheGets &gt;= 0>${cacheInfo.cacheGets?c}</#if></td>
                            <td class="numericalCellValue"><#if cacheInfo.cacheHits &gt;= 0>${cacheInfo.cacheHits?c}</#if></td>
                            <td class="numericalCellValue"><#if cacheInfo.cacheHitRate &gt;= 0>${cacheInfo.cacheHitRate?string["0.0"]}</#if></td>
                            <td class="numericalCellValue"><#if cacheInfo.cacheMisses &gt;= 0>${cacheInfo.cacheMisses?c}</#if></td>
                            <td class="numericalCellValue"><#if cacheInfo.cacheMissRate &gt;= 0>${cacheInfo.cacheMissRate?string["0.0"]}</#if></td>
                            
                            <td class="numericalCellValue"><#if cacheInfo.cacheEvictions &gt;= 0>${cacheInfo.cacheEvictions?c}</#if></td>
                            <td><#if cacheInfo.clearable><a href="#" onclick="AdminCA.clearCache('${cacheInfo.name}');" title="${msg("caches.clearCache.title")?html}">${msg("caches.clearCache.label")?html}</a></#if></td>
                        </tr>
                    </#list>
                </tbody>
            </table>
        </div>
    </div>
</@page>