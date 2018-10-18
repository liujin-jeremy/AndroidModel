package tech.threekilogram.model.memory.lru.size;

/**
 * {@link ObjectSize}的默认实现
 *
 * @author: Liujin
 * @version: V1.0
 * @date: 2018-07-30
 * @time: 8:35
 */
public class SimpleObjectSize<K, V> implements ObjectSize<K, V> {

      @Override
      public int sizeOf ( K key, V value ) {

            return 1;
      }
}
