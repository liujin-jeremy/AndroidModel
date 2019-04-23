package tech.liujin.model.converter;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import com.threekilogram.bitmapreader.BitmapReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * 用于按照规则从file/stream读取bitmap
 *
 * @author: Liujin
 * @version: V1.0
 * @date: 2018-08-15
 * @time: 21:27
 */
public class BitmapConverter implements StreamConverter<Bitmap> {

      /**
       * 读取原图,使用{@link Config#RGB_565}
       *
       * @param file file bitmap
       *
       * @return bitmap
       */
      public Bitmap from ( File file ) {

            return BitmapReader.read( file );
      }

      /**
       * 读取原图
       *
       * @param file file bitmap
       *
       * @return bitmap
       */
      public Bitmap from ( File file, Config config ) {

            return BitmapReader.read( file, config );
      }

      /**
       * 读取图片,并压缩至要求尺寸,像素是RGB_565
       *
       * @param file file bitmap
       *
       * @return bitmap
       */
      public Bitmap from (
          File file, int width, int height ) {

            return from( file, width, height, Config.RGB_565 );
      }

      /**
       * 读取图片,并按照要求压缩尺寸,配置像素格式
       *
       * @param file file bitmap
       * @param width 当压缩尺寸方式需要尺寸信息时,提供压缩宽度
       * @param height 当压缩尺寸方式需要尺寸信息时,提压缩供高度
       * @param config 像素格式
       *
       * @return bitmap 图片
       */
      public Bitmap from (
          File file, int width, int height, Config config ) {

            return BitmapReader.matchSize( file, width, height, config );
      }

      /**
       * 读取原图
       *
       * @param inputStream stream
       *
       * @return bitmap 图片
       */
      @Override
      public Bitmap from ( InputStream inputStream ) {

            return BitmapReader.read( inputStream );
      }

      /**
       * 读取原图
       *
       * @param inputStream stream
       *
       * @return bitmap bitmap from stream
       */
      public Bitmap from ( InputStream inputStream, Config config ) {

            return BitmapReader.read( inputStream, config );
      }

      /**
       * 读取图片,并压缩至要求尺寸,像素是RGB_565
       *
       * @param stream 数据流
       * @param width 压缩后宽度
       * @param height 压缩后高度
       *
       * @return bitmap 压缩后图片
       */
      public Bitmap from (
          InputStream stream, int width, int height ) {

            return from( stream, width, height, Config.RGB_565 );
      }

      /**
       * 读取图片,并压缩至要求尺寸,可以配置像素格式
       *
       * @param stream 数据流
       * @param width 需要的宽度
       * @param height 需要的高度
       * @param config 像素格式
       *
       * @return bitmap 图片
       */
      public Bitmap from (
          InputStream stream, int width, int height, Config config ) {

            return BitmapReader.matchSize( stream, width, height, config );
      }

      /**
       * save bitmap to outputStream
       *
       * @param stream outputStream
       * @param value bitmap
       */
      @Override
      public void to (
          OutputStream stream, Bitmap value ) {

            value.compress( CompressFormat.PNG, 100, stream );
      }

      /**
       * save bitmap to outputStream
       *
       * @param stream outputStream
       * @param value bitmap
       */
      public void to (
          OutputStream stream, Bitmap value, CompressFormat compressFormat, int quality ) {

            value.compress( compressFormat, quality, stream );
      }

      /**
       * save bitmap to outputStream
       *
       * @param file file
       * @param value bitmap
       */
      public void to ( File file, Bitmap value ) {

            try {
                  FileOutputStream outputStream = new FileOutputStream( file );
                  to( outputStream, value );
            } catch(FileNotFoundException e) {
                  e.printStackTrace();
            }
      }
}
