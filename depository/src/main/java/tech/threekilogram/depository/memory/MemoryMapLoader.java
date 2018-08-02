package tech.threekilogram.depository.memory;

import android.support.v4.util.ArrayMap;

/**
 * use {@link ArrayMap} to save value in memory
 *
 * @param <K> the key type of value
 * @param <V> the value type
 *
 * @author liujin
 */
public class MemoryMapLoader<K, V> implements MemoryLoadSupport<ArrayMap<K, V>, K, V> {

      private ArrayMap<K, V> mContainer;

      public MemoryMapLoader () {

            mContainer = new ArrayMap<>();
      }

      public MemoryMapLoader (int size) {

            mContainer = new ArrayMap<>(size);
      }

      @Override
      public ArrayMap<K, V> container () {

            return mContainer;
      }

      @Override
      public int size () {

            return mContainer.size();
      }

      public boolean containsOfValue (V v) {

            return mContainer.containsValue(v);
      }

      @Override
      public boolean containsOfKey (K key) {

            return mContainer.containsKey(key);
      }

      @Override
      public void clear () {

            mContainer.clear();
      }

      @Override
      public V save (K key, V value) {

            return mContainer.put(key, value);
      }

      @Override
      public V remove (K key) {

            return mContainer.remove(key);
      }

      @Override
      public V load (K key) {

            return mContainer.get(key);
      }

      @Override
      public boolean containsOf (K key) {

            return mContainer.containsKey(key);
      }
}
