package tech.threekilogram.depository.net;

import android.support.annotation.NonNull;
import tech.threekilogram.depository.ContainerLoader;
import tech.threekilogram.depository.Loader;

/**
 * @author: Liujin
 * @version: V1.0
 * @date: 2018-08-02
 * @time: 11:46
 */
public class NetLoader<K, V> implements Loader<K, V> {

      protected ContainerLoader<K, V>    mLoader;
      protected NetLoaderConverter<K, V> mNetLoaderConverter;

      public NetLoader (
          @NonNull NetLoaderConverter<K, V> netLoaderConverter,
          ContainerLoader<K, V> loader) {

            mLoader = loader;
            mNetLoaderConverter = netLoaderConverter;
      }

      @Override
      public V save (K key, V value) {

            /* use a container a save, we cant save a value to net */

            if(mLoader != null) {

                  return mLoader.save(key, value);
            }

            return null;
      }

      @Override
      public V remove (K key) {

            /* use container to save and remove from container */

            if(mLoader != null) {
                  return mLoader.remove(key);
            }

            return null;
      }

      @Override
      public V load (K key) {

            /* first read value from container */

            if(mLoader != null && mLoader.containsOf(key)) {
                  V load = mLoader.load(key);
                  if(load != null) {
                        return load;
                  }
            }

            V v = mNetLoaderConverter.loadFromNet(key);

            if(v != null) {
                  save(key, v);
                  return v;
            }

            return null;
      }
}
