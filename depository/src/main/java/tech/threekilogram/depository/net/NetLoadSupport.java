package tech.threekilogram.depository.net;

import java.io.IOException;

/**
 * @author: Liujin
 * @version: V1.0
 * @date: 2018-07-31
 * @time: 9:24
 */
public interface NetLoadSupport<K, V> {



      /**
       * get a value from net
       *
       * @param key download the value to this key
       *
       * @return value or null id there is no this value to key
       *
       * @throws IOException exception
       */
      V loadFromNet (K key) throws IOException;
}
