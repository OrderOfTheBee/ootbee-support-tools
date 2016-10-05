<#include "../admin-template.ftl" />

<@page title=msg("scheduled-jobs.title") readonly=true>

    <div class="column-full">
        <p class="intro">${msg("scheduled-jobs.intro-text")?html}</p>      
  
        <div class="control">
            <table class="data scheduledjobs" width="100%">
                <thead>
                    <tr>
                        <th>${msg("scheduled-jobs.table-header.job-name")?html}</th>
                        <th>${msg("scheduled-jobs.table-header.cron-expression")?html}</th>				
                        <th>${msg("scheduled-jobs.table-header.start-time")?html}</th>
                        <th>${msg("scheduled-jobs.table-header.previous-fire-time")?html}</th>
                        <th>${msg("scheduled-jobs.table-header.next-fire-time")?html}</th>
                        <th>${msg("scheduled-jobs.table-header.time-zone")?html}</th>
                        <th></th>
                    </tr>
                </thead>
                <tbody>
                    <#list scheduledjobs as jobs>
                        <tr>	
                            <td>${jobs.jobsName}</td>
                            <td>${jobs.cronExpression!""}</td>
                            <td><#if jobs.startTime??>${xmldate(jobs.startTime)?html}</#if></td>
                            <td><#if jobs.previousFireTime??>${xmldate(jobs.previousFireTime)?html}</#if></td>
                            <td><#if jobs.nextFireTime??>${xmldate(jobs.nextFireTime)?html}</#if></td>
                            <td>${(jobs.timeZone!"")?html}</td>
                            <td><a href="#" onclick="Admin.showDialog('${url.serviceContext}/ootbee/admin/scheduled-jobs-execute?jobName=${jobs.jobsName?url('UTF-8')}&amp;groupName=${jobs.jobGroup?url('UTF-8')}');">${msg("scheduled-jobs.execute-now")?html}</a>
                        </tr>
                    </#list>
                </tbody>
            </table>
        </div>
    </div>

</@page>