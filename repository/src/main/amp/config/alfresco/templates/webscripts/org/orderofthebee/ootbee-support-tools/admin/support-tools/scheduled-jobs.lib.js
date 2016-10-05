/**
 * Copyright (C) 2016 Axel Faust / Markus Joos
 * Copyright (C) 2016 Order of the Bee
 *
 * This file is part of Community Support Tools
 *
 * Community Support Tools is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Community Support Tools is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Community Support Tools. If not, see <http://www.gnu.org/licenses/>.
 */
/*
 * Linked to Alfresco
 * Copyright (C) 2005-2016 Alfresco Software Limited.
 */

/* exported buildScheduledJobsData */
function buildScheduledJobsData()
{
    var ctxt, scheduler, jobsList, scheduledJobsData, scheduledJobsName, i, j, jobTriggerDetail;

    ctxt = Packages.org.springframework.web.context.ContextLoader.getCurrentWebApplicationContext();
    scheduler = ctxt.getBean('schedulerFactory', Packages.org.quartz.Scheduler);

    jobsList = scheduler.jobGroupNames;
    scheduledJobsData = [];
    scheduledJobsName = [];

    for (i = 0; i < jobsList.length; i++)
    {
        var jobName = scheduler.getJobNames(jobsList[i]);

        for (j = 0; j < jobName.length; j++)
        {
            jobTriggerDetail = scheduler.getTriggersOfJob(jobName[j], jobsList[i]);

            scheduledJobsData.push({
                jobsName : jobName[j],
                // trigger may not be cron-based
                cronExpression : jobTriggerDetail[0].cronExpression || null,
                startTime : jobTriggerDetail[0].startTime,
                previousFireTime : jobTriggerDetail[0].previousFireTime,
                nextFireTime : jobTriggerDetail[0].nextFireTime,
                timeZone : jobTriggerDetail[0].timeZone !== undefined ? jobTriggerDetail[0].timeZone.getID() : null,
                jobGroup : jobsList[i]
            });
        }
    }

    model.scheduledjobs = scheduledJobsData;
}

/* exported executeJobNow */
function executeJobNow(jobName, groupName)
{
    var ctxt, scheduler;

    ctxt = Packages.org.springframework.web.context.ContextLoader.getCurrentWebApplicationContext();
    scheduler = ctxt.getBean('schedulerFactory', Packages.org.quartz.Scheduler);

    scheduler.triggerJob(jobName, groupName);
}