/**
 * Copyright (C) 2016 - 2025 Order of the Bee
 *
 * This file is part of OOTBee Support Tools
 *
 * OOTBee Support Tools is free software: you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 *
 * OOTBee Support Tools is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser
 * General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with OOTBee Support Tools. If not, see
 * <http://www.gnu.org/licenses/>.
 *
 * Linked to Alfresco
 * Copyright (C) 2005 - 2025 Alfresco Software Limited.
 *
 * This file is part of code forked from the alfresco-jscript-extensions project
 * by Jens Goldhammer, which was licensed under the Apache License, Version 2.0.
 * In accordance with that license, the modifications / derivative work
 * is now being licensed under the LGPL as part of the OOTBee Support Tools
 * addon.
 */
package org.orderofthebee.addons.support.tools.repo.jscript.jobs;

import com.google.common.base.Preconditions;
import org.orderofthebee.addons.support.tools.repo.jscript.RhinoUtils;
import org.orderofthebee.addons.support.tools.repo.jscript.batchexecuter.BatchJobParameters;
import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.repo.jscript.BaseScopableProcessorExtension;
import org.alfresco.repo.lock.JobLockService;
import org.alfresco.service.cmr.repository.ScriptService;
import org.apache.commons.lang3.StringUtils;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.Scriptable;
import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The script job service can read the configured quartz jobs within an Alfresco
 * repository.
 * You can iterate over all jobs, get a job by name or print the details.
 * Running, state checking and cancel a job run is part of the ScriptJob class.
 *
 * Refactored for Quartz 2.x API compatibility.
 *
 * @author jgoldhammer
 * @author Order of the Bee
 */
public class ScriptJobService extends BaseScopableProcessorExtension
{

    private static final String SCRIPT_JOB_GROUP = "ootbeeScriptJobGroup";
    private static final String SCRIPT_TRIGGER_GROUP = "ootbeeScriptTriggerGroup";

    private ScriptService scriptService;
    private Scheduler scheduler;
    private JobLockService jobLockService;

    public void setScriptService(ScriptService scriptService)
    {
        this.scriptService = scriptService;
    }

    public void setScheduler(Scheduler scheduler)
    {
        this.scheduler = scheduler;
    }

    public void setJobLockService(JobLockService jobLockService)
    {
        this.jobLockService = jobLockService;
    }

    private Map<String, ScriptJob> getJobs()
    {
        Map<String, ScriptJob> jobs = new HashMap<>();

        try
        {
            List<String> jobGroupNames = scheduler.getJobGroupNames();
            for (String jobGroupName : jobGroupNames)
            {
                Set<JobKey> jobKeys = scheduler
                                      .getJobKeys(org.quartz.impl.matchers.GroupMatcher.jobGroupEquals(jobGroupName));
                for (JobKey jobKey : jobKeys)
                {
                    List<? extends Trigger> triggers = scheduler.getTriggersOfJob(jobKey);
                    if (!triggers.isEmpty())
                    {
                        Trigger jobTrigger = triggers.get(0);
                        ScriptJob job = new ScriptJob(
                            jobKey.getName(),
                            jobKey.getGroup(),
                            scheduler,
                            jobTrigger.getPreviousFireTime(),
                            jobTrigger.getNextFireTime(),
                            jobTrigger.getCalendarName(),
                            jobTrigger.getKey().getName(),
                            jobTrigger.getKey().getGroup());

                        if (jobTrigger instanceof CronTrigger)
                        {
                            job.setCronExpression(((CronTrigger) jobTrigger).getCronExpression());
                        }
                        jobs.put(jobKey.getName(), job);
                    }
                }
            }
        }
        catch (SchedulerException e)
        {
            throw new AlfrescoRuntimeException("Cannot determine the configured Alfresco jobs via Quartz", e);
        }
        return jobs;
    }

    /**
     * Get all scheduled jobs as a JavaScript array.
     */
    public Scriptable getAllJobs()
    {
        Map<String, ScriptJob> jobs = getJobs();
        Scriptable scope = getScope();
        Object[] jobsArray = jobs.values().toArray(new Object[0]);
        return Context.getCurrentContext().newArray(scope, jobsArray);
    }

    /**
     * Get a specific job by name.
     */
    public ScriptJob getJob(String name)
    {
        return getJobs().get(name);
    }

    /**
     * Pause all scheduled jobs.
     */
    public void pauseJobs()
    {
        try
        {
            scheduler.pauseAll();
        }
        catch (SchedulerException e)
        {
            throw new AlfrescoRuntimeException("Unable to pause all jobs", e);
        }
    }

    /**
     * Resume all paused jobs.
     */
    public void resumeJobs()
    {
        try
        {
            scheduler.resumeAll();
        }
        catch (SchedulerException e)
        {
            throw new AlfrescoRuntimeException("Unable to resume all jobs", e);
        }
    }

    /**
     * Put the scheduler in standby mode.
     */
    public void standbyScheduler()
    {
        try
        {
            scheduler.standby();
        }
        catch (SchedulerException e)
        {
            throw new AlfrescoRuntimeException("Unable to put scheduler in standby mode", e);
        }
    }

    /**
     * Start the scheduler.
     */
    public void startScheduler()
    {
        try
        {
            scheduler.start();
        }
        catch (SchedulerException e)
        {
            throw new AlfrescoRuntimeException("Unable to start the scheduler", e);
        }
    }

    /**
     * Schedules a temporary job with an inline script executed on each run.
     *
     * @param jobName        the job name
     * @param script         the JavaScript to execute
     * @param runAsUser      system or null for system user, any other string for a
     *                       valid Alfresco user
     * @param cronExpression the cron expression for scheduling (e.g., use
     *                       http://www.cronmaker.com)
     */
    public void scheduleTemporaryJob(String jobName, String script, String runAsUser, String cronExpression)
    {
        String fullJobName = jobName + " (run as " + (runAsUser != null ? runAsUser : "system") + ")";

        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put(ExecuteScriptJob.PARAM_RUN_AS, runAsUser);
        jobDataMap.put(ExecuteScriptJob.PARAM_SCRIPT, script);
        jobDataMap.put(ExecuteScriptJob.PARAM_SCRIPT_SERVICE, scriptService);
        jobDataMap.put("jobLockService", jobLockService);

        // Quartz 2.x: Use JobBuilder
        JobDetail job = JobBuilder.newJob(ExecuteScriptJob.class)
                        .withIdentity(fullJobName, SCRIPT_JOB_GROUP)
                        .usingJobData(jobDataMap)
                        .build();

        // Quartz 2.x: Use TriggerBuilder with CronScheduleBuilder
        Trigger trigger = TriggerBuilder.newTrigger()
                          .withIdentity("trigger_" + System.nanoTime(), SCRIPT_TRIGGER_GROUP)
                          .withSchedule(CronScheduleBuilder.cronSchedule(cronExpression))
                          .build();

        try
        {
            scheduler.scheduleJob(job, trigger);
        }
        catch (SchedulerException e)
        {
            throw new AlfrescoRuntimeException(
                "Cannot schedule executeScriptJob with script=" + script + " and runAs=" + runAsUser, e);
        }
    }

    /**
     * Schedules a temporary job with parameters from a JavaScript object.
     *
     * @param jobParameter JavaScript object with properties: jobName, runAs,
     *                     cronExpression, script (function)
     * @return the created job name
     */
    public String scheduleTemporaryJob(Object jobParameter)
    {
        Map<String, Object> paramsMap = BatchJobParameters.getParametersMap(jobParameter);
        String jobName = RhinoUtils.getString(paramsMap, "jobName", "Inline Script Job (" + System.nanoTime() + ")");
        String runAsUser = RhinoUtils.getString(paramsMap, ExecuteScriptJob.PARAM_RUN_AS, "system");
        String cronExpression = Preconditions.checkNotNull(
                                    RhinoUtils.getString(paramsMap, "cronExpression", null),
                                    "cronExpression is required");
        Function scriptFunction = Preconditions.checkNotNull(
                                      RhinoUtils.getFunction(paramsMap, ExecuteScriptJob.PARAM_SCRIPT),
                                      "script function is required");

        Context cx = Context.enter();
        try
        {
            String script = cx.decompileFunctionBody(scriptFunction, 3).trim();
            String fullJobName = jobName + " (run as " + (runAsUser != null ? runAsUser : "system") + ")";

            JobDataMap jobDataMap = new JobDataMap();
            jobDataMap.put(ExecuteScriptJob.PARAM_RUN_AS, runAsUser);
            jobDataMap.put(ExecuteScriptJob.PARAM_SCRIPT, script);
            jobDataMap.put(ExecuteScriptJob.PARAM_SCRIPT_SERVICE, scriptService);
            jobDataMap.put("jobLockService", jobLockService);

            JobDetail job = JobBuilder.newJob(ExecuteScriptJob.class)
                            .withIdentity(fullJobName, SCRIPT_JOB_GROUP)
                            .usingJobData(jobDataMap)
                            .build();

            Trigger trigger = TriggerBuilder.newTrigger()
                              .withIdentity("trigger_" + System.nanoTime(), SCRIPT_TRIGGER_GROUP)
                              .withSchedule(CronScheduleBuilder.cronSchedule(cronExpression))
                              .build();

            scheduler.scheduleJob(job, trigger);
            return fullJobName;

        }
        catch (SchedulerException e)
        {
            throw new AlfrescoRuntimeException(
                "Cannot schedule executeScriptJob with cronExpression=" + cronExpression + " and runAs="
                + runAsUser,
                e);
        }
        finally
        {
            Context.exit();
        }
    }

    /**
     * Print details of all configured jobs.
     */
    public String printJobDetails()
    {
        return StringUtils.join(getJobs().values(), "\n");
    }
}
