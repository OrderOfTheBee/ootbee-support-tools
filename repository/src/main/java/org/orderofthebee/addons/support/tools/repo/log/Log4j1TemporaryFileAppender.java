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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.alfresco.util.TempFileProvider;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Layout;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.helpers.LogLog;
import org.apache.log4j.spi.LoggingEvent;

/**
 * A FileAppender that will auto-close and unregister itself after 20 minutes.
 *
 * @author Bindu Wavell <a href="mailto:bindu@ziaconsulting.com">bindu@ziaconsulting.com</a>
 */
public class Log4j1TemporaryFileAppender extends FileAppender
{

    // 20 minutes after logger is created, we assume the UI lost access and automatically deregister this appender
    private static final long AUTO_DEREGISTRATION_TIMEOUT = 1000 * 60 * 20;

    protected final List<Logger> appendedToLoggers = new ArrayList<>();

    protected final Lock closingLock = new ReentrantLock();

    protected final long creationTimestamp;

    protected final String appenderUUID = UUID.randomUUID().toString();

    /**
     * This default constructor transparently initialises the appender to use a file path provided by the Alfresco
     * {@link TempFileProvider} and a pattern consistent with the default log pattern in the Alfresco-provided {@code log4j.properties}
     * configuration.
     *
     * @throws IOException
     *             if the parent class constructor fails to create a file for the temporary file path
     */
    public Log4j1TemporaryFileAppender() throws IOException
    {
        this(new PatternLayout("%d{ISO8601} %-5p [%c] [%t] %m%n"), "ootbee-support-tools-snapshot-");
    }

    /**
     * This constructor initialises the appender to use a specific log message layout and file name prefix for the log file path provided by
     * the Alfresco {@link TempFileProvider}.
     *
     *
     * @param layout
     *            the layout for the messages written by this appender
     * @param fileNamePrefix
     *            the file name prefix for the log file
     * @throws IOException
     *             if the parent class constructor fails to create a file for the temporary file path
     */
    public Log4j1TemporaryFileAppender(final Layout layout, final String fileNamePrefix) throws IOException
    {
        super(layout, TempFileProvider.createTempFile(fileNamePrefix, ".log").getPath());
        this.setName(this.appenderUUID);
        this.creationTimestamp = System.currentTimeMillis();
    }

    /**
     * <p>
     * Assuming we have not timed out, does the usual. If we have timed out then deregisters
     * appender from previously registered loggers and closes the appender. If the appender is
     * closed then the append is simply ignored (parent would log a warning.)
     * </p>
     *
     * {@inheritDoc}
     */
    @Override
    public void append(final LoggingEvent event)
    {
        final boolean active = !this.closed && (System.currentTimeMillis() - this.creationTimestamp) < AUTO_DEREGISTRATION_TIMEOUT;
        if (active)
        {
            super.append(event);
        }
        else
        {
            if (!this.closed)
            {
                if (this.closingLock.tryLock())
                {
                    try
                    {
                        LogLog.warn("Automatically deregistering " + this.fileName + " appender after timeout exceeded.");
                        this.removeAppenderFromLoggers();
                        this.close();
                    }
                    finally
                    {
                        this.closingLock.unlock();
                    }
                }
                // If we can't grab the closing lock we can assume another thread is
                // already closing stuff down and we can start ignoring append calls.
            }
        }
    }

    /**
     * Add this appender to a logger and then remember the logger so we can remove ourselves from
     * all registered loggers when we are done.
     *
     * @param logger
     *            the logger to which to append this appender
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
     * Retrieves the UUID of this appender instance for identification independent of the file path.
     *
     * @return the appender UUID
     */
    public String getAppenderUUID()
    {
        return this.appenderUUID;
    }
}
