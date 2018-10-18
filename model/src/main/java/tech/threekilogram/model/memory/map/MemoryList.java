package tech.threekilogram.model.memory.map;

import java.util.List;

/**
 * 使用数字索引作为key在内存中保存数据
 *
 * @param <V> 数据类型
 *
 * @author liujin
 */
public class MemoryList<V> extends MemoryMap<Integer, V> {

      /**
       * 使用index为起点保存整个数据
       *
       * @param startIndex 起始坐标
       * @param values 数据
       */
      public void saveMore ( int startIndex, List<V> values ) {

            final int size = values.size();
            for( int i = 0; i < size; i++ ) {

                  V v = values.get( i );
                  save( startIndex + i, v );
            }
      }

      /**
       * 使用index为终点保存整个数据
       *
       * @param startIndex 终点
       * @param values 数据
       */
      public void saveLess ( int startIndex, List<V> values ) {

            final int size = values.size();
            final int off = startIndex - size;

            for( int i = 0; i < size; i++ ) {

                  V v = values.get( i );
                  save( off + i + 1, v );
            }
      }
}
