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

import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.util.TempFileProvider;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AbstractOutputStreamAppender;
import org.apache.logging.log4j.core.appender.FileManager;
import org.apache.logging.log4j.core.config.Property;
import org.apache.logging.log4j.core.layout.PatternLayout;
import org.apache.logging.log4j.core.util.Constants;
import org.apache.logging.log4j.spi.LoggerContext;

/**
 *
 * @author Axel Faust
 */
public class Log4j2TemporaryFileAppender extends AbstractOutputStreamAppender<FileManager>
{

    // 20 minutes after last retrieval we assume tail is no longer active and automatically deregister this appender
    private static final long AUTO_DEREGISTRATION_TIMEOUT = 1000 * 60 * 20;

    protected final List<Logger> appendedToLoggers = new ArrayList<>();

    protected final Lock closingLock = new ReentrantLock();

    protected final long creationTimestamp;

    private final String fileName;

    public static Log4j2TemporaryFileAppender createAppender()
    {
        final String absolutePath = TempFileProvider.createTempFile("ootbee-support-tools-snapshot-", ".log").getAbsolutePath();
        final PatternLayout layout = PatternLayout.newBuilder().withCharset(StandardCharsets.UTF_8)
                .withPattern("%d{ISO8601} %x %-5p [%c{3}] [%t] %replace{%m}{[\\r\\n]+}{}%n").build();
        final FileManager manager = FileManager.getFileManager(absolutePath, true, false, true, false, null, layout,
                Constants.ENCODER_BYTE_BUFFER_SIZE, null, null, null, null);
        if (manager == null)
        {
            throw new AlfrescoRuntimeException("Unable to create temporary file appender as Log4j2 FileManager failed to be initialised");
        }

        final String uuid = UUID.randomUUID().toString();
        return new Log4j2TemporaryFileAppender(uuid, layout, manager, absolutePath);
    }

    private Log4j2TemporaryFileAppender(final String name, final Layout<? extends Serializable> layout, final FileManager manager,
            final String filename)
    {

        super(name, layout, null, true, true, Property.EMPTY_ARRAY, manager);
        this.fileName = filename;
        this.creationTimestamp = System.currentTimeMillis();
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

    /**
     * Returns the file name this appender is associated with.
     *
     * @return The File name.
     */
    public String getFileName()
    {
        return this.fileName;
    }

    /**
     *
     * {@inheritDoc}
     */
    @Override
    public void append(final LogEvent event)
    {
        final boolean started = this.isStarted();
        final boolean active = started && (System.currentTimeMillis() - this.creationTimestamp) < AUTO_DEREGISTRATION_TIMEOUT;
        if (active)
        {
            super.append(event);
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
