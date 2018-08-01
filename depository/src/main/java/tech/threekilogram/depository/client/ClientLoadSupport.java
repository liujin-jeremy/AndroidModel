package tech.threekilogram.depository.client;

/**
 * @author: Liujin
 * @version: V1.0
 * @date: 2018-08-01
 * @time: 8:24
 */
public interface ClientLoadSupport<K, V> {

      /**
       * to prepare a value by this key,may be asynchronous
       *
       * @param key key
       */
      void prepareValue (K key);

      /**
       * to notify value prepared
       *
       * @param value when value prepared use this method to notify
       */
      void onValuePrepared (V value);
}
