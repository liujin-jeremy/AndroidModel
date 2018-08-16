package tech.threekilogram.depository.key;

import android.support.annotation.IntDef;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import tech.threekilogram.depository.function.Md5;
import tech.threekilogram.depository.function.StringHash;

/**
 * @author: Liujin
 * @version: V1.0
 * @date: 2018-08-16
 * @time: 21:20
 */
public class KeyNameConverter {

      public static final int MD5   = 11;
      public static final int HASH  = 12;
      @EncodeMode
      private             int mMode = HASH;

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
      public String encodeToName ( String key ) {

            if( mMode == HASH ) {

                  return StringHash.hash( key );
            }

            if( mMode == MD5 ) {

                  return Md5.md5( key );
            }

            return key;
      }

      @IntDef({ MD5, HASH })
      @Retention(RetentionPolicy.SOURCE)
      public @interface EncodeMode { }
}
