/**
 * Copyright (C) 2016 - 2025 Order of the Bee
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
 * Copyright (C) 2005 - 2025 Alfresco Software Limited.
 */
function mapCacheMetrics(metrics, cacheInfo)
{
    if (metrics.cacheGets !== undefined && metrics.cacheGets !== null)
    {
        cacheInfo.cacheGets = metrics.cacheGets;
    }
    if (metrics.cacheHits !== undefined && metrics.cacheHits !== null)
    {
        cacheInfo.cacheHits = metrics.cacheHits;
    }
    if (metrics.cacheMisses !== undefined && metrics.cacheMisses !== null)
    {
        cacheInfo.cacheMisses = metrics.cacheMisses;
    }
    if (metrics.cacheEvictions !== undefined && metrics.cacheEvictions !== null)
    {
        cacheInfo.cacheEvictions = metrics.cacheEvictions;
    }
    if (metrics.cacheHitPercentage !== undefined && metrics.cacheHitPercentage !== null)
    {
        cacheInfo.cacheHitRate = metrics.cacheHitPercentage;
    }
    if (metrics.cacheMissPercentage !== undefined && metrics.cacheMissPercentage !== null)
    {
        cacheInfo.cacheMissRate = metrics.cacheMissPercentage;
    }
}

function buildCacheInfo(cacheName, cache, allowClearGlobal, propertyGetter)
{
    var configCacheName, maxItems, cacheInfo, invHandler, alfCacheStatsEnabled, alfCacheStats, stats;

    configCacheName = propertyGetter('cache.' + cacheName + '.configCacheName', cacheName);
    maxItems = propertyGetter('cache.' + configCacheName + '.maxItems', '-1');

    cacheInfo = {
        name : cacheName,
        definedType : propertyGetter('cache.' + configCacheName + '.cluster.type', ''),
        clearable : allowClearGlobal && propertyGetter('cache.' + configCacheName + '.clearable', '').toLowerCase() === 'true',
        type : '',
        size : cache.keys.size(),
        maxSize : parseInt(maxItems, 10),
        cacheGets : -1,
        cacheHits : -1,
        cacheHitRate : -1,
        cacheMisses : -1,
        cacheMissRate : -1,
        cacheEvictions : -1
    };

    // some cache implementations (i.e. EE) may be using proxies where the class is not really informative
    if (Packages.java.lang.reflect.Proxy.isProxyClass(cache.class))
    {
        // maybe the invocation handler provides access to the backing cache
        invHandler = Packages.java.lang.reflect.Proxy.getInvocationHandler(cache);
        if (invHandler.backingObject !== undefined && invHandler.backingObject !== null)
        {
            cache = invHandler.backingObject;
            cacheInfo.type = String(cache.class.name);
        }
    }
    else
    {
        cacheInfo.type = String(cache.class.name);
    }

    alfCacheStatsEnabled = propertyGetter('cache.' + configCacheName + '.tx.statsEnabled', '').toLowerCase() === 'true';

    if (alfCacheStatsEnabled)
    {
        // in this case the TransactionalCache facade should manage statistics via a global utility on Alfresco-tier
        // note: this will report incorrect numbers if some code does not use the facade
        alfCacheStats = Packages.org.orderofthebee.addons.support.tools.repo.caches.CacheLookupUtils
                .resolveStatisticsViaTransactional(cacheName);
    }

    try
    {
        switch (cacheInfo.type)
        {
            case 'org.alfresco.repo.cache.NullCache':
                // no use for statistics and clearance
                cacheInfo.clearable = false;
                break;
            case 'org.alfresco.repo.cache.MemoryCache':
                if (alfCacheStats !== undefined && alfCacheStats !== null)
                {
                    // fallback to Alfresco cache statistics
                    mapCacheMetrics(alfCacheStats, cacheInfo);
                }

                cacheInfo.clearable = cacheInfo.clearable
                        && propertyGetter('ootbee-support-tools.cache.memory.clearable', '').toLowerCase() === 'true';
                break;
            case 'org.alfresco.repo.cache.DefaultSimpleCache':
                stats = Packages.org.orderofthebee.addons.support.tools.repo.caches.CacheLookupUtils.getDefaultSimpleCacheStats(cache);

                if ((stats === null || stats.requestCount() === 0) && alfCacheStats !== undefined && alfCacheStats !== null)
                {
                    // fallback to Alfresco cache statistics
                    // (DefaultSimpleCache in most/all Alfresco version uses Google cache with stats disabled)
                    mapCacheMetrics(alfCacheStats, cacheInfo);
                }
                else if (stats !== null)
                {
                    cacheInfo.cacheGets = stats.requestCount();
                    cacheInfo.cacheHits = stats.hitCount();
                    cacheInfo.cacheMisses = stats.missCount();
                    cacheInfo.cacheEvictions = stats.evictionCount();
                    cacheInfo.cacheHitRate = stats.hitRate() * 100;
                    cacheInfo.cacheMissRate = stats.missRate() * 100;
                }

                cacheInfo.clearable = cacheInfo.clearable
                        && propertyGetter('ootbee-support-tools.cache.default.clearable', '').toLowerCase() === 'true';
                break;
            case 'org.alfresco.enterprise.repo.cluster.cache.InvalidatingCache':
                stats = Packages.org.orderofthebee.addons.support.tools.repo.caches.CacheLookupUtils.getHzInvalidatingCacheStats(cache);

                if ((stats === null || stats.requestCount() === 0) && alfCacheStats !== undefined && alfCacheStats !== null)
                {
                    // fallback to Alfresco cache statistics
                    // (DefaultSimpleCache in most/all Alfresco version uses Google cache with stats disabled)
                    mapCacheMetrics(alfCacheStats, cacheInfo);
                }
                else if (stats !== null)
                {
                    cacheInfo.cacheGets = stats.requestCount();
                    cacheInfo.cacheHits = stats.hitCount();
                    cacheInfo.cacheMisses = stats.missCount();
                    cacheInfo.cacheEvictions = stats.evictionCount();
                    cacheInfo.cacheHitRate = stats.hitRate() * 100;
                    cacheInfo.cacheMissRate = stats.missRate() * 100;
                }

                cacheInfo.clearable = cacheInfo.clearable
                        && propertyGetter('ootbee-support-tools.cache.invalidating.clearable', '').toLowerCase() === 'true';
                break;
            case 'org.alfresco.enterprise.repo.cluster.cache.HazelcastSimpleCache':
                stats = Packages.org.orderofthebee.addons.support.tools.repo.caches.CacheLookupUtils.getHzSimpleCacheStats(cache);

                /* The values that Hazelcast provides are complete bogus. Hits are only tracked on locally owned entries. The numberOfGets are tracked on a separate layer than hits, and these values appear to be out of sync quite often (hits larger
                than gets). */
                if (alfCacheStats !== undefined && alfCacheStats !== null)
                {
                    // fallback to Alfresco cache statistics
                    mapCacheMetrics(alfCacheStats, cacheInfo);
                }
                else if (stats !== null)
                {
                    cacheInfo.cacheGets = stats.operationStats.numberOfGets;
                    // cacheInfo.cacheHits = stats.hits;
                    // cacheInfo.cacheMisses = cacheInfo.cacheGets - cacheInfo.cacheHits;
                    // cacheInfo.cacheHitRate = cacheInfo.cacheGets > 0 ? (cacheInfo.cacheHits / cacheInfo.cacheGets * 100) : 1;
                    // cacheInfo.cacheMissRate = cacheInfo.cacheGets > 0 ? (cacheInfo.cacheMisses / cacheInfo.cacheGets * 100) : 0;
                    // can't find anything about evictions in either LocalMapStats or LocalMapOperationStats
                }

                cacheInfo.clearable = cacheInfo.clearable
                        && propertyGetter('ootbee-support-tools.cache.distributed.clearable', '').toLowerCase() === 'true';
                break;
            default:
                // check support of CacheWithMetrics without requiring explicit interface inheritance
                if (cache.metrics !== undefined && cache.metrics !== null)
                {
                    mapCacheMetrics(cache.metrics, cacheInfo);
                }
                else if (alfCacheStats !== undefined && alfCacheStats !== null)
                {
                    // fallback to Alfresco cache statistics
                    mapCacheMetrics(alfCacheStats, cacheInfo);
                }
                cacheInfo.clearable = cacheInfo.clearable
                        && propertyGetter('ootbee-support-tools.cache.' + cacheInfo.type + '.clearable',
                                propertyGetter('ootbee-support-tools.cache.unknown.clearable', '')).toLowerCase() === 'true';
        }
    }
    catch (e)
    {
        logger.log('Failed to retrieve statistics from cache ' + cacheName + ': ' + String(e));
    }

    return cacheInfo;
}

function buildPropertyGetter(ctxt)
{
    var globalProperties, placeholderHelper, propertyGetter;

    globalProperties = ctxt.getBean('global-properties', Packages.java.util.Properties);
    placeholderHelper = new Packages.org.springframework.util.PropertyPlaceholderHelper('${', '}', ':', true);

    propertyGetter = function(propertyName, defaultValue)
    {
        var propertyValue;

        propertyValue = globalProperties[propertyName];
        if (propertyValue)
        {
            propertyValue = placeholderHelper.replacePlaceholders(propertyValue, globalProperties);
        }

        // native JS strings are always preferrable
        if (propertyValue !== undefined && propertyValue !== null)
        {
            propertyValue = String(propertyValue);
        }
        else if (defaultValue !== undefined)
        {
            propertyValue = defaultValue;
        }
        
        return propertyValue;
    };

    return propertyGetter;
}

/* exported buildCaches */
function buildCaches()
{
    var TransactionalCache, ctxt, propertyGetter, cacheBeanNames, allowClearGlobal, cacheInfos, idx, cacheBeanName, cache, cacheInfo;

    TransactionalCache = Packages.org.alfresco.repo.cache.TransactionalCache;
    ctxt = Packages.org.springframework.web.context.ContextLoader.getCurrentWebApplicationContext();
    propertyGetter = buildPropertyGetter(ctxt);

    cacheInfos = [];
    cacheBeanNames = ctxt.getBeanNamesForType(Packages.org.alfresco.repo.cache.SimpleCache, false, false);
    
    allowClearGlobal = propertyGetter('ootbee-support-tools.cache.clearable', '').toLowerCase() === 'true';

    for (idx = 0; idx < cacheBeanNames.length; idx++)
    {
        cacheBeanName = String(cacheBeanNames[idx]);

        // only want non-transactional caches
        cache = ctxt.getBean(cacheBeanName, Packages.org.alfresco.repo.cache.SimpleCache);
        if (!(cache instanceof TransactionalCache))
        {
            cacheInfo = buildCacheInfo(cache.cacheName || cacheBeanName, cache, allowClearGlobal, propertyGetter);
            cacheInfos.push(cacheInfo);
        }
    }

    cacheInfos.sort(function(a, b)
    {
        return a.name.localeCompare(b.name);
    });

    model.cacheInfos = cacheInfos;
}

/* exported resetCache */
function resetCache(cacheName)
{
    var TransactionalCache, ctxt, propertyGetter, cacheBeanNames, allowClearGlobal, cache, cacheInfo, idx, cacheBeanName;

    TransactionalCache = Packages.org.alfresco.repo.cache.TransactionalCache;
    ctxt = Packages.org.springframework.web.context.ContextLoader.getCurrentWebApplicationContext();
    propertyGetter = buildPropertyGetter(ctxt);

    cacheBeanNames = ctxt.getBeanNamesForType(Packages.org.alfresco.repo.cache.SimpleCache, false, false);

    allowClearGlobal = propertyGetter('ootbee-support-tools.cache.clearable', '').toLowerCase() === 'true';

    for (idx = 0; idx < cacheBeanNames.length; idx++)
    {
        cacheBeanName = String(cacheBeanNames[idx]);

        // only want non-transactional caches
        cache = ctxt.getBean(cacheBeanName, Packages.org.alfresco.repo.cache.SimpleCache);
        if (!(cache instanceof TransactionalCache) && ((cache.cacheName !== undefined && String(cache.cacheName) === cacheName) || cacheBeanName === cacheName))
        {
            cacheInfo = buildCacheInfo(cache.cacheName || cacheBeanName, cache, allowClearGlobal, propertyGetter);
            break;
        }
    }

    if (cacheInfo !== undefined && cacheInfo !== null)
    {
        if (cacheInfo.clearable)
        {
            cache.clear();
        }
        else
        {
            status.setCode(status.STATUS_FORBIDDEN, 'Clearing cache ' + cacheName + ' is not permitted');
            status.redirect = true;
        }
    }
    else
    {
        status.setCode(status.STATUS_NOT_FOUND, 'Cache ' + cacheName + ' does not exist');
        status.redirect = true;
    }
}