package tech.threekilogram.depository.net.retrofit;

import java.io.IOException;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import tech.threekilogram.depository.global.RetrofitClient;
import tech.threekilogram.depository.net.NetLoaderConverter;

/**
 * @author: Liujin
 * @version: V1.0
 * @date: 2018-08-02
 * @time: 14:13
 */
@SuppressWarnings("WeakerAccess")
public abstract class BaseRetrofitConverter<K, V, S> implements NetLoaderConverter<K, V> {

      protected Retrofit mRetrofit = RetrofitClient.INSTANCE;
      protected Class<S> mServiceType;

      public BaseRetrofitConverter (Class<S> serviceType) {

            mServiceType = serviceType;
      }

      @Override
      public V loadFromNet (K key) {

            String urlFromKey = urlFromKey(key);
            S service = mRetrofit.create(mServiceType);

            Call<ResponseBody> call = configService(key, urlFromKey, service);

            try {
                  Response<ResponseBody> response = call.execute();
                  if(response.isSuccessful()) {

                        ResponseBody responseBody = response.body();

                        try {

                              return onExecuteSuccess(responseBody);
                        } catch(Exception e) {

                              onConvertException(key, urlFromKey, e);
                        }
                  } else {
                        onExecuteFailed(response.code(), response.errorBody());
                  }
            } catch(IOException e) {

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
       * when cant get a correct response {@link Response#isSuccessful()} return failed
       *
       * @param httpCode http code
       * @param errorBody error body
       */
      protected void onExecuteFailed (int httpCode, ResponseBody errorBody) { }

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
      protected abstract V onExecuteSuccess (ResponseBody response) throws Exception;

      /**
       * handle exception from {@link #onExecuteSuccess(ResponseBody)}
       *
       * @param key key
       * @param url url
       * @param e exception
       */
      protected void onConvertException (K key, String url, Exception e) { }
}
