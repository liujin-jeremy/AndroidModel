package tech.liujin.cache.util.io;

import android.support.v4.util.LruCache;
import java.io.File;

/**
 * 缓存 file
 *
 * @author: Liujin
 * @version: V1.0
 * @date: 2018-08-24
 * @time: 16:54
 */
public class FileCache extends LruCache<String, File> {

      public FileCache ( ) {

            super( 16 );
      }

      public FileCache ( int maxSize ) {

            super( maxSize );
      }
}
