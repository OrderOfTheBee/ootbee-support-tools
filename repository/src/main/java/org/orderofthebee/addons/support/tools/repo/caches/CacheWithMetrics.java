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
package org.orderofthebee.addons.support.tools.repo.caches;

/**
 * Instances of this interface are caches that are capable of providing additional metrics data about their usage.
 *
 * Implementing this interface is not strictly required for cache metrics to be supported in the admin console "Caches" tool, however it
 * defines the basic names and expected types for the operations that tool invokes to retrieve the relevant information for display.
 *
 * @author Axel Faust, <a href="http://acosix.de">Acosix GmbH</a>
 */
public interface CacheWithMetrics
{

    /**
     * Retrieves a (snapshot) object containing metrics for this caches usage since its initialisation.
     *
     * @return this cache's metrics
     */
    CacheMetrics getMetrics();

}
