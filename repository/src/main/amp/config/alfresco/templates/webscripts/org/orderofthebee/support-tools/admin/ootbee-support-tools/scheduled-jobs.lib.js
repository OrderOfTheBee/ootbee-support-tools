/**
 * Copyright (C) 2016 - 2018 Axel Faust / Markus Joos / Jens Goldhammer
 * Copyright (C) 2016 - 2018 Order of the Bee
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
 * Copyright (C) 2005-2018 Alfresco Software Limited.
 */

function determineQuartzMajorVersion(scheduler)
{
    var quartzMajorVersion;
    if (scheduler.listenerManager !== undefined)
    {
        // Alfresco 6.0+
        quartzMajorVersion = 2;
    }
    else
    {
        quartzMajorVersion = 1;
    }
    return quartzMajorVersion;
}

/* exported buildScheduledJobsData */
function buildScheduledJobsData()
{
    var ctxt, scheduler, quartzMajorVersion, jobsList, scheduledJobsData, scheduledJobsName, runningJobs, executingJobs, count,
        execContext,effectiveJobName, effectiveJobGroupName, quartz, cronDefinition, parser, descriptor,i, j, jobKeys, jobTriggerDetail, 
        cronExpressionDescription, cronExpression;

    ctxt = Packages.org.springframework.web.context.ContextLoader.getCurrentWebApplicationContext();
    scheduler = ctxt.getBean('schedulerFactory', Packages.org.quartz.Scheduler);

    quartzMajorVersion = determineQuartzMajorVersion(scheduler);
    
    jobsList = quartzMajorVersion === 1 ? scheduler.jobGroupNames : scheduler.jobGroupNames.toArray();
    scheduledJobsData = [];
    scheduledJobsName = [];
    runningJobs = [];

    executingJobs = scheduler.getCurrentlyExecutingJobs();
    for (count = 0; count < executingJobs.size(); count++)
    {
        execContext = executingJobs.get(count);
        effectiveJobName = quartzMajorVersion === 1 ? execContext.jobDetail.name : execContext.jobDetail.key.name;
        effectiveJobGroupName = quartzMajorVersion === 1 ? execContext.jobDetail.group : execContext.jobDetail.key.group;
        runningJobs.push(effectiveJobName + '-' + effectiveJobGroupName);
    }

    quartz = Packages.com.cronutils.model.CronType.QUARTZ;
    cronDefinition = Packages.com.cronutils.model.definition.CronDefinitionBuilder.instanceDefinitionFor(quartz);
    parser = new Packages.com.cronutils.parser.CronParser(cronDefinition);
    descriptor = Packages.com.cronutils.descriptor.CronDescriptor.instance(Packages.org.springframework.extensions.surf.util.I18NUtil
            .getLocale());

    for (i = 0; i < jobsList.length; i++)
    {
        if (quartzMajorVersion === 1)
        {
            jobKeys = scheduler.getJobNames(jobsList[i]);
        }
        else
        {
            jobKeys = scheduler.getJobKeys(Packages.org.quartz.impl.matchers.GroupMatcher.jobGroupEquals(jobsList[i])).toArray();
        }

        for (j = 0; j < jobKeys.length; j++)
        {
            if (quartzMajorVersion === 1)
            {
                jobTriggerDetail = scheduler.getTriggersOfJob(jobKeys[j], jobsList[i]);
            }
            else
            {
                jobTriggerDetail = scheduler.getTriggersOfJob(jobKeys[j]).toArray();
            }

            cronExpression = jobTriggerDetail[0].cronExpression;
            if (cronExpression)
            {
                cronExpressionDescription = descriptor.describe(parser.parse(cronExpression));
            }

            effectiveJobName = quartzMajorVersion === 1 ? jobKeys[j] : jobKeys[j].name;
            effectiveJobGroupName = quartzMajorVersion === 1 ? jobsList[i] : jobKeys[j].group;
            scheduledJobsData.push({
                jobName : effectiveJobName,
                jobDisplayName : effectiveJobName.indexOf('org.springframework.scheduling.quartz.JobDetailFactoryBean') === 0 ? null : effectiveJobName,
                triggerName : jobTriggerDetail[0].name,
                // trigger may not be cron-based
                cronExpression : jobTriggerDetail[0].cronExpression || null,
                cronExpressionDescription : cronExpressionDescription || null,
                startTime : jobTriggerDetail[0].startTime,
                previousFireTime : jobTriggerDetail[0].previousFireTime,
                nextFireTime : jobTriggerDetail[0].nextFireTime,
                timeZone : jobTriggerDetail[0].timeZone !== undefined ? jobTriggerDetail[0].timeZone.getID() : null,
                jobGroup : jobsList[i],
                running : (runningJobs.indexOf(effectiveJobName + '-' + effectiveJobGroupName) !== -1)
            });
        }
    }

    model.scheduledjobs = scheduledJobsData;
    model.locale = Packages.org.springframework.extensions.surf.util.I18NUtil.getLocale().toString();
}

/**
 * uses the quartz scheduler to determine the current running jobs which are made available in the model via runningJobs
 * 
 */

/* exported buildRunningJobsData*/
function buildRunningJobsData()
{
    var ctxt, scheduler, quartzMajorVersion, runningJobsData, executingJobs, count, execContext, effectiveJobName, effectiveJobGroupName;

    ctxt = Packages.org.springframework.web.context.ContextLoader.getCurrentWebApplicationContext();
    scheduler = ctxt.getBean('schedulerFactory', Packages.org.quartz.Scheduler);

    quartzMajorVersion = determineQuartzMajorVersion(scheduler);

    runningJobsData = [];

    executingJobs = scheduler.getCurrentlyExecutingJobs();
    for (count = 0; count < executingJobs.size(); count++)
    {
        execContext = executingJobs.get(count);

        effectiveJobName = quartzMajorVersion === 1 ? execContext.jobDetail.name : execContext.jobDetail.key.name;
        effectiveJobGroupName = quartzMajorVersion === 1 ? execContext.jobDetail.group : execContext.jobDetail.key.group;

        runningJobsData.push({
            jobName : effectiveJobName,
            groupName : effectiveJobGroupName
        });
    }

    model.runningJobs = runningJobsData;
}

/* exported executeJobNow */
function executeJobNow(jobName, groupName)
{
    var ctxt, scheduler, quartzMajorVersion, jobKey;

    ctxt = Packages.org.springframework.web.context.ContextLoader.getCurrentWebApplicationContext();
    scheduler = ctxt.getBean('schedulerFactory', Packages.org.quartz.Scheduler);

    quartzMajorVersion = determineQuartzMajorVersion(scheduler);

    if (quartzMajorVersion === 1)
    {
        scheduler.triggerJob(jobKey);
    }
    else
    {
        jobKey = new Packages.org.quartz.JobKey(jobName, groupName);
        scheduler.triggerJob(jobKey);
    }
}
