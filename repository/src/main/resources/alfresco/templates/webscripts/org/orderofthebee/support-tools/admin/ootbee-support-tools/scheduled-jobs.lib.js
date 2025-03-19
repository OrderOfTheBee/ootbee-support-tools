/**
 * Copyright (C) 2016 - 2025 Order of the Bee
 *
 * This file is part of OOTBee Support Tools
 *
 * OOTBee Support Tools is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OOTBee Support Tools is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with OOTBee Support Tools. If not, see <http://www.gnu.org/licenses/>.
 */
/*
 * Linked to Alfresco
 * Copyright (C) 2005 - 2025 Alfresco Software Limited.
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
    var ctxt, scheduler, quartzMajorVersion, jobsList, jobTriggers, scheduledJobsName, runningJobs, executingJobs, count,
        execContext, effectiveJobName, effectiveJobGroupName, quartz, cronDefinition, parser, descriptor, i, jobKeys, j,
        jobTriggerDetails, k, jobTriggerDetail, triggerState, cronExpression, cronExpressionDescription;

    ctxt = Packages.org.springframework.web.context.ContextLoader.getCurrentWebApplicationContext();
    scheduler = ctxt.getBean('schedulerFactory', Packages.org.quartz.Scheduler);

    quartzMajorVersion = determineQuartzMajorVersion(scheduler);
    
    jobsList = quartzMajorVersion === 1 ? scheduler.jobGroupNames : scheduler.jobGroupNames.toArray();
    jobTriggers = [];
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
                jobTriggerDetails = scheduler.getTriggersOfJob(jobKeys[j], jobsList[i]);
            }
            else
            {
                jobTriggerDetails = scheduler.getTriggersOfJob(jobKeys[j]).toArray();
            }

            for (k = 0; k < jobTriggerDetails.length; k++)
            {
                jobTriggerDetail = jobTriggerDetails[k];
                triggerState = quartzMajorVersion === 1 ? scheduler.getTriggerState(jobTriggerDetail.name, jobTriggerDetail.group) : scheduler.getTriggerState(jobTriggerDetail.key);
                cronExpression = jobTriggerDetail.cronExpression;
                cronExpressionDescription = null;
                if (cronExpression)
                {
                    try
                    {
                        cronExpressionDescription = descriptor.describe(parser.parse(cronExpression));
                    }
                    catch (e)
                    {
                        cronExpressionDescription = msg.get('scheduled-jobs.table-header.cron-expression.unparseable', [ cronExpression ]);
                    }
                }

                effectiveJobName = quartzMajorVersion === 1 ? jobTriggerDetail.jobName : jobTriggerDetail.jobKey.name;
                effectiveJobGroupName = quartzMajorVersion === 1 ? jobTriggerDetail.jobGroup : jobTriggerDetail.jobKey.group;
                jobTriggers.push({
                    triggerName : quartzMajorVersion === 1 ? jobTriggerDetail.name : jobTriggerDetail.key.name,
                    triggerGroup : quartzMajorVersion === 1 ? jobTriggerDetail.group : jobTriggerDetail.key.group,
                    // Quartz 1.x trigger state is -1 based (for none) while Quartz 2.x is an enum starting at 0
                    // standardise on 0 as base
                    triggerState : quartzMajorVersion === 1 ? (triggerState + 1) : triggerState.ordinal(),
                    jobName : effectiveJobName,
                    jobDisplayName : effectiveJobName.indexOf('org.springframework.scheduling.quartz.JobDetailFactoryBean') === 0 ? null : effectiveJobName,
                    jobGroup : effectiveJobGroupName,
                    // trigger may not be cron-based
                    cronExpression : cronExpression || null,
                    cronExpressionDescription : cronExpressionDescription || null,
                    startTime : jobTriggerDetail.startTime,
                    previousFireTime : jobTriggerDetail.previousFireTime,
                    nextFireTime : jobTriggerDetail.nextFireTime,
                    timeZone : (jobTriggerDetail.timeZone !== undefined && jobTriggerDetail.timeZone !== null) ? jobTriggerDetail.timeZone.getID() : null,
                    running : (runningJobs.indexOf(effectiveJobName + '-' + effectiveJobGroupName) !== -1)
                });
            }
        }
    }

    jobTriggers.sort(function(a, b)
    {
        var res;
        res = a.triggerGroup.localeCompare(b.triggerGroup);
        if (res === 0)
        {
            res = a.triggerName.localeCompare(b.triggerName);
        }
        return res;
    });

    model.jobTriggers = jobTriggers;
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
function executeJobNow(jobName, jobGroup)
{
    var ctxt, scheduler, quartzMajorVersion, jobKey;

    ctxt = Packages.org.springframework.web.context.ContextLoader.getCurrentWebApplicationContext();
    scheduler = ctxt.getBean('schedulerFactory', Packages.org.quartz.Scheduler);
    quartzMajorVersion = determineQuartzMajorVersion(scheduler);
    
    if (quartzMajorVersion === 1)
    {
        scheduler.triggerJob(jobName, jobGroup);
    }
    else
    {
        jobKey = new Packages.org.quartz.JobKey(jobName, jobGroup);
        scheduler.triggerJob(jobKey);
    }
}

/* exported pauseTrigger */
function pauseTrigger(triggerName, triggerGroup)
{
    var ctxt, scheduler, quartzMajorVersion, triggerKey;

    ctxt = Packages.org.springframework.web.context.ContextLoader.getCurrentWebApplicationContext();
    scheduler = ctxt.getBean('schedulerFactory', Packages.org.quartz.Scheduler);
    quartzMajorVersion = determineQuartzMajorVersion(scheduler);
    
    if (quartzMajorVersion === 1)
    {
        scheduler.pauseTrigger(triggerName, triggerGroup);
    }
    else
    {
        triggerKey = new Packages.org.quartz.TriggerKey(triggerName, triggerGroup);
        scheduler.pauseTrigger(triggerKey);
    }
}

/* exported resumeTrigger */
function resumeTrigger(triggerName, triggerGroup)
{
    var ctxt, scheduler, quartzMajorVersion, triggerKey;

    ctxt = Packages.org.springframework.web.context.ContextLoader.getCurrentWebApplicationContext();
    scheduler = ctxt.getBean('schedulerFactory', Packages.org.quartz.Scheduler);
    quartzMajorVersion = determineQuartzMajorVersion(scheduler);
    
    if (quartzMajorVersion === 1)
    {
        scheduler.resumeTrigger(triggerName, triggerGroup);
    }
    else
    {
        triggerKey = new Packages.org.quartz.TriggerKey(triggerName, triggerGroup);
        scheduler.resumeTrigger(triggerKey);
    }
}