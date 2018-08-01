package tech.threekilogram.androidmodellib.beauty;

import tech.threekilogram.androidmodellib.GankUrl;

/**
 * @author: Liujin
 * @version: V1.0
 * @date: 2018-07-31
 * @time: 17:38
 */
public class GankBeautyUrl {

      private static final String BASE_BEAUTY_URL =
          GankUrl.BASE_CATEGORY + GankUrl.福利 + GankUrl.DEFAULT_COUNT + "/";

      private static int sCurrentPage = 1;

      public static int currentPage () {

            return sCurrentPage;
      }

      public static String currentPageUrl () {

            return BASE_BEAUTY_URL + sCurrentPage;
      }

      public static String getNextPageUrl () {

            return BASE_BEAUTY_URL + (sCurrentPage + 1);
      }

      public static void currentPageAdd () {

            sCurrentPage++;
      }

      public static void setCurrentPage (int currentPage) {

            sCurrentPage = currentPage;
      }

      public static String getPageUrl (int page) {

            return BASE_BEAUTY_URL + page;
      }
}
