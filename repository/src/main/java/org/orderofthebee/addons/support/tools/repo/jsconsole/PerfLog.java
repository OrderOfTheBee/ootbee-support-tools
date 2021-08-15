/**
 * Copyright (C) 2016 - 2021 Order of the Bee
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
 * Copyright (C) 2005 - 2021 Alfresco Software Limited.
 * 
 * This file is part of code forked from the JavaScript Console project
 * which was licensed under the Apache License, Version 2.0 at the time.
 * In accordance with that license, the modifications / derivative work
 * is now being licensed under the LGPL as part of the OOTBee Support Tools
 * addon.
 */
package org.orderofthebee.addons.support.tools.repo.jsconsole;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * class for performance monitoring. create your instance via constructor, start
 * the monitoring via {@link #start()} or {@link #start(String, Object...)} and
 * stop the monitoring with {@link #stop(String, Object...)} or
 * {@link #stop(int, String, Object...)}.
 *
 * @author jgoldhammer
 */
public class PerfLog
{

    private static final int DEFAULT_ASSERT_PERFORMANCE = 2000;

    private final Logger logger;

    private long startTime;

    /**
     * Creates a new instance of this class.
     */
    public PerfLog()
    {
        this.logger = LoggerFactory.getLogger(PerfLog.class);
    }

    /**
     * Creates a new instance of this class.
     * 
     * @param logger
     *     the logger to log the performance measure
     */
    public PerfLog(Logger logger)
    {
        this.logger = logger;
    }

    /**
     * Starts the performance measurement
     *
     * @param message
     *     the log message to write to document the performance profiling start
     * @param params
     *     the parameters for the log message
     * @return this instance for chained calls
     */
    public PerfLog start(String message, Object... params)
    {
        startTime = System.currentTimeMillis();
        if (message != null && !message.trim().isEmpty())
        {
            this.logger.info(message, params);
        }
        return this;
    }

    /**
     * Starts the performance measurement
     * 
     * @return this instance for chained calls
     */
    public PerfLog start()
    {
        return start(null, (Object) null);
    }

    /**
     * Stops the performance measurement. Logs a warning if the operation is finished after the specified reference time.
     * 
     * @param assertPerformanceOf
     *     comparison value for the duration in milliseconds that the measured operation is allowed to take at most to be considered "in
     *     time"
     * @param message
     *     the log message addition (appended to a common, static prefix) to write to document the performance profiling start
     * @param params
     *     the parameters for the log message
     * @return the time measured for between start and stop in milliseconds
     */
    public long stop(int assertPerformanceOf, String message, Object... params)
    {
        long endTime = System.currentTimeMillis();
        long neededTime = endTime - startTime;
        boolean inTime = neededTime <= assertPerformanceOf;

        boolean hasMessage = message != null && !message.trim().isEmpty();
        Object[] effParams = new Object[params.length + 1];
        System.arraycopy(params, 0, effParams, 1, params.length);
        effParams[0] = neededTime;

        if (inTime)
        {
            String effMessage = "(OK) {} ms";
            if (hasMessage)
            {
                effMessage += ": " + message;
            }
            this.logger.info(effMessage, effParams);
        }
        else
        {
            String effMessage = "(WARNING) {} ms";
            if (hasMessage)
            {
                effMessage += ": " + message;
            }
            this.logger.warn(effMessage, effParams);
        }
        return neededTime;
    }

    /**
     * Stops the performance measurement. Logs a warning if the operation is
     * finished after {@value #DEFAULT_ASSERT_PERFORMANCE} milliseconds. To
     * specify your own time limit, use {@link #stop(int, String, Object...)}.
     *
     * @param message
     *     the log message addition (appended to a common, static prefix) to write to document the performance profiling start
     * @param params
     *     the parameters for the log message
     * @return the time measured for between start and stop in milliseconds
     */
    public long stop(String message, Object... params)
    {
        return stop(DEFAULT_ASSERT_PERFORMANCE, message, params);
    }
}
