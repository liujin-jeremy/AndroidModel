package tech.threekilogram.model.stream;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.support.annotation.Nullable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import tech.threekilogram.model.cache.json.ObjectLoader;
import tech.threekilogram.model.converter.BitmapConverter;
import tech.threekilogram.model.converter.SimpleConverter;
import tech.threekilogram.model.converter.StringConverter;
import tech.threekilogram.model.net.Downer;
import tech.threekilogram.model.net.Downer.OnDownloadUpdateListener;
import tech.threekilogram.model.net.Downer.OnErrorListener;
import tech.threekilogram.model.net.OkHttpLoader;

/**
 * 从网络
 *
 * @author Liujin 2018-10-07:19:56
 */
public class StreamLoader {

      /**
       * 网络获取stream
       */
      private static OkHttpLoader<InputStream> sOkHttpLoader = new OkHttpLoader<>(
          new SimpleConverter()
      );

      /**
       * 辅助从文件读取保存string
       */
      private static StringConverter sConverter;
      /**
       * 辅助读取保存bitmap数据流
       */
      private static BitmapConverter sBitmapConverter;

      private StreamLoader ( ) { }

      /**
       * 从网络加载string
       *
       * @param url url
       *
       * @return url 对应的 string
       */
      public static String loadStringFromNet ( String url ) {

            if( sConverter == null ) {
                  sConverter = new StringConverter();
            }
            InputStream stream = sOkHttpLoader.load( url );
            return sConverter.from( stream );
      }

      /**
       * 保存一个string到文件
       *
       * @param data string 数据
       * @param file string 需要保存到的文件
       */
      public static void saveStringToFile ( String data, File file ) {

            if( sConverter == null ) {
                  sConverter = new StringConverter();
            }

            try {
                  FileOutputStream outputStream = new FileOutputStream( file );
                  sConverter.to( outputStream, data );
            } catch(FileNotFoundException e) {
                  e.printStackTrace();
            }
      }

      /**
       * 从文件加载string
       *
       * @param file string 文件
       *
       * @return file 对应 string
       */
      public static String loadStringFromFile ( File file ) {

            if( sConverter == null ) {
                  sConverter = new StringConverter();
            }

            if( file.exists() && file.isFile() ) {

                  try {
                        FileInputStream inputStream = new FileInputStream( file );
                        return sConverter.from( inputStream );
                  } catch(Exception e) {
                        e.printStackTrace();
                  }
            }
            return null;
      }

      /**
       * 从网络加载json
       *
       * @param url url
       * @param beanClass json bean class
       *
       * @return url 对应的 json bean
       */
      public static <V> V loadJsonFromNet ( String url, Class<V> beanClass ) {

            return ObjectLoader.loadFromNet( url, beanClass );
      }

      /**
       * 保存一个json到文件
       *
       * @param beanClass json bean class
       * @param json string 数据
       * @param file string 需要保存到的文件
       */
      public static <V> void saveJsonToFile ( File file, Class<V> beanClass, V json ) {

            ObjectLoader.toFile( file, json, beanClass );
      }

      /**
       * 从网络加载json
       *
       * @param file json 对应 file
       * @param beanClass json bean class
       *
       * @return url 对应的 json bean
       */
      public static <V> V loadJsonFromFile ( File file, Class<V> beanClass ) {

            return ObjectLoader.loadFromFile( file, beanClass );
      }

      /**
       * 从网络下载url对应文件
       *
       * @param url url
       * @param file url保存的文件
       */
      public static void downLoad ( String url, File file ) {

            Downer.downloadTo( file, url );
      }

      /**
       * 从网络下载url对应文件
       *
       * @param url url
       * @param file url保存的文件
       * @param updateListener 下载进度监听
       */
      public static void downLoad (
          String url,
          File file,
          @Nullable OnDownloadUpdateListener updateListener,
          @Nullable OnErrorListener onErrorListener ) {

            Downer.downloadTo( file, url, updateListener, onErrorListener );
      }

      /**
       * 从网络加载原始图片
       *
       * @param url url
       *
       * @return url 对应的图片
       */
      public static Bitmap loadBitmapFromNet ( String url ) {

            if( sBitmapConverter == null ) {
                  sBitmapConverter = new BitmapConverter();
            }

            InputStream stream = sOkHttpLoader.load( url );
            return sBitmapConverter.from( stream );
      }

      /**
       * 从网络加载图片,之后缩放该图片
       *
       * @param url url
       *
       * @return url 对应的图片
       */
      public static Bitmap loadBitmapFromNet ( String url, int width, int height ) {

            if( sBitmapConverter == null ) {
                  sBitmapConverter = new BitmapConverter();
            }

            InputStream stream = sOkHttpLoader.load( url );
            return sBitmapConverter.from( stream, width, height );
      }

      /**
       * 从网络加载图片,如果需要缩放图片尺寸,请先下载文件到一个文件夹{@link #downLoad(String, File)},之后读取
       *
       * @param url url
       *
       * @return url 对应的图片
       */
      public static Bitmap loadBitmapFromNet ( String url, int width, int height, Config config ) {

            if( sBitmapConverter == null ) {
                  sBitmapConverter = new BitmapConverter();
            }

            InputStream stream = sOkHttpLoader.load( url );
            return sBitmapConverter.from( stream, width, height, config );
      }

      /**
       * 保存一个bitmap到文件
       *
       * @param file string 需要保存到的文件
       */
      public static void saveBitmapToFile ( File file, Bitmap bitmap ) {

            try {
                  bitmap.compress( CompressFormat.PNG, 100, new FileOutputStream( file ) );
            } catch(FileNotFoundException e) {
                  e.printStackTrace();
            }
      }

      /**
       * 保存一个bitmap到文件
       *
       * @param file string 需要保存到的文件
       */
      public static void saveBitmapToFile (
          File file, Bitmap bitmap, CompressFormat compressFormat, int quality ) {

            try {
                  bitmap.compress( compressFormat, quality, new FileOutputStream( file ) );
            } catch(FileNotFoundException e) {
                  e.printStackTrace();
            }
      }

      /**
       * 从一个文件读取bitmap
       */
      public static Bitmap loadBitmapFromFile ( File file ) {

            if( sBitmapConverter == null ) {
                  sBitmapConverter = new BitmapConverter();
            }
            return sBitmapConverter.from( file );
      }

      /**
       * 从一个文件读取bitmap,缩放至指定尺寸
       */
      public static Bitmap loadBitmapFromFile ( File file, int width, int height ) {

            if( sBitmapConverter == null ) {
                  sBitmapConverter = new BitmapConverter();
            }
            return sBitmapConverter.from( file, width, height );
      }

      /**
       * 从一个文件读取bitmap,缩放至指定尺寸,指定格式
       */
      public static Bitmap loadBitmapFromFile ( File file, int width, int height, Config config ) {

            if( sBitmapConverter == null ) {
                  sBitmapConverter = new BitmapConverter();
            }
            return sBitmapConverter.from( file, width, height, config );
      }
}
