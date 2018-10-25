package tech.threekilogram.model.memory;

import tech.threekilogram.model.Container;

/**
 * 这是内存缓存的总接口,所有内存缓存均实现了该接口:
 * {@link tech.threekilogram.model.memory.map.MemoryMap}
 * {@link tech.threekilogram.model.memory.map.MemoryList}
 * {@link tech.threekilogram.model.memory.lru.MemoryLruCache}
 *
 * @author liujin
 */
public interface Memory<K, V> extends Container<K> {

      /**
       * 使用一个key读取对应的value
       *
       * @param key key
       *
       * @return value key 对应的value
       */
      V get ( K key );

      /**
       * save a value to key,then could loadStringFromNet value with this key
       *
       * @param key where to save
       * @param value value to save
       *
       * @return if a value exist at key return it, if key to a null value return null
       */
      void save ( K key, V value );

      /**
       * 数据量
       *
       * @return 数据量
       */
      int size ( );
}
