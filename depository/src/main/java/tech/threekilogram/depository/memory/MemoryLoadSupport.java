package tech.threekilogram.depository.memory;

import tech.threekilogram.depository.ContainerLoader;

/**
 * this make some Object could load from Memory
 *
 * @param <C> container type ,use this to save value in Memory
 * @param <V> value type to load
 * @param <K> identify to value, could use this to get value from container
 *
 * @author liujin
 */
public interface MemoryLoadSupport<C, K, V> extends ContainerLoader<K, V> {

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
       * if contains that value return true
       *
       * @param key key to test if contains in Memory
       *
       * @return true contains that value to this key, false withOut a value match this key
       */
      boolean containsOfKey (K key);

      /**
       * clear all value in memory
       */
      void clear ();
}
