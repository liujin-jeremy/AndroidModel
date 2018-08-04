package tech.threekilogram.depository.client;

/**
 * @author: Liujin
 * @version: V1.0
 * @date: 2018-08-01
 * @time: 8:24
 */
public interface AsyncLoader<K> {

      /**
       * to prepare a value by this key,may be asynchronous
       *
       * @param key key
       */
      void prepareValue (K key);
}