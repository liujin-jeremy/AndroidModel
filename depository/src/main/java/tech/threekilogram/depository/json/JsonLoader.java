package tech.threekilogram.depository.json;

import android.support.v4.util.ArrayMap;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import okhttp3.ResponseBody;
import tech.threekilogram.depository.file.BaseFileConverter;
import tech.threekilogram.depository.file.BaseFileLoader;
import tech.threekilogram.depository.file.loader.DiskLruLoader;
import tech.threekilogram.depository.file.loader.FileLoader;
import tech.threekilogram.depository.key.KeyNameConverter;
import tech.threekilogram.depository.memory.map.MemoryList;
import tech.threekilogram.depository.net.retrofit.BaseRetrofitConverter;
import tech.threekilogram.depository.net.retrofit.loader.RetrofitLoader;

/**
 * @author liujin
 */
public class JsonLoader<V> {

      /**
       * 内存
       */
      protected MemoryList<V>           mMemoryList;
      /**
       * 文件
       */
      protected BaseFileLoader<V>       mFileContainer;
      /**
       * 网络
       */
      protected RetrofitLoader<List<V>> mRetrofitLoader;
      /**
       * 辅助将流转换为json bean
       */
      protected JsonConverter<V>        mJsonConverter;
      /**
       * 是否正在加载
       */
      protected AtomicBoolean        isLoading             = new AtomicBoolean();
      /**
       * 缓存文件过期策略,一旦过期就重新创建
       */
      protected CacheExpiredStrategy mCacheExpiredStrategy = new DefaultCacheExpiredStrategy();
      /**
       * true时,正在缓存文件,不需要再次启动缓存{@link #trimMemory(int, int)}
       */
      protected AtomicBoolean        isCachingFile         = new AtomicBoolean();
      /**
       * 临时保存需要需要写入文件的bean
       */
      protected ArrayMap<Integer, V> mToCacheFileTemp      = new ArrayMap<>();

      /**
       * @param dir 缓存文件夹
       * @param jsonConverter 转换流为bean对象
       */
      public JsonLoader ( File dir, JsonConverter<V> jsonConverter ) {

            mJsonConverter = jsonConverter;

            mMemoryList = new MemoryList<>();

            mFileContainer = new FileLoader<>(
                dir,
                new JsonFileConverter()
            );

            mRetrofitLoader = new RetrofitLoader<>(
                dir,
                100 * 1024 * 1024,
                new JsonRetrofitListConverter()
            );
      }

      /**
       * @param dir 缓存文件夹
       * @param maxFileSize 缓存文件夹最大大小
       * @param jsonConverter 转换流为bean
       */
      public JsonLoader (
          File dir,
          int maxFileSize,
          JsonConverter<V> jsonConverter ) throws IOException {

            mJsonConverter = jsonConverter;

            mMemoryList = new MemoryList<>();

            mFileContainer = new DiskLruLoader<>(
                dir,
                maxFileSize,
                new JsonFileConverter()
            );

            mRetrofitLoader = new RetrofitLoader<>(
                dir,
                maxFileSize,
                new JsonRetrofitListConverter()
            );
      }

      /**
       * 设置缓存文件过期策略,如果没有设置,默认使用{@link DefaultCacheExpiredStrategy}
       */
      public void setCacheExpiredStrategy (
          CacheExpiredStrategy cacheExpiredStrategy ) {

            mCacheExpiredStrategy = cacheExpiredStrategy;
      }

      /**
       * 获取设置的缓存文件过期策略
       */
      public CacheExpiredStrategy getCacheExpiredStrategy ( ) {

            return mCacheExpiredStrategy;
      }

      /**
       * 从内存获取缓存的数据,如果没有数据(return null);
       *
       * @param index value index
       *
       * @return value at index
       */
      public V loadMemory ( int index ) {

            /* 1.从内存中读取 */

            V v = mMemoryList.load( index );

            if( v != null ) {
                  return v;
            }

            /* 2.从移除缓存中读取 */

            v = mToCacheFileTemp.get( index );

            if( v != null ) {

                  mToCacheFileTemp.remove( index );
                  mMemoryList.save( index, v );
                  return v;
            }

            return null;
      }

      /**
       * 修改/添加内存中数据
       *
       * @param index 修改位置
       * @param v 新值
       */
      public void saveToMemory ( int index, V v ) {

            mMemoryList.save( index, v );
      }

      /**
       * 删除内存中该索引值
       *
       * @param index 索引
       *
       * @return 原先在该位置的值
       */
      public V removeAtMemory ( int index ) {

            return mMemoryList.remove( index );
      }

      /**
       * 从缓存文件中获取缓存的数据,如果没有数据或者缓存过期返回null
       *
       * @param index 数据索引
       *
       * @return 数据 or null
       */
      public V loadFile ( int index ) {

            String key = String.valueOf( index );
            File file = mFileContainer.getFile( key );

            if( file.exists() ) {

                  if( mCacheExpiredStrategy.testCacheFile( file ) ) {

                        return null;
                  } else {

                        return mFileContainer.load( key );
                  }
            }
            return null;
      }

      /**
       * 修改/添加缓存文件中数据
       *
       * @param index 修改位置
       * @param v 新值
       */
      public void saveFile ( int index, V v ) {

            mFileContainer.save( String.valueOf( index ), v );
      }

      /**
       * 删除内存中该索引值
       *
       * @param index 索引
       */
      public void deleteFile ( int index ) {

            mFileContainer.remove( String.valueOf( index ) );
      }

      /**
       * 从网络加载更多数据数据
       *
       * @param url url
       */
      public List<V> loadMore ( String url ) {

            isLoading.set( true );
            List<V> list = mRetrofitLoader.load( url );
            isLoading.set( false );
            return list;
      }

      /**
       * 判断{@link #mRetrofitLoader}是否正在运行中
       *
       * @return true : 正在从网络加载数据中
       */
      public boolean isLoading ( ) {

            return isLoading.get();
      }

      /**
       * 从网络加载更多数据数据,并保存到指定索引位置,最好在后台线程操作
       *
       * @param index 加载后的数据使用该索引为起点保存到缓存中
       */
      public void saveMore ( int index, List<V> list ) {

            mMemoryList.saveMore( index, list );
      }

      /**
       * @return 当前内存中数据量
       */
      public int getMemorySize ( ) {

            return mMemoryList.size();
      }

      /**
       * 获取索引位置的缓存文件
       *
       * @param index 索引
       *
       * @return null or 缓存文件
       */
      public File getCacheFile ( int index ) {

            File file = mFileContainer.getFile( String.valueOf( index ) );
            if( file.exists() ) {

                  return file;
            }
            return null;
      }

      /**
       * 从内存中移除指定区间的[from,to)数据,并缓存到文件
       *
       * @param from 起始位置
       * @param to 结束位置
       */
      public void trimMemory ( int from, int to ) {

            if( isCachingFile.get() ) {
                  return;
            }

            try {
                  ArrayMap<Integer, V> container = mMemoryList.container();
                  ArrayMap<Integer, V> cacheFile = mToCacheFileTemp;

                  int size = container.size();
                  for( int i = 0; i < size; i++ ) {
                        Integer key = container.keyAt( i );
                        if( key >= from && key < to ) {

                              V v = container.removeAt( i );
                              cacheFile.put( key, v );
                              i--;
                        }
                  }
            } catch(Exception e) {
                  e.printStackTrace();
            }

            notifyToCacheFile();
      }

      /**
       * 缓存文件
       */
      private void notifyToCacheFile ( ) {

            isCachingFile.set( true );

            ArrayMap<Integer, V> cacheFile = mToCacheFileTemp;
            CacheExpiredStrategy cacheExpiredStrategy = mCacheExpiredStrategy;
            BaseFileLoader<V> fileContainer = mFileContainer;

            while( cacheFile.size() > 0 ) {

                  Integer key = cacheFile.keyAt( 0 );
                  V v = cacheFile.removeAt( 0 );

                  if( v != null ) {

                        String fileKey = key.toString();
                        File file = fileContainer.getFile( key.toString() );
                        if( file.exists() ) {

                              /* 已经有缓存文件,但是已经过期 */
                              if( cacheExpiredStrategy.testCacheFile( file ) ) {

                                    fileContainer.save( fileKey, v );
                              }
                        } else {

                              /* 没有缓存文件需要缓存一下 */
                              fileContainer.save( fileKey, v );
                        }
                  }
            }
            isCachingFile.set( false );
      }

      /**
       * 清空缓存文件夹,每次退出app时或者进入app时推荐清空缓存文件
       *
       * @throws IOException 异常
       */
      public void clearCacheFile ( ) throws IOException {

            mFileContainer.clear();
      }

      // ========================= 辅助内部类 =========================

      /**
       * 辅助保存json为文件
       */
      private class JsonFileConverter extends BaseFileConverter<V> {

            JsonFileConverter ( ) {

                  mKeyNameConverter.setMode( KeyNameConverter.DEFAULT );
            }

            @Override
            public String fileName ( String key ) {

                  return mKeyNameConverter.encodeToName( key );
            }

            @Override
            public V toValue ( String key, InputStream stream ) throws Exception {

                  return mJsonConverter.fromJson( stream );
            }

            @Override
            public void saveValue ( String key, OutputStream outputStream, V value )
                throws IOException {

                  mJsonConverter.toJson( outputStream, value );
            }
      }

      /**
       * 辅助将网络资源流转为json bean list
       */
      private class JsonRetrofitListConverter extends BaseRetrofitConverter<List<V>> {

            @Override
            public List<V> onExecuteSuccess ( String key, ResponseBody response ) throws Exception {

                  return mJsonConverter.fromJsonArray( response.byteStream() );
            }
      }

      /**
       * 缓存过期策略
       */
      public interface CacheExpiredStrategy {

            /**
             * 测试缓存文件是否过期
             *
             * @param cache 缓存文件
             *
             * @return true 过期了
             */
            boolean testCacheFile ( File cache );
      }

      /**
       * 文件默认缓存策略,永不过期,如果需要清除缓存,在app启动时{@link #clearCacheFile()}清除上一次缓存的文件
       */
      public static class DefaultCacheExpiredStrategy implements CacheExpiredStrategy {

            /**
             * @param cache 缓存文件
             */
            @Override
            public boolean testCacheFile ( File cache ) {

                  return false;
            }
      }

      /**
       * 文件缓存策略,使用时间
       */
      public static class CacheExpiredStrategyTime implements CacheExpiredStrategy {

            /**
             * 过期时间
             */
            private long mExpiredTime;

            public CacheExpiredStrategyTime ( long expiredTime ) {

                  mExpiredTime = expiredTime;
            }

            /**
             * @param cache 缓存文件
             */
            @Override
            public boolean testCacheFile ( File cache ) {

                  long l = System.currentTimeMillis();
                  long lastModified = cache.lastModified();

                  return l - lastModified > mExpiredTime;
            }
      }
}
