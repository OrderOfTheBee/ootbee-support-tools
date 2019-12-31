/**
 * Copyright (C) 2016 - 2020 Order of the Bee
 *
 * This file is part of OOTBee Support Tools
 *
 * OOTBee Support Tools is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OOTBee Support Tools is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with OOTBee Support Tools. If not, see <http://www.gnu.org/licenses/>.
 */
/*
 * Linked to Alfresco
 * Copyright (C) 2005 - 2020 Alfresco Software Limited.
 */

function collectLogFilePatterns(allLoggerAppenders, logFilePatterns)
{
    var loggerRepository, loggers, logger, allAppenders, appender;
    
    if (allLoggerAppenders)
    {
        loggerRepository = Packages.org.apache.log4j.LogManager.getLoggerRepository();
        loggers = loggerRepository.currentLoggers;
        while (loggers.hasMoreElements())
        {
            logger = loggers.nextElement();
            allAppenders = logger.allAppenders;
            while (allAppenders.hasMoreElements())
            {
                appender = allAppenders.nextElement();
                if (appender.file !== undefined && appender.file !== null)
                {
                    logFilePatterns[String(appender.file)] = true;
                }
            }
        }
    }
    
    // root logger is not container in currentLoggers for some reason
    logger = Packages.org.apache.log4j.Logger.getRootLogger();
    allAppenders = logger.allAppenders;
    while (allAppenders.hasMoreElements())
    {
        appender = allAppenders.nextElement();
        if (appender.file !== undefined && appender.file !== null)
        {
            logFilePatterns[String(appender.file)] = true;
        }
    }
}

function buildLogFilesModel(useAllLoggerAppenders)
{
    var filePatterns, logFiles, matcherFn, filePattern, path, file, dirStream;

    filePatterns = {};
    logFiles = [];

    collectLogFilePatterns(useAllLoggerAppenders, filePatterns);

    matcherFn = function(path)
    {
        var logFileCandidate = path.toFile();
        if (logFileCandidate.isFile())
        {
            logFiles.push({
                name : String(logFileCandidate.name),
                // standardize paths
                directoryPath : String(logFileCandidate.parentFile.toPath()).replace(/\\/g, '/'),
                path : String(logFileCandidate.toPath()).replace(/\\/g, '/'),
                size : logFileCandidate.length(),
                lastModified : logFileCandidate.lastModified()
            });
        }
    };

    for (filePattern in filePatterns)
    {
        if (filePatterns.hasOwnProperty(filePattern) && filePatterns[filePattern] === true)
        {
            file = new Packages.java.io.File(filePattern);
            path = Packages.java.nio.file.Paths.get(file.toURI()).getParent();
            dirStream = Packages.java.nio.file.Files
                    .newDirectoryStream(path, filePattern.substring(filePattern.lastIndexOf('/') + 1) + '*');
            // Rhino does not support conversion of function to SAM type
            dirStream.forEach({
                accept : matcherFn
            });
        }
    }

    model.logFiles = logFiles;
}

buildLogFilesModel(args.allLoggerAppenders !== null && String(args.allLoggerAppenders) === 'true');