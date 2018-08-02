package tech.threekilogram.depository.file.common;

import java.io.File;
import java.io.IOException;
import tech.threekilogram.depository.file.BaseFileLoadSupport;
import tech.threekilogram.depository.file.FileLoadSupport;
import tech.threekilogram.depository.file.ValueFileConverter;

/**
 * 从本地文件系统中读取缓存对象,需要一个{@link ValueFileConverter}来辅助将{@link File}转换为{@link V}
 *
 *
 *
 * @param <K> key 的类型
 * @param <V> value 的类型
 *
 * @author liujin
 */
public class FileLoader<K, V> extends BaseFileLoadSupport<K, V> {

      /**
       * 辅助loader正常工作
       */
      private ValueFileConverter<K, V, File> mConverter;

      /**
       * 一个文件夹用于统一保存key对应的文件
       */
      private File mDir;

      /**
       * @param dir 保存文件的文件夹
       * @param converter 用于转换{@link File}为{@link V}类型的实例
       */
      public FileLoader (File dir, ValueFileConverter<K, V, File> converter) {

            mDir = dir;
            mConverter = converter;
      }

      @Override
      public V save (K key, V value) {

            /* get file to this key */

            String name = mConverter.fileName(key);
            File file = new File(mDir, name);
            V result = null;

            /* to decide how to write a value to file */

            if(file.exists() && mSaveStrategy == FileLoadSupport.SAVE_STRATEGY_COVER) {

                  /* if should return old value ; get it */

                  try {
                        result = mConverter.toValue(file);
                  } catch(Exception e) {

                        /* maybe can't convert */

                        if(mExceptionHandler != null) {
                              mExceptionHandler.onConvertToValue(e, key);
                        }
                  }
            }

            /* save value to file */

            try {
                  mConverter.saveValue(file, value);
            } catch(IOException e) {

                  /* maybe can't save */

                  if(mExceptionHandler != null) {
                        mExceptionHandler.onSaveValueToFile(e, key, value);
                  }
            }

            /* return old value if should return */

            return result;
      }

      @Override
      public V remove (K key) {

            String name = mConverter.fileName(key);
            File file = new File(mDir, name);
            V result = null;

            if(file.exists()) {

                  if(mSaveStrategy == FileLoadSupport.SAVE_STRATEGY_COVER) {

                        /* should return old value,get old value */

                        try {
                              result = mConverter.toValue(file);
                        } catch(Exception e) {

                              /* maybe can't convert */

                              if(mExceptionHandler != null) {
                                    mExceptionHandler.onConvertToValue(e, key);
                              }
                        }
                  }

                  /* delete file */

                  boolean delete = file.delete();
            }

            return result;
      }

      @Override
      public V load (K key) {

            String name = mConverter.fileName(key);
            File file = new File(mDir, name);
            V result = null;

            if(file.exists()) {

                  try {
                        /* convert the file to value */

                        result = mConverter.toValue(file);
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

            String name = mConverter.fileName(key);
            File file = new File(mDir, name);

            return file.exists();
      }
}
