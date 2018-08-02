package tech.threekilogram.depository.file.impl;

import com.jakewharton.disklrucache.DiskLruCache;
import com.jakewharton.disklrucache.DiskLruCache.Editor;
import com.jakewharton.disklrucache.DiskLruCache.Snapshot;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import tech.threekilogram.depository.file.BaseFileLoadSupport;
import tech.threekilogram.depository.file.ValueFileConverter;
import tech.threekilogram.depository.function.CloseFunction;

/**
 * 底层使用{@link DiskLruCache}缓存数据到文件夹
 *
 * @param <K> kye 类型
 * @param <V> value 类型
 *
 * @author liujin
 */
public class DiskLruCacheLoader<K, V> extends BaseFileLoadSupport<K, V> {

      /**
       * 保存数据
       */
      private DiskLruCache mDiskLruCache;

      /**
       * 辅助该类完成stream到{@link V}的转换工作
       */
      private ValueFileConverter<K, V> mConverter;

      /**
       * @param folder which dir to save data
       * @param maxSize max data size
       * @param converter function to do
       */
      public DiskLruCacheLoader (
          File folder,
          long maxSize,
          ValueFileConverter<K, V> converter) {

            /* create DiskLruCache */

            try {
                  mDiskLruCache = DiskLruCache.open(folder, 1, 1, maxSize);
            } catch(IOException e) {
                  e.printStackTrace();
            }

            /* mDiskLruCache maybe null */

            mConverter = converter;
      }

      @Override
      public V save (K key, V value) {

            String name = mConverter.fileName(key);

            V result = null;

            /* try to read old value at this key if have a value */

            if(mSaveStrategy == SAVE_STRATEGY_RETURN_OLD) {

                  /* config should return old value to this key , so read old */

                  result = load(key);
            }

            /* try to get Editor */

            Editor editor = null;

            try {

                  editor = mDiskLruCache.edit(name);
            } catch(IOException e) {

                  e.printStackTrace();
            }

            if(editor == null) {
                  return null;
            }

            /* try to get output stream */

            OutputStream outputStream = null;

            try {
                  outputStream = editor.newOutputStream(0);
            } catch(IOException e) {
                  e.printStackTrace();
            }

            if(outputStream == null) {
                  return null;
            }

            /* try to save value */

            try {

                  mConverter.saveValue(key, outputStream, value);
                  CloseFunction.close(outputStream);
                  editor.commit();
            } catch(IOException e) {

                  /* save failed abort operator */

                  e.printStackTrace();
                  abortEditor(editor);
            } finally {

                  CloseFunction.close(outputStream);
            }

            try {
                  mDiskLruCache.flush();
            } catch(IOException e) {
                  e.printStackTrace();
            }

            return result;
      }

      private void abortEditor (Editor edit) {

            try {
                  if(edit != null) {

                        edit.abort();
                  }
            } catch(IOException e) {
                  e.printStackTrace();
            }
      }

      @Override
      public V remove (K key) {

            /* disk lru cache will remove file auto , so didn't support this operator */

            return null;
      }

      @Override
      public V load (K key) {

            InputStream inputStream = null;

            String stringKey = mConverter.fileName(key);

            /* try to get snapShort */

            Snapshot snapshot = null;
            try {
                  snapshot = mDiskLruCache.get(stringKey);
            } catch(IOException e) {
                  e.printStackTrace();
            }

            /* try to load value from snapShot's stream */

            if(snapshot != null) {

                  inputStream = snapshot.getInputStream(0);

                  try {

                        return mConverter.toValue(key, inputStream);
                  } catch(Exception e) {

                        e.printStackTrace();

                        if(mExceptionHandler != null) {
                              mExceptionHandler.onConvertToValue(e, key);
                        }
                  } finally {

                        CloseFunction.close(inputStream);
                  }
            }

            return null;
      }

      @Override
      public boolean containsOf (K key) {

            String name = mConverter.fileName(key);

            try {
                  Snapshot snapshot = mDiskLruCache.get(name);
                  return snapshot != null;
            } catch(IOException e) {
                  e.printStackTrace();
            }
            return false;
      }
}
