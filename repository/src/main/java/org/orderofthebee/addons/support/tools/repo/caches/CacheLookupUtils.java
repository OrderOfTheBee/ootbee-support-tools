/**
 * Copyright (C) 2016 Axel Faust
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
package org.orderofthebee.addons.support.tools.repo.caches;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.alfresco.repo.cache.DefaultSimpleCache;
import org.alfresco.repo.cache.SimpleCache;
import org.alfresco.util.ParameterCheck;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheStats;

/**
 * @author Axel Faust, <a href="http://acosix.de">Acosix GmbH</a>
 */
public class CacheLookupUtils
{

    /**
     * Retrieves the statistics data from an Alfresco Enterprise Edition {@code InvalidatingCache}
     *
     * @param invalidatingCache
     *            the invalidating cache
     * @return the statistics object
     * @throws Exception
     *             if the reflective access fails for any reason
     */
    public static Object getHzInvalidatingCacheStats(final SimpleCache<?, ?> invalidatingCache) throws Exception
    {
        ParameterCheck.mandatory("invalidatingCache", invalidatingCache);

        final Field cacheField = invalidatingCache.getClass().getDeclaredField("cache");
        // will fail in Java 9 but no other way due to private visibility, Enterprise-only class and lack of accessor
        cacheField.setAccessible(true);
        final Object internalCache = cacheField.get(invalidatingCache);

        if (!(internalCache instanceof DefaultSimpleCache<?, ?>))
        {
            throw new IllegalArgumentException("internalCache should be an instance of DefaultSimpleCache");
        }

        return getDefaultSimpleCacheStats((DefaultSimpleCache<?, ?>) internalCache);
    }

    /**
     * Retrieves the statistics data from an Alfresco Enterprise Edition {@code HazelcastSimpleCache}
     *
     * @param simpleCache
     *            the simple cache
     * @return the statistics object
     * @throws Exception
     *             if the reflective access fails for any reason
     */
    public static Object getHzSimpleCacheStats(final SimpleCache<?, ?> simpleCache) throws Exception
    {
        ParameterCheck.mandatory("simpleCache", simpleCache);

        final Field mapField = simpleCache.getClass().getDeclaredField("map");
        // will fail in Java 9 but no other way due to private visibility, Enterprise-only class and lack of accessor
        mapField.setAccessible(true);
        final Object internalMap = mapField.get(simpleCache);
        final Method mapStatsGetter = internalMap.getClass().getMethod("getLocalMapStats");
        final Object stats = mapStatsGetter.invoke(internalMap);

        return stats;
    }

    /**
     * Retrieves the statistics data from an Alfresco default {@code DefaultSimpleCache}
     *
     * @param defaultSimpleCache
     *            the simple cache instance
     * @return the statistics object
     * @throws Exception
     *             if the reflective access fails for any reason
     */
    public static Object getDefaultSimpleCacheStats(final DefaultSimpleCache<?, ?> defaultSimpleCache) throws Exception
    {
        ParameterCheck.mandatory("defaultSimpleCache", defaultSimpleCache);

        final Field googleCacheField = DefaultSimpleCache.class.getDeclaredField("cache");
        // will fail in Java 9 but no other way due to private visibility and lack of accessor
        googleCacheField.setAccessible(true);
        final Object googleCache = googleCacheField.get(defaultSimpleCache);

        final CacheStats stats = ((Cache<?, ?>) googleCache).stats();
        return stats;
    }
}
