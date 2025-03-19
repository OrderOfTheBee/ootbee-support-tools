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
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.Property;
import org.apache.logging.log4j.spi.LoggerContext;

/**
 * @author Axel Faust
 */
public class Log4j2LimitedListAppender extends AbstractAppender
{

    // 20 minutes after last retrieval we assume tail is no longer active and automatically deregister this appender
    private static final long AUTO_DEREGISTRATION_TIMEOUT = 1000 * 60 * 20;

    protected final List<LogEvent> storedEvents = new LinkedList<>();

    protected final List<Logger> appendedToLoggers = new ArrayList<>();

    protected final Lock closingLock = new ReentrantLock();

    protected final int eventCountLimit;

    protected long lastRetrievalTimestamp = System.currentTimeMillis();

    public Log4j2LimitedListAppender(final String uuid, final int eventCountLimit)
    {
        super(uuid, null, null, true, Property.EMPTY_ARRAY);

        if (eventCountLimit <= 0)
        {
            throw new IllegalArgumentException("eventCountLimit must be a positive integer");
        }

        this.eventCountLimit = eventCountLimit;
        this.start();
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
        if (this.isStarted())
        {
            synchronized (this.appendedToLoggers)
            {
                if (logger instanceof org.apache.logging.log4j.core.Logger)
                {
                    ((org.apache.logging.log4j.core.Logger) logger).addAppender(this);
                    this.appendedToLoggers.add(logger);
                }
            }
        }
    }

    public List<LogEvent> retrieveLogEvents()
    {
        List<LogEvent> retrievedEvents;
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
    public void append(final LogEvent event)
    {
        final boolean started = this.isStarted();
        final boolean active = started && (System.currentTimeMillis() - this.lastRetrievalTimestamp) < AUTO_DEREGISTRATION_TIMEOUT;
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
        else if (started && this.closingLock.tryLock())
        {
            try
            {
                this.stop();
            }
            finally
            {
                this.closingLock.unlock();
            }
        }
    }

    /**
     *
     * {@inheritDoc}
     */
    @Override
    public boolean stop(final long timeout, final TimeUnit timeUnit)
    {
        this.setStopping();

        // make sure we clear the data to avoid retaining memory
        if (!this.storedEvents.isEmpty())
        {
            synchronized (this.storedEvents)
            {
                this.storedEvents.clear();
            }
        }

        synchronized (this.appendedToLoggers)
        {
            this.appendedToLoggers.stream().forEach(logger -> ((org.apache.logging.log4j.core.Logger) logger).removeAppender(this));
            this.appendedToLoggers.clear();
        }

        final LoggerContext context = LogManager.getContext();
        if (context instanceof org.apache.logging.log4j.core.LoggerContext)
        {
            ((org.apache.logging.log4j.core.LoggerContext) context).getConfiguration().getAppenders().remove(this.getName());
        }

        this.setStopped();

        return true;
    }
}
