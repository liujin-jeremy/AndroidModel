package tech.threekilogram.depository.net.retrofit;

import java.io.IOException;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import tech.threekilogram.depository.Loader;
import tech.threekilogram.depository.instance.RetrofitClient;
import tech.threekilogram.depository.net.NetConverter;
import tech.threekilogram.depository.net.NetConverter.NetExceptionHandler;

/**
 * 该类是{@link Loader}的retrofit实现版本,用于使用retrofit从网络获取value,需要配置Service才能正常工作
 *
 * @param <V> value 类型
 * @param <S> 用于{@link retrofit2.Retrofit#create(Class)}中的class类型,即:retrofit服务类型
 *
 * @author liujin
 */
@SuppressWarnings("WeakerAccess")
public abstract class BaseRetrofitLoader<V, S> implements Loader<String, V> {

      /**
       * retrofit 客户端
       */
      protected Retrofit mRetrofit = RetrofitClient.INSTANCE;
      /**
       * 完成从{@link ResponseBody} 到value {@link V} 转换
       */
      protected BaseRetrofitConverter<V>    mNetConverter;
      /**
       * retrofit service 类型
       */
      protected Class<S>                    mServiceType;
      /**
       * 异常处理助手
       */
      protected NetExceptionHandler<String> mExceptionHandler;

      /**
       * 最少需要这两个才能正常工作
       *
       * @param serviceType 服务类型
       * @param netConverter 转换器
       */
      protected BaseRetrofitLoader (
          Class<S> serviceType,
          BaseRetrofitConverter<V> netConverter ) {

            mNetConverter = netConverter;
            mServiceType = serviceType;
      }

      public Retrofit getRetrofit ( ) {

            return mRetrofit;
      }

      /**
       * 设置一个新的 {@link Retrofit}
       *
       * @param retrofit 新的{@link Retrofit}
       */
      public void setRetrofit ( Retrofit retrofit ) {

            mRetrofit = retrofit;
      }

      public NetExceptionHandler<String> getExceptionHandler ( ) {

            return mExceptionHandler;
      }

      public void setExceptionHandler (
          NetExceptionHandler<String> exceptionHandler ) {

            mExceptionHandler = exceptionHandler;
      }

      @Override
      public V load ( String key ) {

            /* 1. 获得url */

            java.lang.String urlFromKey = mNetConverter.urlFromKey( key );
            S service = mRetrofit.create( mServiceType );

            /* 2. 制造一个call对象 */

            Call<ResponseBody> call = configService( key, urlFromKey, service );

            /* 3. 执行call */

            try {
                  Response<ResponseBody> response = call.execute();

                  /* 4. 如果成功获得数据 */
                  if( response.isSuccessful() ) {

                        ResponseBody responseBody = response.body();

                        try {

                              /* 5. 转换数据 */
                              return mNetConverter.onExecuteSuccess( key, responseBody );
                        } catch(Exception e) {

                              e.printStackTrace();
                              /* 6. 转换异常 */
                              if( mExceptionHandler != null ) {
                                    mExceptionHandler.onConvertException( key, urlFromKey, e );
                              }
                        }
                  } else {

                        /* 4. 连接到网络,但是没有获取到数据 */
                        mNetConverter.onExecuteFailed( key, response.code(), response.errorBody() );
                  }
            } catch(IOException e) {

                  /* 4. 没有连接到网络 */
                  e.printStackTrace();
                  if( mExceptionHandler != null ) {
                        mExceptionHandler.onConnectException( key, urlFromKey, e );
                  }
            }

            return null;
      }

      /**
       * config retrofit service
       *
       * @param key key
       * @param url url from key at {@link NetConverter#urlFromKey(String)}}
       * @param service service to config
       *
       * @return a call to execute
       */
      protected abstract Call<ResponseBody> configService (
          String key,
          String url,
          S service );
}
