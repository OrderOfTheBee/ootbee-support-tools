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

import org.alfresco.error.AlfrescoRuntimeException;
import org.quartz.JobExecutionContext;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.TriggerKey;

import java.util.Date;
import java.util.List;

/**
 * Class representing a job which can be used to trigger a new run, check if the
 * job is running and
 * cancel a running job (when the job class supports it).
 *
 * Refactored for Quartz 2.x API compatibility.
 *
 * @author jgoldhammer
 * @author Order of the Bee
 */
public class ScriptJob
{

    public final String jobName;
    public final String groupName;
    public final Scheduler scheduler;
    public final Date previousFireTime;
    public final Date nextFireTime;
    public final String calendarName;
    public final String triggerName;
    public final String triggerGroup;
    public String cronExpression;

    public ScriptJob(String jobName, String jobGroupName, Scheduler scheduler, Date previousFireTime,
                     Date nextFireTime, String calendarName, String triggerName, String triggerGroup)
    {
        this.jobName = jobName;
        this.groupName = jobGroupName;
        this.scheduler = scheduler;
        this.previousFireTime = previousFireTime;
        this.nextFireTime = nextFireTime;
        this.calendarName = calendarName;
        this.triggerName = triggerName;
        this.triggerGroup = triggerGroup;
    }

    /**
     * Starts to trigger the job via quartz runtime which will start the job.
     */
    public void runNow()
    {
        try
        {
            scheduler.triggerJob(JobKey.jobKey(this.jobName, this.groupName));
        }
        catch (SchedulerException e)
        {
            throw new AlfrescoRuntimeException("Cannot start job " + this, e);
        }
    }

    /**
     * Checks if the current job is running.
     *
     * @return true if running, false if not.
     */
    public boolean isRunning()
    {
        boolean isRunning = false;
        try
        {
            List<JobExecutionContext> currentlyExecutingJobs = scheduler.getCurrentlyExecutingJobs();
            for (JobExecutionContext job : currentlyExecutingJobs)
            {
                JobKey key = job.getJobDetail().getKey();
                if (key.getName().equals(this.jobName) && key.getGroup().equals(this.groupName))
                {
                    isRunning = true;
                    break;
                }
            }
        }
        catch (SchedulerException e)
        {
            throw new AlfrescoRuntimeException("Cannot check if the current job " + this + " is running", e);
        }
        return isRunning;
    }

    @Override
    public String toString()
    {
        return "ScriptJob{" +
               "jobName='" + jobName + '\'' +
               ", groupName='" + groupName + '\'' +
               ", previousFireTime=" + previousFireTime +
               ", nextFireTime=" + nextFireTime +
               ", calendarName='" + calendarName + '\'' +
               ", triggerName='" + triggerName + '\'' +
               ", triggerGroup='" + triggerGroup + '\'' +
               ", cronExpression='" + cronExpression + '\'' +
               '}';
    }

    /**
     * Unschedule/cancel the job trigger.
     */
    public void cancelRun()
    {
        try
        {
            scheduler.unscheduleJob(TriggerKey.triggerKey(this.triggerName, this.triggerGroup));
        }
        catch (SchedulerException e)
        {
            throw new AlfrescoRuntimeException("Unable to cancel the job " + this, e);
        }
    }

    /**
     * Pause the job.
     */
    public void pauseJob()
    {
        try
        {
            scheduler.pauseJob(JobKey.jobKey(this.jobName, this.groupName));
        }
        catch (SchedulerException e)
        {
            throw new AlfrescoRuntimeException("Unable to pause the job " + this, e);
        }
    }

    /**
     * Resume a paused job.
     */
    public void resumeJob()
    {
        try
        {
            scheduler.resumeJob(JobKey.jobKey(this.jobName, this.groupName));
        }
        catch (SchedulerException e)
        {
            throw new AlfrescoRuntimeException("Unable to resume the job " + this, e);
        }
    }

    /**
     * Delete the job from the scheduler.
     */
    public void deleteJob()
    {
        try
        {
            scheduler.deleteJob(JobKey.jobKey(this.jobName, this.groupName));
        }
        catch (SchedulerException e)
        {
            throw new AlfrescoRuntimeException("Cannot delete the job with name " + jobName + " and group " + groupName,
                                               e);
        }
    }

    public void setCronExpression(String cronExpression)
    {
        this.cronExpression = cronExpression;
    }
}
