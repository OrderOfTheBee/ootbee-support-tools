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

import java.nio.file.Path;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

public interface Log4jHelper
{

    /**
     * Retrieves the name of the root logger in the specific log4j implementation version
     *
     * @return the name of the root logger
     */
    String getRootLoggerName();

    /**
     * Sets the level of the log4j root logger.
     *
     * @param level
     *     the level to set
     */
    void setRootLevel(String level);

    /**
     * Sets the level of a log4j logger.
     *
     * @param loggerName
     *     the name of the logger on which to set the level
     * @param level
     *     the level to set
     */
    void setLevel(String loggerName, String level);

    /**
     * Retrieves the level of the log4j root logger.
     *
     * @return the level of the logger
     */
    String getRootLevel();

    /**
     * Retrieves the level of a log4j logger.
     *
     * @param loggerName
     *     the name of the logger for which to retrieve the level
     * @return the level of the logger
     */
    String getLevel(String loggerName);

    /**
     * Retrieves information about a specific logger in the sytem.
     *
     * @param loggerName
     *     the name of the logger
     * @return the information for the logger, or {@code null} if the logger does not exist
     */
    LoggerInfo getLogger(String loggerName);

    /**
     * Retrieves information about the loggers in the system.
     *
     * @param loggerNamePattern
     *     a name pattern to filter loggers or {@code null} if no filtering should be applied - the root logger will only be included in the
     *     result when no filtering based on logger name is to be applied
     * @param showUnconfiguredLoggers
     *     {@code true} if the result should include loggers without an explicitly configured log level, {@code false} otherwise
     * @return the list information for matching/all loggers
     */
    List<LoggerInfo> getLoggers(String loggerNamePattern, boolean showUnconfiguredLoggers);

    /**
     * Validates a file path, processing the path if cannot be matched with a Log4J appender or forwarding the resolved absolute path
     * to the respective consumer.
     *
     * @param path
     *     the path(s) to validate and resolve
     * @param invalidPathConsumer
     *     handles an invalid path, i.e. one that does not match any appender's configured file name (pattern).
     * @param resolvedPathConsumer
     *     handles a resolved path
     */
    default void validateFilePath(final String path, final Consumer<String> invalidPathConsumer, final Consumer<Path> resolvedPathConsumer)
    {
        this.validateFilePath(Collections.singleton(path), invalidPathConsumer, resolvedPathConsumer);
    }

    /**
     * Validates file paths, processing path(s) that cannot be matched with a log4j appender and forwarding the resolved absolute path(s)
     * to the respective consumers.
     *
     * @param paths
     *     the paths to validate and resolve
     * @param invalidPathConsumer
     *     handles an invalid path, i.e. one that does not match any appender's configured file name (pattern).
     * @param resolvedPathConsumer
     *     handles a resolved path
     */
    void validateFilePath(Collection<String> paths, Consumer<String> invalidPathConsumer, Consumer<Path> resolvedPathConsumer);

    /**
     * Resolves and collects the paths for log files as handled by the log4j implementation.
     *
     * @param useAllLoggerAppenders
     *     {@code true} if appenders from all loggers should be evaluated or ({@code false}) only the root logger
     * @return the sorted list of log file paths (as best as they can be resolved, considering limitations with dynamic path placeholders)
     */
    List<Path> collectLogFilePaths(boolean useAllLoggerAppenders);

    /**
     * Creates an in-memory appender to collect log output of the root logger for regular polling retrieval and returns the unique name of
     * the appender for future reference. The tailing appender will be automatically deregistered after an amount of time configured in the
     * system configuration.
     *
     * @return the UUID of the appender
     */
    String createTailingAppender();

    /**
     * Retrieves all collected log events collected by the trailing appender since registration or the last retrieval.
     *
     * @param uuid
     *     the UUID of the appender
     * @return the list of log events in the order they were recorded
     */
    List<?> retrieveTailingAppenderEvents(String uuid);
}
