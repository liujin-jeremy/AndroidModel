package tech.threekilogram.depository.json;

import android.support.v4.util.ArrayMap;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import okhttp3.ResponseBody;
import tech.threekilogram.depository.file.BaseFileContainer;
import tech.threekilogram.depository.file.BaseFileConverter;
import tech.threekilogram.depository.file.loader.DiskLruContainer;
import tech.threekilogram.depository.file.loader.FileContainer;
import tech.threekilogram.depository.memory.map.MemoryList;
import tech.threekilogram.depository.net.retrofit.BaseRetrofitConverter;
import tech.threekilogram.depository.net.retrofit.loader.RetrofitLoader;

/**
 * @author: Liujin
 * @version: V1.0
 * @date: 2018-08-17
 * @time: 13:48
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
       * 是否正在加载
       */
      private transient   boolean isLoading;

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
       * @return 当前内存中数据量
       */
      public int getMemorySize ( ) {

            return mMemoryList.size();
      }

      /**
       * 清空缓存文件夹
       *
       * @throws IOException 异常
       */
      public void clearFile ( ) throws IOException {

            mFileContainer.clear();
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
       * 加载数据
       *
       * @param url url
       * @param startIndex 加载后数据,将会使用该索引开始保存所有数据
       */
      public void load ( String url, int startIndex ) {

            isLoading = true;

            List<V> load = mRetrofitLoader.load( url );
            mMemoryList.saveMore( startIndex, load );
            int size = load.size();
            mCacheCount += size;
            mLoadMoreCount += size;

            isLoading = false;
      }

      /**
       * 加载更多数据
       *
       * @param url url
       */
      public void loadMore ( String url ) {

            isLoading = true;

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

            isLoading = false;
      }

      /**
       * 加载更少数据
       *
       * @param url url
       */
      public void loadLess ( String url ) {

            isLoading = true;

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

            isLoading = false;
      }

      /**
       * 压缩内存中最大数据量不超过[low,high]
       *
       * @param low low limit
       * @param high high limit
       */
      public void trimMemory ( int low, int high ) {

            int size = mMemoryList.size();
            if( size <= Math.abs( high - low ) ) {
                  return;
            }

            ArrayMap<Integer, V> container = mMemoryList.container();

            /* 移除小于下限的值 */
            Integer key = container.keyAt( 0 );
            while( key < low ) {

                  V v = container.removeAt( 0 );
                  mFileContainer.save( key.toString(), v );
                  key = container.keyAt( 0 );
            }

            /* 移除大于上限的值 */
            key = container.keyAt( container.size() - 1 );
            while( key > high ) {

                  V v = container.removeAt( container.size() - 1 );
                  mFileContainer.save( key.toString(), v );
                  key = container.keyAt( container.size() - 1 );
            }
      }

      /**
       * @return true : 正在加载数据中,该方法用于判断是否正在加载中,通过判断该值可以防止多次加载同一数据
       */
      public boolean isLoading ( ) {

            return isLoading;
      }

      /**
       * 获取缓存的数据,如果没有数据需要{@link #loadMore(String)}或者{@link #loadLess(String)}加载数据
       *
       * @param index value index
       *
       * @return value at index
       */
      public V get ( int index ) {

            V load = mMemoryList.load( index );

            if( load == null ) {
                  load = mFileContainer.load( String.valueOf( index ) );
                  mMemoryList.save( index, load );
            }

            return load;
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
