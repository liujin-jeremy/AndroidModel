package tech.threekilogram.depository.size;

/**
 * @author: Liujin
 * @version: V1.0
 * @date: 2018-07-30
 * @time: 8:35
 */
public class DefaultValueSize<K, V> implements ValueSize<K, V> {

      @Override
      public int sizeOf (K key, V value) {

            return 1;
      }
}
