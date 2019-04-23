package tech.liujin.model.file.loader;

import com.jakewharton.disklrucache.DiskLruCache;
import com.jakewharton.disklrucache.DiskLruCache.Editor;
import com.jakewharton.disklrucache.DiskLruCache.Snapshot;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import tech.liujin.model.converter.StreamConverter;
import tech.liujin.model.file.BaseFileLoader;
import tech.liujin.model.util.io.Close;
import tech.liujin.model.util.io.FileCache;

/**
 * 和{@link FileLoader}功能一致,但是底层使用{@link DiskLruCache}缓存数据到文件夹
 *
 * @param <V> value 类型
 *
 * @author liujin
 */
public class DiskLruLoader<V> extends BaseFileLoader<V> {

      /**
       * 保存数据
       */
      private DiskLruCache mDiskLruCache;
      /**
       * 文件夹
       */
      private File         mDir;
      /**
       * 缓存file
       */
      private FileCache    mFileLoader = new FileCache();

      /**
       * @param folder 缓存文件夹
       * @param maxSize 缓存文件最大大小
       *
       * @throws IOException 创建{@link DiskLruCache}时异常
       */
      public DiskLruLoader (
          File folder,
          long maxSize, StreamConverter<V> converter ) throws IOException {
            /* create DiskLruCache */
            super( converter );
            mDir = folder;
            mDiskLruCache = DiskLruCache.open( folder, 1, 1, maxSize );
      }

      @Override
      public void save ( String url, V value ) {

            save( url, value, mConverter );
      }

      @Override
      public void save (
          String url, V value, StreamConverter<V> converter ) {

            String name = encodeKey( url );

            try {
                  mDiskLruCache.remove( name );
            } catch(IOException e) {
                  e.printStackTrace();
            }

            Editor editor = null;

            try {
                  editor = mDiskLruCache.edit( name );
            } catch(IOException e) {
                  e.printStackTrace();
            }

            if( editor == null ) {
                  return;
            }

            OutputStream outputStream = null;

            try {
                  outputStream = editor.newOutputStream( 0 );
            } catch(IOException e) {
                  e.printStackTrace();
            }

            if( outputStream == null ) {
                  return;
            }

            try {
                  converter.to( outputStream, value );
                  Close.close( outputStream );
                  editor.commit();
            } catch(IOException e) {
                  e.printStackTrace();
                  abortEditor( editor );
                  if( mOnErrorListener != null ) {
                        mOnErrorListener.onSaveToFile( e, url, value );
                  }
            } finally {

                  Close.close( outputStream );
            }

            try {
                  mDiskLruCache.flush();
            } catch(IOException e) {
                  e.printStackTrace();
            }
      }

      @Override
      public void remove ( String url ) {

            try {
                  String fileName = encodeKey( url );
                  mDiskLruCache.remove( fileName );
            } catch(IOException e) {

                  e.printStackTrace();
            }
      }

      @Override
      public boolean containsOf ( String url ) {

            String name = encodeKey( url );

            try {

                  Snapshot snapshot = mDiskLruCache.get( name );
                  boolean result = snapshot != null;
                  if( result ) {
                        snapshot.close();
                  }
                  return result;
            } catch(IOException e) {

                  e.printStackTrace();
            }
            return false;
      }

      @Override
      public void clear ( ) {

            try {
                  mDiskLruCache.delete();
            } catch(IOException e) {
                  e.printStackTrace();
            }
      }

      @Override
      public V load ( String url ) {

            return load( url, mConverter );
      }

      @Override
      public V load ( String url, StreamConverter<V> converter ) {

            String stringKey = encodeKey( url );

            /* try to get snapShort */

            Snapshot snapshot = null;
            InputStream inputStream = null;
            try {

                  snapshot = mDiskLruCache.get( stringKey );
            } catch(IOException e) {

                  e.printStackTrace();
            }

            /* try to loadFromNet value from snapShot's stream */

            if( snapshot != null ) {

                  inputStream = snapshot.getInputStream( 0 );

                  try {

                        return converter.from( inputStream );
                  } catch(Exception e) {

                        e.printStackTrace();

                        if( mOnErrorListener != null ) {
                              mOnErrorListener.onLoadFromFile( e, url );
                        }
                  } finally {

                        Close.close( inputStream );
                        Close.close( snapshot );
                  }
            }

            return null;
      }

      private void abortEditor ( Editor edit ) {

            try {
                  if( edit != null ) {
                        edit.abort();
                  }
            } catch(IOException e) {

                  e.printStackTrace();
            }
      }

      @Override
      public File getFile ( String url ) {

            File file = mFileLoader.get( url );

            if( file == null ) {

                  String fileName = encodeKey( url );
                  file = new File( mDir, fileName + ".0" );
                  mFileLoader.put( url, file );
                  return file;
            }

            return file;
      }
}
