<#-- 
Copyright (C) 2017 Cesar Capillas
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
<#include "../admin-template.ftl" />

<#function iecBinaryUnitSize value>
    <#local result = '' />
    <#if value &gt;= (1024 * 1024 * 1024)>
        <#local result = msg('solr-tracking.gibibytes', (value/1024/1024/1024)?string('0.00')) />
    <#elseif value &gt;= (1024 * 1024)>
        <#local result = msg('solr-tracking.mebibytes', (value/1024/1024)?string('0.00')) />
    <#elseif value &gt;= 1024>
        <#local result = msg('solr-tracking.kibibytes', (value/1024)?string('0.00')) />
    <#else>
        <#local result = msg('solr-tracking.bytes', value?string('0.00')) />
    </#if>
    <#return result />
</#function>

<@page title=msg("solr-tracking.title") readonly=true>

    <#list coreNames as coreName>
        <div class="column-full">
            <#assign coreLabel= msg("solr-tracking.section." + coreName + ".title") />
            <#if coreLabel == "solr-tracking.section." + coreName + ".title">
                <#assign coreLabel= msg("solr-tracking.section.genericCore.title", coreName) />
            </#if>
            <@section label=coreLabel />
        </div>

        <div class="column-full">
            <div class="column-left">
                <@field value="${trackingStatus[coreName]['index']['numDocs']?c}"  label=msg("solr-tracking.section.numdocs.title") description=msg("solr-tracking.section.numdocs.description") />  
                <@field value="${trackingStatus[coreName]['index']['deletedDocs']?c}" label=msg("solr-tracking.section.deleted.title") description=msg("solr-tracking.section.deleted.description") />  
                <#if trackingStatus[coreName]['index']['indexHeapUsageBytes'] != -1>
                    <@field value="${iecBinaryUnitSize(trackingStatus[coreName]['index']['indexHeapUsageBytes'])}" label=msg("solr-tracking.section.memory.usage.title") description=msg("solr-tracking.section.memory.usage.description")/>
                </#if>
            </div>

            <div class="column-right">   
                <@field value="${trackingStatus[coreName]['index']['maxDoc']?c}"  label=msg("solr-tracking.section.maxdocs.title") description=msg("solr-tracking.section.maxdocs.description") />  
                <@field value="${iecBinaryUnitSize(trackingStatus[coreName]['index']['sizeInBytes'])}" label=msg("solr-tracking.section.disk.usage.title") description=msg("solr-tracking.section.disk.usage.description") />          
            </div>
        </div>

        <#-- reset after uneven field count -->
        <div class="column-full">
            <div class="column-left">
                <@field value="${trackingSummary[coreName]['MetadataTracker Active']?string(msg('solr-tracking.true'), msg('solr-tracking.false'))}" label=msg("solr-tracking.section.detail.metadata.title") description=msg("solr-tracking.section.detail.metadata.description")/>
                <@field value="${trackingSummary[coreName]['Approx transactions remaining']?c}" label=msg("solr-tracking.section.detail.metadata.count.title") description=msg("solr-tracking.section.detail.metadata.count.description") />
                <@field value="${trackingSummary[coreName]['Approx transaction indexing time remaining']}" label=msg("solr-tracking.section.approx.time.title") description=msg("solr-tracking.section.approx.time.description") />  
            </div>

            <div class="column-right">
                <@field value="${trackingSummary[coreName]['Id for last TX in index']?c}" label=msg("solr-tracking.section.last.transaction.title") description=msg("solr-tracking.section.last.transaction.description") />  
                <@field value="${trackingSummary[coreName]['TX Lag']}" label=msg("solr-tracking.section.index.lag.title") description=msg("solr-tracking.section.index.lag.description")/> 
            </div>
        </div>

        <#-- reset after uneven field count -->
        <div class="column-full">
            <div class="column-left">
                <@field value="${trackingSummary[coreName]['AclTracker Active']?string(msg('solr-tracking.true'), msg('solr-tracking.false'))}" label=msg("solr-tracking.section.detail.acl.title") description=msg("solr-tracking.section.detail.acl.description")/>
                <@field value="${trackingSummary[coreName]['ContentTracker Active']?string(msg('solr-tracking.true'), msg('solr-tracking.false'))}" label=msg("solr-tracking.section.detail.content.title") description=msg("solr-tracking.section.detail.content.description")/>
                <@field value="${(cascadeTracker[coreName] > 0)?string(msg('solr-tracking.true'), msg('solr-tracking.false'))}" label=msg("solr-tracking.section.detail.cascade.title") description=msg("solr-tracking.section.detail.cascade.description")/>
            </div>

            <div class="column-right">
                <@field value="${trackingSummary[coreName]['Approx change sets remaining']?c}" label=msg("solr-tracking.section.detail.acl.count.title") description=msg("solr-tracking.section.detail.acl.count.description") />
                <#if trackingSummary[coreName]['FTS']['Node count whose content needs to be updated']?has_content>
                    <@field value="${trackingSummary[coreName]['FTS']['Node count whose content needs to be updated']?c}" label=msg("solr-tracking.section.detail.content.count.title") description=msg("solr-tracking.section.detail.content.count.description")/>
                <#else>
                    <@field value="${(trackingSummary[coreName]['FTS']['Node count with FTSStatus New'] + trackingSummary[coreName]['FTS']['Node count with FTSStatus Dirty'])?c}" label=msg("solr-tracking.section.detail.content.count.title") description=msg("solr-tracking.section.detail.content.count.description")/>
                </#if>
                <@field value="${cascadeTracker[coreName]?c}" label=msg("solr-tracking.section.detail.cascade.count.title") description=msg("solr-tracking.section.detail.cascade.count.description") />
            </div>
        </div>

    </#list>
</@page>