package tech.threekilogram.depository.json;

import android.support.v4.util.ArrayMap;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import okhttp3.ResponseBody;
import tech.threekilogram.depository.file.BaseFileContainer;
import tech.threekilogram.depository.file.BaseFileConverter;
import tech.threekilogram.depository.file.loader.DiskLruContainer;
import tech.threekilogram.depository.file.loader.FileContainer;
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
      protected BaseFileContainer<V>    mFileContainer;
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
      protected final     int     mStartIndex;
      /**
       * 已缓存数量
       */
      protected transient int     mCacheCount;
      /**
       * 已缓存更多数据量
       */
      protected transient int     mLoadMoreCount;
      /**
       * 已缓存更少数据量
       */
      protected transient int     mLoadLessCount;
      /**
       * 标识在已经存在文件缓存的情况下如何保存文件缓存
       */
      private             boolean isOverWrite;
      /**
       * 是否正在加载
       */
      protected AtomicBoolean isLoading            = new AtomicBoolean();
      /**
       * 内存最大保存数量
       */
      protected int           mMemoryMaxCount      = 128;
      /**
       * {@link #cacheMemory(int, int, boolean)}每次调用时缓存多少数据
       */
      protected int           mCacheCountEveryTime = mMemoryMaxCount >> 2;
      /**
       * true时,正在缓存文件,不需要再次启动缓存
       */
      private   AtomicBoolean isCachingFile        = new AtomicBoolean();

      /**
       * 临时保存需要需要写入文件的bean
       */
      private ArrayMap<Integer, V> mCacheFile = new ArrayMap<>();

      /**
       * @param jsonConverter 转换流为bean对象
       */
      public JsonLoader ( JsonConverter<V> jsonConverter ) {

            this( jsonConverter, 0 );
      }

      /**
       * @param jsonConverter 转换流为bean对象
       */
      public JsonLoader ( JsonConverter<V> jsonConverter, int startIndex ) {

            mJsonConverter = jsonConverter;

            mMemoryList = new MemoryList<>();

            mRetrofitLoader = new RetrofitLoader<>(
                new JsonRetrofitListConverter()
            );

            mStartIndex = startIndex;
      }

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

            mFileContainer = new FileContainer<>(
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

            mFileContainer = new DiskLruContainer<>(
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

            mMemoryMaxCount = memoryMaxCount;
            mCacheCountEveryTime = memoryMaxCount >> 2;
      }

      /**
       * 加载数据
       *
       * @param url url
       * @param startIndex 加载后数据,将会使用该索引开始保存所有数据
       */
      public void load ( String url, int startIndex ) {

            int memoryMinIndex = getMemoryMinIndex();
            int memoryMaxIndex = getMemoryMaxIndex();

            isLoading.set( true );

            List<V> load = mRetrofitLoader.load( url );
            mMemoryList.saveMore( startIndex, load );
            int size = load.size();
            mCacheCount += size;
            mLoadMoreCount += size;

            isLoading.set( false );

            /* 根据需要缓存bean对象 */

            if( startIndex < memoryMinIndex ) {

                  /* 一个阈值,加载到该阈值就需要缓存文件了 */
                  final int offset = 20;

                  if( mMemoryList.size() >= mMemoryMaxCount - offset ) {

                        int maxIndex = getMemoryMaxIndex();
                        cacheMemory( maxIndex - mCacheCountEveryTime, maxIndex + 1, true );
                  }
            }

            int endIndex = startIndex + size;
            if( endIndex > memoryMaxIndex ) {

                  /* 一个阈值,加载到该阈值就需要缓存文件了 */
                  final int offset = 20;

                  if( mMemoryList.size() >= mMemoryMaxCount - offset ) {

                        int minIndex = getMemoryMinIndex();
                        cacheMemory( minIndex, minIndex + mCacheCountEveryTime + 1, false );
                  }
            }
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

                  mMemoryList.saveMore( getMemoryMaxIndex() + 1, load );
            }
            mCacheCount += size;
            mLoadMoreCount += size;

            isLoading.set( false );

            /* 一个阈值,加载到该阈值就需要缓存文件了 */
            final int offset = 20;

            if( mMemoryList.size() >= mMemoryMaxCount - offset ) {

                  int minIndex = getMemoryMinIndex();
                  cacheMemory( minIndex, minIndex + mCacheCountEveryTime + 1, false );
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

                  mMemoryList.saveLess( getMemoryMinIndex() - 1, load );
            }
            mCacheCount += size;
            mLoadLessCount += size;

            isLoading.set( false );

            /* 一个阈值,加载到该阈值就需要缓存文件了 */
            final int offset = 20;

            if( mMemoryList.size() >= mMemoryMaxCount - offset ) {

                  int maxIndex = getMemoryMaxIndex();
                  cacheMemory( maxIndex - mCacheCountEveryTime, maxIndex + 1, true );
            }
      }

      /**
       * @return true : 正在加载数据中,该方法用于判断是否正在加载中,通过判断该值可以防止多次加载同一数据
       */
      public boolean isLoading ( ) {

            return isLoading.get();
      }

      /**
       * 获取缓存的数据,如果没有数据需要{@link #loadMore(String)}或者{@link #loadLess(String)}加载数据
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
       * @return 当前内存中数据量
       */
      public int getMemorySize ( ) {

            return mMemoryList.size();
      }

      /**
       * @return 读取已经缓存最大的索引,-1没有数据
       */
      public int getMemoryMaxIndex ( ) {

            try {

                  ArrayMap<Integer, V> container = mMemoryList.container();
                  int size = container.size();
                  return container.keyAt( size - 1 );
            } catch(Exception e) {

                  e.printStackTrace();
                  return -1;
            }
      }

      /**
       * @return 读取已经缓存最小的索引,-1没有数据
       */
      public int getMemoryMinIndex ( ) {

            try {

                  ArrayMap<Integer, V> container = mMemoryList.container();
                  return container.keyAt( 0 );
            } catch(Exception e) {

                  e.printStackTrace();
                  return -1;
            }
      }

      /**
       * @param overWrite true:如果本地存在该缓存将会重写缓存,false:不会更新缓存直接使用旧的
       */
      public void setOverWrite ( boolean overWrite ) {

            isOverWrite = overWrite;
      }

      /**
       * 从内存中移除指定区间的数据,并缓存到文件
       *
       * @param low low limit
       * @param high high limit
       */
      private void cacheMemory ( int low, int high, boolean fromStart ) {

            try {
                  ArrayMap<Integer, V> container = mMemoryList.container();
                  ArrayMap<Integer, V> cacheFile = mCacheFile;

                  if( fromStart ) {

                        int size = container.size();
                        Integer key = container.keyAt( size - 1 );
                        while( key >= low && key < high ) {

                              V v = container.removeAt( size - 1 );
                              cacheFile.put( key, v );

                              size = container.size();
                              key = container.keyAt( size - 1 );
                        }
                  } else {

                        Integer key = container.keyAt( 0 );
                        while( key >= low && key < high ) {

                              V v = container.removeAt( 0 );
                              cacheFile.put( key, v );

                              key = container.keyAt( 0 );
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

            if( isCachingFile.get() ) {

                  return;
            }
            isCachingFile.set( true );

            ArrayMap<Integer, V> cacheFile = mCacheFile;
            BaseFileContainer<V> fileContainer = mFileContainer;

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
       * @return 获取程序运行期间已经缓存的最大索引, 即文件中保存的最大索引
       */
      public int getCacheMaxIndex ( ) {

            return mStartIndex + mLoadMoreCount - 1;
      }

      /**
       * @return 获取程序运行期间已经缓存的最小索引, 即文件中保存的最小索引
       */
      public int getCacheMinIndex ( ) {

            return mStartIndex - mLoadLessCount;
      }

      /**
       * @return 读取已经缓存数量
       */
      public int getCacheCount ( ) {

            return mCacheCount;
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
