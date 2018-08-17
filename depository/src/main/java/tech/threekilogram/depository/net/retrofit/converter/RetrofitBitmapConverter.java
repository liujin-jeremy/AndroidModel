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

      /**
       * 下载到一个缓存文件夹
       */
      private RetrofitDownConverter mDownConverter;
      /**
       * 转换为bitmap
       */
      private BitmapConverter       mBitmapConverter;

      /**
       * @param bitmapConverter 转换stream为bitmap
       * @param file cache dir
       */
      public RetrofitBitmapConverter ( BitmapConverter bitmapConverter, File file ) {

            mBitmapConverter = bitmapConverter;
            mDownConverter = new RetrofitDownConverter( file );
      }

      @Override
      public Bitmap onExecuteSuccess ( String key, ResponseBody response ) throws Exception {

            /* save then read */
            File file = mDownConverter.onExecuteSuccess( key, response );
            return mBitmapConverter.read( file );
      }
}
