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

<#include "../admin-template.ftl" />

<@page title=msg("log-settings.logFiles.title") dialog=true customJSFiles=["ootbee-support-tools/js/log-files.js", "ootbee-support-tools/js/moment-with-locales.min.js"]>
    
    <script type="text/javascript">//<![CDATA[
    moment.locale('${locale?replace('_', '-')?js_string}');
    //]]></script>
    
    <div class="column-full">

        <table id="log-files-table" class="results">
            <tr>
                <th>${msg("log-settings.logFile")?html}</th>
                <th>${msg("log-settings.path")?html}</th>
                <th>${msg("log-settings.size")?html}</th>
                <th>${msg("log-settings.lastModified")?html}</th>
                <th></th>
            </tr>
            <#if logFiles??><#list logFiles as logFile>
                <tr>
                    <td>${logFile.name?html}</td>
                    <td>${logFile.path?html}</td>
                    <td>${logFile.size?c}</td>
                    <td>${xmldate(logFile.lastModified)?html}</td>
                    <td><a href="${url.serviceContext}/ootbee/admin/log4j-log-file?path=${logFile.path?url('UTF-8')}&logFile=${logFile.name?url('UTF-8')}">${msg("log-settings.download")?html}</a></td>
                </tr>
            </#list></#if>
        </table>

        <@dialogbuttons />
    </div>

</@page>