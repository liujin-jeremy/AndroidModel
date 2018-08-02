package tech.threekilogram.depository.file.lru;

import com.jakewharton.disklrucache.DiskLruCache;
import com.jakewharton.disklrucache.DiskLruCache.Editor;
import com.jakewharton.disklrucache.DiskLruCache.Snapshot;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import tech.threekilogram.depository.file.BaseFileLoadSupport;
import tech.threekilogram.depository.function.CloseFunction;

/**
 * @author: Liujin
 * @version: V1.0
 * @date: 2018-07-30
 * @time: 18:13
 */
public class DiskLruCacheLoader<K, V> extends BaseFileLoadSupport<K, V> {

      private DiskLruCache                mDiskLruCache;
      private DiskLruCacheConverter<K, V> mConverter;

      /**
       * @param folder which dir to save data
       * @param maxSize max data size
       * @param converter function to do
       */
      public DiskLruCacheLoader (File folder, long maxSize, DiskLruCacheConverter<K, V> converter) {

            /* make folder is exists and folder is must a directory */

            if(!folder.exists()) {
                  boolean mkdirs = folder.mkdirs();
            }

            if(folder.isFile()) {
                  boolean delete = folder.delete();
                  boolean mkdirs = folder.mkdirs();
            }

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

            OutputStream outputStream = null;
            Editor edit = null;

            V result = null;

            try {
                  String name = mConverter.fileName(key);

                  if(mSaveStrategy == SAVE_STRATEGY_RETURN_OLD) {
                        Snapshot snapshot = mDiskLruCache.get(name);
                        if(snapshot != null) {
                              result = loadFromSnapshot(key, snapshot);
                        }
                  }

                  edit = mDiskLruCache.edit(name);
                  outputStream = edit.newOutputStream(0);

                  try {
                        mConverter.saveValue(outputStream, value);
                        edit.commit();
                  } catch(IOException e) {

                        try {
                              edit.abort();
                        } catch(IOException e1) {
                              e1.printStackTrace();
                        }
                  }
            } catch(Exception e) {

                  if(edit != null) {
                        try {
                              edit.abort();
                        } catch(IOException e1) {
                              e1.printStackTrace();
                        }
                  }
            } finally {

                  CloseFunction.close(outputStream);

                  try {
                        mDiskLruCache.flush();
                  } catch(IOException e) {
                        e.printStackTrace();
                  }
            }

            return result;
      }

      @Override
      public V remove (K key) {

            /* disk lru cache will remove file auto , so didn't support */

            return null;
      }

      @Override
      public V load (K key) {

            InputStream inputStream = null;
            try {
                  String stringKey = mConverter.fileName(key);
                  Snapshot snapshot = mDiskLruCache.get(stringKey);

                  if(snapshot != null) {
                        inputStream = snapshot.getInputStream(0);
                        return mConverter.toValue(inputStream);
                  }
            } catch(Exception e) {

                  if(mExceptionHandler != null) {
                        mExceptionHandler.onConvertToValue(e, key);
                  }
            } finally {

                  CloseFunction.close(inputStream);
            }

            return null;
      }

      private V loadFromSnapshot (K key, Snapshot snapshot) {

            InputStream inputStream = null;
            try {
                  if(snapshot != null) {
                        inputStream = snapshot.getInputStream(0);
                        return mConverter.toValue(inputStream);
                  }
            } catch(Exception e) {

                  if(mExceptionHandler != null) {
                        mExceptionHandler.onConvertToValue(e, key);
                  }
            } finally {

                  CloseFunction.close(inputStream);
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
