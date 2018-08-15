package tech.threekilogram.depository.net.retrofit.converter;

import android.graphics.Bitmap;
import okhttp3.ResponseBody;
import tech.threekilogram.depository.bitmap.BitmapConverter;
import tech.threekilogram.depository.net.retrofit.BaseRetrofitConverter;

/**
 * 辅助类,适用于使用一个Url作为key的情况
 *
 * @author liujin
 */
public class RetrofitBitmapConverter extends BaseRetrofitConverter<Bitmap> {

      private BitmapConverter mBitmapConverter;

      public RetrofitBitmapConverter (
          BitmapConverter bitmapConverter ) {

            mBitmapConverter = bitmapConverter;
      }

      @Override
      public Bitmap onExecuteSuccess ( String key, ResponseBody response ) throws Exception {

            return mBitmapConverter.read( response.byteStream() );
      }

      @Override
      public void onExecuteFailed ( String key, int httpCode, ResponseBody errorResponse ) {

      }
}
