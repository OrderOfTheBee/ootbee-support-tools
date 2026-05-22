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

import java.io.Serializable;
import java.util.Map;

/**
 * @author jgoldhammer
 *
 */
public class ScriptAuditValue
{

    private String applicationName;
    private String user;
    private long time;
    private Map<String, Serializable> values;

    public ScriptAuditValue(String applicationName, String user, long time,
                            Map<String, Serializable> values)
    {
        this.applicationName = applicationName;
        this.user = user;
        this.time = time;
        this.values = values;
    }

    public String getApplicationName()
    {
        return applicationName;
    }

    public String getUser()
    {
        return user;
    }

    public long getTime()
    {
        return time;
    }

    public Map<String, Serializable> getValues()
    {
        return values;
    }

    @Override
    public String toString()
    {
        return "ScriptAuditValue [applicationName=" + applicationName
               + ", user=" + user + ", time=" + time + ", values=" + values
               + "]";
    }

}
