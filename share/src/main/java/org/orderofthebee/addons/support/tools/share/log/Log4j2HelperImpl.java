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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import org.alfresco.util.EqualsHelper;
import org.alfresco.util.Pair;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.appender.FileAppender;
import org.apache.logging.log4j.core.appender.RandomAccessFileAppender;
import org.apache.logging.log4j.core.appender.RollingFileAppender;
import org.apache.logging.log4j.core.appender.RollingRandomAccessFileAppender;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.apache.logging.log4j.simple.SimpleLogger;
import org.apache.logging.log4j.spi.LoggerContext;
import org.apache.logging.log4j.status.StatusLogger;

/**
 * This log4j helper implementation handles log4j version 2 compatibility for Alfresco Content Services from 7.4
 *
 * @author Axel Faust
 */
public class Log4j2HelperImpl implements Log4jHelper
{

    private static final String FRAGMENT_PATTERN = "%\\d*i|%d\\{[^\\}]+\\}|\\$\\$\\{[^\\}]+\\}";

    private static final org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory.getLogger(Log4j2HelperImpl.class);

    /**
     * {@inheritDoc}
     */
    @Override
    public String getRootLoggerName()
    {
        return LogManager.ROOT_LOGGER_NAME;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setRootLevel(final String level)
    {
        this.setLevel(this.getRootLoggerName(), level);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getRootLevel()
    {
        return this.getLevel(this.getRootLoggerName());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setLevel(final String loggerName, final String level)
    {
        final LoggerContext context = this.getContext();
        if (context instanceof org.apache.logging.log4j.core.LoggerContext)
        {
            final Configuration configuration = ((org.apache.logging.log4j.core.LoggerContext) context).getConfiguration();
            LoggerConfig loggerConfig = configuration.getLoggerConfig(loggerName);
            if (!EqualsHelper.nullSafeEquals(loggerConfig.getName(), loggerName))
            {
                loggerConfig = LoggerConfig.newBuilder().withAdditivity(true).withLevel(level != null ? Level.valueOf(level) : null)
                        .withLoggerName(loggerName).withIncludeLocation(String.valueOf(loggerConfig.requiresLocation()))
                        .withConfig(configuration).build();

                configuration.addLogger(loggerName, loggerConfig);
                Log4jCompatibilityUtils.LOG_SETTING_TRACKER.recordChange(loggerName, null, level);
            }
            else
            {
                final String originalLevel = Log4jCompatibilityUtils.LOG_SETTING_TRACKER.getOriginalLevel(loggerName);
                final boolean isBeingResetToUnset = originalLevel == null && level == null
                        && Log4jCompatibilityUtils.LOG_SETTING_TRACKER.canBeReset(loggerName);

                final Level oldLevel = loggerConfig.getExplicitLevel();
                if (isBeingResetToUnset && loggerConfig.isAdditive() && loggerConfig.getAppenderRefs().isEmpty())
                {
                    // this LoggerConfig was likely added dynamically, so when resetting we just remove it
                    configuration.removeLogger(loggerName);
                }
                else
                {
                    loggerConfig.setLevel(level != null ? Level.valueOf(level) : null);
                }
                Log4jCompatibilityUtils.LOG_SETTING_TRACKER.recordChange(loggerName, oldLevel != null ? oldLevel.toString() : null, level);
            }

            ((org.apache.logging.log4j.core.LoggerContext) context).updateLoggers();
        }
        // else-fallback should never be necessary in regular Alfresco
        // note: setting levels directly on the logger instances won't properly inherit changes to descendant loggers
        else
        {
            final Logger logger = context.getLogger(loggerName);
            final Level oldLevel = this.determineConfigLevel(logger);
            final Level newLevel = level != null ? Level.getLevel(level) : null;
            if (logger instanceof SimpleLogger)
            {
                ((SimpleLogger) logger).setLevel(newLevel);
                Log4jCompatibilityUtils.LOG_SETTING_TRACKER.recordChange(logger.getName(), oldLevel != null ? oldLevel.toString() : null,
                        level);
            }
            else if (logger instanceof StatusLogger)
            {
                ((StatusLogger) logger).setLevel(newLevel);
                Log4jCompatibilityUtils.LOG_SETTING_TRACKER.recordChange(logger.getName(), oldLevel != null ? oldLevel.toString() : null,
                        level);
            }
            else if (logger instanceof org.apache.logging.log4j.core.Logger)
            {
                ((org.apache.logging.log4j.core.Logger) logger).setLevel(newLevel);
                Log4jCompatibilityUtils.LOG_SETTING_TRACKER.recordChange(logger.getName(), oldLevel != null ? oldLevel.toString() : null,
                        level);
            }
            else
            {
                LOGGER.warn("Log4j2 logger type {} does not support runtime configuration of the level", logger.getClass());
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getLevel(final String loggerName)
    {
        final Level level = this.determineConfigLevel(this.getContext().getLogger(loggerName));
        return level != null ? level.toString() : null;
    }

    /**
     *
     * {@inheritDoc}
     */
    @Override
    public LoggerInfo getLogger(final String loggerName)
    {
        final Logger logger = this.getContext().getLogger(loggerName);
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

        final String rootLoggerName = this.getRootLoggerName();
        final List<LoggerInfo> loggers = new ArrayList<>();

        if (effectiveLoggerNamePatternP == null)
        {
            loggers.add(this.getLogger(rootLoggerName));
        }

        // Log4j2 makes it harder to find all loggers - unless used, even configured loggers are not contained in the logger registry, so we
        // need to look up the actual configs
        final LoggerContext context = this.getContext();
        final Set<String> loggersAddedFromConfig = new HashSet<>();
        if (context instanceof org.apache.logging.log4j.core.LoggerContext)
        {
            final Map<String, LoggerConfig> loggerConfigs = ((org.apache.logging.log4j.core.LoggerContext) context).getConfiguration()
                    .getLoggers();
            loggerConfigs.forEach((name, config) -> {
                if (!EqualsHelper.nullSafeEquals(name, rootLoggerName)
                        && (effectiveLoggerNamePatternP == null || effectiveLoggerNamePatternP.matcher(name).matches()))
                {
                    loggersAddedFromConfig.add(name);
                    loggers.add(this.toLoggerInfo(context.getLogger(name)));
                }
            });
        }

        Stream<? extends Logger> stream = this.getContext().getLoggerRegistry().getLoggers().stream()
                .filter(l -> !EqualsHelper.nullSafeEquals(l.getName(), rootLoggerName))
                .filter(l -> !loggersAddedFromConfig.contains(l.getName()))
                .filter(l -> effectiveLoggerNamePatternP == null || effectiveLoggerNamePatternP.matcher(l.getName()).matches());

        if (!showUnconfiguredLoggers)
        {
            stream = stream.filter(org.apache.logging.log4j.core.Logger.class::isInstance)
                    .map(org.apache.logging.log4j.core.Logger.class::cast).filter(l -> {

                        // Log4j2 makes it harder to find out if a logger was explicitly configured via properties file or runtime
                        // if configured via properties, it has a LoggerConfig with its own name
                        // if configured via runtime, it has an effective level different from its parent
                        final LoggerConfig config = l.get();
                        return EqualsHelper.nullSafeEquals(config.getName(), l.getName())
                                || l.getParent() != null && !EqualsHelper.nullSafeEquals(l.getLevel(), l.getParent().getLevel());
                    });
        }
        stream.forEach(l -> loggers.add(this.toLoggerInfo(l)));

        // order probably mixed due to map-based processing of configs
        Collections.sort(loggers, (infoA, infoB) -> {
            int result = 0;
            if (rootLoggerName.equals(infoA.getName()))
            {
                result = -1;
            }
            else if (rootLoggerName.equals(infoB.getName()))
            {
                result = 1;
            }
            else
            {
                result = infoA.getName().compareTo(infoB.getName());
            }
            return result;
        });

        return loggers;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void validateFilePath(final Collection<String> paths, final Consumer<String> invalidPathConsumer,
            final Consumer<Path> resolvedPathConsumer)
    {
        final LoggerContext context = this.getContext();
        if (context instanceof org.apache.logging.log4j.core.LoggerContext)
        {
            final Configuration configuration = ((org.apache.logging.log4j.core.LoggerContext) context).getConfiguration();
            final Collection<String> procesedPaths = new HashSet<>();
            // decouple before non-trivial iteration on value set
            new ArrayList<>(configuration.getAppenders().values()).forEach(appender -> {
                if (appender instanceof FileAppender || appender instanceof RandomAccessFileAppender)
                {
                    final Path configuredFilePath = this.getSimpleFileAppenderFilePath(appender);

                    paths.stream().filter(s -> !procesedPaths.contains(s)).forEach(s -> {
                        final Path path = Paths.get(s);
                        final boolean validPath = path.startsWith(configuredFilePath.getParent())
                                && nameMatches(path.getFileName().toString(), configuredFilePath.getFileName().toString());

                        if (validPath)
                        {
                            resolvedPathConsumer.accept(path);
                            procesedPaths.add(s);
                        }
                    });
                }
                else if (appender instanceof RollingFileAppender || appender instanceof RollingRandomAccessFileAppender)
                {
                    final Pair<Path, String> basePathAndRollingFile = this.getRollingFileAppenderPath(appender);

                    final Path configuredFilePath = basePathAndRollingFile.getFirst();
                    final String rollingFile = basePathAndRollingFile.getSecond();

                    final boolean simpleRollingFile = this.isSimpleRollingFile(rollingFile);
                    final Path configuredRollingPath = simpleRollingFile ? Paths.get(rollingFile).toAbsolutePath() : null;

                    paths.stream().filter(s -> !procesedPaths.contains(s)).forEach(s -> {
                        final Path path = Paths.get(s);
                        final boolean validPath = (path.startsWith(configuredFilePath.getParent())
                                && nameMatches(path.getFileName().toString(), configuredFilePath.getFileName().toString()))
                                || (configuredRollingPath != null && path.startsWith(configuredRollingPath.getParent())
                                        && nameMatches(path.getFileName().toString(), configuredRollingPath.getFileName().toString()))
                                || (configuredRollingPath == null && pathMatchesRollingPatternPath(path, rollingFile));

                        if (validPath)
                        {
                            resolvedPathConsumer.accept(path);
                            procesedPaths.add(s);
                        }
                    });
                }
            });
        }
        else
        {
            LOGGER.warn("Log4j2 logger context is a {} without access to appenders - unable to validate log file paths",
                    context.getClass());
            paths.forEach(invalidPathConsumer);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Path> collectLogFilePaths(final boolean useAllLoggerAppenders)
    {
        final Set<Path> paths = new HashSet<>();

        final Consumer<Appender> appenderHandler = appender -> {
            if (appender instanceof FileAppender || appender instanceof RandomAccessFileAppender)
            {
                final Path filePath = this.getSimpleFileAppenderFilePath(appender);
                if (Files.exists(filePath))
                {
                    paths.add(filePath);
                }
            }
            else if (appender instanceof RollingFileAppender || appender instanceof RollingRandomAccessFileAppender)
            {
                final Pair<Path, String> basePathAndRollingFile = this.getRollingFileAppenderPath(appender);

                final Path filePath = basePathAndRollingFile.getFirst();
                if (Files.exists(filePath))
                {
                    paths.add(filePath);
                }

                final String rollingFile = basePathAndRollingFile.getSecond();
                final boolean simpleRollingFile = this.isSimpleRollingFile(rollingFile);
                final Path configuredRollingPath = simpleRollingFile ? Paths.get(rollingFile).toAbsolutePath() : null;
                if (configuredRollingPath != null)
                {
                    try
                    {
                        Files.newDirectoryStream(configuredRollingPath.getParent()).forEach(path -> {
                            if (Files.isRegularFile(path)
                                    && nameMatches(path.getFileName().toString(), configuredRollingPath.getFileName().toString()))
                            {
                                paths.add(path);
                            }
                        });
                    }
                    catch (final IOException ioex)
                    {
                        LOGGER.warn("Failed to collect rolling file log paths from {}", configuredRollingPath, ioex);
                    }
                }
                else
                {
                    LOGGER.info("Not resolving complex rolling file pattern {} to runtime log files", rollingFile);
                }
            }
        };

        if (useAllLoggerAppenders)
        {
            this.getContext().getLoggerRegistry().getLoggers().stream().filter(org.apache.logging.log4j.core.Logger.class::isInstance)
                    .map(org.apache.logging.log4j.core.Logger.class::cast)
                    .forEach(logger -> new ArrayList<>(logger.getAppenders().values()).forEach(appenderHandler));
        }
        else
        {
            final Logger rootLogger = this.getContext().getLogger(this.getRootLoggerName());
            if (rootLogger instanceof org.apache.logging.log4j.core.Logger)
            {
                new ArrayList<>(((org.apache.logging.log4j.core.Logger) rootLogger).getAppenders().values()).forEach(appenderHandler);
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
    public String createTailingAppender()
    {
        final String uuid = UUID.randomUUID().toString();
        final Log4j2LimitedListAppender appender = new Log4j2LimitedListAppender(uuid, 10000);
        appender.registerAsAppender(this.getContext().getLogger(this.getRootLoggerName()));
        return uuid;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<?> retrieveTailingAppenderEvents(final String uuid)
    {
        List<?> events = Collections.emptyList();

        final Logger rootLogger = this.getContext().getLogger(this.getRootLoggerName());
        if (rootLogger instanceof org.apache.logging.log4j.core.Logger)
        {
            final Appender appender = ((org.apache.logging.log4j.core.Logger) rootLogger).getAppenders().get(uuid);
            if (appender instanceof Log4j2LimitedListAppender)
            {
                events = ((Log4j2LimitedListAppender) appender).retrieveLogEvents();
            }
        }

        return events;
    }

    private static boolean nameMatches(final String name, final String nameOrRollingPattern)
    {
        // to account for out-of-process rolling / compression (logrotate) we only do prefix match
        boolean matches = true;

        // look for %i/%d{...}/$${...} patterns
        // those are too costly to match so we split and match only the static parts of the pattern
        final Matcher matcher = Pattern.compile(FRAGMENT_PATTERN).matcher(nameOrRollingPattern);
        int patternOffsetIdx = 0;
        int nameOffsetIdx = 0;
        while (matches && matcher.find(patternOffsetIdx))
        {
            final int start = matcher.start();
            final String fragment = nameOrRollingPattern.substring(patternOffsetIdx, start);

            final int fragmentIdx = name.indexOf(fragment, nameOffsetIdx);
            if (fragmentIdx != -1)
            {
                nameOffsetIdx = fragmentIdx + fragment.length();
            }
            else
            {
                matches = false;
            }

            // we skip over the dynamic pattern (too much effort to match precisely)
            patternOffsetIdx = matcher.end();
        }

        if (matches && patternOffsetIdx < nameOrRollingPattern.length())
        {
            final String lastFragment = nameOrRollingPattern.substring(patternOffsetIdx);
            final int fragmentIdx = name.indexOf(lastFragment, nameOffsetIdx);
            matches = fragmentIdx != -1;
        }

        return matches;
    }

    private static boolean pathMatchesRollingPatternPath(final Path path, final String rollingFilePattern)
    {
        boolean matches = true;

        final String rollingFilePatternPath = rollingFilePattern.replace('\\', '/');
        int rollingFilePatternPathOffsetIdx = 0;
        int slashIdx = rollingFilePatternPath.indexOf('/', rollingFilePatternPathOffsetIdx);

        Path basePath;
        if (slashIdx == 0)
        {
            basePath = Paths.get("/");
            rollingFilePatternPathOffsetIdx = slashIdx + 1;
        }
        else if (slashIdx != -1)
        {
            final String basePathFragment = rollingFilePatternPath.substring(0, slashIdx);
            if (basePathFragment.matches(rollingFilePatternPath))
            {
                basePath = Paths.get("t").toAbsolutePath().getParent();
            }
            else
            {
                basePath = Paths.get(basePathFragment).toAbsolutePath();
                rollingFilePatternPathOffsetIdx = slashIdx + 1;
            }
        }
        else
        {
            basePath = Paths.get("t").toAbsolutePath().getParent();
        }
        matches = path.startsWith(basePath);

        if (matches)
        {
            final Path relativePath = path.relativize(basePath);
            slashIdx = rollingFilePatternPath.indexOf('/', rollingFilePatternPathOffsetIdx);
            while (matches && slashIdx != -1)
            {
                if (relativePath.getNameCount() > 1)
                {
                    final Path nextPathEl = relativePath.getName(0);
                    final String fragment = rollingFilePatternPath.substring(rollingFilePatternPathOffsetIdx, slashIdx);
                    if (fragment.matches(rollingFilePatternPath))
                    {
                        final String pattern = fragment.replaceAll("%(0?\\d+)?i", "\\d+").replaceAll(FRAGMENT_PATTERN, ".+");
                        if (nextPathEl.toString().matches("^" + pattern + "$"))
                        {
                            relativePath.relativize(nextPathEl);
                        }
                        else
                        {
                            matches = false;
                        }
                    }
                    else
                    {
                        matches = nextPathEl.toString().equals(fragment);
                        relativePath.relativize(nextPathEl);
                    }
                    rollingFilePatternPathOffsetIdx = slashIdx + 1;
                    slashIdx = rollingFilePatternPath.indexOf('/', rollingFilePatternPathOffsetIdx);
                }
                else
                {
                    matches = false;
                }
            }

            if (matches)
            {
                if (relativePath.getNameCount() > 1)
                {
                    matches = false;
                }
                else
                {
                    final String nameOrRollingPattern = rollingFilePatternPath.substring(rollingFilePatternPathOffsetIdx);
                    matches = nameMatches(relativePath.toString(), nameOrRollingPattern);
                }
            }
        }

        return matches;
    }

    private Path getSimpleFileAppenderFilePath(final Appender appender)
    {
        String appenderFile;
        if (appender instanceof FileAppender)
        {
            appenderFile = ((FileAppender) appender).getFileName();
        }
        else
        {
            appenderFile = ((RandomAccessFileAppender) appender).getFileName();
        }
        final File configuredFile = new File(appenderFile);
        final Path configuredFilePath = configuredFile.toPath().toAbsolutePath();
        return configuredFilePath;
    }

    private Pair<Path, String> getRollingFileAppenderPath(final Appender appender)
    {
        String appenderFile;
        String appenderRollingFile;

        if (appender instanceof RollingFileAppender)
        {
            appenderFile = ((RollingFileAppender) appender).getFileName();
            appenderRollingFile = ((RollingFileAppender) appender).getFilePattern();
        }
        else
        {
            appenderFile = ((RollingRandomAccessFileAppender) appender).getFileName();
            appenderRollingFile = ((RollingRandomAccessFileAppender) appender).getFilePattern();
        }

        final File configuredFile = new File(appenderFile);
        final Path configuredFilePath = configuredFile.toPath().toAbsolutePath();

        return new Pair<>(configuredFilePath, appenderRollingFile);
    }

    private boolean isSimpleRollingFile(final String rollingFile)
    {
        boolean simpleRollingFile = false;
        if (!rollingFile.contains("/"))
        {
            simpleRollingFile = true;
        }
        else
        {
            final int lastSlashIdx = rollingFile.lastIndexOf('/');
            final int lastDollarIdx = rollingFile.lastIndexOf('$');
            final int lastPercentIdx = rollingFile.lastIndexOf('%');
            simpleRollingFile = (lastDollarIdx == -1 || lastSlashIdx < lastDollarIdx)
                    && (lastPercentIdx == -1 || lastSlashIdx < lastPercentIdx);
        }
        return simpleRollingFile;
    }

    private LoggerInfo toLoggerInfo(final Logger logger)
    {
        boolean isAdditive = false;
        Logger parent = null;
        final Level configLevel = this.determineConfigLevel(logger);
        final Level effectiveLevel = logger.getLevel();

        if (logger instanceof org.apache.logging.log4j.core.Logger)
        {
            final org.apache.logging.log4j.core.Logger coreLogger = (org.apache.logging.log4j.core.Logger) logger;
            // if the config is inherited from the parent the effective flag for THIS logger is true
            if (!EqualsHelper.nullSafeEquals(coreLogger.get().getName(), logger.getName()))
            {
                isAdditive = true;
            }
            else
            {
                isAdditive = coreLogger.isAdditive();
            }
            parent = coreLogger.getParent();
        }

        final LoggerInfo info = new LoggerInfo(logger.getName(), this.getRootLoggerName().equals(logger.getName()),
                parent != null ? parent.getName() : null, parent != null ? this.getRootLoggerName().equals(parent.getName()) : false,
                configLevel != null ? configLevel.toString() : null, effectiveLevel != null ? effectiveLevel.toString() : null, isAdditive);

        Logger currentLogger = logger;
        while (currentLogger != null)
        {
            if (currentLogger instanceof org.apache.logging.log4j.core.Logger)
            {
                final org.apache.logging.log4j.core.Logger coreLogger = (org.apache.logging.log4j.core.Logger) currentLogger;
                coreLogger.getAppenders().keySet().forEach(info::addAppenderName);
                final boolean additive = coreLogger.isAdditive();
                currentLogger = additive ? coreLogger.getParent() : null;
            }
            else
            {
                currentLogger = null;
            }
        }

        return info;
    }

    private Level determineConfigLevel(final Logger logger)
    {
        Level level = null;
        final LoggerContext context = this.getContext();
        if (context instanceof org.apache.logging.log4j.core.LoggerContext)
        {
            final LoggerConfig loggerConfig = ((org.apache.logging.log4j.core.LoggerContext) context).getConfiguration()
                    .getLoggerConfig(logger.getName());
            if (EqualsHelper.nullSafeEquals(loggerConfig.getName(), logger.getName()))
            {
                level = loggerConfig.getExplicitLevel();
            }
        }
        else if (logger instanceof org.apache.logging.log4j.core.Logger)
        {
            final org.apache.logging.log4j.core.Logger coreLogger = (org.apache.logging.log4j.core.Logger) logger;
            final Logger parent = coreLogger.getParent();
            // either logger has its own config or level differs from parent (set at runtime)
            if (EqualsHelper.nullSafeEquals(coreLogger.get().getName(), coreLogger.getName())
                    || (parent != null && !EqualsHelper.nullSafeEquals(coreLogger.getLevel(), parent.getLevel())))
            {
                level = logger.getLevel();
            }
        }
        else if (logger != null)
        {
            level = logger.getLevel();
        }
        return level;
    }

    private LoggerContext getContext()
    {
        // need to lookup LoggerContext similar to org.apache.logging.log4j.spi.AbstractLoggerAdapter
        // this class is base for org.apache.logging.slf4j.Log4jLoggerFactory used in ACS
        final boolean classLoaderDependent = LogManager.getFactory().isClassLoaderDependent();
        LoggerContext context;
        if (classLoaderDependent)
        {
            ClassLoader cl = this.getClass().getClassLoader();
            if (cl == null)
            {
                cl = Thread.currentThread().getContextClassLoader();
            }
            context = LogManager.getContext(cl, false);
        }
        else
        {
            context = LogManager.getContext(false);
        }
        return context;
    }
}