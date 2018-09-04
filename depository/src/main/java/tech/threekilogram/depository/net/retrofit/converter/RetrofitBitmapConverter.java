package tech.threekilogram.depository.net.retrofit.converter;

import static tech.threekilogram.depository.bitmap.BitmapConverter.MATCH_SIZE;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import java.io.File;
import java.io.IOException;
import okhttp3.ResponseBody;
import tech.threekilogram.depository.bitmap.BitmapConverter;
import tech.threekilogram.depository.bitmap.BitmapConverter.ScaleMode;
import tech.threekilogram.depository.net.retrofit.BaseRetrofitConverter;

/**
 * 辅助类,适用于使用一个Url作为key的情况
 *
 * @author liujin
 */
public class RetrofitBitmapConverter extends BaseRetrofitConverter<Bitmap> {

      /**
       * 下载到一个缓存文件夹
       */
      private RetrofitDownConverter mDownConverter;
      /**
       * 转换为bitmap
       */
      private BitmapConverter       mBitmapConverter;

      /**
       * @param file cache dir
       */
      public RetrofitBitmapConverter ( File file ) {

            mBitmapConverter = new BitmapConverter();
            mDownConverter = new RetrofitDownConverter( file );
      }

      /**
       * @param file cache dir
       */
      public RetrofitBitmapConverter ( File file, long maxSize ) throws IOException {

            mBitmapConverter = new BitmapConverter();
            mDownConverter = new RetrofitDownConverter( file, maxSize );
      }

      /**
       * 配置bitmap加载配置,将会影响下一次加载,直到有新的配置发生
       *
       * @param width 需求宽度
       * @param height 需求高度
       */
      public void configBitmap ( int width, int height ) {

            configBitmap( width, height, MATCH_SIZE, Config.RGB_565 );
      }

      /**
       * 配置bitmap加载配置,将会影响下一次加载,直到有新的配置发生
       *
       * @param width 需求宽度
       * @param height 需求高度
       * @param scaleMode 缩放方式
       */
      public void configBitmap ( int width, int height, @ScaleMode int scaleMode ) {

            configBitmap( width, height, scaleMode, Config.RGB_565 );
      }

      /**
       * 配置bitmap加载配置,将会影响下一次加载,直到有新的配置发生
       *
       * @param width 需求宽度
       * @param height 需求高度
       * @param scaleMode 缩放方式
       * @param config bitmap 像素格式
       */
      public void configBitmap ( int width, int height, @ScaleMode int scaleMode, Config config ) {

            mBitmapConverter.setWidth( width );
            mBitmapConverter.setHeight( height );
            mBitmapConverter.setMode( scaleMode );
            mBitmapConverter.setBitmapConfig( config );
      }

      @Override
      public Bitmap onExecuteSuccess ( String key, ResponseBody response ) throws Exception {

            /* save then from */
            File file = mDownConverter.onExecuteSuccess( key, response );
            return mBitmapConverter.read( file );
      }
}
