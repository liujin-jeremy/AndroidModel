package tech.threekilogram.depository.json;

import android.support.v4.util.ArrayMap;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import okhttp3.ResponseBody;
import tech.threekilogram.depository.file.BaseFileConverter;
import tech.threekilogram.depository.file.BaseFileLoader;
import tech.threekilogram.depository.file.BaseFileLoader.SaveStrategyValue;
import tech.threekilogram.depository.file.loader.DiskLruLoader;
import tech.threekilogram.depository.file.loader.FileLoader;
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
       * 标记开始索引,用于{@link #loadMore(String)}{@link #loadLess(String)}时添加数据的起始索引
       */
      protected final int mStartIndex;
      /**
       * 已缓存数量
       */
      protected AtomicInteger mCacheCount    = new AtomicInteger();
      /**
       * 已缓存更多数据量
       */
      protected AtomicInteger mLoadMoreCount = new AtomicInteger();
      /**
       * 已缓存更少数据量
       */
      protected AtomicInteger mLoadLessCount = new AtomicInteger();
      /**
       * 标识在已经存在文件缓存的情况下如何保存文件缓存
       */
      private boolean isOverWrite;
      /**
       * 是否正在加载
       */
      protected AtomicBoolean        isLoading            = new AtomicBoolean();
      /**
       * 内存最大保存数量
       */
      protected int                  mMemoryMaxCount      = 128;
      /**
       * {@link #cacheMemory(int, boolean)}每次调用时缓存多少数据
       */
      protected int                  mCacheCountEveryTime = mMemoryMaxCount >> 2;
      /**
       * true时,正在缓存文件,不需要再次启动缓存
       */
      protected AtomicBoolean        isCachingFile        = new AtomicBoolean();
      /**
       * 临时保存需要需要写入文件的bean
       */
      protected ArrayMap<Integer, V> mCacheFile           = new ArrayMap<>();

      /**
       * @param dir 缓存文件夹
       * @param jsonConverter 转换流为bean对象
       */
      public JsonLoader ( File dir, JsonConverter<V> jsonConverter ) {

            this( dir, jsonConverter, 0 );
      }

      /**
       * @param dir 缓存文件夹
       * @param jsonConverter 转换流为bean对象
       * @param index 第一次加载时,如果使用{@link #loadMore(String)},该值作为起始index,
       *     如果使用{@link #loadLess(String)},该值-1是该次加载索引的最大值
       */
      public JsonLoader ( File dir, JsonConverter<V> jsonConverter, int index ) {

            mJsonConverter = jsonConverter;

            mMemoryList = new MemoryList<>();

            mFileContainer = new FileLoader<>(
                dir,
                new JsonFileConverter()
            );

            mRetrofitLoader = new RetrofitLoader<>(
                new JsonRetrofitListConverter()
            );

            mStartIndex = index;
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

            this( dir, maxFileSize, jsonConverter, 0 );
      }

      /**
       * @param dir 缓存文件夹
       * @param maxFileSize 缓存文件夹最大大小
       * @param jsonConverter 转换流为bean
       * @param index 第一次加载时,如果使用{@link #loadMore(String)},该值作为起始index,
       *     如果使用{@link #loadLess(String)},该值-1是该次加载索引的最大值
       *
       * @throws IOException IoException
       */
      public JsonLoader (
          File dir,
          int maxFileSize,
          JsonConverter<V> jsonConverter,
          int index ) throws IOException {

            mJsonConverter = jsonConverter;

            mMemoryList = new MemoryList<>();

            mFileContainer = new DiskLruLoader<>(
                dir,
                maxFileSize,
                new JsonFileConverter()
            );

            mRetrofitLoader = new RetrofitLoader<>(
                new JsonRetrofitListConverter()
            );

            mStartIndex = index;
      }

      /**
       * 配置内存中最大保存数据量,最好是2^n
       *
       * @param memoryMaxCount 最大内存中保存数据量
       */
      public void setMemoryMaxCount ( int memoryMaxCount ) {

            if( memoryMaxCount < 16 ) {
                  mMemoryMaxCount = 16;
            }

            mMemoryMaxCount = memoryMaxCount;
            mCacheCountEveryTime = memoryMaxCount >> 2;
      }

      /**
       * 仅从网络加载数据,并不缓存
       *
       * @param url url
       */
      public List<V> load ( String url ) {

            return mRetrofitLoader.load( url );
      }

      /**
       * 加载更多数据
       *
       * @param url url
       */
      public void loadMore ( String url ) {

            isLoading.set( true );

            List<V> load = mRetrofitLoader.load( url );

            /* empty */
            int size;
            if( load == null || ( size = load.size() ) == 0 ) {
                  return;
            }
            /* save to memory */
            int memorySize = mMemoryList.size();
            if( memorySize == 0 ) {

                  mMemoryList.saveMore( mStartIndex, load );
            } else {

                  mMemoryList.saveMore( getCacheMaxIndex() + 1, load );
            }
            mCacheCount.addAndGet( size );
            mLoadMoreCount.addAndGet( size );

            isLoading.set( false );

            if( mMemoryList.size() >= mMemoryMaxCount * 75 / 100 ) {

                  cacheMemory( mCacheCountEveryTime, false );
            }
      }

      /**
       * 加载更少数据
       *
       * @param url url
       */
      public void loadLess ( String url ) {

            isLoading.set( true );

            List<V> load = mRetrofitLoader.load( url );

            /* empty */
            int size;
            if( load == null || ( size = load.size() ) == 0 ) {
                  return;
            }
            /* save to memory */
            int memorySize = mMemoryList.size();
            if( memorySize == 0 ) {

                  mMemoryList.saveLess( mStartIndex - 1, load );
            } else {

                  mMemoryList.saveLess( getCacheMinIndex() - 1, load );
            }
            mCacheCount.addAndGet( size );
            mLoadLessCount.addAndGet( size );

            isLoading.set( false );

            if( mMemoryList.size() >= mMemoryMaxCount * 75 / 100 ) {

                  cacheMemory( mCacheCountEveryTime, true );
            }
      }

      /**
       * @return true : 正在加载数据中,该方法用于判断是否正在加载中,通过判断该值可以防止多次加载同一数据
       */
      public boolean isLoading ( ) {

            return isLoading.get();
      }

      /**
       * 获取缓存的数据,如果没有数据(return null)需要{@link #loadMore(String)}或者{@link #loadLess(String)}加载数据
       *
       * @param index value index
       *
       * @return value at index
       */
      public V get ( int index ) {

            V v = mMemoryList.load( index );

            if( v != null ) {
                  return v;
            }

            v = mCacheFile.get( index );

            if( v != null ) {

                  mCacheFile.remove( index );
                  mMemoryList.save( index, v );
                  return v;
            }

            if( mFileContainer != null ) {

                  Integer key = index;
                  v = mFileContainer.load( key.toString() );
                  if( v != null ) {

                        mMemoryList.save( key, v );
                        return v;
                  }
            }

            return null;
      }

      /**
       * 修改/添加内存中数据
       *
       * @param index 修改位置
       * @param v 新值
       */
      public void save ( int index, V v ) {

            mMemoryList.save( index, v );

            if( index < getCacheMinIndex() ) {

                  mLoadLessCount.addAndGet( 1 );
            } else if( index > getCacheMaxIndex() ) {

                  mLoadMoreCount.addAndGet( 1 );
            }
      }

      /**
       * 修改/添加缓存文件中数据
       *
       * @param index 位置
       * @param v 新值
       */
      public void saveFile ( int index, V v ) {

            mFileContainer.save( String.valueOf( index ), v );
      }

      /**
       * 删除内存中该索引值
       *
       * @param index 索引
       *
       * @return 在索引位置的值
       */
      public V delete ( int index ) {

            return mMemoryList.remove( index );
      }

      /**
       * 设置缓存文件保存/读取策略{@link BaseFileLoader#setSaveStrategy(int)}
       *
       * @param strategy 策略
       */
      public void setSaveStrategy ( @SaveStrategyValue int strategy ) {

            mFileContainer.setSaveStrategy( strategy );
      }

      /**
       * 删除该索引缓存文件,该方法返回值与{@link #setSaveStrategy(int)}有关
       *
       * @param index 索引
       *
       * @return 在索引位置的值或者null,
       */
      public V deleteFile ( int index ) {

            return mFileContainer.remove( String.valueOf( index ) );
      }

      /**
       * 获取索引位置的缓存文件
       *
       * @param index 索引
       *
       * @return null or 缓存文件
       */
      public File getCacheFile ( int index ) {

            return mFileContainer.getFile( String.valueOf( index ) );
      }

      /**
       * @return 当前内存中数据量
       */
      public int getMemorySize ( ) {

            return mMemoryList.size();
      }

      /**
       * @param overWrite true:如果本地存在该缓存将会重写缓存,false:不会更新缓存直接使用旧的
       */
      public void setShouldOverWrite ( boolean overWrite ) {

            isOverWrite = overWrite;
      }

      /**
       * 从内存中移除指定区间的数据,并缓存到文件
       */
      private void cacheMemory ( final int count, boolean fromStart ) {

            if( isCachingFile.get() ) {

                  return;
            }

            try {
                  ArrayMap<Integer, V> container = mMemoryList.container();
                  ArrayMap<Integer, V> cacheFile = mCacheFile;

                  if( fromStart ) {

                        int deleteCount = 0;
                        while( deleteCount < count ) {

                              int size = container.size();
                              Integer key = container.keyAt( size - 1 );
                              V v = container.removeAt( size - 1 );
                              cacheFile.put( key, v );

                              deleteCount++;
                        }
                  } else {

                        int deleteCount = 0;
                        while( deleteCount < count ) {

                              Integer key = container.keyAt( 0 );
                              V v = container.removeAt( 0 );
                              cacheFile.put( key, v );

                              deleteCount++;
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

            ArrayMap<Integer, V> cacheFile = mCacheFile;
            BaseFileLoader<V> fileContainer = mFileContainer;

            while( cacheFile.size() > 0 ) {

                  Integer key = cacheFile.keyAt( 0 );
                  V v = cacheFile.removeAt( 0 );

                  if( v != null ) {

                        String fileKey = key.toString();
                        if( fileContainer.containsOf( fileKey ) ) {

                              /* 已经有缓存文件,但是配置需要覆盖它 */

                              if( isOverWrite ) {
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
       * @return 获取程序运行期间已经缓存的最大索引, 即文件中保存的最大索引,-1没有数据
       */
      public int getCacheMaxIndex ( ) {

            return mStartIndex + mLoadMoreCount.get() - 1;
      }

      /**
       * @return 获取程序运行期间已经缓存的最小索引, 即文件中保存的最小索引,-1没有数据
       */
      public int getCacheMinIndex ( ) {

            return mStartIndex - mLoadLessCount.get();
      }

      /**
       * @return 读取已经缓存数量
       */
      public int getCacheCount ( ) {

            return mCacheCount.get();
      }

      /**
       * 清空缓存文件夹,每次退出app时或者进入app时推荐清空缓存文件
       *
       * @throws IOException 异常
       */
      public void clearFileCache ( ) throws IOException {

            mFileContainer.clear();
      }

      // ========================= 辅助内部类 =========================

      /**
       * 辅助保存json为文件
       */
      private class JsonFileConverter extends BaseFileConverter<V> {

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
}
