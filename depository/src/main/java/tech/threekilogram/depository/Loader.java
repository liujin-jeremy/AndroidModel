package tech.threekilogram.depository;

/**
 * @author: Liujin
 * @version: V1.0
 * @date: 2018-08-01
 * @time: 9:33
 */
public interface Loader<K, V> {

      /**
       * save a value to key
       *
       * @param key where to save
       * @param value value to save
       *
       * @return if a value exist at key return it, if key to a null value return null
       */
      V save (K key, V value);

      /**
       * remove a value at key
       *
       * @param key remove the value at key
       *
       * @return if key exist remove value at key , or null returned
       */
      V remove (K key);

      /**
       * load a value by this key from memory / file / database / net
       *
       * @param key load a value with this key
       *
       * @return value to this key
       */
      V load (K key);
}
