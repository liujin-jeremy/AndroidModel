package tech.threekilogram.model.net;

import java.io.IOException;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
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
public class RetrofitLoader<V> extends BaseNetLoader<V> {

      /**
       * retrofit
       */
      protected Retrofit      mRetrofit = NetClient.RETROFIT;
      /**
       * service
       */
      protected StreamService mService;

      public RetrofitLoader ( StreamConverter<V> netConverter ) {

            super( netConverter );
      }

      public Retrofit getRetrofit ( ) {

            return mRetrofit;
      }

      public void setRetrofit ( Retrofit retrofit ) {

            mRetrofit = retrofit;
      }

      @Override
      public V load ( String url ) {


            /* 制造一个call对象 */
            if( mService == null ) {

                  mService = mRetrofit.create( StreamService.class );
            }
            Call<ResponseBody> call = mService.toGet( url );

            /* 执行call */
            try {
                  Response<ResponseBody> response = call.execute();

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