package tech.threekilogram.model.container.memory.lru.size;

/**
 * {@link ObjectSize}的默认实现
 *
 * @author: Liujin
 * @version: V1.0
 * @date: 2018-07-30
 * @time: 8:35
 */
public class SimpleObjectSize<V> implements ObjectSize<V> {

      @Override
      public int sizeOf ( V value ) {

            return 1;
      }
}
