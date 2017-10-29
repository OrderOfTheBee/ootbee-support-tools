/**
 * Copyright (C) 2016 Axel Faust / Markus Joos
 * Copyright (C) 2016 Order of the Bee
 *
 * This file is part of Community Support Tools
 *
 * Community Support Tools is free software: you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 *
 * Community Support Tools is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser
 * General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Community Support Tools. If not, see
 * <http://www.gnu.org/licenses/>.
 */
package org.orderofthebee.addons.support.tools.share;

import java.io.File;
import java.io.IOException;
import java.io.Writer;

import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptException;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;

/**
 * @author Axel Faust, <a href="http://acosix.de">Acosix GmbH</a>
 */
public class LogFileDelete extends AbstractLogFileWebScript
{

    /**
     *
     * {@inheritDoc}
     */
    @Override
    public void execute(final WebScriptRequest req, final WebScriptResponse res) throws IOException
    {
        final String servicePath = req.getServicePath();
        final String matchPath = req.getServiceMatch().getPath();
        final String filePath = servicePath.substring(servicePath.indexOf(matchPath) + matchPath.length());

        final File file = LogFileHandler.validateFilePath(filePath);
        if (file.delete())
        {
            res.setStatus(Status.STATUS_OK);
            // we "have" to send a dummy JSON as repository admin console client JS always tries to parse
            final Writer writer = res.getWriter();
            try
            {
                writer.write("{}");
            }
            finally
            {
                writer.close();
            }
        }
        else
        {
            throw new WebScriptException(Status.STATUS_INTERNAL_SERVER_ERROR, "Log file could not be deleted immediately");
        }
    }

}
