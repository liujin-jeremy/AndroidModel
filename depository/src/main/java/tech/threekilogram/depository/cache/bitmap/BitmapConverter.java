package tech.threekilogram.depository.cache.bitmap;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.support.annotation.IntDef;
import com.threekilogram.bitmapreader.BitmapReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

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
       * 配置bitmap加载配置
       *
       * @param width 需求宽度
       * @param height 需求高度
       */
      public void configBitmap ( int width, int height ) {

            configBitmap( width, height, MATCH_SIZE, Config.RGB_565 );
      }

      /**
       * 配置bitmap加载配置
       *
       * @param width 需求宽度
       * @param height 需求高度
       * @param scaleMode 缩放方式
       */
      public void configBitmap ( int width, int height, @ScaleMode int scaleMode ) {

            configBitmap( width, height, scaleMode, Config.RGB_565 );
      }

      /**
       * 配置bitmap加载配置
       *
       * @param width 需求宽度
       * @param height 需求高度
       * @param scaleMode 缩放方式
       * @param config bitmap 像素格式
       */
      public void configBitmap ( int width, int height, @ScaleMode int scaleMode, Config config ) {

            setWidth( width );
            setHeight( height );
            setMode( scaleMode );
            setBitmapConfig( config );
      }

      /**
       * 配置图片保存方式
       *
       * @param compressFormat 保存格式
       * @param compressQuality 保存质量
       */
      public void configCompress ( CompressFormat compressFormat, int compressQuality ) {

            setCompressFormat( compressFormat );
            setCompressQuality( compressQuality );
      }

      /**
       * from a bitmap
       *
       * @param file file bitmap
       *
       * @return bitmap
       */
      public Bitmap from ( File file ) {

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

            if( mMode == MATCH_SIZE ) {
                  return BitmapReader.matchSize( file, mWidth, mHeight, mBitmapConfig );
            }

            if( mMode == SRC_RGB ) {
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
      public void to ( OutputStream stream, Bitmap value ) {

            value.compress( CompressFormat.PNG, 100, stream );
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

      /**
       * 缩放模式:等比例缩放至最接近{@link #mWidth}和{@link #mHeight},缩放后图片尺寸不会小于设置的值
       */
      public static final int SAMPLE       = 1;
      /**
       * 缩放模式:等比例缩放至最接近{@link #mWidth}和{@link #mHeight},缩放后图片尺寸均会小于设置的值
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
       * 缩放模式:等比例缩放至匹配{@link #mHeight}或者匹配{@link #mWidth}
       */
      public static final int MATCH_SIZE   = 5;
      /**
       * 加载原图,使用rgb_565格式
       */
      public static final int SRC_RGB      = 6;
      /**
       * 加载原图,使用ARGB_8888格式
       */
      public static final int SRC_ARGB     = 7;

      /**
       * 配置缩放模式
       */
      @IntDef({ SAMPLE, SAMPLE_MAX, MATCH_WIDTH, MATCH_HEIGHT, MATCH_SIZE, SRC_RGB, SRC_ARGB })
      @Retention(RetentionPolicy.SOURCE)
      public @interface ScaleMode { }
}
