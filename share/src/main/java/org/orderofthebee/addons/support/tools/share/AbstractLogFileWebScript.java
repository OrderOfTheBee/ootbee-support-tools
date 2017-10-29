/**
 * Copyright (C) 2016, 2017 Axel Faust / Markus Joos
 * Copyright (C) 2016, 2017 Order of the Bee
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

import org.springframework.extensions.webscripts.AbstractWebScript;

/**
 *
 * @author Axel Faust, <a href="http://acosix.de">Acosix GmbH</a>
 */
public abstract class AbstractLogFileWebScript extends AbstractWebScript
{

    protected LogFileHandler logFileHandler;

    /**
     * @param logFileHandler
     *            the logFileHandler to set
     */
    public void setLogFileHandler(final LogFileHandler logFileHandler)
    {
        this.logFileHandler = logFileHandler;
    }
}