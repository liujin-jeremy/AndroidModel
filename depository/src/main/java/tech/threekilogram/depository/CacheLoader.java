package tech.threekilogram.depository;

/**
 * @author liujin
 */
public interface CacheLoader<V> {

      /**
       * 从网络加载
       *
       * @param url url
       *
       * @return 对象
       */
      V loadFromNet ( String url );

      /**
       * 保存一个对象到内存
       *
       * @param key 使用该key保存值
       * @param v 需要保存的值
       */
      void saveToMemory ( String key, V v );

      /**
       * 测试在内存中是否包含该值
       *
       * @param key key
       *
       * @return true 包含该值
       */
      boolean containsOfMemory ( String key );

      /**
       * 从内存中删除该值
       *
       * @param key key
       *
       * @return 该key对应的值
       */
      V removeFromMemory ( String key );

      /**
       * 从内存中加载该值
       *
       * @param key key
       *
       * @return 该key对应的值
       */
      V loadFromMemory ( String key );

      /**
       * 返回内存中数据数量
       *
       * @return 数据量
       */
      int memorySize ( );

      /**
       * 清除所有内存中数据
       */
      void clearMemory ( );

      /**
       * 测试该key对应的value是否存在于本地文件中
       *
       * @param key key
       *
       * @return true:存在于本地文件中
       */
      boolean containsOfFile ( String key );

      /**
       * 保存一个json对象到本地文件
       *
       * @param key key
       * @param v value
       */
      void saveToFile ( String key, V v );

      /**
       * 删除该key对应的缓存文件
       *
       * @param key key
       */
      void removeFromFile ( String key );

      /**
       * 从本地文件加载json对象
       *
       * @param key key
       *
       * @return 该key对应json对象
       */
      V loadFromFile ( String key );

      /**
       * 清除所有文件
       */
      void clearFile ( );

      /**
       * 同时测试内存和本地文件是否包含该缓存
       *
       * @param key key
       *
       * @return true:包含该缓存
       */
      boolean containsOf ( String key );

      /**
       * 同时保存到内存和本地文件
       *
       * @param key key
       * @param v value
       */
      void save ( String key, V v );

      /**
       * 依次从内存本地缓存读取该值
       *
       * @param key key
       *
       * @return 该key对应的值
       */
      V load ( String key );
}
