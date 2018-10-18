package tech.threekilogram.depository.stream;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import tech.threekilogram.depository.cache.bitmap.BitmapConverter;
import tech.threekilogram.depository.cache.bitmap.ScaleMode;
import tech.threekilogram.depository.cache.json.ObjectLoader;
import tech.threekilogram.depository.file.converter.FileStringConverter;
import tech.threekilogram.depository.net.okhttp.OkhttpLoader;
import tech.threekilogram.depository.net.responsebody.BodyBitmapConverter;
import tech.threekilogram.depository.net.responsebody.BodyStringConverter;
import tech.threekilogram.depository.net.retrofit.down.Downer;
import tech.threekilogram.depository.net.retrofit.down.Downer.OnDownloadUpdateListener;

/**
 * 从网络
 *
 * @author Liujin 2018-10-07:19:56
 */
public class StreamLoader {

      /**
       * 网络获取string
       */
      private static OkhttpLoader<String> sRetrofitStringLoader;
      /**
       * 辅助从文件读取保存string
       */
      private static FileStringConverter  sFileStringConverter;
      /**
       * 网络获取bitmap
       */
      private static OkhttpLoader<Bitmap> sRetrofitBitmapLoader;
      /**
       * 辅助读取保存bitmap数据流
       */
      private static BitmapConverter      sBitmapConverter;

      private StreamLoader ( ) { }

      /**
       * 从网络加载string
       *
       * @param url url
       *
       * @return url 对应的 string
       */
      public static String loadStringFromNet ( String url ) {

            if( sRetrofitStringLoader == null ) {
                  sRetrofitStringLoader = new OkhttpLoader<>(
                      new BodyStringConverter()
                  );
            }
            return sRetrofitStringLoader.load( url );
      }

      /**
       * 保存一个string到文件
       *
       * @param data string 数据
       * @param file string 需要保存到的文件
       */
      public static void saveStringToFile ( String data, File file ) {

            if( sFileStringConverter == null ) {
                  sFileStringConverter = new FileStringConverter();
            }

            try {
                  FileOutputStream outputStream = new FileOutputStream( file );
                  sFileStringConverter.to( outputStream, data );
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

            if( sFileStringConverter == null ) {
                  sFileStringConverter = new FileStringConverter();
            }

            if( file.exists() && file.isFile() ) {

                  try {
                        FileInputStream inputStream = new FileInputStream( file );
                        return sFileStringConverter.from( inputStream );
                  } catch(FileNotFoundException e) {
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
          String url, File file, OnDownloadUpdateListener updateListener ) {

            Downer.downloadTo( file, url, updateListener, null, null );
      }

      /**
       * 从网络加载图片,如果需要缩放图片尺寸,请先下载文件到一个文件夹{@link #downLoad(String, File)},之后读取
       *
       * @param url url
       *
       * @return url 对应的图片
       */
      public static Bitmap loadBitmapFromNet ( String url ) {

            if( sRetrofitBitmapLoader == null ) {
                  sRetrofitBitmapLoader = new OkhttpLoader<>( new BodyBitmapConverter() );
            }

            return sRetrofitBitmapLoader.load( url );
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

      /**
       * 从一个文件读取bitmap,缩放至指定尺寸,指定格式
       */
      public static Bitmap loadBitmapFromFile (
          File file, @ScaleMode int scaleMode, int width, int height, Config config ) {

            if( sBitmapConverter == null ) {
                  sBitmapConverter = new BitmapConverter();
            }
            return sBitmapConverter.from( file, scaleMode, width, height, config );
      }
}
