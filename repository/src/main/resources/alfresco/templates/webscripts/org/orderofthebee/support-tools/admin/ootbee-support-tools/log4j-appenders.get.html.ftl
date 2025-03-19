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

<#include "../admin-template.ftl" />

<@page title=msg("log-settings.appenders.title", (logger == '-root-')?string(msg('log-settings.rootLogger'), logger) ) dialog=true customCSSFiles=["ootbee-support-tools/css/log-appenders.css"]>

    <div class="column-full">

        <ol>
            <#list appenders as appender>
                <li class="appenderDetails">
                    <@renderModelDetails appender />
                </li>
            </#list>
        </ol>

        <@dialogbuttons />
    </div>

</@page>

<#macro renderModelDetails modelObject>
    <#if modelObject.name??>
        <div class="detail">
            <div class="label">name:</div>
            <div class="value">${modelObject.name?html}</div>
        </div>
    </#if>
    <#if modelObject.class??>
        <div class="detail">
            <div class="label">class:</div>
            <div class="value">${modelObject.class?html}</div>
        </div>
    </#if>
    
    <#list modelObject?keys as key>
        <#if key != 'name' && key != 'class'>
            <div class="detail">
                <div class="label">${key?html}:</div>
                <div class="value">
                    <#if modelObject[key]??>
                        <@renderValue modelObject[key] />
                    <#else>
                        null
                    </#if>
                </div>
            </div>
        </#if>
    </#list>
</#macro>

<#macro renderValue value>
    <#if value?is_string || value?is_number || value?is_boolean || !value?is_hash>
        <#if value?is_number>
            ${value?c}
        <#elseif value?is_boolean>
            ${value?string(msg('log-settings.true'), msg('log-settings.false'))}
        <#elseif value?is_sequence>
            <ol>
                <#list value as element>
                    <li><#if element??><@renderValue element /><#else>null</#if></li>
                </#list>
            </ol>
        <#elseif !value?is_string>
            ${value?string?html}
        <#else>
            ${value?html}
        </#if>
    <#elseif value?is_hash>
        <div class="complex">
            <@renderModelDetails value />
        </div>
    </#if>
</#macro>