package tech.threekilogram.androidmodellib.util;

import java.util.Locale;

/**
 * @author: Liujin
 * @version: V1.0
 * @date: 2018-07-31
 * @time: 17:38
 */
public class GankUrl {

      /* http://gank.io/api/data/数据类型/请求个数/第几页 */

      public static final String HISTORY       = "https://gank.io/api/day/history";
      public static final String DAY           = "https://gank.io/api/day/%s/%s/%s";
      public static final int    DEFAULT_COUNT = 10;

      public static String historyUrl ( ) {

            return HISTORY;
      }

      public static String dayUrl ( String history ) {

            //"2015-07-17"
            String year = history.substring( 0, 4 );
            String month = history.substring( 5, 7 );
            String day = history.substring( 8, 10 );

            return String.format( Locale.ENGLISH, DAY, year, month, day );
      }
}
