package tech.threekilogram.depository.cache.json;

import android.support.annotation.Nullable;
import android.support.v4.util.ArrayMap;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import okhttp3.ResponseBody;
import tech.threekilogram.depository.CacheLoader;
import tech.threekilogram.depository.file.BaseFileConverter;
import tech.threekilogram.depository.file.BaseFileLoader;
import tech.threekilogram.depository.file.loader.DiskLruLoader;
import tech.threekilogram.depository.file.loader.FileLoader;
import tech.threekilogram.depository.memory.map.MemoryMap;
import tech.threekilogram.depository.net.retrofit.converter.ResponseBodyConverter;
import tech.threekilogram.depository.net.retrofit.loader.RetrofitLoader;

/**
 * 该类提供json对象三级缓存
 *
 * @author liujin
 */
@SuppressWarnings("WeakerAccess")
public class JsonLoader<V> implements CacheLoader<V> {

      /**
       * 内存
       */
      protected MemoryMap<String, V>    mMemoryList;
      /**
       * 文件
       */
      @Nullable
      protected BaseFileLoader<V>       mFileContainer;
      /**
       * 网络list
       */
      protected RetrofitLoader<List<V>> mRetrofitListLoader;
      /**
       * 网络bean
       */
      protected RetrofitLoader<V>       mRetrofitLoader;
      /**
       * 辅助将流转换为json bean
       */
      protected JsonConverter<V>        mJsonConverter;
      /**
       * 临时保存需要需要写入文件的bean
       */
      protected final ArrayMap<String, V> mWillCacheToFile = new ArrayMap<>();
      /**
       * true : 正在缓存文件
       */
      protected final AtomicBoolean       mIsCacheToFile   = new AtomicBoolean();

      /**
       * 创建一个内存/网络缓存的loader
       *
       * @param type bean 类型
       */
      public JsonLoader ( Class<V> type ) {

            this( new GsonConverter<V>( type ) );
      }

      /**
       * 创建一个内存/网络缓存的loader
       *
       * @param jsonConverter 转换流为bean对象
       */
      public JsonLoader ( JsonConverter<V> jsonConverter ) {

            mMemoryList = new MemoryMap<>();

            mJsonConverter = jsonConverter;

            mRetrofitListLoader = new RetrofitLoader<>(
                new JsonRetrofitListConverter()
            );

            mRetrofitLoader = new RetrofitLoader<>(
                new JsonRetrofitConverter()
            );
      }

      /**
       * 创建一个内存/文件/网络缓存的loader
       *
       * @param dir 缓存文件夹
       * @param type json bean type
       */
      public JsonLoader ( File dir, Class<V> type ) {

            this( dir, new GsonConverter<V>( type ) );
      }

      /**
       * 创建一个内存/文件/网络缓存的loader
       *
       * @param dir 缓存文件夹
       * @param jsonConverter 转换流为bean对象
       */
      public JsonLoader ( File dir, JsonConverter<V> jsonConverter ) {

            this( jsonConverter );
            mFileContainer = new FileLoader<>(
                dir,
                new JsonFileConverter()
            );
      }

      /**
       * 创建一个内存/文件/网络缓存的loader
       *
       * @param dir 缓存文件夹
       * @param maxFileSize max file size
       * @param type bean type
       *
       * @throws IOException cont create
       */
      public JsonLoader ( File dir, long maxFileSize, Class<V> type )
          throws IOException {

            this( dir, maxFileSize, new GsonConverter<V>( type ) );
      }

      /**
       * 创建一个内存/文件/网络缓存的loader
       *
       * @param dir 缓存文件夹
       * @param jsonConverter 转换流为bean对象
       */
      public JsonLoader ( File dir, long maxFileSize, JsonConverter<V> jsonConverter )
          throws IOException {

            this( jsonConverter );
            mFileContainer = new DiskLruLoader<>(
                dir,
                maxFileSize,
                new JsonFileConverter()
            );
      }

      /**
       * 从网络加载一组数据,该方法需要实现{@link JsonConverter#fromArray(InputStream)},才能正常工作
       *
       * @param url url
       *
       * @return 一组数据
       */
      public List<V> loadListFromNet ( String url ) {

            return mRetrofitListLoader.load( url );
      }

      /**
       * 从网络加载一个数据,加载成功缓存到内存中
       *
       * @param url url
       *
       * @return 数据
       */
      @Override
      public V loadFromNet ( String url ) {

            V v = mRetrofitLoader.load( url );
            if( v != null ) {
                  saveToMemory( url, v );
            }
            return v;
      }

      /**
       * 从网络加载一个数据,如果加载成功那么缓存到内存/文件中
       *
       * @param url url
       *
       * @return 数据
       */
      public V loadFromNetAndCache ( String url ) {

            V v = mRetrofitLoader.load( url );
            if( v != null ) {
                  save( url, v );
            }
            return v;
      }

      /**
       * 保存数据到内存中
       *
       * @param url value url
       * @param v value
       */
      @Override
      public void saveToMemory ( String url, V v ) {

            mMemoryList.save( url, v );
      }

      /**
       * 测试该key对应的value是否存在与内存中
       *
       * @param url url
       *
       * @return true :存在于内存中
       */
      @Override
      public boolean containsOfMemory ( String url ) {

            return mMemoryList.containsOf( url );
      }

      /**
       * 从内存中删除
       *
       * @return 值
       */
      @Override
      public V removeFromMemory ( String url ) {

            return mMemoryList.remove( url );
      }

      /**
       * 从内存中读取
       *
       * @return 该key对应的值 or null (if not in memory)
       */
      @Override
      public V loadFromMemory ( String url ) {

            return mMemoryList.load( url );
      }

      @Override
      public int memorySize ( ) {

            return mMemoryList.size();
      }

      /**
       * 清除所有内存中数据
       */
      @Override
      public void clearMemory ( ) {

            mMemoryList.clear();
      }

      /**
       * 测试该key对应的value是否存在于本地文件中
       *
       * @param url url
       *
       * @return true:存在于本地文件中
       */
      @Override
      public boolean containsOfFile ( String url ) {

            return mFileContainer != null && mFileContainer.containsOf( url );
      }

      /**
       * 保存一个json对象到本地文件,如果本地已经有缓存那么不会覆盖它,如果需要覆盖它请使用{@link #removeFromFile(String)}先删除
       * 或者使用{@link #saveToFileForce(String, Object)}
       *
       * @param url url
       * @param v value
       */
      @Override
      public void saveToFile ( String url, V v ) {

            if( mFileContainer == null ) {
                  return;
            }
            if( mFileContainer.getFile( url ).exists() ) {
                  return;
            }
            /* 只有本地没有缓存时才保存到文件 */
            mFileContainer.save( url, v );
      }

      /**
       * 保存一个json对象到本地文件,如果本地已经有缓存那么直接覆盖它
       *
       * @param url url
       * @param v value
       */
      public void saveToFileForce ( String url, V v ) {

            if( mFileContainer == null ) {
                  return;
            }
            /* 强制保存到文件,无论是否有缓存文件 */
            mFileContainer.save( url, v );
      }

      /**
       * 删除该key对应的缓存文件
       *
       * @param url url
       */
      @Override
      public void removeFromFile ( String url ) {

            if( mFileContainer == null ) {
                  return;
            }
            mFileContainer.remove( url );
      }

      /**
       * 从本地文件加载json对象
       *
       * @param url url
       *
       * @return 该key对应json对象
       */
      @Override
      public V loadFromFile ( String url ) {

            if( mFileContainer == null ) {
                  return null;
            }
            V v = mFileContainer.load( url );
            if( v != null ) {
                  saveToMemory( url, v );
            }
            return v;
      }

      /**
       * 清除所有文件
       */
      @Override
      public void clearFile ( ) {

            if( mFileContainer == null ) {
                  return;
            }

            mFileContainer.clear();
      }

      @Override
      public boolean containsOf ( String url ) {

            return containsOfMemory( url ) || containsOfFile( url );
      }

      @Override
      public void save ( String url, V v ) {

            saveToMemory( url, v );
            saveToFile( url, v );
      }

      @Override
      public V load ( String url ) {

            V v = loadFromMemory( url );
            if( v != null ) {

                  return v;
            }

            return loadFromFile( url );
      }

      /**
       * 压缩内存,将指定数据缓存到本地缓存,同时在内存中删除,该方法线程安全
       *
       * @param url 需要缓存索引
       */
      public void trimMemory ( String url ) {

            V v = mMemoryList.remove( url );
            if( v != null ) {

                  synchronized(mWillCacheToFile) {
                        mWillCacheToFile.put( url, v );
                  }
                  notifyCacheFile();
            }
      }

      /**
       * 通知缓存文件到本地
       */
      private void notifyCacheFile ( ) {

            if( mIsCacheToFile.get() ) {
                  return;
            }

            mIsCacheToFile.set( true );
            ArrayMap<String, V> willCacheToFile = mWillCacheToFile;
            BaseFileLoader<V> fileContainer = mFileContainer;

            while( willCacheToFile.size() > 0 ) {

                  String key = null;
                  V v = null;

                  synchronized(mWillCacheToFile) {

                        key = willCacheToFile.keyAt( 0 );
                        v = willCacheToFile.removeAt( 0 );
                  }

                  if( fileContainer == null ) {
                        willCacheToFile.clear();
                        continue;
                  }

                  File file = fileContainer.getFile( key );
                  if( !file.exists() ) {
                        fileContainer.save( key, v );
                  }
            }

            mIsCacheToFile.set( false );
      }

      // ========================= 辅助转换 =========================

      /**
       * 辅助保存json为文件
       */
      private class JsonFileConverter extends BaseFileConverter<V> {

            @Override
            public String fileName ( String key ) {

                  return mNameConverter.encode( key );
            }

            @Override
            public V from ( InputStream stream ) {

                  return mJsonConverter.from( stream );
            }

            @Override
            public void to ( OutputStream outputStream, V value ) {

                  mJsonConverter.to( outputStream, value );
            }
      }

      /**
       * 辅助将网络资源流转为json bean list
       */
      private class JsonRetrofitListConverter implements ResponseBodyConverter<List<V>> {

            @Override
            public List<V> onExecuteSuccess ( String url, ResponseBody response ) throws Exception {

                  return mJsonConverter.fromArray( response.byteStream() );
            }
      }

      /**
       * 辅助将网络资源流转为json bean
       */
      private class JsonRetrofitConverter implements ResponseBodyConverter<V> {

            @Override
            public V onExecuteSuccess ( String url, ResponseBody response ) throws Exception {

                  return mJsonConverter.from( response.byteStream() );
            }
      }
}
