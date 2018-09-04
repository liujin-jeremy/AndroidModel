package tech.threekilogram.depository.net.retrofit.loader;

import java.io.IOException;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import tech.threekilogram.depository.function.instance.RetrofitClient;
import tech.threekilogram.depository.net.BaseNetLoader;
import tech.threekilogram.depository.net.retrofit.converter.ResponseBodyConverter;

/**
 * 使用 retrofit 从网络使用get方法获取{@link ResponseBody}响应,然后使用{@link ResponseBodyConverter}完成转换工作
 * <p>
 * 使用的service为{@link StreamService}
 *
 * @param <V> value type
 *
 * @author liujin
 */
public class RetrofitLoader<V> extends BaseNetLoader<V, ResponseBody> {

      /**
       * retrofit
       */
      protected Retrofit      mRetrofit;
      /**
       * service
       */
      protected StreamService mService;

      public RetrofitLoader ( ResponseBodyConverter<V> netConverter ) {

            this( RetrofitClient.INSTANCE, netConverter );
      }

      public RetrofitLoader ( Retrofit retrofit, ResponseBodyConverter<V> netConverter ) {

            super( netConverter );
            mRetrofit = retrofit;
      }

      public Retrofit getRetrofit ( ) {

            return mRetrofit;
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