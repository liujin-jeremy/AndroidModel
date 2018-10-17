package tech.threekilogram.depository.net.okhttp;

import java.io.IOException;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import tech.threekilogram.depository.function.instance.NetClient;
import tech.threekilogram.depository.net.BaseNetLoader;
import tech.threekilogram.depository.net.responsebody.ResponseBodyConverter;
import tech.threekilogram.depository.net.retrofit.loader.StreamService;

/**
 * 使用 retrofit 从网络使用get方法获取{@link ResponseBody}响应,然后使用{@link ResponseBodyConverter}完成转换工作
 * <p>
 * 使用的service为{@link StreamService}
 *
 * @param <V> value type
 *
 * @author liujin
 */
public class OkhttpLoader<V> extends BaseNetLoader<V, ResponseBody> {

      /**
       * retrofit
       */
      protected OkHttpClient mOkHttpClient = NetClient.OKHTTP;

      /**
       * 构建一个加载器
       *
       * @param netConverter 辅助完成网络响应到值的转换
       */
      protected OkhttpLoader ( ResponseBodyConverter<V> netConverter ) {

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
                              return mNetConverter.onExecuteSuccess( url, responseBody );
                        } catch(Exception e) {

                              e.printStackTrace();
                              /* 转换异常 */
                              if( mOnNetExceptionListener != null ) {
                                    mOnNetExceptionListener.onConvertException( url, e );
                              }
                        }
                  } else {

                        /* 连接到网络,但是没有获取到数据 */
                        if( mOnNoResourceListener != null ) {
                              mOnNoResourceListener.onExecuteFailed( url, response.code() );
                        }
                  }
            } catch(IOException e) {

                  /* 没有连接到网络 */
                  e.printStackTrace();
                  if( mOnNetExceptionListener != null ) {
                        mOnNetExceptionListener.onConnectException( url, e );
                  }
            }

            return null;
      }
}