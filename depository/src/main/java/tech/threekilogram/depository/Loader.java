package tech.threekilogram.depository;

/**
 * @author: Liujin
 * @version: V1.0
 * @date: 2018-08-01
 * @time: 9:33
 */
public interface Loader<K, V> {

      /**
       * load a value by this key from memory / file / database / net
       *
       * @param key load a value with this key
       *
       * @return value to this key
       */
      V load (K key);
}
