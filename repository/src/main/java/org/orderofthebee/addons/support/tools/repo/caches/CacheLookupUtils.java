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

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Map;

import org.alfresco.repo.cache.CacheStatistics;
import org.alfresco.repo.cache.DefaultSimpleCache;
import org.alfresco.repo.cache.NoStatsForCache;
import org.alfresco.repo.cache.OperationStats;
import org.alfresco.repo.cache.SimpleCache;
import org.alfresco.repo.cache.TransactionStats.OpType;
import org.alfresco.repo.cache.TransactionalCache;
import org.alfresco.util.EqualsHelper;
import org.alfresco.util.ParameterCheck;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.PropertyValue;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.config.TypedStringValue;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;

import com.google.common.cache.Cache;

/**
 * This utility class abstracts common reverse or reflection-based lookup logic to access low-level cache implementations or statistics
 * objects.
 *
 * @author Axel Faust, <a href="http://acosix.de">Acosix GmbH</a>
 */
public class CacheLookupUtils
{

    private static final Logger LOGGER = LoggerFactory.getLogger(CacheLookupUtils.class);

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

        Object stats;

        try
        {
            final Field cacheField = invalidatingCache.getClass().getDeclaredField("cache");
            // may fail in Java 9 but no other way due to private visibility, Enterprise-only class and lack of accessor
            // (dependent on SecurityManager)
            cacheField.setAccessible(true);
            final Object internalCache = cacheField.get(invalidatingCache);

            if (!(internalCache instanceof DefaultSimpleCache<?, ?>))
            {
                throw new IllegalArgumentException("internalCache should be an instance of DefaultSimpleCache");
            }

            stats = getDefaultSimpleCacheStats((DefaultSimpleCache<?, ?>) internalCache);
        }
        catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | SecurityException e)
        {
            LOGGER.debug("Error accessing statistics of cache {}", invalidatingCache, e);
            stats = null;
        }

        return stats;
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

        Object stats;

        try
        {
            final Field mapField = simpleCache.getClass().getDeclaredField("map");
            // may fail in Java 9 but no other way due to private visibility, Enterprise-only class and lack of accessor
            // (dependent on SecurityManager)
            mapField.setAccessible(true);
            final Object internalMap = mapField.get(simpleCache);
            final Method mapStatsGetter = internalMap.getClass().getMethod("getLocalMapStats");
            stats = mapStatsGetter.invoke(internalMap);
        }
        catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | SecurityException e)
        {
            LOGGER.debug("Error accessing statistics of cache {}", simpleCache, e);
            stats = null;
        }

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

        Object stats;

        try
        {
            final Field googleCacheField = DefaultSimpleCache.class.getDeclaredField("cache");
            // may fail in Java 9 but no other way due to private visibility and lack of accessor
            // (dependent on SecurityManager)
            googleCacheField.setAccessible(true);
            final Object googleCache = googleCacheField.get(defaultSimpleCache);

            stats = ((Cache<?, ?>) googleCache).stats();
        }
        catch (IllegalAccessException | SecurityException e)
        {
            LOGGER.debug("Error accessing statistics of cache {}", defaultSimpleCache, e);
            stats = null;
        }

        return stats;
    }

    /**
     * Resolves the {@link CacheStatistics cache statistics} of the {@link TransactionalCache transactional cache} instance that facades a
     * specific {@link SimpleCache shared
     * cache} and provides it in a more script-friendly representation.
     *
     * @param sharedCacheName
     *            the name of the shared cache
     * @return a facade to a snapshot of the cache statistics
     */
    public static AlfrescoCacheStatsFacade resolveStatisticsViaTransactional(final String sharedCacheName)
    {
        ParameterCheck.mandatoryString("sharedCacheName", sharedCacheName);

        LOGGER.debug("Trying to resolve transactional cache for shared cache {}", sharedCacheName);

        String txnCacheName = null;

        final WebApplicationContext applicationContext = ContextLoader.getCurrentWebApplicationContext();

        if (applicationContext instanceof ConfigurableApplicationContext)
        {
            final ConfigurableListableBeanFactory beanFactory = ((ConfigurableApplicationContext) applicationContext).getBeanFactory();
            final String[] txnCacheBeanNames = applicationContext.getBeanNamesForType(TransactionalCache.class, false, false);

            // this is a rather ugly reference lookup, but so far I see no other way
            for (final String txnCacheBeanName : txnCacheBeanNames)
            {
                final BeanDefinition txnCacheDefinition = beanFactory.getBeanDefinition(txnCacheBeanName);

                final PropertyValue sharedCacheValue = txnCacheDefinition.getPropertyValues().getPropertyValue("sharedCache");
                final PropertyValue nameValue = txnCacheDefinition.getPropertyValues().getPropertyValue("name");
                if (nameValue != null && sharedCacheValue != null)
                {
                    final Object sharedCacheRef = sharedCacheValue.getValue();
                    if (sharedCacheRef instanceof RuntimeBeanReference)
                    {
                        final String sharedCacheBeanName = ((RuntimeBeanReference) sharedCacheRef).getBeanName();

                        Object nameValueObj = nameValue.getValue();
                        if (nameValueObj instanceof TypedStringValue)
                        {
                            nameValueObj = ((TypedStringValue) nameValueObj).getValue();
                        }

                        if (EqualsHelper.nullSafeEquals(sharedCacheBeanName, sharedCacheName))
                        {
                            if (txnCacheName != null)
                            {
                                LOGGER.info("Shared cache {} is referenced by multiple transactional caches", sharedCacheName);
                                txnCacheName = null;
                                break;
                            }
                            txnCacheName = String.valueOf(nameValueObj);
                            LOGGER.debug("Resolved transactional cache {} for shared cache {}", txnCacheName, sharedCacheName);
                        }
                        else
                        {
                            try
                            {
                                final DefaultSimpleCache<?, ?> defaultSimpleCache = applicationContext.getBean(sharedCacheBeanName,
                                        DefaultSimpleCache.class);
                                if (EqualsHelper.nullSafeEquals(defaultSimpleCache.getCacheName(), sharedCacheName))
                                {
                                    if (txnCacheName != null)
                                    {
                                        LOGGER.info("Shared cache {} is referenced by multiple transactional caches", sharedCacheName);
                                        txnCacheName = null;
                                        break;
                                    }
                                    txnCacheName = String.valueOf(nameValueObj);
                                    LOGGER.debug("Resolved transactional cache {} for shared cache {}", txnCacheName, sharedCacheName);
                                }
                                continue;
                            }
                            catch (final BeansException be)
                            {
                                // ignore - can be expected e.g. in EE or with alternative cache implementations
                            }
                        }
                    }
                }
            }

            if (txnCacheName == null)
            {
                LOGGER.debug("Unable to resolve unique transactional cache for shared cache {}", sharedCacheName);
            }
        }
        else
        {
            LOGGER.debug("Application context is not a configurable application context - unable to resolve transactional cache");
        }

        AlfrescoCacheStatsFacade facade = null;
        if (txnCacheName != null)
        {
            final CacheStatistics cacheStatistics = applicationContext.getBean("cacheStatistics", CacheStatistics.class);
            try
            {
                final Map<OpType, OperationStats> allStats = cacheStatistics.allStats(txnCacheName);
                facade = new AlfrescoCacheStatsFacade(allStats);
            }
            catch (final NoStatsForCache e)
            {
                facade = new AlfrescoCacheStatsFacade(Collections.emptyMap());
            }
        }

        return facade;
    }

    public static class AlfrescoCacheStatsFacade implements CacheMetrics
    {

        private final Map<OpType, OperationStats> stats;

        public AlfrescoCacheStatsFacade(final Map<OpType, OperationStats> stats)
        {
            this.stats = stats;
        }

        /**
         *
         * {@inheritDoc}
         */
        @Override
        public long getCacheGets()
        {
            return this.getCacheHits() + this.getCacheMisses();
        }

        /**
         *
         * {@inheritDoc}
         */
        @Override
        public long getCacheHits()
        {
            final long cacheHits;

            final OperationStats hitStats = this.stats.get(OpType.GET_HIT);
            cacheHits = hitStats != null ? hitStats.getCount() : 0;

            return cacheHits;
        }

        /**
         *
         * {@inheritDoc}
         */
        @Override
        public long getCacheMisses()
        {
            final long cacheMisses;

            final OperationStats missStats = this.stats.get(OpType.GET_MISS);
            cacheMisses = missStats != null ? missStats.getCount() : 0;

            return cacheMisses;
        }

        /**
         *
         * {@inheritDoc}
         */
        @Override
        public double getCacheHitPercentage()
        {
            double cacheHitPercentage;

            final long cacheGets = this.getCacheGets();
            if (cacheGets == 0)
            {
                cacheHitPercentage = 100;
            }
            else
            {
                cacheHitPercentage = 1.0d * this.getCacheHits() / cacheGets * 100;
            }
            return cacheHitPercentage;
        }

        /**
         *
         * {@inheritDoc}
         */
        @Override
        public double getCacheMissPercentage()
        {
            double cacheMissPercentage;

            final long cacheGets = this.getCacheGets();
            if (cacheGets == 0)
            {
                cacheMissPercentage = 0;
            }
            else
            {
                cacheMissPercentage = 1.0d * this.getCacheMisses() / cacheGets * 100;
            }
            return cacheMissPercentage;
        }

        /**
         *
         * {@inheritDoc}
         */
        @Override
        public long getCacheEvictions()
        {
            // no way to know due to lack of details
            return -1;
        }
    }
}
