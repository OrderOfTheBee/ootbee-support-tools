/**
 * Copyright (C) 2016 Axel Faust
 * Copyright (C) 2016 Order of the Bee
 *
 * This file is part of Community Support Tools
 *
 * Community Support Tools is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Community Support Tools is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Community Support Tools. If not, see <http://www.gnu.org/licenses/>.
 */
/*
 * Linked to Alfresco
 * Copyright (C) 2005-2016 Alfresco Software Limited.
 */

function buildCacheInfo(cacheBeanName, cache, globalProperties)
{
    var maxItems, cacheInfo, invHandler, internalCacheField, internalCache, stats;

    maxItems = globalProperties[cacheBeanName + '.maxItems'] || '-1';

    cacheInfo = {
        name : cacheBeanName,
        definedType : globalProperties[cacheBeanName + '.cluster.type'],
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
            cacheInfo.type = String(invHandler.backingObject.class.name);
        }
    }
    else
    {
        cacheInfo.type = String(cache.class.name);
    }

    if (cacheInfo.type === 'org.alfresco.repo.cache.DefaultSimpleCache')
    {
        try
        {
            internalCacheField = cache.class.getDeclaredField('cache');
            // will fail on Java 9 but there is currently no other way to get to the internal cache
            internalCacheField.setAccessible(true);
            internalCache = internalCacheField.get(cache);
            stats = internalCache.stats();

            cacheInfo.cacheGets = stats.requestCount();
            cacheInfo.cacheHits = stats.hitCount();
            cacheInfo.cacheMisses = stats.missCount();
            cacheInfo.cacheEvictions = stats.evictionCount();
            cacheInfo.cacheHitRate = stats.hitRate() * 100;
            cacheInfo.cacheMissRate = stats.missRate() * 100;
        }
        catch (e)
        {
            logger.log('Failed to retrieve statistics from ' + cacheBeanName + ': ' + String(e));
        }
    }
    // check support of CacheWithMetrics without requiring explicit interface inheritance
    else if (cache.metrics !== undefined && cache.metrics !== null)
    {
        try
        {
            stats = cache.metrics;

            if (stats.cacheGets !== undefined && stats.cacheGets !== null)
            {
                cacheInfo.cacheGets = stats.cacheGets;
            }
            if (stats.cacheHits !== undefined && stats.cacheHits !== null)
            {
                cacheInfo.cacheHits = stats.cacheHits;
            }
            if (stats.cacheMisses !== undefined && stats.cacheMisses !== null)
            {
                cacheInfo.cacheMisses = stats.cacheMisses;
            }
            if (stats.cacheEvictions !== undefined && stats.cacheEvictions !== null)
            {
                cacheInfo.cacheEvictions = stats.cacheEvictions;
            }
            if (stats.cacheHitPercentage !== undefined && stats.cacheHitPercentage !== null)
            {
                cacheInfo.cacheHitRate = stats.cacheHitPercentage;
            }
            if (stats.cacheMissPercentage !== undefined && stats.cacheMissPercentage !== null)
            {
                cacheInfo.cacheMissRate = stats.cacheMissPercentage;
            }
        }
        catch (e)
        {
            logger.log('Failed to retrieve statistics from ' + cacheBeanName + ': ' + String(e));
        }
    }

    return cacheInfo;
}

/* exported buildCaches */
function buildCaches()
{
    var TransactionalCache, ctxt, globalProperties, cacheBeanNames, cacheInfos, idx, cacheBeanName, cache, cacheInfo;

    TransactionalCache = Packages.org.alfresco.repo.cache.TransactionalCache;
    ctxt = Packages.org.springframework.web.context.ContextLoader.getCurrentWebApplicationContext();
    globalProperties = ctxt.getBean('global-properties', Packages.java.util.Properties);

    cacheInfos = [];
    cacheBeanNames = ctxt.getBeanNamesForType(Packages.org.alfresco.repo.cache.SimpleCache, false, false);

    for (idx = 0; idx < cacheBeanNames.length; idx++)
    {
        cacheBeanName = String(cacheBeanNames[idx]);

        // only want non-transactional caches
        cache = ctxt.getBean(cacheBeanName, Packages.org.alfresco.repo.cache.SimpleCache);
        if (!(cache instanceof TransactionalCache))
        {
            cacheInfo = buildCacheInfo('cache.' + (cache.cacheName || cacheBeanName), cache, globalProperties);
            cacheInfos.push(cacheInfo);
        }
    }

    cacheInfos.sort(function(a, b)
    {
        return a.name.localeCompare(b.name);
    });

    model.cacheInfos = cacheInfos;
}