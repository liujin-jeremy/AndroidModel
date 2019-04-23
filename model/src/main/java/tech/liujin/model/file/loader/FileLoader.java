package tech.liujin.model.file.loader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import tech.liujin.model.converter.StreamConverter;
import tech.liujin.model.file.BaseFileLoader;
import tech.liujin.model.util.io.Close;
import tech.liujin.model.util.io.FileCache;
import tech.liujin.model.util.io.FileClear;

/**
 * 从本地文件系统中读取缓存对象,需要一个{@link StreamConverter}来辅助将{@link
 * java.io.File}转换为{@link V}
 *
 * @param <V> value 的类型
 *
 * @author liujin
 */
public class FileLoader<V> extends BaseFileLoader<V> {

      /**
       * 一个文件夹用于统一保存key对应的文件
       */
      private File      mDir;
      /**
       * 缓存创建的file
       */
      private FileCache mFileLoader = new FileCache();

      /**
       * @param dir 保存文件的文件夹
       */
      public FileLoader ( File dir, StreamConverter<V> converter ) {

            super( converter );
            mDir = dir;
      }

      @Override
      public void save ( String url, V value ) {

            save( url, value, mConverter );
      }

      @Override
      public void save (
          String url, V value, StreamConverter<V> converter ) {

            /* save value to file */
            FileOutputStream stream = null;

            try {

                  File file = getFile( url );
                  stream = new FileOutputStream( file );
                  converter.to( stream, value );
            } catch(IOException e) {

                  /* maybe can't save */

                  e.printStackTrace();

                  if( mOnErrorListener != null ) {
                        mOnErrorListener.onSaveToFile( e, url, value );
                  }
            } finally {

                  Close.close( stream );
            }
      }

      @Override
      public void remove ( String url ) {

            boolean delete = getFile( url ).delete();
      }

      @Override
      public V load ( String url ) {

            return load( url, mConverter );
      }

      @Override
      public V load ( String url, StreamConverter<V> converter ) {

            File file = getFile( url );
            V result = null;

            if( file.exists() ) {

                  FileInputStream stream = null;

                  try {
                        /* convert the file to value */

                        stream = new FileInputStream( file );
                        result = converter.from( stream );
                  } catch(Exception e) {

                        /* maybe can't convert */

                        e.printStackTrace();

                        if( mOnErrorListener != null ) {
                              mOnErrorListener.onLoadFromFile( e, url );
                        }
                  } finally {

                        Close.close( stream );
                  }
            }

            return result;
      }

      @Override
      public boolean containsOf ( String url ) {

            return getFile( url ).exists();
      }

      /**
       * @param url key
       *
       * @return file to this key, file may not exist
       */
      @Override
      public File getFile ( String url ) {

            File file = mFileLoader.get( url );

            if( file == null ) {

                  String name = encodeKey( url );
                  file = new File( mDir, name );
                  mFileLoader.put( url, file );
                  return file;
            }

            return file;
      }

      @Override
      public void clear ( ) {

            FileClear.clearFile( mDir );
      }

      public File getDir ( ) {

            return mDir;
      }
}
