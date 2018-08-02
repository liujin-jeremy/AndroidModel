package tech.threekilogram.depository.net;

/**
 * @author: Liujin
 * @version: V1.0
 * @date: 2018-08-02
 * @time: 11:42
 */
public interface NetLoaderConverter<K, V> {

      /**
       * load a value from net
       *
       * @param key load a value from Net
       *
       * @return value to this key
       */
      V loadFromNet (K key);
}
