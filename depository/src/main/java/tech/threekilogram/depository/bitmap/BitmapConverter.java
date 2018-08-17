package tech.threekilogram.depository.bitmap;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.support.annotation.IntDef;
import com.example.bitmapreader.BitmapReader;
import java.io.File;
import java.io.OutputStream;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 用于按照规则读取bitmap从file
 *
 * @author: Liujin
 * @version: V1.0
 * @date: 2018-08-15
 * @time: 21:27
 */
public class BitmapConverter {

      /**
       * 图片缩放宽度
       */
      private int mWidth;
      /**
       * 图片缩放高度
       */
      private int mHeight;
      /**
       * 图片缩放模式
       */
      private int mMode;
      /**
       * 图片像素配置
       */
      private Bitmap.Config mBitmapConfig = Config.RGB_565;

      /**
       * 保存格式
       */
      private Bitmap.CompressFormat mCompressFormat  = CompressFormat.PNG;
      /**
       * 保存质量
       */
      private int                   mCompressQuality = 100;

      /**
       * 获取设置的图片保存格式
       */
      public CompressFormat getCompressFormat ( ) {

            return mCompressFormat;
      }

      /**
       * 设置图片保存格式
       *
       * @param compressFormat 格式
       */
      public void setCompressFormat ( CompressFormat compressFormat ) {

            mCompressFormat = compressFormat;
      }

      /**
       * 获取设置的图片保存质量
       */
      public int getCompressQuality ( ) {

            return mCompressQuality;
      }

      /**
       * 设置图片保存质量
       */
      public void setCompressQuality ( int compressQuality ) {

            mCompressQuality = compressQuality;
      }

      /**
       * 获取设置的图片像素质量
       */
      public Config getBitmapConfig ( ) {

            return mBitmapConfig;
      }

      /**
       * 设置图片像素质量
       */
      public void setBitmapConfig ( Config bitmapConfig ) {

            mBitmapConfig = bitmapConfig;
      }

      /**
       * 设置缩放宽度
       *
       * @param width 宽度
       */
      public void setWidth ( int width ) {

            mWidth = width;
      }

      public int getWidth ( ) {

            return mWidth;
      }

      /**
       * 设置缩放高度
       *
       * @param height 高度
       */
      public void setHeight ( int height ) {

            mHeight = height;
      }

      public int getHeight ( ) {

            return mHeight;
      }

      /**
       * 设置缩放模式
       *
       * @param mode 模式
       */
      public void setMode ( @ScaleMode int mode ) {

            mMode = mode;
      }

      public int getMode ( ) {

            return mMode;
      }

      /**
       * read a bitmap
       *
       * @param file file bitmap
       *
       * @return bitmap
       */
      public Bitmap read ( File file ) {

            /* size is 0 */

            if( mWidth == 0 || mHeight == 0 || mMode == 0 ) {

                  BitmapFactory.Options options = new Options();
                  options.inPreferredConfig = mBitmapConfig;
                  return BitmapFactory.decodeFile( file.getAbsolutePath(), options );
            }

            if( mMode == SAMPLE ) {
                  return BitmapReader.sampledBitmap( file, mWidth, mHeight, mBitmapConfig );
            }

            if( mMode == SAMPLE_MAX ) {
                  return BitmapReader.maxSampledBitmap( file, mWidth, mHeight, mBitmapConfig );
            }

            if( mMode == MATCH_WIDTH ) {
                  return BitmapReader.matchWidth( file, mWidth, mBitmapConfig );
            }

            if( mMode == MATCH_HEIGHT ) {
                  return BitmapReader.matchHeight( file, mHeight, mBitmapConfig );
            }

            return BitmapReader.matchSize( file, mWidth, mHeight, mBitmapConfig );
      }

      /**
       * save bitmap to outputStream
       *
       * @param stream outputStream
       * @param value bitmap
       */
      public void write ( OutputStream stream, Bitmap value ) {

            value.compress( CompressFormat.PNG, 100, stream );
      }

      /**
       * 缩放模式:等比例缩放至最接近{@link #mWidth}和{@link #mHeight},不会小于设置的值
       */
      public static final int SAMPLE       = 1;
      /**
       * 缩放模式:等比例缩放至最接近{@link #mWidth}和{@link #mHeight},均会小于缩放的值
       */
      public static final int SAMPLE_MAX   = 2;
      /**
       * 缩放模式:等比例缩放至匹配{@link #mWidth}
       */
      public static final int MATCH_WIDTH  = 3;
      /**
       * 缩放模式:等比例缩放至匹配{@link #mHeight}
       */
      public static final int MATCH_HEIGHT = 4;

      /**
       * 配置缩放模式
       */
      @IntDef({ SAMPLE, SAMPLE_MAX, MATCH_WIDTH, MATCH_HEIGHT })
      @Retention(RetentionPolicy.SOURCE)
      public @interface ScaleMode { }
}
