package tech.threekilogram.model;

/**
 * @author Liujin 2018-10-25:18:28
 */
public interface Container<K> {

      /**
       * remove the value to this key
       *
       * @param key remove the value at key
       *
       * @return if key exist remove value at key , or null returned
       */
      void remove ( K key );

      /**
       * test contains a value or not
       *
       * @param key contains of a value to this key
       *
       * @return true contains this value
       */
      boolean containsOf ( K key );

      /**
       * 清空
       */
      void clear ( );
}
