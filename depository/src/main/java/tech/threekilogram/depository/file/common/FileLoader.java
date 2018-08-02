package tech.threekilogram.depository.file.common;

import java.io.File;
import java.io.IOException;
import tech.threekilogram.depository.file.BaseFileLoadSupport;
import tech.threekilogram.depository.file.FileLoadSupport;
import tech.threekilogram.depository.file.ValueFileConverter;

/**
 * to get a value from a file defined by key
 *
 * @param <K> key to get file
 * @param <V> value get from file
 *
 * @author liujin
 */
public class FileLoader<K, V> extends BaseFileLoadSupport<K, V> {

      /**
       * 辅助loader正常工作
       */
      private ValueFileConverter<K, V, File> mFileValueConverter;

      /**
       * 一个文件夹用于统一保存key对应的文件
       */
      private File mDir;

      public FileLoader (File dir, ValueFileConverter<K, V, File> fileValueConverter) {

            mDir = dir;
            mFileValueConverter = fileValueConverter;
      }

      @Override
      public V save (K key, V value) {

            String name = mFileValueConverter.fileName(key);
            File file = new File(mDir, name);
            V result = null;

            if(file.exists() && mSaveStrategy == FileLoadSupport.SAVE_STRATEGY_COVER) {

                  try {
                        result = mFileValueConverter.toValue(file);
                  } catch(Exception e) {

                        /* maybe can't convert */

                        if(mExceptionHandler != null) {
                              mExceptionHandler.onConvertToValue(e, key);
                        }
                  }
            }

            try {
                  mFileValueConverter.saveValue(file, value);
            } catch(IOException e) {

                  if(mExceptionHandler != null) {
                        mExceptionHandler.onSaveValueToFile(e, key, value);
                  }
            }
            return result;
      }

      @Override
      public V remove (K key) {

            String name = mFileValueConverter.fileName(key);
            File file = new File(mDir, name);
            V result = null;

            if(file.exists()) {

                  if(mSaveStrategy == FileLoadSupport.SAVE_STRATEGY_COVER) {

                        try {
                              result = mFileValueConverter.toValue(file);
                        } catch(Exception e) {

                              /* maybe can't convert */

                              if(mExceptionHandler != null) {
                                    mExceptionHandler.onConvertToValue(e, key);
                              }
                        }
                  }

                  boolean delete = file.delete();
            }

            return result;
      }

      @Override
      public V load (K key) {

            String name = mFileValueConverter.fileName(key);
            File file = new File(mDir, name);
            V result = null;

            if(file.exists()) {

                  try {
                        result = mFileValueConverter.toValue(file);
                  } catch(Exception e) {

                        /* maybe can't convert */

                        if(mExceptionHandler != null) {
                              mExceptionHandler.onConvertToValue(e, key);
                        }
                  }
            }

            return result;
      }

      @Override
      public boolean containsOf (K key) {

            String name = mFileValueConverter.fileName(key);
            File file = new File(mDir, name);

            return file.exists();
      }
}
