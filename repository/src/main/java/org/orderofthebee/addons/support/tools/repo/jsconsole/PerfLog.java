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
 * This file is part of code forked from the JavaScript Console project
 * which was licensed under the Apache License, Version 2.0 at the time.
 * In accordance with that license, the modifications / derivative work
 * is now being licensed under the LGPL as part of the OOTBee Support Tools
 * addon.
 */
package org.orderofthebee.addons.support.tools.repo.jsconsole;

import org.alfresco.util.ParameterCheck;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * class for performance monitoring. create your instance via constructor, start the monitoring via {@link #start()} and stop the monitoring
 * with {@link #stop(String, Object...)}.
 *
 * @author jgoldhammer
 */
public class PerfLog
{

    private static final Logger LOGGER = LoggerFactory.getLogger(PerfLog.class);

    private long startTime;

    /**
     * Starts the performance measurement
     *
     * @return this instance for chained calls
     */
    public PerfLog start()
    {
        this.startTime = System.currentTimeMillis();
        return this;
    }

    /**
     * Stops the performance measurement.
     *
     * @param message
     *     the log message for logging execution performance in debug level
     * @param params
     *     the parameters for the log message
     * @return the time measured for between start and stop in milliseconds
     */
    public long stop(final String message, final Object... params)
    {
        ParameterCheck.mandatoryString("message", message);

        final long endTime = System.currentTimeMillis();
        final long neededTime = endTime - this.startTime;

        final Object[] effParams = new Object[params.length + 1];
        System.arraycopy(params, 0, effParams, 1, params.length);
        effParams[0] = neededTime;

        LOGGER.debug(message, effParams);

        return neededTime;
    }
}
