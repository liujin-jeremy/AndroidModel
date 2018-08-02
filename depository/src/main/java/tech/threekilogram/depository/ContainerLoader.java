package tech.threekilogram.depository;

/**
 * @author: Liujin
 * @version: V1.0
 * @date: 2018-08-01
 * @time: 9:33
 */
public interface ContainerLoader<K, V> extends Loader<K, V> {

      /**
       * test contains a value or not
       *
       * @param key contains of a value to this key
       *
       * @return true contains this value
       */
      boolean containsOf (K key);
}
