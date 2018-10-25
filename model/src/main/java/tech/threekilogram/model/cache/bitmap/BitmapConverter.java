package tech.threekilogram.model.cache.bitmap;

import static tech.threekilogram.model.cache.bitmap.ScaleMode.MATCH_HEIGHT;
import static tech.threekilogram.model.cache.bitmap.ScaleMode.MATCH_SIZE;
import static tech.threekilogram.model.cache.bitmap.ScaleMode.MATCH_WIDTH;
import static tech.threekilogram.model.cache.bitmap.ScaleMode.SAMPLE;
import static tech.threekilogram.model.cache.bitmap.ScaleMode.SAMPLE_MAX;
import static tech.threekilogram.model.cache.bitmap.ScaleMode.SRC_RGB;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import tech.threekilogram.model.StreamConverter;

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
       * 读取原图
       *
       * @param file file bitmap
       *
       * @return bitmap
       */
      public Bitmap from (
          File file ) {

            return from( file, ScaleMode.SRC_ARGB, 0, 0, Config.ARGB_8888 );
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

            return from( file, ScaleMode.MATCH_SIZE, width, height, Config.RGB_565 );
      }

      /**
       * 读取图片,并压缩至要求尺寸,可以配置像素格式
       *
       * @param file file bitmap
       * @param width 需要的宽度
       * @param height 需要的高度
       * @param config 像素格式
       *
       * @return bitmap 图片
       */
      public Bitmap from (
          File file, int width, int height, Config config ) {

            return from( file, ScaleMode.MATCH_SIZE, width, height, config );
      }

      /**
       * 读取图片,并按照要求压缩尺寸,配置像素格式
       *
       * @param file file bitmap
       * @param scaleMode 尺寸压缩方式
       * @param width 当压缩尺寸方式需要尺寸信息时,提供压缩宽度
       * @param height 当压缩尺寸方式需要尺寸信息时,提压缩供高度
       * @param config 像素格式
       *
       * @return bitmap 图片
       */
      public Bitmap from (
          File file, @ScaleMode int scaleMode, int width, int height, Config config ) {

            if( scaleMode == SAMPLE ) {
                  return BitmapReader.sampledBitmap( file, width, height, config );
            }

            if( scaleMode == SAMPLE_MAX ) {
                  return BitmapReader.maxSampledBitmap( file, width, height, config );
            }

            if( scaleMode == MATCH_WIDTH ) {
                  return BitmapReader.matchWidth( file, width, config );
            }

            if( scaleMode == MATCH_HEIGHT ) {
                  return BitmapReader.matchHeight( file, height, config );
            }

            if( scaleMode == MATCH_SIZE ) {
                  return BitmapReader.matchSize( file, width, height, config );
            }

            if( scaleMode == SRC_RGB ) {
                  return BitmapReader.readRgb( file );
            }

            return BitmapReader.readArgb( file );
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

            return from( inputStream, ScaleMode.SRC_ARGB, 0, 0, Config.ARGB_8888 );
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

            return from( stream, ScaleMode.MATCH_SIZE, width, height, Config.RGB_565 );
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

            return from( stream, ScaleMode.MATCH_SIZE, width, height, config );
      }

      /**
       * 读取图片,并按照要求压缩尺寸,配置像素格式
       *
       * @param stream 数据流
       * @param scaleMode 尺寸压缩方式
       * @param width 当压缩尺寸方式需要尺寸信息时,提供压缩宽度
       * @param height 当压缩尺寸方式需要尺寸信息时,提压缩供高度
       * @param config 像素格式
       *
       * @return bitmap 图片
       */
      public Bitmap from (
          InputStream stream, @ScaleMode int scaleMode, int width, int height, Config config ) {

            if( scaleMode == SAMPLE ) {
                  return BitmapReader.sampledBitmap( stream, width, height, config );
            }

            if( scaleMode == SAMPLE_MAX ) {
                  return BitmapReader.maxSampledBitmap( stream, width, height, config );
            }

            if( scaleMode == MATCH_WIDTH ) {
                  return BitmapReader.matchWidth( stream, width, config );
            }

            if( scaleMode == MATCH_HEIGHT ) {
                  return BitmapReader.matchHeight( stream, height, config );
            }

            if( scaleMode == MATCH_SIZE ) {
                  return BitmapReader.matchSize( stream, width, height, config );
            }

            if( scaleMode == SRC_RGB ) {
                  return BitmapReader.readRgb( stream );
            }

            return BitmapReader.readArgb( stream );
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
