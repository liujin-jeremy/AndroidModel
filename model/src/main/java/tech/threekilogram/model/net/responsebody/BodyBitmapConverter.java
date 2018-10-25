package tech.threekilogram.model.net.responsebody;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import java.io.IOException;
import java.io.InputStream;
import okhttp3.ResponseBody;
import tech.threekilogram.model.function.io.Close;
import tech.threekilogram.model.net.BaseNetLoader.OnErrorListener;

/**
 * 将网络响应转换为string对象
 *
 * @author liujin
 */
public class BodyBitmapConverter implements ResponseBodyConverter<Bitmap>,
                                            OnErrorListener {

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

      @Override
      public void onConvertException ( String url, Exception e ) {

      }

      @Override
      public void onConnectException ( String url, IOException e ) {

      }

      @Override
      public void onNullResource ( String url, int httpCode ) {

      }
}
