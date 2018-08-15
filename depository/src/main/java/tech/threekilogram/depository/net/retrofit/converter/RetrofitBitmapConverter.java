package tech.threekilogram.depository.net.retrofit.converter;

import android.graphics.Bitmap;
import java.io.File;
import okhttp3.ResponseBody;
import tech.threekilogram.depository.bitmap.BitmapConverter;
import tech.threekilogram.depository.net.retrofit.BaseRetrofitConverter;

/**
 * 辅助类,适用于使用一个Url作为key的情况
 *
 * @author liujin
 */
public class RetrofitBitmapConverter extends BaseRetrofitConverter<Bitmap> {

      private static final String TAG = RetrofitBitmapConverter.class.getSimpleName();
      private RetrofitDownConverter mDownConverter;
      private BitmapConverter       mBitmapConverter;

      public RetrofitBitmapConverter (
          BitmapConverter bitmapConverter, File file ) {

            mBitmapConverter = bitmapConverter;
            mDownConverter = new RetrofitDownConverter( file );
      }

      @Override
      public Bitmap onExecuteSuccess ( String key, ResponseBody response ) throws Exception {

            File file = mDownConverter.onExecuteSuccess( key, response );
            return mBitmapConverter.read( file );
      }

      @Override
      public void onExecuteFailed ( String key, int httpCode, ResponseBody errorResponse ) {

      }
}
