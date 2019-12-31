/**
 * Copyright (C) 2016 - 2020 Order of the Bee
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
 * Copyright (C) 2005 - 2020 Alfresco Software Limited.
 */
package org.orderofthebee.addons.support.tools.share;

import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.springframework.extensions.webscripts.DeclarativeWebScript;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;

/**
 * @author Axel Faust, <a href="http://acosix.de">Acosix GmbH</a>
 */
public class LogStateWebScript extends DeclarativeWebScript
{

    protected static final LogSettingTracker LOG_SETTING_TRACKER = new LogSettingTracker();

    /**
     * {@inheritDoc}
     */
    @Override
    protected Map<String, Object> createScriptParameters(final WebScriptRequest req, final WebScriptResponse res,
            final ScriptDetails script, final Map<String, Object> customParams)
    {
        final Map<String, Object> scriptParameters = super.createScriptParameters(req, res, script, customParams);

        scriptParameters.put("logSettingTracker", LOG_SETTING_TRACKER);

        return scriptParameters;
    }

    public static class LogSettingTracker
    {

        private final Map<Logger, Level> originalLevels = new IdentityHashMap<>();

        public synchronized void recordChange(final Logger logger, final Level oldLevel, final Level newLevel)
        {
            if (!this.originalLevels.containsKey(logger))
            {
                this.originalLevels.put(logger, oldLevel);
            }
        }

        public synchronized boolean canBeReset(final Logger logger)
        {
            boolean canBeReset = false;
            if (this.originalLevels.containsKey(logger))
            {
                final Level level = logger.getLevel();
                canBeReset = level != this.originalLevels.get(logger);
            }

            return canBeReset;
        }

        public synchronized void resetToDefault()
        {
            for (final Entry<Logger, Level> entry : this.originalLevels.entrySet())
            {
                final Level level = entry.getValue();
                entry.getKey().setLevel(level);
            }

            this.originalLevels.clear();
        }

        public synchronized void resetToDefault(final Logger logger)
        {
            if (this.originalLevels.containsKey(logger))
            {
                final Level level = this.originalLevels.get(logger);
                logger.setLevel(level);
                this.originalLevels.remove(logger);
            }
        }
    }
}
