package tech.threekilogram.depository.function.encode;

import android.support.annotation.IntDef;
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

      private StringEncoder ( ) { }

      /**
       * 转换key为string
       *
       * @param key key
       *
       * @return string name
       */
      public static String encode ( String key, @EncodeMode int mode ) {

            if( mode == HASH ) {
                  return StringHash.hash( key );
            }
            if( mode == MD5 ) {
                  return Md5.encode( key );
            }
            return key;
      }

      @Retention(RetentionPolicy.SOURCE)
      @IntDef(value = { MD5, HASH, DEFAULT })
      public @interface EncodeMode { }
}
