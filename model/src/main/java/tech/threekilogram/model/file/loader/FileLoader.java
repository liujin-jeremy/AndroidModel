package tech.threekilogram.model.file.loader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import tech.threekilogram.model.converter.StreamConverter;
import tech.threekilogram.model.file.BaseFileLoader;
import tech.threekilogram.model.util.io.Close;
import tech.threekilogram.model.util.io.FileCache;
import tech.threekilogram.model.util.io.FileClear;

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
      public FileLoader ( File dir ) {

            mDir = dir;
      }

      @Override
      public void save ( String key, V value, StreamConverter<V> converter ) {

            /* save value to file */
            FileOutputStream stream = null;

            try {

                  File file = getFile( key );
                  stream = new FileOutputStream( file );
                  converter.to( stream, value );
            } catch(IOException e) {

                  /* maybe can't save */

                  e.printStackTrace();

                  if( mOnErrorListener != null ) {
                        mOnErrorListener.onSaveToFile( e, key, value );
                  }
            } finally {

                  Close.close( stream );
            }
      }

      @Override
      public void remove ( String key ) {

            boolean delete = getFile( key ).delete();
      }

      @Override
      public V load ( String s, StreamConverter<V> converter ) {

            File file = getFile( s );
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
                              mOnErrorListener.onLoadFromFile( e, s );
                        }
                  } finally {

                        Close.close( stream );
                  }
            }

            return result;
      }

      @Override
      public boolean containsOf ( String key ) {

            return getFile( key ).exists();
      }

      /**
       * @param key key
       *
       * @return file to this key, file may not exist
       */
      @Override
      public File getFile ( String key ) {

            File file = mFileLoader.get( key );

            if( file == null ) {

                  String name = encodeKey( key );
                  file = new File( mDir, name );
                  mFileLoader.put( key, file );
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
