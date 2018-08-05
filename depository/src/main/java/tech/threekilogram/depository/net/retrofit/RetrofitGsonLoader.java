package tech.threekilogram.depository.net.retrofit;

import com.google.gson.Gson;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import okhttp3.ResponseBody;
import retrofit2.Call;
import tech.threekilogram.depository.function.CloseFunction;
import tech.threekilogram.depository.global.GsonClient;
import tech.threekilogram.depository.net.NetConverter;
import tech.threekilogram.depository.net.UrlConverter;
import tech.threekilogram.depository.net.retrofit.service.StreamService;

/**
 * @author: Liujin
 * @version: V1.0
 * @date: 2018-08-02
 * @time: 14:57
 */
@SuppressWarnings("WeakerAccess")
public class RetrofitGsonLoader<K, V> extends BaseRetrofitLoader<K, V, StreamService> {

      public RetrofitGsonLoader (UrlConverter<K> urlConverter, Class<V> valueType) {

            mNetConverter = new RetrofitGsonConverter<>(urlConverter, valueType);
            mServiceType = StreamService.class;
      }

      public RetrofitGsonLoader (RetrofitGsonConverter<K, V> converter) {

            mNetConverter = converter;
            mServiceType = StreamService.class;
      }

      @Override
      protected Call<ResponseBody> configService (
          K key, String url, StreamService service) {

            return service.toGet(url);
      }

      // ========================= 内部类 =========================

      public static class RetrofitGsonConverter<K, V> implements NetConverter<K, V, ResponseBody> {

            protected Gson mGson = GsonClient.INSTANCE;
            protected Class<V>        mValueType;
            protected UrlConverter<K> mUrlConverter;

            public RetrofitGsonConverter (UrlConverter<K> urlConverter, Class<V> valueType) {

                  mValueType = valueType;
                  mUrlConverter = urlConverter;
            }

            @Override
            public String urlFromKey (K key) {

                  return mUrlConverter.urlFromKey(key);
            }

            @Override
            public V onExecuteSuccess (
                K key, ResponseBody response) throws Exception {

                  InputStream inputStream = null;
                  Reader reader = null;
                  V v = null;

                  try {
                        inputStream = response.byteStream();
                        reader = new InputStreamReader(inputStream);

                        v = mGson.fromJson(reader, mValueType);
                  } finally {

                        CloseFunction.close(reader);
                        CloseFunction.close(inputStream);
                  }

                  return v;
            }

            @Override
            public void onExecuteFailed (K key, int httpCode, ResponseBody errorResponse) {

            }
      }
}
