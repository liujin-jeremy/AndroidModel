package tech.threekilogram.depository.net.retrofit;

import java.io.IOException;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import tech.threekilogram.depository.global.RetrofitClient;
import tech.threekilogram.depository.net.NetLoaderConverter;

/**
 * 该类是{@link NetLoaderConverter}的retrofit实现版本,用于和{@link tech.threekilogram.depository.net.NetLoader}配合
 *
 * @param <K> key 类型
 * @param <V> value 类型
 * @param <S> 用于{@link retrofit2.Retrofit#create(Class)}中的class类型,即:retrofit服务类型
 *
 * @author liujin
 */
@SuppressWarnings("WeakerAccess")
public abstract class BaseRetrofitConverter<K, V, S> implements NetLoaderConverter<K, V> {

      protected Retrofit mRetrofit = RetrofitClient.INSTANCE;
      protected Class<S> mServiceType;

      public BaseRetrofitConverter (Class<S> serviceType) {

            mServiceType = serviceType;
      }

      /**
       * 设置一个新的 {@link Retrofit}
       *
       * @param retrofit 新的{@link Retrofit}
       */
      public void setRetrofit (Retrofit retrofit) {

            mRetrofit = retrofit;
      }

      @Override
      public V loadFromNet (K key) {

            /* 1. 获得url */

            String urlFromKey = urlFromKey(key);
            S service = mRetrofit.create(mServiceType);

            /* 2. 制造一个call对象 */

            Call<ResponseBody> call = configService(key, urlFromKey, service);

            /* 3. 执行call */

            try {
                  Response<ResponseBody> response = call.execute();
                  if(response.isSuccessful()) {

                        /* 4. 如果成功获得数据 */

                        ResponseBody responseBody = response.body();

                        try {

                              /* 5. 转换数据 */
                              return onExecuteSuccess(key, responseBody);
                        } catch(Exception e) {

                              e.printStackTrace();
                              /* 6. 转换异常 */
                              onConvertException(key, urlFromKey, e);
                        }
                  } else {

                        /* 4. 连接到网络,但是没有获取到数据 */

                        onExecuteFailed(key, response.code(), response.errorBody());
                  }
            } catch(IOException e) {

                  /* 4. 没有连接到网络 */
                  e.printStackTrace();
                  onConnectException(key, urlFromKey, e);
            }

            return null;
      }

      /**
       * 从一个key返回一个Url
       *
       * @param key key
       *
       * @return url to get value
       */
      protected abstract String urlFromKey (K key);

      /**
       * config retrofit service
       *
       * @param key key
       * @param url url from key at {@link #urlFromKey(Object)}
       * @param service service to config
       *
       * @return a call to execute
       */
      protected abstract Call<ResponseBody> configService (K key, String url, S service);

      /**
       * when {@link Call} from {@link #configService(Object, String, Object)} run {@link
       * Call#execute()} may
       * occur a {@link IOException} , handle exception there
       *
       * @param key key
       * @param url url
       * @param e exception
       */
      protected void onConnectException (K key, String url, IOException e) { }

      /**
       * get a success response then convert it to value
       *
       * @param response response
       *
       * @return value
       *
       * @throws Exception when convert  {@link ResponseBody} to {@link V} may occur a exception
       *                   {@see #onConvertException(Object, String, Exception)}
       */
      protected abstract V onExecuteSuccess (K key, ResponseBody response) throws Exception;

      /**
       * handle exception from {@link #onExecuteSuccess(K, ResponseBody)}
       *
       * @param key key
       * @param url url
       * @param e exception
       */
      protected void onConvertException (K key, String url, Exception e) { }

      /**
       * when cant get a correct response {@link Response#isSuccessful()} return failed
       *
       * @param httpCode http code
       * @param errorBody error body
       */
      protected void onExecuteFailed (K key, int httpCode, ResponseBody errorBody) { }
}
