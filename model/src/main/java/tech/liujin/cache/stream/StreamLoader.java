package tech.liujin.cache.stream;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.support.annotation.Nullable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import tech.liujin.cache.converter.BitmapConverter;
import tech.liujin.cache.converter.GsonConverter;
import tech.liujin.cache.converter.InputStreamConverter;
import tech.liujin.cache.converter.StringConverter;
import tech.liujin.cache.net.DownLoader.OnProgressUpdateListener;
import tech.liujin.cache.net.Downer;
import tech.liujin.cache.net.OkHttpLoader;
import tech.liujin.cache.net.OnErrorListener;

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
          new InputStreamConverter()
      );

      /**
       * 辅助从文件读取保存string
       */
      private static StringConverter sStringConverter;
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

            if( sStringConverter == null ) {
                  sStringConverter = new StringConverter();
            }
            InputStream stream = sOkHttpLoader.load( url );
            return sStringConverter.from( stream );
      }

      /**
       * 保存一个string到文件
       *
       * @param data string 数据
       * @param file string 需要保存到的文件
       */
      public static void saveStringToFile ( String data, File file ) {

            if( sStringConverter == null ) {
                  sStringConverter = new StringConverter();
            }

            try {
                  FileOutputStream outputStream = new FileOutputStream( file );
                  sStringConverter.to( outputStream, data );
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

            if( sStringConverter == null ) {
                  sStringConverter = new StringConverter();
            }

            if( file.exists() && file.isFile() ) {

                  try {
                        FileInputStream inputStream = new FileInputStream( file );
                        return sStringConverter.from( inputStream );
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

            InputStream inputStream = sOkHttpLoader.load( url );
            return new GsonConverter<>( beanClass ).from( inputStream );
      }

      /**
       * 保存一个json到文件
       *
       * @param json string 数据
       * @param file string 需要保存到的文件
       */
      @SuppressWarnings("unchecked")
      public static <V> void saveJsonToFile ( File file, V json ) {

            Class<V> aClass = (Class<V>) json.getClass();
            try {
                  FileOutputStream outputStream = new FileOutputStream( file );
                  new GsonConverter<V>( aClass ).to( outputStream, json );
            } catch(FileNotFoundException e) {
                  e.printStackTrace();
            }
      }

      /**
       * 从本地加载json
       *
       * @param file json 对应 file
       * @param beanClass json bean class
       *
       * @return url 对应的 json bean
       */
      public static <V> V loadJsonFromFile ( File file, Class<V> beanClass ) {

            try {
                  FileInputStream inputStream = new FileInputStream( file );
                  return new GsonConverter<>( beanClass ).from( inputStream );
            } catch(FileNotFoundException e) {
                  e.printStackTrace();
            }
            return null;
      }

      /**
       * 从网络下载url对应文件
       *
       * @param url url
       * @param file url保存的文件
       */
      public static void downLoad ( String url, File file ) {

            Downer.down( url, file );
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
          @Nullable OnProgressUpdateListener updateListener,
          @Nullable OnErrorListener onErrorListener ) {

            Downer.down( url, file, updateListener, onErrorListener );
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
