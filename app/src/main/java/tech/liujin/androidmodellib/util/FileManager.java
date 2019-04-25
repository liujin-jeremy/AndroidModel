package tech.liujin.androidmodellib.util;

import android.content.Context;
import android.os.Environment;
import java.io.File;

/**
 * @author wuxio 2018-05-05:19:13
 */
public class FileManager {

      private static File sAppDir;

      public static void init ( Context context ) {

            sAppDir = context.getExternalFilesDir( null );
            if( sAppDir == null ) {
                  sAppDir = context.getFilesDir();
            }

            if( !sAppDir.exists() ) {
                  boolean mkdirs = sAppDir.mkdirs();
            }
      }

      /**
       * @return app 目录,位于外存储或者内存储的gank目录下
       */
      public static File getAppDir ( ) {

            return sAppDir;
      }

      /**
       * @param type {@link android.os.Environment}
       *
       * @return app 某个类型文件夹
       */
      public static File getDir ( String type ) {

            File result = new File( sAppDir, type );
            if( !result.exists() ) {
                  boolean mkdirs = result.mkdirs();
            }
            return result;
      }

      public static File getMusic ( ) {

            return getDir( Environment.DIRECTORY_MUSIC );
      }

      public static File getPicture ( ) {

            return getDir( Environment.DIRECTORY_PICTURES );
      }

      public static File getMovies ( ) {

            return getDir( Environment.DIRECTORY_MOVIES );
      }

      public static File getDownloads ( ) {

            return getDir( Environment.DIRECTORY_DOWNLOADS );
      }

      public static File getDocuments ( ) {

            return getDir( "Documents" );
      }
}
