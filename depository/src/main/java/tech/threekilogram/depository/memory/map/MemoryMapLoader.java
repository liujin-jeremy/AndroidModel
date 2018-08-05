package tech.threekilogram.depository.memory.map;

import android.support.v4.util.ArrayMap;
import tech.threekilogram.depository.memory.MemoryLoader;

/**
 * 使用键值对的形式保存数据到内存中,低层使用的是{@link ArrayMap}
 * <p>
 * use {@link ArrayMap} to save value in memory
 *
 * @param <K> the key type of value
 * @param <V> the value type
 *
 * @author liujin
 */

public class MemoryMapLoader<K, V> implements MemoryLoader<K, V> {

      private ArrayMap<K, V> mContainer;

      @SuppressWarnings("WeakerAccess")
      public MemoryMapLoader () {

            mContainer = new ArrayMap<>();
      }

      public MemoryMapLoader (int size) {

            mContainer = new ArrayMap<>(size);
      }

      public ArrayMap<K, V> container () {

            return mContainer;
      }

      @Override
      public int size () {

            return mContainer.size();
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
