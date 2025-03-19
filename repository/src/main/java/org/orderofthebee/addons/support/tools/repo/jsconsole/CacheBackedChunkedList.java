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
 * 
 * This file is part of code forked from the JavaScript Console project
 * which was licensed under the Apache License, Version 2.0 at the time.
 * In accordance with that license, the modifications / derivative work
 * is now being licensed under the LGPL as part of the OOTBee Support Tools
 * addon.
 */
package org.orderofthebee.addons.support.tools.repo.jsconsole;

import java.io.Serializable;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;

import org.alfresco.repo.cache.SimpleCache;
import org.alfresco.util.Pair;

/**
 * A simple list which transfers entries onto a backing cache in chunks of a defined size. This class is <b>not thread-safe</b>.
 * 
 * @author Axel Faust
 */
public class CacheBackedChunkedList<K extends Serializable, E extends Serializable> extends AbstractList<E>
{

    private final int chunkSize;

    private final K primaryCacheKey;

    private final List<E> backingInMemoryList = new ArrayList<E>();

    private final SimpleCache<Pair<K, Integer>, List<E>> backingCache;

    private int lastChunkTransferred = -1;

    public CacheBackedChunkedList(final SimpleCache<Pair<K, Integer>, List<E>> cache, final K primaryCacheKey, final int chunkSize)
    {
        this.primaryCacheKey = primaryCacheKey;
        this.backingCache = cache;
        this.chunkSize = chunkSize;
    }

    /**
     * 
     * {@inheritDoc}
     */
    @Override
    public E get(final int index)
    {
        E element;
        if (index >= ((this.lastChunkTransferred + 1) * this.chunkSize))
        {
            element = this.backingInMemoryList.get(index - ((this.lastChunkTransferred + 1) * this.chunkSize));
        }
        else
        {
            final int chunk = index / this.chunkSize;
            final Pair<K, Integer> chunkKey = new Pair<K, Integer>(this.primaryCacheKey, Integer.valueOf(chunk));
            final List<E> chunkList = this.backingCache.get(chunkKey);
            element = chunkList.get(index - (chunk * this.chunkSize));
        }
        return element;
    }

    /**
     * 
     * {@inheritDoc}
     */
    @Override
    public int size()
    {
        return this.backingInMemoryList.size() + ((this.lastChunkTransferred + 1) * this.chunkSize);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void add(final int index, final E e)
    {
        if (index == (this.backingInMemoryList.size() + ((this.lastChunkTransferred + 1) * this.chunkSize)))
        {
            this.backingInMemoryList.add(e);

            if (this.backingInMemoryList.size() >= this.chunkSize)
            {
                final int nextChunk = this.lastChunkTransferred + 1;
                final List<E> toTransfer = this.backingInMemoryList.subList(0, 5);
                final List<E> arrToTransfer = new ArrayList<E>(toTransfer);
                toTransfer.clear();
                final Pair<K, Integer> chunkKey = new Pair<K, Integer>(this.primaryCacheKey, Integer.valueOf(nextChunk));

                this.backingCache.put(chunkKey, arrToTransfer);

                this.lastChunkTransferred = nextChunk;
            }

        }
        else
        {
            throw new UnsupportedOperationException();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void clear()
    {
        // clear the backing list
        this.backingInMemoryList.clear();

        // clear the backing cache
        for (int chunk = 0; chunk <= this.lastChunkTransferred; chunk++)
        {
            final Pair<K, Integer> chunkKey = new Pair<K, Integer>(this.primaryCacheKey, Integer.valueOf(chunk));
            this.backingCache.remove(chunkKey);
        }
        this.lastChunkTransferred = -1;
    }

}
