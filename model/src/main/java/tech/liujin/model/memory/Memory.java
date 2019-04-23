package tech.liujin.model.memory;

import tech.threekilogram.model.Loader;

/**
 * 这是内存缓存的总接口,所有内存缓存均实现了该接口:
 * {@link tech.threekilogram.model.memory.map.MemoryMap}
 * {@link tech.threekilogram.model.memory.map.MemoryList}
 * {@link tech.threekilogram.model.memory.lru.MemoryLruCache}
 *
 * @author liujin
 */
public interface Memory<K, V> extends Loader<K, V> {

      /**
       * 使用一个key读取对应的value
       *
       * @param key key
       *
       * @return value key 对应的value
       */
      @Override
      V load ( K key );

      /**
       * save a value to key,then could loadStringFromNet value with this key
       *
       * @param key where to save
       * @param value value to save
       *
       * @return if a value exist at key return it, if key to a null value return null
       */
      V save ( K key, V value );

      /**
       * 数据量
       *
       * @return 数据量
       */
      int size ( );

      /**
       * remove the value to this key
       *
       * @param key remove the value at key
       *
       * @return if key exist remove value at key , or null returned
       */
      V remove ( K key );

      /**
       * test contains a value or not
       *
       * @param key contains of a value to this key
       *
       * @return true contains this value
       */
      boolean containsOf ( K key );

      /**
       * 清空
       */
      void clear ( );
}
