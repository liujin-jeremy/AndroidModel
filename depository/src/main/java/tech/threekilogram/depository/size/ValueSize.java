package tech.threekilogram.depository.size;

/**
 * @author: Liujin
 * @version: V1.0
 * @date: 2018-07-30
 * @time: 8:33
 */
public interface ValueSize<K, V> {

      /**
       * get a size of value
       *
       * @param k key for value
       * @param v value to get size
       *
       * @return the size of value
       */
      int sizeOf (K k, V v);
}
