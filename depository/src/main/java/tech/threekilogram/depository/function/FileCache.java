package tech.threekilogram.depository.function;

import java.io.File;

/**
 * 缓存 file
 *
 * @author: Liujin
 * @version: V1.0
 * @date: 2018-08-24
 * @time: 16:54
 */
public class FileCache extends android.support.v4.util.LruCache<String, File> {

      public FileCache ( ) {

            super( 16 );
      }

      public FileCache ( int maxSize ) {

            super( maxSize );
      }
}
