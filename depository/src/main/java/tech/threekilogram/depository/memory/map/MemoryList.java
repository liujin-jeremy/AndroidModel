package tech.threekilogram.depository.memory.map;

import java.util.List;

/**
 * 使用数字索引在内存中保存数据,底层不使用{@link java.util.List}的原因:
 * 在保存数据的时候可能数字索引并不是按照顺序保存,例如保存顺序: 9,1,8,2,6,7,3,4,5,6;
 * 即先保存索引为9的后保存索引为1的,使用{@link java.util.List}无法实现,它只能按照提交顺序获取索引
 *
 * @param <V> type of value 数据类型
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
