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
 * Instances of this interface provide data for a particular cache instance regarding absolute and relative access metrics, hits vs misses,
 * as well as evictions.
 *
 * Implementing this interface is not strictly required for cache metrics to be supported in the admin console "Caches" tool, however it
 * defines the basic names and expected types for the operations that tool invokes to retrieve the relevant information for display.
 *
 * @author Axel Faust, <a href="http://acosix.de">Acosix GmbH</a>
 */
public interface CacheMetrics
{

    /**
     * Retrieves the amount of read accesses to this cache.
     *
     * @return the number of read accesses
     */
    long getCacheGets();

    /**
     * Retrieves the number of read accesses that returned an existing cache entry.
     *
     * @return the number of cache hits
     */
    long getCacheHits();

    /**
     * Retrieves the relative percentage of read accesses that returned an existing cache entry.
     *
     * @return the cache hit percentage
     */
    double getCacheHitPercentage();

    /**
     * Retrieves the number of read accesses for non-existing cache entries.
     *
     * @return the number of cache misses
     */
    long getCacheMisses();

    /**
     * Retrieves the relative percentage of read accesses for non-existing cache entries.
     *
     * @return the cache miss percentage
     */
    double getCacheMissPercentage();

    /**
     * Retrieves the number of cache entries that have been evicted from the cache, e.g. as part of time-to-live schemes.
     *
     * @return the number of entries evicted from the cache
     */
    long getCacheEvictions();

    // TODO Add additional stats (e.g. puts + timing) when supported enough by Alfresco or 3rd party caches for inclusion in view
}
