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
    <#-- close the dummy form -->
    </form>
    
    <script type="text/javascript">//<![CDATA[
    AdminLF.setServiceContext('${url.serviceContext}');
    
    moment.locale('${locale?replace('_', '-')?js_string}');
    //]]></script>
    
    <div class="column-full">
        <form action="${url.serviceContext}/ootbee/admin/log4j-log-files.zip" method="POST" enctype="multipart/form-data" accept-charset="utf-8">
            <table id="log-files-table" class="results">
                <tr>
                    <th></th>
                    <th>${msg("log-settings.logFile")?html}</th>
                    <th>${msg("log-settings.path")?html}</th>
                    <th>${msg("log-settings.size")?html}</th>
                    <th>${msg("log-settings.lastModified")?html}</th>
                    <th></th>
                    <th></th>
                </tr>
                <#if logFiles??><#list logFiles as logFile>
                    <tr id="log-row-${logFile_index?c}">
                        <td><input id="log-row-${logFile_index?c}-check" type="checkbox" name="paths" value="${logFile.path?html}" /></td>
                        <td>${logFile.name?html}</td>
                        <td>${logFile.directoryPath?html}</td>
                        <td>${logFile.size?c}</td>
                        <td>${xmldate(logFile.lastModified)?html}</td>
                        <#assign pathFragments = logFile.path?split('/') />
                        <#assign urlPath = "" />
                        <#list pathFragments as pathFragment>
                            <#assign urlPath = urlPath + pathFragment?url('UTF-8')?replace(':', '%3A', 'f') />
                            <#if pathFragment_has_next><#assign urlPath = urlPath + '/' /></#if>
                        </#list>
                        <td><a href="${url.serviceContext}/ootbee/admin/log4j-log-file/${urlPath}" target="_blank">${msg("log-settings.download")?html}</a></td>
                        <td><a href="#" onclick="AdminLF.deleteLogFile('${logFile.path?js_string}', 'log-row-${logFile_index?c}');">${msg("log-settings.delete")?html}</a></td>
                    </tr>
                </#list></#if>
            </table>
            <@dialogbuttons>
                <input type="submit" value="${msg("log-settings.downloadZIP")}"/>
            </@>
        </form>

    </div>

</@page>