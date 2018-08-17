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

            final int limit = Integer.MAX_VALUE / 31;
            int count = 0;

            if( len > 0 ) {
                  for( int i = 0; i < len; i++ ) {
                        if( h >= limit ) {
                              count++;
                        }
                        h = 31 * h + source.charAt( i );
                  }
            }
            return Integer.toHexString( len )
                + Integer.toHexString( count )
                + String.format( "%016x", h );
      }
}
