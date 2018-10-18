package tech.threekilogram.model;

import java.io.File;

/**
 * 三级缓存接口,实现该接口,可以提供三级缓存,内存/文件/网络
 *
 * @author liujin
 */
public interface CacheLoader<V> {

      /**
       * 从网络加载,如果加载成功需要缓存到内存中{@link #saveToMemory(String, Object)}
       *
       * @param url url
       *
       * @return 对象
       */
      V loadFromNet ( String url );

      /**
       * 从网络下载到文件夹,并不读取成对象
       *
       * @param url url
       */
      void download ( String url );

      /**
       * 从网络下载到文件夹,之后读取成对象
       *
       * @param url url
       *
       * @return 缓存对应得对象
       */
      V loadFromDownload ( String url );

      /**
       * 保存一个对象到内存
       *
       * @param url 使用该key保存值
       * @param v 需要保存的值
       */
      void saveToMemory ( String url, V v );

      /**
       * 测试在内存中是否包含该值
       *
       * @param url url
       *
       * @return true 包含该值
       */
      boolean containsOfMemory ( String url );

      /**
       * 从内存中删除该值
       *
       * @param url url
       *
       * @return 该key对应的值
       */
      V removeFromMemory ( String url );

      /**
       * 从内存中加载该值
       *
       * @param url url
       *
       * @return 该key对应的值
       */
      V loadFromMemory ( String url );

      /**
       * 内存中数据量
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
       * @param url url
       *
       * @return true:存在于本地文件中
       */
      boolean containsOfFile ( String url );

      /**
       * 保存一个json对象到本地文件
       *
       * @param url url
       * @param v value
       */
      void saveToFile ( String url, V v );

      /**
       * 获取url对应的缓存文件
       *
       * @param url url
       *
       * @return 该url对应的缓存文件, 该文件可能不存在, 需要自己判断一下
       */
      File getFile ( String url );

      /**
       * 删除该key对应的缓存文件
       *
       * @param url url
       */
      void removeFromFile ( String url );

      /**
       * 从本地文件加载json对象,如果加载成功缓存到内存中{@link #saveToMemory(String, Object)}
       *
       * @param url url
       *
       * @return 该key对应json对象
       */
      V loadFromFile ( String url );

      /**
       * 清除所有文件
       */
      void clearFile ( );

      /**
       * 同时测试内存和本地文件是否包含该缓存
       *
       * @param url url
       *
       * @return true:包含该缓存
       */
      boolean containsOf ( String url );

      /**
       * 同时保存到内存和本地文件
       *
       * @param url url
       * @param v value
       */
      void save ( String url, V v );

      /**
       * 依次从内存本地缓存读取该值
       *
       * @param url url
       *
       * @return 该key对应的值
       */
      V load ( String url );
}
