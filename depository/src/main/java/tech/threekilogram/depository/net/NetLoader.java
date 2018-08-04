package tech.threekilogram.depository.net;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import tech.threekilogram.depository.ContainerLoader;
import tech.threekilogram.depository.Loader;

/**
 * 用于从网络获取缓存文件,需要一个辅助类{@link NetLoaderConverter}来帮助该类正常工作,同时也可以提供一个{@link
 * ContainerLoader}保存网络数据到内存或者文件系统
 *
 * @param <K> key 类型
 * @param <V> value 类型
 *
 * @author liujin
 */
@SuppressWarnings("WeakerAccess")
public class NetLoader<K, V> implements Loader<K, V> {

      /**
       * 用于辅助该类保存数据到本地
       */
      protected ContainerLoader<K, V>    mLoader;
      /**
       * 辅助该类完成转换工作
       */
      protected NetLoaderConverter<K, V> mConverter;

      /**
       * @param converter 不可以为null,该类需要它辅助工作
       */
      public NetLoader (
          @NonNull NetLoaderConverter<K, V> converter) {

            this(converter, null);
      }

      /**
       * @param converter 不可以为null,该类需要它辅助工作
       * @param loader 可以为null,不为null时,可以辅助保存数据
       */
      public NetLoader (
          @NonNull NetLoaderConverter<K, V> converter,
          @Nullable ContainerLoader<K, V> loader) {

            mLoader = loader;
            mConverter = converter;
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

            /* if without value saved load from net */

            V v = mConverter.loadFromNet(key);

            if(v != null) {
                  save(key, v);
                  return v;
            }

            return null;
      }
}
