package tech.threekilogram.depository.function.encode;

import static tech.threekilogram.depository.function.encode.EncodeMode.HASH;
import static tech.threekilogram.depository.function.encode.EncodeMode.MD5;

/**
 * @author: Liujin
 * @version: V1.0
 * @date: 2018-08-16
 * @time: 21:20
 */
public class StringEncoder {

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
}
