package tech.threekilogram.depository.memory;

import tech.threekilogram.depository.Loader;

/**
 * this make some Object could load from Memory
 *
 * @param <C> container type ,use this to save value in Memory
 * @param <V> value type to load
 * @param <K> identify to value, could use this to get value from container
 *
 * @author liujin
 */
public interface MemoryLoadSupport<C, K, V> extends Loader<K, V> {

      /**
       * use this container to save Value in memory could use this to get all operating, such as
       * clear all Value in Memory
       *
       * @return container
       */
      C container ();

      /**
       * size of this container
       *
       * @return size of container
       */
      int size ();

      /**
       * put a value to memory
       *
       * @param key key for value
       * @param v value
       *
       * @return if key already for a value,return it, or if key for a null then return null
       */
      V putInToMemory (K key, V v);

      /**
       * if contains that value return true
       *
       * @param key key to test if contains in Memory
       *
       * @return true contains that value to this key, false withOut a value match this key
       */
      boolean containsOfKey (K key);

      /**
       * remove a value from memory
       *
       * @param key use this key to find out value to remove
       *
       * @return value removed , or null if there is no that identify
       */
      V removeFromMemory (K key);

      /**
       * clear all value in memory
       */
      void clear ();
}
