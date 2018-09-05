package tech.threekilogram.depository.function.encode;

import android.support.annotation.NonNull;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * encode string util
 *
 * @author: Liujin
 * @version: V1.0
 * @date: 2018-07-30
 * @time: 0:05
 */
public class Md5 {

      /**
       * 十六进制下数字到字符的映射数组
       */
      private final static String[] HEX_DIGITS = { "0", "1", "2", "3", "4", "5",
                                                   "6", "7", "8", "9", "a", "b",
                                                   "c", "d", "e", "f" };

      /**
       * encoder
       */
      private static MessageDigest md;

      static {
            try {
                  md = MessageDigest.getInstance( "MD5" );
            } catch(NoSuchAlgorithmException e) {
                  e.printStackTrace();
            }
      }

      /**
       * 使用md5 得到一段数字摘要字符串
       *
       * @param src 原始字符串
       *
       * @return encode 后的字符串
       */
      public static String encode ( @NonNull String src ) {

            return encodeByMD5( src );
      }

      /**
       * 对字符串进行MD5编码
       */
      private static String encodeByMD5 ( String originString ) {

            if( originString != null ) {
                  try {
                        byte[] results = md.digest( originString.getBytes() );
                        return byteArrayToHexString( results );
                  } catch(Exception ex) {
                        ex.printStackTrace();
                  }
            }
            return null;
      }

      /**
       * 转换字节数组为16进制字串
       *
       * @param b 字节数组
       *
       * @return 十六进制字串
       */
      private static String byteArrayToHexString ( byte[] b ) {

            StringBuilder builder = new StringBuilder();
            for( byte byteItem : b ) {
                  byteToHexString( builder, byteItem );
            }
            return builder.toString();
      }

      /**
       * 将一个字节转化成16进制形式的字符串
       */
      private static void byteToHexString ( StringBuilder builder, byte b ) {

            int n = b;
            if( n < 0 ) {
                  n = 256 + n;
            }
            int d1 = n / 16;
            int d2 = n % 16;
            builder.append( HEX_DIGITS[ d1 ] ).append( HEX_DIGITS[ d2 ] );
      }
}
