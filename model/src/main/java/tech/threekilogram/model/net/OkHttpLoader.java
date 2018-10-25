package tech.threekilogram.model.net;

import java.io.IOException;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import tech.threekilogram.model.converter.StreamConverter;
import tech.threekilogram.model.util.instance.NetClient;

/**
 * 使用 retrofit 从网络使用get方法获取{@link ResponseBody}响应,然后使用{@link StreamConverter}完成转换工作
 * <p>
 * 使用的service为{@link StreamService}
 *
 * @param <V> value type
 *
 * @author liujin
 */
public class OkHttpLoader<V> extends BaseNetLoader<V> {

      /**
       * retrofit
       */
      protected OkHttpClient mOkHttpClient = NetClient.OKHTTP;

      /**
       * 构建一个加载器
       *
       * @param netConverter 辅助完成网络响应到值的转换
       */
      public OkHttpLoader ( StreamConverter<V> netConverter ) {

            super( netConverter );
      }

      public OkHttpClient getOkHttpClient ( ) {

            return mOkHttpClient;
      }

      public void setOkHttpClient ( OkHttpClient okHttpClient ) {

            mOkHttpClient = okHttpClient;
      }

      @Override
      public V load ( String url ) {

            /* 执行call */
            try {
                  Request request = new Request.Builder()
                      .url( url )
                      .build();
                  Response response = mOkHttpClient.newCall( request ).execute();

                  /* 如果成功获得数据 */
                  if( response.isSuccessful() ) {

                        ResponseBody responseBody = response.body();

                        try {

                              /* 转换数据 */
                              assert responseBody != null;
                              return mNetConverter.from( responseBody.byteStream() );
                        } catch(Exception e) {

                              e.printStackTrace();
                              /* 转换异常 */
                              if( mErrorListener != null ) {
                                    mErrorListener.onConvertException( url, e );
                              }
                        }
                  } else {

                        /* 连接到网络,但是没有获取到数据 */
                        if( mErrorListener != null ) {
                              mErrorListener.onNullResource( url, response.code() );
                        }
                  }
            } catch(IOException e) {

                  /* 没有连接到网络 */
                  e.printStackTrace();
                  if( mErrorListener != null ) {
                        mErrorListener.onConnectException( url, e );
                  }
            }

            return null;
      }
}