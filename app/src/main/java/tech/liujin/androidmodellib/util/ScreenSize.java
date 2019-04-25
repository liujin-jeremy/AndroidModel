package tech.liujin.androidmodellib.util;

import android.content.Context;
import android.util.DisplayMetrics;

/**
 * @author: Liujin
 * @version: V1.0
 * @date: 2018-08-22
 * @time: 10:17
 */
public class ScreenSize {

      private static int sWidth  = -1;
      private static int sHeight = -1;

      public static void init ( Context context ) {

            if( sWidth == -1 || sHeight == -1 ) {

                  DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
                  sWidth = displayMetrics.widthPixels;
                  sHeight = displayMetrics.heightPixels;
            }
      }

      public static int getWidth ( ) {

            return sWidth;
      }

      public static int getHeight ( ) {

            return sHeight;
      }

      public static int getWidth ( float percent ) {

            return (int) ( sWidth * percent );
      }

      public static int getHeight ( float percent ) {

            return (int) ( sHeight * percent );
      }
}
