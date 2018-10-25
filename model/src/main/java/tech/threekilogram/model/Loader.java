package tech.threekilogram.model;

/**
 * @author Liujin 2018-10-25:18:21
 */
public interface Loader<K, V> {

      /**
       * 使用一个key读取对应的value
       *
       * @param k key
       *
       * @return value key 对应的value
       */
      V load ( K k );
}
