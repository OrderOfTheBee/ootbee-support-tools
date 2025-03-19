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

<@page title=msg("systeminformation.title") readonly=true customCSSFiles=["ootbee-support-tools/css/jquery.dataTables.css"]
    customJSFiles=["ootbee-support-tools/js/jquery-2.2.3.js", "ootbee-support-tools/js/jquery.dataTables.js", "ootbee-support-tools/js/system-information.js"]>

    <div class="column-full">

        <p class="intro">${msg("systeminformation.intro")?html}</p>

        <h2 class="intro">${msg("systeminformation.table.alfrescoProperties")?html}</h2>
        <div class="section"/>
            <table id="globalProperties" class="data results" cellspacing="0" width="100%">
                <thead>
                    <tr>
                        <th>${msg("systeminformation.key")?html}</th>
                        <th>${msg("systeminformation.value")?html}</th>
                    </tr>
                </thead>
                <tbody>
                    <#if globalProperties?has_content >
                        <#list globalProperties?keys?sort as key>
                        <tr>
                            <td>${key?html}</td>
                            <td>${sanitizeValue(key, globalProperties[key], sensitiveKeys)?html}</td>
                        </tr>
                        </#list>
                     </#if>
                </tbody>
            </table>
        </div>

        <h2 class="intro">${msg("systeminformation.table.javaProperties")?html}</h2>
        <div class="section"/>
            <table id="javaProperties" class="data results" width="100%">
                <thead>
                    <tr>
                        <th>${msg("systeminformation.key")?html}</th>
                        <th>${msg("systeminformation.value")?html}</th>
                    </tr>
                </thead>
                <tbody>
                    <tr>
                        <td>${msg("systeminformation.serverStartTime")?html}</td>
                        <td>${startTime?html}</td>
                    </tr>
                    <tr>
                        <td>${msg("systeminformation.serverUptime")?html}</td>
                        <td>${upTime?html}</td>
                    </tr>
                    <tr>
                        <td>${msg("systeminformation.javaArgs")?html}</td>
                        <td>
                            <#list javaArguments as javaArgument>
                                ${sanitizeEnvValWithDashD(javaArgument, sensitiveKeys)?html}<br/>
                            </#list>
                        </td>
                    </tr>
                    <tr>
                        <td>${msg("systeminformation.bootClasspath")?html}</td>
                        <td>
                            <#list bootClassPath as bootClassPathEntry>
                                ${bootClassPathEntry?html}<br/>
                            </#list>
                        </td>
                    </tr>
                </tbody>
            </table>
        </div>

        <h2 class="intro">${msg("systeminformation.table.systemProperties")?html}</h2>
        <div class="section"/>
            <table id="systemProperties" class="data results" width="100%">
                <thead>
                    <tr>
                        <th>${msg("systeminformation.key")?html}</th>
                        <th>${msg("systeminformation.value")?html}</th>
                    </tr>
                </thead>
                <tbody>
                    <#if systemProperties?has_content >
                        <#list systemProperties?keys?sort as key>
                            <tr>
                                <td>${key?html}</td>
                                <td>${sanitizeValue(key, systemProperties[key], sensitiveKeys)?html}</td>
                            </tr>
                        </#list>
                     </#if>
                </tbody>
            </table>
        </div>

        <h2 class="intro">${msg("systeminformation.table.environmentProperties")?html}</h2>
        <div class="section"/>
            <table id="environmentProperties" class="data results" width="100%">
                <thead>
                    <tr>
                        <th>${msg("systeminformation.key")?html}</th>
                        <th>${msg("systeminformation.value")?html}</th>
                    </tr>
                </thead>
                <tbody>
                    <#if systemProperties?has_content >
                        <#list environmentProperties?keys?sort as key>
                            <tr>
                                <td>${key?html}</td>
                                <td>${sanitizeEnv(key, environmentProperties[key], sensitiveKeys)?html}</td>
                            </tr>
                        </#list>
                     </#if>
                </tbody>
            </table>
        </div>
    </div>
</@page>

<#function sanitizeValue key val sensitiveKeys>
    <#local res = val />
    <#local keySensitive = false />
    <#list sensitiveKeys as sensitiveKey>
        <#if !keySensitive && sensitiveKey?trim?has_content>
            <#local keySensitive = key?lower_case?ends_with(sensitiveKey?trim?lower_case)/>
        </#if>
    </#list>
    <#if keySensitive>
        <#local res = "***" />
    </#if>
    <#return res />
</#function>

<#function textUntilNextQuote text>
    <#local res = "" />
    <#local nextEscapedQuot = text?index_of('\\"') />
    <#local nextQuot = text?index_of('"') />
    <#if nextEscapedQuot &gt; 0 && (nextQuot == nextEscapedQuot + 1 || nextQuot &gt; nextEscapedQuot + 1)>
        <#local res = text?substring(0, nextEscapedQuot + 2) + textUntilNextQuote(text?substring(nextEscapedQuot + 2)) />
    <#elseif nextQuot != -1>
        <#local res = text?substring(0, nextQuot) />
    <#else>
        <#local res = text />
    </#if>
    <#return res />
</#function>

<#function textUntilNextWs text>
    <#local res = "" />
    <#local nextEscapedWs = text?index_of("\\ ") />
    <#local nextWs = text?index_of(" ") />
    <#if nextEscapedWs &gt; 0 && (nextWs == nextEscapedWs + 1 || nextWs &gt; nextEscapedWs + 1)>
        <#local res = text?substring(0, nextEscapedWs + 2) + textUntilNextWs(text?substring(nextEscapedWs + 2)) />
    <#elseif nextWs != -1>
        <#local res = text?substring(0, nextWs) />
    <#else>
        <#local res = text />
    </#if>
    <#return res />
</#function>

<#function sanitizeEnvValWithDashD text sensitiveKeys>
    <#local res = "" />
    <#if text?has_content>
        <#local nextDashD = text?index_of("-D") />
        <#local nextDashDQuot = text?index_of('"-D') />
        <#local remainder = "" />

        <#if nextDashD == 0>
            <#local dashDKeyVal = textUntilNextWs(text) />
            <#if text?length &gt; dashDKeyVal?length>
                <#local remainder = text?substring(dashDKeyVal?length + 1) />
            </#if>
            <#local valSep = dashDKeyVal?index_of("=") />
            <#if valSep != -1>
                <#local key = dashDKeyVal?substring(2, valSep) />
                <#local val = dashDKeyVal?substring(valSep + 1) />
                <#local res = "-D" + key + "=" + sanitizeValue(key, val, sensitiveKeys) + " " />
            <#else>
                <#local res = dashDKeyVal + " " />
            </#if>
        <#elseif nextDashDQuot == 0>
            <#local dashDKeyVal = textUntilNextQuote(text?substring(1)) />
            <#if text?length &gt; dashDKeyVal?length + 1>
                <#local remainder = text?substring(1 + dashDKeyVal?length + 1) />
            </#if>
            <#local valSep = dashDKeyVal?index_of("=") />
            <#if valSep != -1>
                <#local key = dashDKeyVal?substring(2, valSep) />
                <#local val = dashDKeyVal?substring(valSep + 1) />
                <#local res = '"-D' + key + "=" + sanitizeValue(key, val, sensitiveKeys) + '"' />
            <#else>
                <#local res = '"' + dashDKeyVal + '"' />
            </#if>
        <#elseif text?starts_with(" ")>
            <#if text?length &gt; 1>
                <#local remainder = text?substring(1) />
            </#if>
            <#local res = " " />
        <#elseif text?starts_with('"')>
            <#local quotText = textUntilNextQuote(text?substring(1)) />
            <#if text?length &gt; quotText?length + 1>
                <#local remainder = text?substring(1 + quotText?length + 1) />
            </#if>
            <#local res = '"' + quotText + '"' />
        <#else>
            <#local anyText = textUntilNextWs(text) />
            <#if text?length &gt; anyText?length>
                <#local remainder = text?substring(anyText?length + 1) />
            </#if>
            <#local res = anyText + " " />
        </#if>
        <#if remainder?has_content>
            <#local res = res +  sanitizeEnvValWithDashD(remainder, sensitiveKeys) />
        </#if>
    </#if>
    <#return res />
</#function>

<#function sanitizeEnv key val sensitiveKeys>
    <#local res = val />
    <#if val?index_of(" -D") == 0 || val?index_of("-D") != -1>
        <#local res = sanitizeEnvValWithDashD(val, sensitiveKeys) />
    <#else>
        <#local res = sanitizeValue(key, val, sensitiveKeys) />
    </#if>
    <#return res />
</#function>