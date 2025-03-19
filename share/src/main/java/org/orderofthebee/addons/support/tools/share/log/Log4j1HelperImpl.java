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
package org.orderofthebee.addons.support.tools.share.log;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.regex.Pattern;

import org.apache.log4j.Appender;
import org.apache.log4j.Category;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

/**
 * This log4j helper implementation handles log4j version 1 compatibility for Alfresco Content Services up to 7.3
 *
 * @author Axel Faust
 */
public class Log4j1HelperImpl implements Log4jHelper
{

    private static final org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory.getLogger(Log4j1HelperImpl.class);

    /**
     * {@inheritDoc}
     */
    @Override
    public String getRootLoggerName()
    {
        return Logger.getRootLogger().getName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setRootLevel(final String level)
    {
        final Logger logger = Logger.getRootLogger();
        Log4jCompatibilityUtils.LOG_SETTING_TRACKER.recordChange(logger.getName(), logger.getLevel().toString(), level);
        logger.setLevel(Level.toLevel(level));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getRootLevel()
    {
        final Level level = Logger.getRootLogger().getLevel();
        return level != null ? level.toString() : null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setLevel(final String loggerName, final String level)
    {
        final Logger logger = Logger.getLogger(loggerName);
        Log4jCompatibilityUtils.LOG_SETTING_TRACKER.recordChange(loggerName,
                logger.getLevel() != null ? logger.getLevel().toString() : null, level);
        logger.setLevel(level != null ? Level.toLevel(level) : null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getLevel(final String loggerName)
    {
        final Level level = Logger.getLogger(loggerName).getLevel();
        return level != null ? level.toString() : null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LoggerInfo getLogger(final String loggerName)
    {
        final Logger logger = LogManager.getLogger(loggerName);
        return logger != null ? this.toLoggerInfo(logger) : null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<LoggerInfo> getLoggers(final String loggerNamePattern, final boolean showUnconfiguredLoggers)
    {
        String effectiveLoggerNamePattern = loggerNamePattern != null && !loggerNamePattern.isEmpty() ? loggerNamePattern : null;
        if (effectiveLoggerNamePattern != null)
        {
            if (!effectiveLoggerNamePattern.startsWith("*"))
            {
                effectiveLoggerNamePattern = "*" + effectiveLoggerNamePattern;
            }
            if (!effectiveLoggerNamePattern.endsWith("*"))
            {
                effectiveLoggerNamePattern = effectiveLoggerNamePattern + "*";
            }
            effectiveLoggerNamePattern = effectiveLoggerNamePattern.replace(".", "\\.").replace("*", ".+");
        }
        final Pattern effectiveLoggerNamePatternP = effectiveLoggerNamePattern != null
                ? Pattern.compile(effectiveLoggerNamePattern, Pattern.CASE_INSENSITIVE)
                : null;

        final List<LoggerInfo> loggers = new ArrayList<>();

        if (effectiveLoggerNamePatternP == null)
        {
            loggers.add(this.toLoggerInfo(Logger.getRootLogger()));
        }

        final Enumeration<?> currentLoggers = LogManager.getCurrentLoggers();
        while (currentLoggers.hasMoreElements())
        {
            final Logger logger = (Logger) currentLoggers.nextElement();
            if ((effectiveLoggerNamePatternP == null || effectiveLoggerNamePatternP.matcher(logger.getName()).matches())
                    && (logger.getLevel() != null || showUnconfiguredLoggers))
            {
                loggers.add(this.toLoggerInfo(logger));
            }
        }

        return loggers;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void validateFilePath(final Collection<String> paths, final Consumer<String> invalidPathConsumer,
            final Consumer<Path> resolvedPathConsumer)
    {
        final List<Logger> loggers = new ArrayList<>();
        loggers.add(LogManager.getRootLogger());
        @SuppressWarnings("unchecked")
        final Enumeration<Logger> currentLoggers = LogManager.getCurrentLoggers();
        while (currentLoggers.hasMoreElements())
        {
            loggers.add(currentLoggers.nextElement());
        }

        final Collection<String> procesedPaths = new HashSet<>();

        for (final Logger logger : loggers)
        {
            @SuppressWarnings("unchecked")
            final Enumeration<Appender> allAppenders = logger.getAllAppenders();
            while (allAppenders.hasMoreElements())
            {
                final Appender appender = allAppenders.nextElement();
                if (appender instanceof FileAppender)
                {
                    final String appenderFile = ((FileAppender) appender).getFile();
                    final File configuredFile = new File(appenderFile);
                    final Path configuredFilePath = configuredFile.toPath().toAbsolutePath().getParent();

                    paths.stream().filter(s -> !procesedPaths.contains(s)).forEach(s -> {
                        final Path path = Paths.get(s);
                        final boolean validPath = path.startsWith(configuredFilePath)
                                && path.getFileName().toString().startsWith(configuredFile.getName());

                        if (validPath)
                        {
                            resolvedPathConsumer.accept(path);
                            procesedPaths.add(s);
                        }
                    });
                }
            }
        }

        paths.stream().filter(s -> !procesedPaths.contains(s)).forEach(invalidPathConsumer);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String createTailingAppender()
    {
        final String uuid = UUID.randomUUID().toString();
        final Log4j1LimitedListAppender appender = new Log4j1LimitedListAppender(uuid, 10000);
        appender.registerAsAppender(Logger.getRootLogger());
        return uuid;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Path> collectLogFilePaths(final boolean useAllLoggerAppenders)
    {
        final Set<Path> paths = new HashSet<>();

        final List<Logger> loggers = new ArrayList<>();
        loggers.add(LogManager.getRootLogger());
        if (useAllLoggerAppenders)
        {
            @SuppressWarnings("unchecked")
            final Enumeration<Logger> currentLoggers = LogManager.getCurrentLoggers();
            while (currentLoggers.hasMoreElements())
            {
                loggers.add(currentLoggers.nextElement());
            }
        }

        for (final Logger logger : loggers)
        {
            @SuppressWarnings("unchecked")
            final Enumeration<Appender> allAppenders = logger.getAllAppenders();
            while (allAppenders.hasMoreElements())
            {
                final Appender appender = allAppenders.nextElement();
                if (appender instanceof FileAppender)
                {
                    final String fileName = ((FileAppender) appender).getFile();
                    final Path path = Paths.get(fileName).toAbsolutePath().getParent();
                    try (final DirectoryStream<Path> directoryStream = Files.newDirectoryStream(path,
                            (fileName.indexOf('/') != -1 ? fileName.substring(fileName.lastIndexOf('/') + 1) : fileName) + "*"))
                    {
                        directoryStream.forEach(pathInDir -> {
                            if (Files.isRegularFile(pathInDir))
                            {
                                paths.add(pathInDir);
                            }
                        });
                    }
                    catch (final IOException ioex)
                    {
                        LOGGER.warn("Failed to collect (rolling) file log paths from {}", path, ioex);
                    }
                }
            }
        }

        final List<Path> sortedPaths = new ArrayList<>(paths);
        Collections.sort(sortedPaths);
        return sortedPaths;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<?> retrieveTailingAppenderEvents(final String uuid)
    {
        final Appender appender = Logger.getRootLogger().getAppender(uuid);

        List<?> events = Collections.emptyList();
        if (appender instanceof Log4j1LimitedListAppender)
        {
            events = ((Log4j1LimitedListAppender) appender).retrieveLogEvents();
        }

        return events;
    }

    private LoggerInfo toLoggerInfo(final Logger logger)
    {
        final boolean isRoot = this.getRootLoggerName().equals(logger.getName());
        final Category parent = isRoot ? logger.getParent() : null;
        final boolean parentIsRoot = parent != null ? this.getRootLoggerName().equals(parent.getName()) : false;
        final LoggerInfo info = new LoggerInfo(logger.getName(), isRoot, parent != null ? parent.getName() : null, parentIsRoot,
                logger.getLevel() != null ? logger.getLevel().toString() : null, logger.getEffectiveLevel().toString(),
                logger.getAdditivity());

        Category currentCategory = logger;
        while (currentCategory != null)
        {
            @SuppressWarnings("unchecked")
            final Enumeration<Appender> appenders = currentCategory.getAllAppenders();
            while (appenders.hasMoreElements())
            {
                final Appender appender = appenders.nextElement();
                info.addAppenderName(appender.getName());
            }
            currentCategory = currentCategory.getAdditivity() ? currentCategory.getParent() : null;
        }

        return info;
    }
}
