/**
 * Copyright (C) 2016 Axel Faust / Markus Joos / Jens Goldhammer
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
    var ctxt, scheduler, jobsList, scheduledJobsData, scheduledJobsName, i, j, jobTriggerDetail, runningJobs, count;

    ctxt = Packages.org.springframework.web.context.ContextLoader.getCurrentWebApplicationContext();
    scheduler = ctxt.getBean('schedulerFactory', Packages.org.quartz.Scheduler);

    jobsList = scheduler.jobGroupNames;
    scheduledJobsData = [];
    scheduledJobsName = [];
    runningJobs = [];

    var executingJobs = scheduler.getCurrentlyExecutingJobs();
    for(count=0;count<executingJobs.size();count++){
        var execContext = executingJobs.get(count);
        runningJobs.push(execContext.getJobDetail().getName()+"-"+execContext.getJobDetail().getGroup());
    }

    var quartz = Packages.com.cronutils.model.CronType.QUARTZ;
    var cronDefinition = Packages.com.cronutils.model.definition.CronDefinitionBuilder.instanceDefinitionFor(quartz);
    var parser = new Packages.com.cronutils.parser.CronParser(cronDefinition);
    var descriptor = Packages.com.cronutils.descriptor.CronDescriptor.instance(java.util.Locale.UK);

    var cronExpressionDescription;
    var cronExpression;

    for (i = 0; i < jobsList.length; i++)
    {
        var jobName = scheduler.getJobNames(jobsList[i]);
        java.util.Arrays.sort(jobName);

        for (j = 0; j < jobName.length; j++)
        {
            jobTriggerDetail = scheduler.getTriggersOfJob(jobName[j], jobsList[i]);

            cronExpression = jobTriggerDetail[0].cronExpression;
            if (cronExpression) {
                cronExpressionDescription = descriptor.describe(parser.parse(cronExpression));
            }

            scheduledJobsData.push({
                jobsName : jobName[j],
                // trigger may not be cron-based
                cronExpression : jobTriggerDetail[0].cronExpression || null,
                cronExpressionDescription : cronExpressionDescription || null,
                startTime : jobTriggerDetail[0].startTime,
                previousFireTime : jobTriggerDetail[0].previousFireTime,
                nextFireTime : jobTriggerDetail[0].nextFireTime,
                timeZone : jobTriggerDetail[0].timeZone !== undefined ? jobTriggerDetail[0].timeZone.getID() : null,
                jobGroup : jobsList[i],
                running: (runningJobs.indexOf(jobName[j]+"-"+jobsList[i]) !== -1)
            });
        }
    }

    model.scheduledjobs = scheduledJobsData;
}


/**
* uses the quartz scheduler to determine the current running jobs which are made available in the model via runningJobs
*
**/

/* exported buildRunningJobsData*/
function buildRunningJobsData(){

    var ctxt, scheduler, runningJobsData, count;

    ctxt = Packages.org.springframework.web.context.ContextLoader.getCurrentWebApplicationContext();
    scheduler = ctxt.getBean('schedulerFactory', Packages.org.quartz.Scheduler);
    runningJobsData =[];

    var executingJobs = scheduler.getCurrentlyExecutingJobs();
    for(count=0;count<executingJobs.size();count++){
        var execContext = executingJobs.get(count);
        runningJobsData.push({
            jobName: execContext.getJobDetail().getName(),
            groupName: execContext.getJobDetail().getGroup()
        });
    }

    model.runningJobs = runningJobsData;

}

/* exported executeJobNow */
function executeJobNow(jobName, groupName)
{
    var ctxt, scheduler;

    ctxt = Packages.org.springframework.web.context.ContextLoader.getCurrentWebApplicationContext();
    scheduler = ctxt.getBean('schedulerFactory', Packages.org.quartz.Scheduler);
    scheduler.triggerJob(jobName, groupName);
}