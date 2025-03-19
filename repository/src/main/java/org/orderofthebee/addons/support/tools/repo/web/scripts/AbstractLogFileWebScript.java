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
package org.orderofthebee.addons.support.tools.repo.web.scripts;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import org.alfresco.util.ParameterCheck;
import org.orderofthebee.addons.support.tools.repo.log.Log4jCompatibilityUtils;
import org.springframework.extensions.webscripts.AbstractWebScript;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptException;
import org.springframework.extensions.webscripts.WebScriptRequest;

/**
 *
 * @author Axel Faust
 */
public abstract class AbstractLogFileWebScript extends AbstractWebScript
{

    private static final String LOG_FILE_PATH_MSG_PREFIX = "The log file path ";

    private static final String LOG_FILE_RESOLUTION_ERROR_MSG_SUFFIX = " could not be resolved to a valid log file - access to any other file system contents is forbidden via this web script";

    private static final String LOG_FILE_EXIST_ERROR_MSG_SUFFIX = " could not be resolved to an existing log file";

    /**
     * Validates a single log file path and resolves it to a file handle.
     *
     * @param filePath
     *     the file path to validate
     * @return the resolved file handle if the file path is valid and allowed to be accessed
     *
     * @throws WebScriptException
     *     if access to the log file is prohibited
     */
    protected File validateFilePath(final String filePath)
    {
        ParameterCheck.mandatoryString("filePath", filePath);

        final AtomicReference<File> file = new AtomicReference<>();
        Log4jCompatibilityUtils.LOG4J_HELPER.validateFilePath(filePath, s -> {
            throw new WebScriptException(Status.STATUS_FORBIDDEN, LOG_FILE_PATH_MSG_PREFIX + s + LOG_FILE_RESOLUTION_ERROR_MSG_SUFFIX);
        }, p -> {
            final File f = p.toFile();
            if (!f.exists())
            {
                throw new WebScriptException(Status.STATUS_NOT_FOUND, LOG_FILE_PATH_MSG_PREFIX + p + LOG_FILE_EXIST_ERROR_MSG_SUFFIX);
            }
            file.set(f);
        });

        return file.get();
    }

    /**
     * Validates a log file paths and resolves them to file handles.
     *
     * @param filePaths
     *     the file paths to validate
     * @return the resolved file handles if the file paths are valid and allowed to be accessed
     *
     * @throws WebScriptException
     *     if access to any log file is prohibited
     */
    protected List<File> validateFilePaths(final List<String> filePaths)
    {
        ParameterCheck.mandatoryCollection("filePaths", filePaths);

        final List<File> files = new ArrayList<>();
        Log4jCompatibilityUtils.LOG4J_HELPER.validateFilePath(filePaths, s -> {
            throw new WebScriptException(Status.STATUS_FORBIDDEN, LOG_FILE_PATH_MSG_PREFIX + s + LOG_FILE_RESOLUTION_ERROR_MSG_SUFFIX);
        }, p -> {
            final File f = p.toFile();
            if (!f.exists())
            {
                throw new WebScriptException(Status.STATUS_NOT_FOUND, LOG_FILE_PATH_MSG_PREFIX + p + LOG_FILE_EXIST_ERROR_MSG_SUFFIX);
            }
            files.add(f);
        });

        return files;
    }

    protected String getFilePath(final WebScriptRequest req)
    {
        final String servicePath = req.getServicePath();
        final String matchPath = req.getServiceMatch().getPath();
        String filePath = servicePath.substring(servicePath.indexOf(matchPath) + matchPath.length());

        if (!filePath.startsWith("/"))
        {
            filePath = "/" + filePath;
        }

        return filePath;
    }

}