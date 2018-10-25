package tech.threekilogram.model;

/**
 * 这是这个数据层框架的总接口,所有内存缓存,文件缓存,网络缓存都需要实现该接口,
 * 该框架可以使用任意类型的key{@code K}保存/读取任意类型的数据{@code V}
 *
 * @param <K> key 类型
 * @param <V> value 类型
 *
 * @author liujin
 */
public interface Loader<K, V> {

      /**
       * 使用一个key读取对应的value
       *
       * @param key key
       *
       * @return value key 对应的value
       */
      V load ( K key );
}
