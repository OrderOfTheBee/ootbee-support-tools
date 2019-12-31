<#-- 
Copyright (C) 2016 - 2020 Order of the Bee

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
Copyright (C) 2005 - 2020 Alfresco Software Limited.
 
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
                        <#assign keySensitive = false/>
                        <#list sensitiveKeys as sensitiveKey>
                            <#if !keySensitive && sensitiveKey?trim?has_content>
                                <#assign keySensitive = key?lower_case?ends_with(sensitiveKey?trim?lower_case)/>
                            </#if>
                        </#list>
                        <tr>
                            <td>${key}</td>
                            <#if keySensitive>
                                <td>***</td>
                            <#else>
                                <td>${globalProperties[key]}</td>
                            </#if>
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
                        <td>${startTime}</td>
                    </tr>
                    <tr>
                        <td>${msg("systeminformation.serverUptime")?html}</td>
                        <td>${upTime}</td>
                    </tr>
                    <tr>
                        <td>${msg("systeminformation.javaArgs")?html}</td>
                        <td>
                            <#list javaArguments as javaArgument>
                                ${javaArgument}<br/>
                            </#list>
                        </td>
                    </tr>
                    <tr>
                        <td>${msg("systeminformation.bootClasspath")?html}</td>
                        <td>
                            <#list bootClassPath as bootClassPathEntry>
                                ${bootClassPathEntry}<br/>
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
                                <td>${key}</td>
                                <td>${systemProperties[key]}</td>
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
                                <td>${key}</td>
                                <td>${environmentProperties[key]}</td>
                            </tr>
                        </#list>
                     </#if>
                </tbody>
            </table>
        </div>
    </div>
</@page>