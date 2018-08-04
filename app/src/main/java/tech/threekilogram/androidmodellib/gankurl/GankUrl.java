package tech.threekilogram.androidmodellib.gankurl;

import static tech.threekilogram.androidmodellib.GankUrl.ANDROID;
import static tech.threekilogram.androidmodellib.GankUrl.BASE_CATEGORY;
import static tech.threekilogram.androidmodellib.GankUrl.BEAUTY;
import static tech.threekilogram.androidmodellib.GankUrl.DEFAULT_COUNT;

/**
 * @author: Liujin
 * @version: V1.0
 * @date: 2018-07-31
 * @time: 17:38
 */
public class GankUrl {

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
