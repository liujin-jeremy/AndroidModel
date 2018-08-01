package tech.threekilogram.depository.memory;

import android.support.v4.util.LruCache;
import tech.threekilogram.depository.size.DefaultValueSize;
import tech.threekilogram.depository.size.ValueSize;

/**
 * @author: Liujin
 * @version: V1.0
 * @date: 2018-07-30
 * @time: 8:26
 */
public class MemoryLruCacheLoader<K, V> implements MemoryLoadSupport<LruCache<K, V>, K, V> {

      /**
       * 容器
       */
      private ConstructLruCache mContainer;

      /**
       * 创建一个默认50条数据的缓存
       */
      public MemoryLruCacheLoader () {

            this(50, new DefaultValueSize<K, V>());
      }

      /**
       * 创建一个指定最大数量的缓存
       */
      public MemoryLruCacheLoader (int maxSize) {

            this(maxSize, new DefaultValueSize<K, V>());
      }

      /**
       * 创建一个指定最大数量,指定每个数据大小的容器
       *
       * @param maxSize 最大容量
       * @param valueSize 每个数据大小计算
       */
      public MemoryLruCacheLoader (
          int maxSize,
          ValueSize<K, V> valueSize) {

            mContainer = new ConstructLruCache(maxSize, valueSize);
      }

      @Override
      public LruCache<K, V> container () {

            return mContainer;
      }

      @Override
      public int size () {

            return mContainer.size();
      }

      @Override
      public V putInToMemory (K key, V v) {

            return mContainer.put(key, v);
      }

      @Override
      public boolean containsOfKey (K key) {

            V v = mContainer.get(key);
            return v != null;
      }

      @Override
      public V load (K key) {

            return mContainer.get(key);
      }

      @Override
      public V removeFromMemory (K key) {

            return mContainer.remove(key);
      }

      @Override
      public void clear () {

            mContainer.evictAll();
      }

      // ========================= lru cache =========================

      private class ConstructLruCache extends LruCache<K, V> {

            private ValueSize<K, V> mValueSize;

            /**
             * @param maxSize for caches that do not override {@link #sizeOf}, this is the maximum
             * number of entries in the cache. For all other caches, this is the maximum sum of the
             * sizes of the entries in this cache.
             */
            public ConstructLruCache (int maxSize, ValueSize<K, V> valueSize) {

                  super(maxSize);
                  mValueSize = valueSize;
            }

            @Override
            protected int sizeOf (K key, V value) {

                  return mValueSize.sizeOf(key, value);
            }
      }
}
