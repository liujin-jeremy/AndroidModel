package tech.threekilogram.depository.json;

import android.support.annotation.Nullable;
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
import tech.threekilogram.depository.memory.map.MemoryMap;
import tech.threekilogram.depository.net.retrofit.BaseRetrofitConverter;
import tech.threekilogram.depository.net.retrofit.loader.RetrofitLoader;

/**
 * @author liujin
 */
@SuppressWarnings("WeakerAccess")
public class JsonLoader<V> {

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
       * 从网络加载一组数据
       *
       * @param url url
       *
       * @return 一组数据
       */
      public List<V> loadListFromNet ( String url ) {

            return mRetrofitListLoader.load( url );
      }

      /**
       * 从网络加载一个数据
       *
       * @param url url
       *
       * @return 数据
       */
      public V loadFromNet ( String url ) {

            return mRetrofitLoader.load( url );
      }

      /**
       * 保存数据到内存中
       *
       * @param key value key
       * @param v value
       */
      public void saveToMemory ( String key, V v ) {

            mMemoryList.save( key, v );
      }

      /**
       * 测试该key对应的value是否存在与内存中
       *
       * @param key key
       *
       * @return true :存在于内存中
       */
      public boolean containsOfMemory ( String key ) {

            return mMemoryList.containsOf( key );
      }

      /**
       * 从内存中删除
       *
       * @return 值
       */
      public V removeFromMemory ( String key ) {

            return mMemoryList.remove( key );
      }

      /**
       * 从内存中读取
       *
       * @return 该key对应的值 or null (if not in memory)
       */
      public V loadFromMemory ( String key ) {

            return mMemoryList.load( key );
      }

      /**
       * 返回内存中数据数量
       *
       * @return 数据量
       */
      public int memorySize ( ) {

            return mMemoryList.size();
      }

      /**
       * 清除所有内存中数据
       */
      public void clearMemory ( ) {

            mMemoryList.clear();
      }

      /**
       * 测试该key对应的value是否存在于本地文件中
       *
       * @param key key
       *
       * @return true:存在于本地文件中
       */
      public boolean containsOfFile ( String key ) {

            return mFileContainer != null && mFileContainer.containsOf( key );
      }

      /**
       * 保存一个json对象到本地文件,如果本地已经有缓存那么不会覆盖它,如果需要覆盖它请使用{@link #removeFromFile(String)}先删除
       * 或者使用{@link #saveToFileForce(String, Object)}
       *
       * @param key key
       * @param v value
       */
      public void saveToFile ( String key, V v ) {

            if( mFileContainer == null ) {
                  return;
            }
            if( mFileContainer.getFile( key ).exists() ) {
                  return;
            }
            /* 只有本地没有缓存时才保存到文件 */
            mFileContainer.save( key, v );
      }

      /**
       * 保存一个json对象到本地文件,如果本地已经有缓存那么直接覆盖它
       *
       * @param key key
       * @param v value
       */
      public void saveToFileForce ( String key, V v ) {

            if( mFileContainer == null ) {
                  return;
            }
            /* 强制保存到文件,无论是否有缓存文件 */
            mFileContainer.save( key, v );
      }

      /**
       * 删除该key对应的缓存文件
       *
       * @param key key
       */
      public void removeFromFile ( String key ) {

            if( mFileContainer == null ) {
                  return;
            }
            mFileContainer.remove( key );
      }

      /**
       * 从本地文件加载json对象
       *
       * @param key key
       *
       * @return 该key对应json对象
       */
      public V loadFromFile ( String key ) {

            if( mFileContainer == null ) {
                  return null;
            }
            return mFileContainer.load( key );
      }

      /**
       * 清除所有文件
       */
      public void clearFile ( ) {

            if( mFileContainer == null ) {
                  return;
            }

            try {
                  mFileContainer.clear();
            } catch(IOException e) {
                  e.printStackTrace();
            }
      }

      /**
       * 压缩内存,将指定数据缓存到本地缓存,同时在内存中删除,该方法线程安全
       *
       * @param key 需要缓存索引
       */
      public void trimMemory ( String key ) {

            V v = mMemoryList.remove( key );
            if( v != null ) {

                  synchronized(mWillCacheToFile) {
                        mWillCacheToFile.put( key, v );
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

      /**
       * 同时保存到内存和本地文件中,因为涉及到io操作最好异步执行,或者分别调用调用{@link #saveToMemory(String, Object)}和{@link
       * #saveToFile(String, Object)}
       *
       * @param key key
       * @param v value
       */
      public void save ( String key, V v ) {

            saveToMemory( key, v );
            saveToFile( key, v );
      }

      /**
       * 从内存和本地缓存文件中测试是否包含该key对应的数据缓存
       */
      public boolean containsOf ( String key ) {

            return containsOfMemory( key ) || containsOfFile( key );
      }

      /**
       * 同时从内存本地缓存删除该key对应的缓存
       *
       * @param key key
       */
      public void remove ( String key ) {

            removeFromMemory( key );
            removeFromFile( key );
      }

      /**
       * 尝试从内存本地文件读取缓存,如果均没有缓存返回null
       *
       * @param key key
       *
       * @return 该key对应的value or null
       */
      public V load ( String key ) {

            V v = loadFromMemory( key );
            if( v == null ) {
                  v = loadFromFile( key );
                  if( v != null ) {
                        saveToMemory( key, v );
                  }
            }
            return v;
      }

      // ========================= 辅助转换 =========================

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

                  return mJsonConverter.from( stream );
            }

            @Override
            public void saveValue ( String key, OutputStream outputStream, V value )
                throws IOException {

                  mJsonConverter.to( outputStream, value );
            }
      }

      /**
       * 辅助将网络资源流转为json bean list
       */
      private class JsonRetrofitListConverter extends BaseRetrofitConverter<List<V>> {

            @Override
            public List<V> onExecuteSuccess ( String key, ResponseBody response ) throws Exception {

                  return mJsonConverter.fromArray( response.byteStream() );
            }
      }

      /**
       * 辅助将网络资源流转为json bean
       */
      private class JsonRetrofitConverter extends BaseRetrofitConverter<V> {

            @Override
            public V onExecuteSuccess ( String key, ResponseBody response ) throws Exception {

                  return mJsonConverter.from( response.byteStream() );
            }
      }
}
