package de.fme.jsconsole;

import java.io.Serializable;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;

import org.alfresco.repo.cache.SimpleCache;
import org.alfresco.util.Pair;

/**
 * A simple list which transfers entries onto a backing cache in chunks of a defined size. This class is <b>not thread-safe</b>.
 *
 * @author Axel Faust, <a href="http://www.prodyna.com">PRODYNA AG</a>
 * @param <K> the key type
 * @param <E> the element type
 */
public class CacheBackedChunkedList<K extends Serializable, E extends Serializable> extends AbstractList<E>
{

    /** The chunk size. */
    private final int chunkSize;

    /** The primary cache key. */
    private final K primaryCacheKey;

    /** The backing in memory list. */
    private final List<E> backingInMemoryList = new ArrayList<E>();

    /** The backing cache. */
    private final SimpleCache<Pair<K, Integer>, List<E>> backingCache;

    /** The last chunk transferred. */
    private int lastChunkTransferred = -1;

    /**
     * Instantiates a new cache backed chunked list.
     *
     * @param cache the cache
     * @param primaryCacheKey the primary cache key
     * @param chunkSize the chunk size
     */
    public CacheBackedChunkedList(final SimpleCache<Pair<K, Integer>, List<E>> cache, final K primaryCacheKey, final int chunkSize)
    {
        this.primaryCacheKey = primaryCacheKey;
        this.backingCache = cache;
        this.chunkSize = chunkSize;
    }

    /**
     * Gets the.
     *
     * @param index the index
     * @return the e
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
     * Size.
     *
     * @return the int
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
