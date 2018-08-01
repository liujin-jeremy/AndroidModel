package tech.threekilogram.depository.file.lru;

import com.jakewharton.disklrucache.DiskLruCache;
import com.jakewharton.disklrucache.DiskLruCache.Editor;
import com.jakewharton.disklrucache.DiskLruCache.Snapshot;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import tech.threekilogram.depository.file.FileLoadSupport;

/**
 * @author: Liujin
 * @version: V1.0
 * @date: 2018-07-30
 * @time: 18:13
 */
public class DiskLruCacheLoader<K, V> implements FileLoadSupport<K, V> {

      private DiskLruCache             mDiskLruCache;
      private DiskLruCacheMapper<K, V> mMapper;

      public DiskLruCacheLoader (File folder, long maxSize, DiskLruCacheMapper<K, V> mapper)
          throws IOException {

            mDiskLruCache = DiskLruCache.open(folder, 1, 1, maxSize);
            mMapper = mapper;
      }

      @Override
      public V loadFromFile (K k) throws IOException {

            String key = mMapper.stringKey(k);
            Snapshot snapshot = mDiskLruCache.get(key);
            if(snapshot != null) {

                  InputStream inputStream = null;
                  try {
                        inputStream = snapshot.getInputStream(0);
                        int length = inputStream.available();
                        byte[] bytes = new byte[length];
                        int read = inputStream.read(bytes);
                        inputStream.close();
                        return mMapper.toValue(bytes);
                  } finally {

                        if(inputStream != null) {
                              inputStream.close();
                        }
                  }
            }

            return null;
      }

      @Override
      public boolean fileExist (K key) {

            String stringKey = mMapper.stringKey(key);
            try {
                  Snapshot snapshot = mDiskLruCache.get(stringKey);
                  return snapshot != null;
            } catch(IOException e) {
                  e.printStackTrace();
            }
            return false;
      }

      @Override
      public void writeToFile (K key, V value) throws IOException {

            String stringKey = mMapper.stringKey(key);
            byte[] bytes = mMapper.toFile(value);

            if(bytes == null || bytes.length == 0) {
                  return;
            }

            OutputStream outputStream = null;
            Editor edit = null;
            try {
                  edit = mDiskLruCache.edit(stringKey);
                  outputStream = edit.newOutputStream(0);
                  outputStream.write(bytes);
                  outputStream.close();
                  edit.commit();
            } catch(Exception e) {

                  if(edit != null) {
                        edit.abort();
                  }
            } finally {

                  if(outputStream != null) {

                        outputStream.close();
                  }

                  mDiskLruCache.flush();
            }
      }
}
