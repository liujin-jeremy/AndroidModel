package tech.threekilogram.depository.memory;

import tech.threekilogram.depository.ContainerLoader;

/**
 * 这是内存缓存的总接口,所有内存缓存均实现了该接口,
 * <p>
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
       * 因为数据保存形式的多样性(可以是 list/map/set/lruCache),所以提供该方法用于获取容器,之后可以直接操作容器
       * <p>
       * use this container to save Value in memory could use this to get all operating, such as
       * clear all Value in Memory
       *
       * @return container
       */
      C container ();

      /**
       * 已经保存数据数量
       * <p>
       * size of this container
       *
       * @return size of container
       */
      int size ();

      /**
       * clear all value in memory
       */
      void clear ();
}
