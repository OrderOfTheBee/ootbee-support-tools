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

<@page title=msg("scheduled-jobs.title") readonly=true customJSFiles=["ootbee-support-tools/js/scheduled-jobs.js"]>


    <script type="text/javascript">//<![CDATA[
        AdminSJ.setServiceUrl('${url.service}');
        AdminSJ.setServiceContext('${url.serviceContext}');

        AdminSJ.addMessage('running', '${msg("scheduled-jobs.state.running")?js_string}');
        AdminSJ.addMessage('notRunning', '${msg("scheduled-jobs.state.notRunning")?js_string}');
    //]]></script>

    <div class="column-full">
        <p class="intro">${msg("scheduled-jobs.intro-text")?html}</p>      
  
        <div class="control">
            <table id="jobs-table" class="data results scheduledjobs" width="100%">
                <thead>
                    <tr>
                        <th>${msg("scheduled-jobs.table-header.job-name")?html}</th>
                        <th>${msg("scheduled-jobs.table-header.cron-expression")?html}</th>				
                        <th>${msg("scheduled-jobs.table-header.start-time")?html}</th>
                        <th>${msg("scheduled-jobs.table-header.previous-fire-time")?html}</th>
                        <th>${msg("scheduled-jobs.table-header.next-fire-time")?html}</th>
                        <th>${msg("scheduled-jobs.table-header.time-zone")?html}</th>
                        <th>${msg("scheduled-jobs.table-header.state")?html}</th>
                        <th></th>
                    </tr>
                </thead>
                <tbody>
                    <#list scheduledjobs as jobs>
                        <tr id="${jobs.jobsName}">
                            <td>${jobs.jobsName}</td>
                            <td>${jobs.cronExpression!""}</td>
                            <td><#if jobs.startTime??>${xmldate(jobs.startTime)?html}</#if></td>
                            <td><#if jobs.previousFireTime??>${xmldate(jobs.previousFireTime)?html}</#if></td>
                            <td><#if jobs.nextFireTime??>${xmldate(jobs.nextFireTime)?html}</#if></td>
                            <td>${(jobs.timeZone!"")?html}</td>
                            <#if jobs.running==true>
                            <td id="jobState">
                                 ${msg("scheduled-jobs.state.running")?html}
                            </td>
                            <#else>
                            <td id="jobState">
                                 ${msg("scheduled-jobs.state.notRunning")?html}
                            </#if>
                            </td>
                            <td><a href="#" onclick="Admin.showDialog('${url.serviceContext}/ootbee/admin/scheduled-jobs-execute?jobName=${jobs.jobsName?url('UTF-8')}&amp;groupName=${jobs.jobGroup?url('UTF-8')}');">${msg("scheduled-jobs.execute-now")?html}</a></td>
                        </tr>
                    </#list>
                </tbody>
            </table>
        </div>
    </div>

</@page>