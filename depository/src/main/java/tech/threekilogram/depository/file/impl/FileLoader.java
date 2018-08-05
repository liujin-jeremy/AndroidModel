package tech.threekilogram.depository.file.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import tech.threekilogram.depository.file.BaseFileLoader;
import tech.threekilogram.depository.file.FileConverter;

/**
 * 从本地文件系统中读取缓存对象,需要一个{@link FileConverter}来辅助将{@link File}转换为{@link V}
 *
 * @param <K> key 的类型
 * @param <V> value 的类型
 *
 * @author liujin
 */
public class FileLoader<K, V> extends BaseFileLoader<K, V> {

      /**
       * 一个文件夹用于统一保存key对应的文件
       */
      private File                mDir;
      /**
       * 辅助loader正常工作
       */
      private FileConverter<K, V> mConverter;

      /**
       * @param dir 保存文件的文件夹
       * @param converter 用于转换{@link File}为{@link V}类型的实例
       */
      public FileLoader (File dir, FileConverter<K, V> converter) {

            mDir = dir;
            mConverter = converter;
      }

      public File getFile (K key) {

            String name = mConverter.fileName(key);
            return new File(mDir, name);
      }

      public File getDir () {

            return mDir;
      }

      @Override
      public V save (K key, V value) {

            /* get file to this key */

            V result = null;

            /* to decide how to write a value to file */

            if(mSaveStrategy == SAVE_STRATEGY_COVER) {

                  result = load(key);

                  if(result != null) {
                        boolean delete = getFile(key).delete();
                  }
            }

            /* save value to file */

            try {

                  File file = getFile(key);
                  FileOutputStream stream = new FileOutputStream(file);
                  mConverter.saveValue(key, stream, value);
            } catch(IOException e) {

                  /* maybe can't save */

                  e.printStackTrace();

                  if(mExceptionHandler != null) {
                        mExceptionHandler.onSaveValueToFile(e, key, value);
                  }
            }

            /* return old value if should return */

            return result;
      }

      @Override
      public V remove (K key) {

            V result = null;

            if(mSaveStrategy == SAVE_STRATEGY_COVER) {

                  result = load(key);
            }

            boolean delete = getFile(key).delete();
            return result;
      }

      @Override
      public V load (K key) {

            File file = getFile(key);
            V result = null;

            if(file.exists()) {

                  try {
                        /* convert the file to value */

                        FileInputStream stream = new FileInputStream(file);
                        result = mConverter.toValue(key, stream);
                  } catch(Exception e) {

                        /* maybe can't convert */

                        e.printStackTrace();

                        if(mExceptionHandler != null) {
                              mExceptionHandler.onConvertToValue(e, key);
                        }
                  }
            }

            return result;
      }

      @Override
      public boolean containsOf (K key) {

            return getFile(key).exists();
      }
}
