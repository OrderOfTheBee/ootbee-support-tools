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
/**
 *
 */
package org.orderofthebee.addons.support.tools.repo.jscript.audit;

import com.google.common.collect.Maps;
import org.alfresco.repo.jscript.BaseScopableProcessorExtension;
import org.alfresco.service.cmr.audit.AuditQueryParameters;
import org.alfresco.service.cmr.audit.AuditService;
import org.alfresco.service.cmr.audit.AuditService.AuditQueryCallback;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.Map;

/**
 * ScriptAuditService is a wrapper around the Auditservice of Alfresco. It allows to enable/disable the auditservice,
 * query the auditservice and clearing the audit entries of a given app.
 *
 * @author jgoldhammer
 *
 */
public class ScriptAuditService extends BaseScopableProcessorExtension
{

    AuditService auditService;

    public void setAuditService(AuditService auditService)
    {
        this.auditService = auditService;
    }

    public boolean isAllEnabled()
    {
        return auditService.isAuditEnabled();
    }


    public boolean isEnabledFor(String appName, String path)
    {
        return auditService.isAuditEnabled(appName, path);
    }

    public void enableAll()
    {
        auditService.setAuditEnabled(true);
    }

    public void disableAll()
    {
        auditService.setAuditEnabled(false);
    }

    public Map<String, AuditService.AuditApplication> getApplications()
    {
        return auditService.getAuditApplications();
    }

    public void clearAll(String appName)
    {
        auditService.clearAudit(appName, null, null);
    }

    /**
     * Remove .audit entries for the given application between the time ranges.
     * If no start time is given then entries are deleted as far back as they
     * exist. If no end time is given then entries are deleted up until the
     * current time.
     *
     * @param appName
     *            the name of the application for which to remove entries
     * @param start
     *            the start time of entries to remove (inclusive and optional)
     * @param end
     *            the end time of entries to remove (exclusive and optional)
     *
     * @since 3.4
     **/
    public void clear(String appName, long start, long end)
    {
        auditService.clearAudit(appName, start, end);
    }

    /**
     * Issue an audit query using the given parameters and consuming results.
     * Results are returned in entry order, corresponding to time order.
     *
     * @param appName
     *            if not null, find entries logged against this application
     * @param user
     *            if not null, find entries logged against this user
     * @param path
     *            if not null, find entries logged against this path
     * @param fromTime
     *            the start search time (<code>null</code> to start at the
     *            beginning)
     * @param toTime
     *            the end search time (<code>null</code> for no limit)
     * @param forward
     *            <code>true</code> for results to ordered from first to last, or
     *            <code>false</code> to order from last to first
     * @param limit
     *            the maximum number of results to retrieve (zero or negative to
     *            ignore)
     * @param valuesRequired
     *            Determines whether the entries will be populated with data
     * @return an array of maps with key=noderef and values=entryvalues
     */
    public Map<String, ScriptAuditValue> query(String appName, String user,
            String path, Long fromTime, Long toTime, Boolean forward,
            Integer limit, Boolean valuesRequired)
    {

        final AuditQueryParameters params = createAuditParameters(appName,
                                            user, fromTime, toTime, forward);

        if (valuesRequired == null)
        {
            valuesRequired = Boolean.TRUE;
        }

        if (limit == null)
        {
            limit = 25;
        }

        final Map<String, ScriptAuditValue> results = Maps.newLinkedHashMap();
        auditService.auditQuery(new AuditQueryCallback()
        {
            @Override
            public boolean valuesRequired()
            {
                return true;
            }

            @Override
            public boolean handleAuditEntryError(Long entryId, String errorMsg,
                                                 Throwable error)
            {
                return true;
            }

            @Override
            public boolean handleAuditEntry(Long entryId,
                                            String applicationName, String user, long time,
                                            Map<String, Serializable> values)
            {
                results.put(String.valueOf(entryId), new ScriptAuditValue(
                                applicationName, user, time, values));
                return true;

            }
        }, params, limit);
        return results;
    }

    private AuditQueryParameters createAuditParameters(String appName,
            String user, Long fromTime, Long toTime, Boolean forward)
    {
        final AuditQueryParameters params = new AuditQueryParameters();

        if (forward != null)
        {
            params.setForward(forward);
        }

        if (StringUtils.isNotBlank(appName))
        {
            params.setApplicationName(appName);
        }

        if (StringUtils.isNotBlank(user))
        {
            params.setUser(user);
        }

        if (fromTime != null)
        {
            params.setFromTime(fromTime);
        }

        if (toTime != null)
        {
            params.setToTime(toTime);
        }
        return params;
    }

}
