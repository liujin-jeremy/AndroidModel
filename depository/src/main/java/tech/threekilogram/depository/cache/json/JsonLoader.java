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
import tech.threekilogram.depository.memory.Memory;
import tech.threekilogram.depository.memory.lru.MemoryLruCache;
import tech.threekilogram.depository.memory.map.MemoryMap;
import tech.threekilogram.depository.net.retrofit.converter.ResponseBodyConverter;
import tech.threekilogram.depository.net.retrofit.down.Downer;
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
      protected Memory<String, V>       mMemoryContainer;
      /**
       * 文件
       */
      @Nullable
      protected BaseFileLoader<V>       mFileContainer;
      /**
       * 网络bean
       */
      protected RetrofitLoader<V>       mRetrofitLoader;
      /**
       * 辅助将流转换为json bean
       */
      protected JsonConverter<V>        mJsonConverter;
      /**
       * 网络list
       */
      protected RetrofitLoader<List<V>> mRetrofitListLoader;
      /**
       * 临时保存需要需要写入文件的bean
       */
      protected final ArrayMap<String, V> mWillCacheToFile = new ArrayMap<>();
      /**
       * true : 正在缓存文件
       */
      protected final AtomicBoolean       mIsCacheToFile   = new AtomicBoolean();

      /**
       * 创建一个json三级缓存,指定了内存中数据量(超过该数据量,根据{@link android.support.v4.util.LruCache})规则从内存中删除多余条目,
       * 指定了缓存文件夹及其大小,超多文件夹大小根据规则{@link com.jakewharton.disklrucache.DiskLruCache}删除多余文件
       *
       * @param memoryItemCount 内存中最大数据量,如果为负数那么将不会从内存中删除数据
       * @param dir 缓存文件夹
       * @param maxFileSize 缓存文件夹最大大小
       * @param jsonConverter 辅助将数据流转换为json对象
       *
       * @throws IOException 创建{@link com.jakewharton.disklrucache.DiskLruCache}时异常
       */
      public JsonLoader (
          int memoryItemCount,
          File dir,
          long maxFileSize,
          JsonConverter<V> jsonConverter ) throws IOException {

            mJsonConverter = jsonConverter;
            if( memoryItemCount > 0 ) {
                  mMemoryContainer = new MemoryLruCache<>( memoryItemCount );
            } else {
                  mMemoryContainer = new MemoryMap<>();
            }
            mFileContainer = new DiskLruLoader<>( dir, maxFileSize, new JsonFileConverter() );
            mRetrofitLoader = new RetrofitLoader<>( new JsonRetrofitConverter() );
      }

      /**
       * 创建一个json三级缓存,指定了内存中数据量(超过该数据量,根据{@link android.support.v4.util.LruCache})规则从内存中删除多余条目,
       * 指定了缓存文件夹及其大小,超多文件夹大小根据规则{@link com.jakewharton.disklrucache.DiskLruCache}删除多余文件
       *
       * @param memoryItemCount 内存中最大数据量,如果为负数那么将不会从内存中删除数据
       * @param dir 缓存文件夹
       * @param maxFileSize 缓存文件夹最大大小
       * @param jsonType json bean类,会将数据流转换为指定类型的对象
       *
       * @throws IOException 创建{@link com.jakewharton.disklrucache.DiskLruCache}时异常
       */
      public JsonLoader (
          int memoryItemCount,
          File dir,
          long maxFileSize,
          Class<V> jsonType ) throws IOException {

            this( memoryItemCount, dir, maxFileSize, new GsonConverter<V>( jsonType ) );
      }

      /**
       * 创建一个json三级缓存,指定了内存中数据量(超过该数据量,根据{@link android.support.v4.util.LruCache})规则从内存中删除多余条目,
       * 可以选择指定缓存文件夹,用来支持本地文件缓存操作
       *
       * @param memoryItemCount 内存中最大数据量,如果为负数那么将不会从内存中删除数据
       * @param dir 缓存文件夹,如果为null将不支持本地文件缓存
       * @param jsonConverter 辅助将数据流转换为json对象
       */
      public JsonLoader (
          int memoryItemCount,
          @Nullable File dir,
          JsonConverter<V> jsonConverter ) {

            mJsonConverter = jsonConverter;
            if( memoryItemCount > 0 ) {
                  mMemoryContainer = new MemoryLruCache<>( memoryItemCount );
            } else {
                  mMemoryContainer = new MemoryMap<>();
            }
            if( dir != null ) {
                  mFileContainer = new FileLoader<>( dir, new JsonFileConverter() );
            }
            mRetrofitLoader = new RetrofitLoader<>( new JsonRetrofitConverter() );
      }

      /**
       * 创建一个json三级缓存,指定了内存中数据量(超过该数据量,根据{@link android.support.v4.util.LruCache})规则从内存中删除多余条目,
       * 可以选择指定缓存文件夹,用来支持本地文件缓存操作
       *
       * @param memoryItemCount 内存中最大数据量,如果为负数那么将不会从内存中删除数据
       * @param dir 缓存文件夹,如果为null将不支持本地文件缓存
       * @param jsonType json bean类,会将数据流转换为指定类型的对象
       */
      public JsonLoader (
          int memoryItemCount,
          @Nullable File dir,
          Class<V> jsonType ) {

            this( memoryItemCount, dir, new GsonConverter<V>( jsonType ) );
      }

      /**
       * 从网络加载一组数据,该方法需要实现{@link JsonConverter#fromArray(InputStream)},才能正常工作
       *
       * @param url url
       *
       * @return 一组数据
       */
      public List<V> loadListFromNet ( String url ) {

            if( mRetrofitListLoader == null ) {
                  mRetrofitListLoader = new RetrofitLoader<>(
                      new JsonRetrofitListConverter()
                  );
            }

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

      @Override
      public void download ( String url ) {

            File file = getFile( url );
            if( file != null && file.exists() ) {
                  return;
            }
            Downer.downloadTo( file, url );
      }

      @Override
      public V loadFromDownload ( String url ) {

            download( url );
            File file = getFile( url );
            if( file != null && file.exists() ) {

                  return loadFromFile( url );
            }
            return null;
      }

      /**
       * 保存数据到内存中
       *
       * @param url value url
       * @param v value
       */
      @Override
      public void saveToMemory ( String url, V v ) {

            mMemoryContainer.save( url, v );
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

            return mMemoryContainer.containsOf( url );
      }

      /**
       * 从内存中删除
       *
       * @return 值
       */
      @Override
      public V removeFromMemory ( String url ) {

            return mMemoryContainer.remove( url );
      }

      /**
       * 从内存中读取
       *
       * @return 该key对应的值 or null (if not in memory)
       */
      @Override
      public V loadFromMemory ( String url ) {

            return mMemoryContainer.load( url );
      }

      @Override
      public int memorySize ( ) {

            return mMemoryContainer.size();
      }

      /**
       * 清除所有内存中数据
       */
      @Override
      public void clearMemory ( ) {

            mMemoryContainer.clear();
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

      @Override
      public File getFile ( String url ) {

            if( mFileContainer == null ) {
                  return null;
            }
            return mFileContainer.getFile( url );
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

            V v = mMemoryContainer.remove( url );
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
