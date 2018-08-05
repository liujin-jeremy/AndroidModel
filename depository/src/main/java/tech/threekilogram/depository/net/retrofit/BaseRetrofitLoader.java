package tech.threekilogram.depository.net.retrofit;

import java.io.IOException;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import tech.threekilogram.depository.Loader;
import tech.threekilogram.depository.global.RetrofitClient;
import tech.threekilogram.depository.net.NetConverter;
import tech.threekilogram.depository.net.NetConverter.NetExceptionHandler;

/**
 * 该类是{@link Loader}的retrofit实现版本,用于和{@link Loader}配合
 *
 * @param <K> key 类型
 * @param <V> value 类型
 * @param <S> 用于{@link retrofit2.Retrofit#create(Class)}中的class类型,即:retrofit服务类型
 *
 * @author liujin
 */
@SuppressWarnings("WeakerAccess")
public abstract class BaseRetrofitLoader<K, V, S> implements Loader<K, V> {

      /**
       * retrofit 客户端
       */
      protected Retrofit mRetrofit = RetrofitClient.INSTANCE;

      /**
       * 完成从 {@link K} 到 {@link V} 转换
       */
      protected NetConverter<K, V, ResponseBody> mNetConverter;
      /**
       * retrofit service 类型
       */
      protected Class<S>                         mServiceType;
      /**
       * 异常处理助手
       */
      protected NetExceptionHandler<K>           mExceptionHandler;

      /**
       * 设置一个新的 {@link Retrofit}
       *
       * @param retrofit 新的{@link Retrofit}
       */
      public void setRetrofit (Retrofit retrofit) {

            mRetrofit = retrofit;
      }

      public Retrofit getRetrofit () {

            return mRetrofit;
      }

      public NetExceptionHandler<K> getExceptionHandler () {

            return mExceptionHandler;
      }

      public void setExceptionHandler (
          NetExceptionHandler<K> exceptionHandler) {

            mExceptionHandler = exceptionHandler;
      }

      @Override
      public V load (K key) {

            /* 1. 获得url */

            String urlFromKey = mNetConverter.urlFromKey(key);
            S service = mRetrofit.create(mServiceType);

            /* 2. 制造一个call对象 */

            Call<ResponseBody> call = configService(key, urlFromKey, service);

            /* 3. 执行call */

            try {
                  Response<ResponseBody> response = call.execute();

                  /* 4. 如果成功获得数据 */
                  if(response.isSuccessful()) {

                        ResponseBody responseBody = response.body();

                        try {

                              /* 5. 转换数据 */
                              return mNetConverter.onExecuteSuccess(key, responseBody);
                        } catch(Exception e) {

                              e.printStackTrace();
                              /* 6. 转换异常 */
                              if(mExceptionHandler != null) {
                                    mExceptionHandler.onConvertException(key, urlFromKey, e);
                              }
                        }
                  } else {

                        /* 4. 连接到网络,但是没有获取到数据 */
                        mNetConverter.onExecuteFailed(key, response.code(), response.errorBody());
                  }
            } catch(IOException e) {

                  /* 4. 没有连接到网络 */
                  e.printStackTrace();
                  if(mExceptionHandler != null) {
                        mExceptionHandler.onConnectException(key, urlFromKey, e);
                  }
            }

            return null;
      }

      /**
       * config retrofit service
       *
       * @param key key
       * @param url url from key at {@link NetConverter#urlFromKey(Object)}}
       * @param service service to config
       *
       * @return a call to execute
       */
      protected abstract Call<ResponseBody> configService (K key, String url, S service);
}
