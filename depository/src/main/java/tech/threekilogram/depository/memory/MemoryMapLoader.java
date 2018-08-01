package tech.threekilogram.depository.memory;

import android.support.v4.util.ArrayMap;
import java.util.Collection;
import java.util.Iterator;

/**
 * use {@link ArrayMap} to save value in memory
 *
 * @param <K> the key type of value
 * @param <V> the value type
 *
 * @author liujin
 */
public class MemoryMapLoader<K, V> implements MemoryLoadSupport<ArrayMap<K, V>, V, K> {

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

      @Override
      public V putInToMemory (K key, V v) {

            return mContainer.put(key, v);
      }

      public boolean containsOfValue (V v) {

            return mContainer.containsValue(v);
      }

      @Override
      public boolean containsOfKey (K key) {

            return mContainer.containsKey(key);
      }

      @Override
      public V loadFromMemory (K key) {

            return mContainer.get(key);
      }

      @Override
      public V removeFromMemory (K key) {

            return mContainer.remove(key);
      }

      @Override
      public void clear () {

            mContainer.clear();
      }

      public Collection<V> allValue () {

            return mContainer.values();
      }

      public Iterator<V> iterator () {

            return allValue().iterator();
      }
}
