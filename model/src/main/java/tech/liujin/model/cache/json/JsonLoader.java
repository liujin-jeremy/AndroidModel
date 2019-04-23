package tech.liujin.model.cache.json;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import tech.threekilogram.model.cache.CacheLoader;
import tech.threekilogram.model.converter.GsonConverter;
import tech.threekilogram.model.memory.Memory;
import tech.threekilogram.model.memory.lru.MemoryLruCache;
import tech.threekilogram.model.memory.map.MemoryMap;
import tech.threekilogram.model.net.DownLoader;
import tech.threekilogram.model.util.io.FileClear;

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
      protected Memory<String, V> mMemoryContainer;
      /**
       * 辅助将流转换为json bean
       */
      protected GsonConverter<V>  mGsonConverter;
      /**
       * 下载json文件
       */
      protected DownLoader        mDownLoader;

      /**
       * 创建一个json三级缓存,指定了内存中数据量(超过该数据量,根据{@link android.support.v4.util.LruCache})规则从内存中删除多余条目,
       * 指定了缓存文件夹及其大小,超多文件夹大小根据规则{@link com.jakewharton.disklrucache.DiskLruCache}删除多余文件
       *
       * @param memoryItemCount 内存中最大数据量,如果为负数那么将不会从内存中删除数据
       * @param dir 缓存文件夹
       * @param jsonType json bean类,会将数据流转换为指定类型的对象
       */
      public JsonLoader (
          int memoryItemCount,
          File dir,
          Class<V> jsonType ) {

            if( memoryItemCount > 0 ) {
                  mMemoryContainer = new MemoryLruCache<>( memoryItemCount );
            } else {
                  mMemoryContainer = new MemoryMap<>();
            }
            mDownLoader = new DownLoader( dir );
            mGsonConverter = new GsonConverter<>( jsonType );
      }

      /**
       * 创建一个json三级缓存,指定了内存中数据量(超过该数据量,根据{@link android.support.v4.util.LruCache})规则从内存中删除多余条目,
       * 指定了缓存文件夹及其大小,超多文件夹大小根据规则{@link com.jakewharton.disklrucache.DiskLruCache}删除多余文件
       *
       * @param dir 缓存文件夹
       * @param jsonType json bean类,会将数据流转换为指定类型的对象
       */
      public JsonLoader (
          File dir,
          Class<V> jsonType ) {

            this( -1, dir, jsonType );
      }

      private InputStream getFileInputStream ( File file ) {

            try {
                  return new FileInputStream( file );
            } catch(FileNotFoundException e) {
                  e.printStackTrace();
            }
            return null;
      }

      private OutputStream getFileOutputStream ( File file ) {

            try {
                  return new FileOutputStream( file );
            } catch(FileNotFoundException e) {
                  e.printStackTrace();
            }
            return null;
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

            File file = mDownLoader.down( url );

            if( file != null && file.exists() ) {
                  InputStream fileInputStream = getFileInputStream( file );
                  V v = mGsonConverter.from( fileInputStream );
                  if( v != null ) {
                        saveToMemory( url, v );
                  }
                  return v;
            }

            return null;
      }

      @Override
      public File download ( String url ) {

            File file = getFile( url );
            if( file != null && file.exists() ) {
                  return file;
            }
            mDownLoader.down( url );
            return file;
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

            File file = getFile( url );
            return file != null && file.exists();
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

            File file = getFile( url );
            if( file.exists() ) {
                  return;
            }

            OutputStream outputStream = getFileOutputStream( file );
            mGsonConverter.to( outputStream, v );
      }

      @Override
      public File getFile ( String url ) {

            return mDownLoader.getFile( url );
      }

      public File getDir ( ) {

            return mDownLoader.getDir();
      }

      /**
       * 保存一个json对象到本地文件,如果本地已经有缓存那么直接覆盖它
       *
       * @param url url
       * @param v value
       */
      public void saveToFileForce ( String url, V v ) {

            OutputStream outputStream = getFileOutputStream( getFile( url ) );
            mGsonConverter.to( outputStream, v );
      }

      /**
       * 删除该key对应的缓存文件
       *
       * @param url url
       */
      @Override
      public void removeFromFile ( String url ) {

            File file = getFile( url );
            if( file != null && file.exists() ) {
                  boolean delete = file.delete();
            }
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

            File file = getFile( url );
            if( file != null && file.exists() ) {

                  InputStream inputStream = getFileInputStream( file );
                  V v = mGsonConverter.from( inputStream );
                  if( v != null ) {
                        saveToMemory( url, v );
                  }
                  return v;
            }
            return null;
      }

      /**
       * 清除所有文件
       */
      @Override
      public void clearFile ( ) {

            FileClear.clearFile( mDownLoader.getDir() );
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
}
