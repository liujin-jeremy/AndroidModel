package tech.threekilogram.depository.file.converter;

import static tech.threekilogram.depository.bitmap.BitmapConverter.MATCH_SIZE;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import tech.threekilogram.depository.bitmap.BitmapConverter;
import tech.threekilogram.depository.bitmap.BitmapConverter.ScaleMode;
import tech.threekilogram.depository.file.BaseFileConverter;
import tech.threekilogram.depository.file.BaseFileLoader;

/**
 * 用于{@link File}和{@link Bitmap}相互转换
 *
 * @author: Liujin
 * @version: V1.0
 * @date: 2018-08-15
 * @time: 18:22
 */
public class FileBitmapConverter extends BaseFileConverter<Bitmap> {

      /**
       * 转换stream为指定格式的bitmap
       */
      private BitmapConverter        mBitmapConverter;
      private BaseFileLoader<Bitmap> mLoader;

      public FileBitmapConverter ( ) {

            mBitmapConverter = new BitmapConverter();
      }

      /**
       * 配置bitmap加载配置
       *
       * @param width 需求宽度
       * @param height 需求高度
       */
      public void configBitmap ( BaseFileLoader<Bitmap> loader, int width, int height ) {

            configBitmap( loader, width, height, MATCH_SIZE, Config.RGB_565 );
      }

      /**
       * 配置bitmap加载配置
       *
       * @param width 需求宽度
       * @param height 需求高度
       * @param scaleMode 缩放方式
       */
      public void configBitmap (
          BaseFileLoader<Bitmap> loader, int width, int height, @ScaleMode int scaleMode ) {

            configBitmap( loader, width, height, scaleMode, Config.RGB_565 );
      }

      /**
       * 配置bitmap加载配置
       *
       * @param width 需求宽度
       * @param height 需求高度
       * @param scaleMode 缩放方式
       * @param config bitmap 像素格式
       */
      public void configBitmap (
          BaseFileLoader<Bitmap> loader, int width, int height, @ScaleMode int scaleMode,
          Config config ) {

            mBitmapConverter.setWidth( width );
            mBitmapConverter.setHeight( height );
            mBitmapConverter.setMode( scaleMode );
            mBitmapConverter.setBitmapConfig( config );
            mLoader = loader;
      }

      @Override
      public Bitmap toValue (
          String key,
          InputStream stream ) throws Exception {

            return mBitmapConverter.read( getFile( key ) );
      }

      private File getFile ( String key ) {

            return mLoader.getFile( key );
      }

      @Override
      public void saveValue (
          String key,
          OutputStream outputStream,
          Bitmap value ) throws IOException {

            mBitmapConverter.write( outputStream, value );
      }
}
