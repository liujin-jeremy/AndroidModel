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
import com.threekilogram.bitmapreader.BitmapReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;

/**
 * 用于按照规则从file读取bitmap
 *
 * @author: Liujin
 * @version: V1.0
 * @date: 2018-08-15
 * @time: 21:27
 */
public class BitmapConverter {

      /**
       * from a bitmap
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
       * from a bitmap
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
       * from a bitmap
       *
       * @param file file bitmap
       *
       * @return bitmap
       */
      public Bitmap from (
          File file, int width, int height, Config config ) {

            return from( file, ScaleMode.MATCH_SIZE, width, height, config );
      }

      /**
       * from a bitmap
       *
       * @param file file bitmap
       *
       * @return bitmap
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
       * save bitmap to outputStream
       *
       * @param stream outputStream
       * @param value bitmap
       */
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
