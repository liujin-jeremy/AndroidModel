package tech.threekilogram.depository.file.loader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import tech.threekilogram.depository.file.BaseFileContainer;
import tech.threekilogram.depository.file.FileConverter;
import tech.threekilogram.depository.function.Close;

/**
 * 从本地文件系统中读取缓存对象,需要一个{@link FileConverter}来辅助将{@link java.io.File}转换为{@link V}
 *
 * @param <V> value 的类型
 *
 * @author liujin
 */
public class FileContainer<V> extends BaseFileContainer<V> {

      /**
       * 一个文件夹用于统一保存key对应的文件
       */
      private File             mDir;
      /**
       * 辅助loader正常工作
       */
      private FileConverter<V> mConverter;

      /**
       * @param dir 保存文件的文件夹
       * @param converter 用于转换{@link java.io.File}为{@link V}类型的实例
       */
      public FileContainer ( File dir, FileConverter<V> converter ) {

            mDir = dir;
            mConverter = converter;
      }

      @Override
      public V save ( String key, V value ) {

            /* get file to this key */

            V result = null;

            /* to decide how to write a value to file */

            if( mSaveStrategy == SAVE_STRATEGY_RETURN_OLD ) {

                  result = load( key );

                  if( result != null ) {
                        boolean delete = getFile( key ).delete();
                  }
            }

            /* save value to file */

            FileOutputStream stream = null;

            try {

                  File file = getFile( key );
                  stream = new FileOutputStream( file );
                  mConverter.saveValue( key, stream, value );
            } catch(IOException e) {

                  /* maybe can't save */

                  e.printStackTrace();

                  if( mExceptionHandler != null ) {
                        mExceptionHandler.onSaveValueToFile( e, key, value );
                  }
            } finally {

                  Close.close( stream );
            }

            /* return old value if should return */

            return result;
      }

      @Override
      public V remove ( String key ) {

            V result = null;

            if( mSaveStrategy == SAVE_STRATEGY_RETURN_OLD ) {

                  result = load( key );
            }

            boolean delete = getFile( key ).delete();
            return result;
      }

      @Override
      public boolean containsOf ( String key ) {

            return getFile( key ).exists();
      }

      @Override
      public V load ( String key ) {

            File file = getFile( key );
            V result = null;

            if( file.exists() ) {

                  FileInputStream stream = null;

                  try {
                        /* convert the file to value */

                        stream = new FileInputStream( file );
                        result = mConverter.toValue( key, stream );
                  } catch(Exception e) {

                        /* maybe can't convert */

                        e.printStackTrace();

                        if( mExceptionHandler != null ) {
                              mExceptionHandler.onConvertToValue( e, key );
                        }
                  } finally {

                        Close.close( stream );
                  }
            }

            return result;
      }

      /**
       * @param key key
       *
       * @return file to this key, file may not exist
       */
      @Override
      public File getFile ( String key ) {

            String name = mConverter.fileName( key );
            return new File( mDir, name );
      }

      public File getDir ( ) {

            return mDir;
      }
}
