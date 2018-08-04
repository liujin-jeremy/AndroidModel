package tech.threekilogram.androidmodellib.gankurl;

/**
 * @author: Liujin
 * @version: V1.0
 * @date: 2018-07-31
 * @time: 17:38
 */
public class GankUrl {

      /* http://gank.io/api/data/数据类型/请求个数/第几页 */

      public static final String BASE_CATEGORY = "http://gank.io/api/data/";

      public static final String BEAUTY  = "福利/";
      public static final String ANDROID = "Android/";

      public static final int DEFAULT_COUNT = 10;

      private static final String BASE_BEAUTY_URL =
          BASE_CATEGORY
              + BEAUTY
              + DEFAULT_COUNT + "/";

      private static final String BASE_ANDROID_URL =
          BASE_CATEGORY
              + ANDROID
              + DEFAULT_COUNT + "/";

      public static String getBeautyPageUrl (int page) {

            return BASE_BEAUTY_URL + page;
      }

      public static String getAndroidPageUrl (int page) {

            return BASE_ANDROID_URL + page;
      }
}
