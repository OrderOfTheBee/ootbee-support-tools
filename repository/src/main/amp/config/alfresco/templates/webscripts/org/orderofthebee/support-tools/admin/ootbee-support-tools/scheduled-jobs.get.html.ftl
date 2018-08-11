<#-- 
Copyright (C) 2016 - 2018 Axel Faust / Markus Joos / Jens Goldhammer
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
  
<#include "../admin-template.ftl" />

<@page title=msg("scheduled-jobs.title") readonly=true customJSFiles=["ootbee-support-tools/js/scheduled-jobs.js","ootbee-support-tools/js/moment-with-locales.min.js"]>


    <script type="text/javascript">//<![CDATA[
        AdminSJ.setServiceUrl('${url.service}');

        AdminSJ.addMessage('running', '${msg("scheduled-jobs.state.running")?js_string}');
        AdminSJ.addMessage('notRunning', '${msg("scheduled-jobs.state.notRunning")?js_string}');
        
        moment.locale('${locale?replace('_', '-')?js_string}');
    //]]></script>

    <div class="column-full">
        <p class="intro">${msg("scheduled-jobs.intro-text")?html}</p>      
  
        <div class="control">
            <table id="jobs-table" class="data results scheduledjobs" width="100%">
                <thead>
                    <tr>
                        <th>${msg("scheduled-jobs.table-header.job-name")?html}</th>
                        <th>${msg("scheduled-jobs.table-header.trigger-name")?html}</th>
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
                        <tr id="${jobs.jobName?html}">
                            <td>${(jobs.jobDisplayName!"")?html}</td>
                            <td>${jobs.triggerName?html}</td>
                            <td title="${(jobs.cronExpressionDescription!"")?html}">${(jobs.cronExpression!"")?html}</td>
                            <td id="jobStartTime"><#if jobs.startTime??>${xmldate(jobs.startTime)?js_string}</#if></td>
                            <td id="jobPreviousFire"><#if jobs.previousFireTime??>${xmldate(jobs.previousFireTime)?html}</#if></td>
                            <td id="jobNextFire"><#if jobs.nextFireTime??>${xmldate(jobs.nextFireTime)?html}</#if></td>
                            <td>${(jobs.timeZone!"")?html}</td>
                            <td id="jobState">
                                 ${msg(jobs.running?string("scheduled-jobs.state.running", "scheduled-jobs.state.notRunning"))?html}
                            </td>
                            <td><a href="#" onclick="Admin.showDialog('${url.serviceContext}/ootbee/admin/scheduled-jobs-execute?jobName=${jobs.jobName?url('UTF-8')}&amp;groupName=${jobs.jobGroup?url('UTF-8')}');">${msg("scheduled-jobs.execute-now")?html}</a></td>
                        </tr>
                    </#list>
                </tbody>
            </table>
        </div>
    </div>

</@page>
