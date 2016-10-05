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

function executeJobNow(jobName, groupName)
{
    var ctxt, scheduler;

    ctxt = Packages.org.springframework.web.context.ContextLoader.getCurrentWebApplicationContext();
    scheduler = ctxt.getBean('schedulerFactory', Packages.org.quartz.Scheduler);

    scheduler.triggerJob(jobName, groupName)
}
