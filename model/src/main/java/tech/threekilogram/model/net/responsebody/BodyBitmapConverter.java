package tech.threekilogram.model.net.responsebody;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import java.io.InputStream;
import okhttp3.ResponseBody;
import tech.threekilogram.model.function.io.Close;

/**
 * 将网络响应转换为string对象
 *
 * @author liujin
 */
public class BodyBitmapConverter implements ResponseBodyConverter<Bitmap> {

      @Override
      public Bitmap onExecuteSuccess (
          String url, ResponseBody response )
          throws Exception {

            InputStream inputStream = null;
            try {

                  inputStream = response.byteStream();
                  return BitmapFactory.decodeStream( inputStream );
            } finally {

                  Close.close( inputStream );
            }
      }
}
