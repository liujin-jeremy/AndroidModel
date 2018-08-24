package tech.threekilogram.depository.file.loader;

import android.util.LruCache;
import java.io.File;

/**
 * 保存key file
 *
 * @author: Liujin
 * @version: V1.0
 * @date: 2018-08-24
 * @time: 16:54
 */
public class KeyFileLoader<K> {

      private LruCache<K, File> mCache = new LruCache<>( 16 );

      public void put ( K key, File file ) {

            mCache.put( key, file );
      }

      public File get ( K key ) {

            return mCache.get( key );
      }
}
