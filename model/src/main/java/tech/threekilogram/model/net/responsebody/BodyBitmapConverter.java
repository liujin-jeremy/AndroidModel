package tech.threekilogram.model.net.responsebody;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.support.v4.util.ArrayMap;
import java.io.IOException;
import java.io.InputStream;
import okhttp3.ResponseBody;
import tech.threekilogram.model.bitmap.BitmapConverter;
import tech.threekilogram.model.function.io.Close;
import tech.threekilogram.model.net.BaseNetLoader.OnErrorListener;

/**
 * 将网络响应转换为string对象
 *
 * @author liujin
 */
public class BodyBitmapConverter implements ResponseBodyConverter<Bitmap>,
                                            OnErrorListener {

      private final ArrayMap<String, Holder> mHolders   = new ArrayMap<>();
      private       BitmapConverter          mConverter = new BitmapConverter();

      public void setBitmapLoadConfig (
          String url, int width, int height, Config config ) {

            Holder value = new Holder( width, height, config );
            synchronized(mHolders) {
                  mHolders.put( url, value );
            }
      }

      @Override
      public Bitmap onExecuteSuccess (
          String url,
          ResponseBody response )
          throws Exception {

            InputStream inputStream = null;
            try {
                  inputStream = response.byteStream();

                  Holder holder = null;
                  synchronized(mHolders) {
                        holder = mHolders.remove( url );
                  }
                  if( holder != null ) {
                        return mConverter.from(
                            inputStream,
                            holder.width,
                            holder.height,
                            holder.config
                        );
                  } else {
                        return BitmapFactory.decodeStream( inputStream );
                  }
            } finally {

                  Close.close( inputStream );
            }
      }

      @Override
      public void onConvertException ( String url, Exception e ) { }

      @Override
      public void onConnectException ( String url, IOException e ) {

            synchronized(mHolders) {
                  mHolders.remove( url );
            }
      }

      @Override
      public void onNullResource ( String url, int httpCode ) {

            synchronized(mHolders) {
                  mHolders.remove( url );
            }
      }

      /**
       * 临时保存图片加载配置
       */
      private class Holder {

            private int    width;
            private int    height;
            private Config config;

            public Holder ( int width, int height, Config config ) {

                  this.width = width;
                  this.height = height;
                  this.config = config;
            }
      }
}
