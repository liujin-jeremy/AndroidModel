package tech.liujin.cache.util;

/**
 * @author Liujin 2018-10-26:9:42
 */

import java.io.File;
import tech.liujin.cache.util.encode.EncodeMode;
import tech.liujin.cache.util.encode.StringEncoder;
import tech.liujin.cache.util.io.FileCache;

/**
 * @author Liujin 2018-10-25:19:34
 */
public class FileHelper {

      /**
       * 一个文件夹用于统一保存key对应的文件
       */
      private File      mDir;
      @EncodeMode
      private int       mMode       = EncodeMode.HASH;
      /**
       * 缓存创建的file
       */
      private FileCache mFileLoader = new FileCache();

      public FileHelper ( File dir ) {

            mDir = dir;
      }

      public File getDir ( ) {

            return mDir;
      }

      /**
       * 设置文件名字转化模式
       */
      public void setMode ( @EncodeMode int mode ) {

            mMode = mode;
      }

      @EncodeMode
      public int getMode ( ) {

            return mMode;
      }

      /**
       * 获取一个合法名字
       */
      public String encodeKey ( String key ) {

            return StringEncoder.encode( key, mMode );
      }

      public boolean exists ( String key ) {

            return getFile( key ).exists();
      }

      public File getFile ( String key ) {

            File file = mFileLoader.get( key );

            if( file == null ) {

                  String name = encodeKey( key );
                  file = new File( mDir, name );
                  mFileLoader.put( key, file );
                  return file;
            }

            return file;
      }
}
