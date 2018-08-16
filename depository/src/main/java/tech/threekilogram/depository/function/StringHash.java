package tech.threekilogram.depository.function;

/**
 * @author: Liujin
 * @version: V1.0
 * @date: 2018-08-16
 * @time: 20:49
 */
public class StringHash {

      public static String hash ( String source ) {

            long h = 0;
            final int len = source.length();

            if( len > 0 ) {
                  for( int i = 0; i < len; i++ ) {
                        h = 31 * h + source.charAt( i );
                  }
            }
            return String.valueOf( h );
      }
}
