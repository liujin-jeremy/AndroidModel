package tech.threekilogram.depository;

/**
 * 扩展了 {@link Loader} 接口,当实现内存缓存和本地缓存时实现该接口,可以测试key对应的value是否存在,
 * 而网络缓存没有必要实现测试是否存在该value的需要,网络缓存是获取数据的最后一步如果获取到就获取到了,
 * 如果不能那么没有办法了,只能返回一个null,并且从网络测试是否有文件代价太高了
 *
 * @author: Liujin
 * @version: V1.0
 * @date: 2018-08-01
 * @time: 9:33
 */
public interface Container<K, V> extends Loader<K, V> {

      /**
       * save a value to key,then could load value with this key
       *
       * @param key where to save
       * @param value value to save
       *
       * @return if a value exist at key return it, if key to a null value return null
       */
      V save (K key, V value);

      /**
       * remove the value to this key
       *
       * @param key remove the value at key
       *
       * @return if key exist remove value at key , or null returned
       */
      V remove (K key);

      /**
       * test contains a value or not
       *
       * @param key contains of a value to this key
       *
       * @return true contains this value
       */
      boolean containsOf (K key);
}
