package tech.threekilogram.depository.net;

/**
 * 该接口完成根据key从网络下载value
 *
 * @author: Liujin
 * @version: V1.0
 * @date: 2018-08-02
 * @time: 11:42
 */
public interface NetLoader<K, V> {

      /**
       * load a value from net with this key
       *
       * @param key load a value from Net
       *
       * @return value to this key
       */
      V loadFromNet (K key);
}
