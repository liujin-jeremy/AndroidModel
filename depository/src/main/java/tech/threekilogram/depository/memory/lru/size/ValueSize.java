package tech.threekilogram.depository.memory.lru.size;

/**
 * 该接口实现给定一个类{@code V}返回在内存中的大小,
 * 如果在{@link tech.threekilogram.depository.memory.lru.MemoryLruCacheLoader}只关心数据多少直接返回1,
 * 如果在{@link tech.threekilogram.depository.memory.lru.MemoryLruCacheLoader}关心内存多少,那么需要返回数据占据内存的大小
 * <p>
 * 该接口用于辅助{@link tech.threekilogram.depository.memory.lru.MemoryLruCacheLoader} 正常工作,需要用户实现,
 * 提供了一个默认实现{@link DefaultValueSize}任何数据均返回1
 *
 * @author: Liujin
 * @version: V1.0
 * @date: 2018-07-30
 * @time: 8:33
 */
public interface ValueSize<K, V> {

      /**
       * get a size of value
       *
       * @param k key for value
       * @param v value to get size
       *
       * @return the size of value
       */
      int sizeOf (K k, V v);
}
