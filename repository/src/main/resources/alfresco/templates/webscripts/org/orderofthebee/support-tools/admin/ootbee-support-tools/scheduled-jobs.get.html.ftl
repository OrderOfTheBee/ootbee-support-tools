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
                        <th>${msg("scheduled-jobs.table-header.trigger-name")?html}</th>
                        <th title="${msg("scheduled-jobs.table-header.trigger-group-title")?html}">${msg("scheduled-jobs.table-header.trigger-group")?html}</th>
                        <th title="${msg("scheduled-jobs.table-header.trigger-state-title")?html}">${msg("scheduled-jobs.table-header.trigger-state")?html}</th>
                        <th>${msg("scheduled-jobs.table-header.job-name")?html}</th>
                        <th title="${msg("scheduled-jobs.table-header.job-group-title")?html}">${msg("scheduled-jobs.table-header.job-group")?html}</th>
                        <th title="${msg("scheduled-jobs.table-header.job-state-title")?html}">${msg("scheduled-jobs.table-header.job-state")?html}</th>
                        <th>${msg("scheduled-jobs.table-header.cron-expression")?html}</th>				
                        <th>${msg("scheduled-jobs.table-header.start-time")?html}</th>
                        <th>${msg("scheduled-jobs.table-header.previous-fire-time")?html}</th>
                        <th>${msg("scheduled-jobs.table-header.next-fire-time")?html}</th>
                        <th>${msg("scheduled-jobs.table-header.time-zone")?html}</th>
                        <th></th>
                    </tr>
                </thead>
                <tbody>
                    <#assign stateMessages = [
                        msg('scheduled-jobs.table-header.trigger-state.none'),
                        msg('scheduled-jobs.table-header.trigger-state.normal'),
                        msg('scheduled-jobs.table-header.trigger-state.paused'),
                        msg('scheduled-jobs.table-header.trigger-state.complete'),
                        msg('scheduled-jobs.table-header.trigger-state.error'),
                        msg('scheduled-jobs.table-header.trigger-state.blocked')] />
                    <#list jobTriggers as trigger>
                        <tr id="${trigger.triggerName?html}">
                            <td name="triggerName">${trigger.triggerName?html}</td>
                            <td name="triggerGroup">${trigger.triggerGroup?html}</td>
                            <#-- offset by one as states have value space -1 (none) to 4 (blocked) -->
                            <td name="triggerState">${stateMessages[trigger.triggerState]?html}</td>
                            <td name="jobName" data-technicalName="${trigger.jobName?html}">${(trigger.jobDisplayName!"")?html}</td>
                            <td name="jobGroup">${trigger.jobGroup?html}</td>
                            <td name="jobState">
                                 ${msg(trigger.running?string("scheduled-jobs.state.running", "scheduled-jobs.state.notRunning"))?html}
                            </td>
                            <td title="${(trigger.cronExpressionDescription!"")?html}">${(trigger.cronExpression!"")?html}</td>
                            <td name="jobStartTime"><#if trigger.startTime??>${xmldate(trigger.startTime)?html}</#if></td>
                            <td name="jobPreviousFire"><#if trigger.previousFireTime??>${xmldate(trigger.previousFireTime)?html}</#if></td>
                            <td name="jobNextFire"><#if trigger.nextFireTime??>${xmldate(trigger.nextFireTime)?html}</#if></td>
                            <td>${(trigger.timeZone!"")?html}</td>
                            <td>
                                <p><a href="#" onclick="Admin.showDialog('${url.serviceContext}/ootbee/admin/scheduled-jobs-execute?jobName=${trigger.jobName?url('UTF-8')}&amp;groupName=${trigger.jobGroup?url('UTF-8')}');">${msg("scheduled-jobs.execute-now")?html}</a></p>
                                <#if trigger.triggerState == 1>
                                    <p><a href="#" onclick="AdminSJ.pauseTrigger('${trigger.triggerName?js_string}', '${trigger.triggerGroup?js_string}');">${msg("scheduled-jobs.pause-trigger")?html}</a></p>
                                <#elseif trigger.triggerState == 2>
                                    <p><a href="#" onclick="AdminSJ.resumeTrigger('${trigger.triggerName?js_string}', '${trigger.triggerGroup?js_string}');">${msg("scheduled-jobs.resume-trigger")?html}</a></p>
                                </#if>
                            </td>
                        </tr>
                    </#list>
                </tbody>
            </table>
        </div>
    </div>

</@page>
