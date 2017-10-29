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

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;

/**
 * @author Axel Faust, <a href="http://acosix.de">Acosix GmbH</a>
 */
public class LogFileGet extends AbstractLogFileWebScript
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

        final Map<String, Object> model = new HashMap<>();
        final Status status = new Status();
        final Cache cache = new Cache(this.getDescription().getRequiredCache());
        model.put("status", status);
        model.put("cache", cache);

        final String attachParam = req.getParameter("a");
        final boolean attach = attachParam != null && Boolean.parseBoolean(attachParam);

        this.logFileHandler.handleLogFileRequest(filePath, attach, req, res, model);
    }

}
