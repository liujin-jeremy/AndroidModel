package tech.threekilogram.depository.net;

/**
 * 该接口辅助{@link NetLoader}完成从key到value的变换
 *
 * @author: Liujin
 * @version: V1.0
 * @date: 2018-08-02
 * @time: 11:42
 */
public interface NetLoaderConverter<K, V> {

      /**
       * load a value from net with this key
       *
       * @param key load a value from Net
       *
       * @return value to this key
       */
      V loadFromNet (K key);
}
