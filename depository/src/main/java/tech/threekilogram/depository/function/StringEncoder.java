package tech.threekilogram.depository.function;

import android.support.annotation.IntDef;
import java.io.File;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author: Liujin
 * @version: V1.0
 * @date: 2018-08-16
 * @time: 21:20
 */
public class StringEncoder {

      public static final int MD5     = 11;
      public static final int HASH    = 12;
      public static final int DEFAULT = 13;

      @EncodeMode
      private int mMode = HASH;

      public void setMode ( @EncodeMode int mode ) {

            mMode = mode;
      }

      /**
       * 转换key为string
       *
       * @param key key
       *
       * @return string name
       */
      public String encode ( String key ) {

            if( mMode == HASH ) {

                  return StringHash.hash( key );
            }

            if( mMode == MD5 ) {

                  return Md5.md5( key );
            }

            return key;
      }

      @Retention(RetentionPolicy.SOURCE)
      @IntDef(value = { MD5, HASH, DEFAULT })
      public @interface EncodeMode { }

      /**
       * 根据一个url获取一个文件
       *
       * @param dir 文件夹
       * @param url 文件url,将会转为文件名字
       *
       * @return 位于文件夹下的文件
       */
      public File getFile ( File dir, String url ) {

            return new File( dir, encode( url ) );
      }
}
