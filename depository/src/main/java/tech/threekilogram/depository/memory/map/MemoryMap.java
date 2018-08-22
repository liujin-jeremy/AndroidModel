package tech.threekilogram.depository.memory.map;

import android.support.v4.util.ArrayMap;
import tech.threekilogram.depository.memory.Memory;

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

public class MemoryMap<K, V> implements Memory<K, V> {

      protected ArrayMap<K, V> mContainer;

      @SuppressWarnings("WeakerAccess")
      public MemoryMap () {

            mContainer = new ArrayMap<>();
      }

      public MemoryMap ( int size) {

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
