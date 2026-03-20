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
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.schedule.AbstractScheduledLockedJob;
import org.alfresco.service.cmr.repository.ScriptService;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * Quartz job that executes a scheduled inline JS script.
 * The job execution is cluster aware and uses the JobLockService.
 *
 * Compatible with Quartz 2.x API used by Alfresco.
 *
 * @author Jens Goldhammer
 * @author Order of the Bee
 */
public class ExecuteScriptJob extends AbstractScheduledLockedJob
{

    public static final String PARAM_SCRIPT = "script";
    public static final String PARAM_RUN_AS = "runAs";
    public static final String PARAM_SCRIPT_SERVICE = "scriptService";

    /**
     * Executes the scheduled script
     *
     * @see org.quartz.Job#execute(org.quartz.JobExecutionContext)
     */
    @Override
    public void executeJob(JobExecutionContext context) throws JobExecutionException
    {
        JobDataMap jobData = context.getJobDetail().getJobDataMap();

        // Get the script service from the job map
        Object scriptServiceObj = jobData.get(PARAM_SCRIPT_SERVICE);
        if (scriptServiceObj == null || !(scriptServiceObj instanceof ScriptService))
        {
            throw new AlfrescoRuntimeException(
                "ExecuteScriptJob data must contain valid script service");
        }

        // Get the script from the job map
        String script = (String) jobData.get(PARAM_SCRIPT);
        if (script == null)
        {
            throw new AlfrescoRuntimeException(
                "ExecuteScriptJob data must contain valid script as String");
        }

        // Get the runAs user from the job map
        String runAs = null;
        Object runAsParam = jobData.get(PARAM_RUN_AS);
        if (runAsParam != null && (runAsParam instanceof String))
        {
            runAs = (String) runAsParam;
        }

        try
        {
            if (runAs == null || runAs.equalsIgnoreCase("system"))
            {
                AuthenticationUtil.setRunAsUserSystem();
            }
            else
            {
                AuthenticationUtil.setRunAsUser(runAs);
            }

            // Execute the script
            ((ScriptService) scriptServiceObj).executeScriptString(script, null);
        }
        finally
        {
            AuthenticationUtil.clearCurrentSecurityContext();
        }
    }
}
