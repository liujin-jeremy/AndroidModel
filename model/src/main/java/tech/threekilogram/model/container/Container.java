package tech.threekilogram.model.container;

import tech.threekilogram.model.Loader;

/**
 * 容器接口,实现该接口,可以保存key value到一个容器中,例如内存或者本地文件或者数据库中
 *
 * @author: Liujin
 * @version: V1.0
 * @date: 2018-08-01
 * @time: 9:33
 */
public interface Container<K, V> extends Loader<K, V> {

      /**
       * save a value to key,then could loadStringFromNet value with this key
       *
       * @param key where to save
       * @param value value to save
       *
       * @return if a value exist at key return it, if key to a null value return null
       */
      V save ( K key, V value );

      /**
       * remove the value to this key
       *
       * @param key remove the value at key
       *
       * @return if key exist remove value at key , or null returned
       */
      V remove ( K key );

      /**
       * test contains a value or not
       *
       * @param key contains of a value to this key
       *
       * @return true contains this value
       */
      boolean containsOf ( K key );
}
