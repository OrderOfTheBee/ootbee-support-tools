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
 */
package org.orderofthebee.addons.support.tools.repo.log;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.alfresco.util.ParameterCheck;
import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Logger;
import org.apache.log4j.helpers.LogLog;
import org.apache.log4j.spi.LoggingEvent;

/**
 * @author Axel Faust
 */
public class Log4j1LimitedListAppender extends AppenderSkeleton
{

    // 20 minutes after last retrieval we assume tail is no longer active and automatically deregister this appender
    private static final long AUTO_DEREGISTRATION_TIMEOUT = 1000 * 60 * 20;

    protected final List<LoggingEvent> storedEvents = new LinkedList<>();

    protected final List<Logger> appendedToLoggers = new ArrayList<>();

    protected final Lock closingLock = new ReentrantLock();

    protected final int eventCountLimit;

    protected long lastRetrievalTimestamp = System.currentTimeMillis();

    public Log4j1LimitedListAppender(final String uuid, final int eventCountLimit)
    {
        ParameterCheck.mandatory("uuid", uuid);
        this.setName(uuid);

        if (eventCountLimit <= 0)
        {
            throw new IllegalArgumentException("eventCountLimit must be a positive integer");
        }

        this.eventCountLimit = eventCountLimit;
    }

    /**
     * Add this appender to a logger and then remember the logger so we can remove ourselves from
     * all registered loggers when we are done.
     *
     * @param logger
     *     the logger to which to append this appender
     */
    public void registerAsAppender(final Logger logger)
    {
        synchronized (this.appendedToLoggers)
        {
            logger.addAppender(this);
            this.appendedToLoggers.add(logger);
        }
    }

    /**
     * Makes sure appender is removed from all loggers where it was previously registered.
     */
    public void removeAppenderFromLoggers()
    {
        synchronized (this.appendedToLoggers)
        {
            for (final Logger logger : this.appendedToLoggers)
            {
                logger.removeAppender(this);
            }
            this.appendedToLoggers.clear();
        }
    }

    /**
     *
     * {@inheritDoc}
     */
    @Override
    public boolean requiresLayout()
    {
        return false;
    }

    public List<LoggingEvent> retrieveLogEvents()
    {
        List<LoggingEvent> retrievedEvents;
        synchronized (this.storedEvents)
        {
            retrievedEvents = new ArrayList<>(this.storedEvents);
            this.storedEvents.clear();
            this.lastRetrievalTimestamp = System.currentTimeMillis();
        }

        return retrievedEvents;
    }

    /**
     *
     * {@inheritDoc}
     */
    @Override
    public synchronized void close()
    {
        if (this.closed)
        {
            return;
        }
        this.closed = true;
    }

    /**
     *
     * {@inheritDoc}
     */
    @Override
    protected void append(final LoggingEvent event)
    {
        final boolean active = !this.closed && (System.currentTimeMillis() - this.lastRetrievalTimestamp) < AUTO_DEREGISTRATION_TIMEOUT;
        if (active)
        {
            synchronized (this.storedEvents)
            {
                this.storedEvents.add(event);

                while (this.storedEvents.size() > this.eventCountLimit)
                {
                    this.storedEvents.remove(0);
                }
            }
        }
        else
        {
            if (!this.closed)
            {
                if (this.closingLock.tryLock())
                {
                    try
                    {
                        LogLog.warn("Automatically deregistering limited list appender after timeout exceeded.");
                        this.removeAppenderFromLoggers();
                        this.close();
                    }
                    finally
                    {
                        this.closingLock.unlock();
                    }
                }
            }

            // make sure we clear the data to avoid retaining memory
            if (!this.storedEvents.isEmpty())
            {
                synchronized (this.storedEvents)
                {
                    this.storedEvents.clear();
                }
            }
        }
    }
}
