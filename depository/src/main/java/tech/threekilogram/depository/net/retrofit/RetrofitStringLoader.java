package tech.threekilogram.depository.net.retrofit;

import com.google.gson.Gson;
import java.io.InputStream;
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
public class RetrofitStringLoader<K> extends BaseRetrofitLoader<K, String, StreamService> {

      protected Gson mGson = GsonClient.INSTANCE;

      public RetrofitStringLoader (UrlConverter<K> urlConverter) {

            mNetConverter = new RetrofitStringConverter<>(urlConverter);
            mServiceType = StreamService.class;
      }

      public RetrofitStringLoader (NetConverter<K, String, ResponseBody> converter) {

            mNetConverter = converter;
            mServiceType = StreamService.class;
      }

      @Override
      protected Call<ResponseBody> configService (
          K key, String url, StreamService service) {

            return service.toGet(url);
      }

      // ========================= 内部类 =========================

      public static class RetrofitStringConverter<K> implements
                                                     NetConverter<K, String, ResponseBody> {

            private UrlConverter<K> mUrlConverter;

            public RetrofitStringConverter (UrlConverter<K> urlConverter) {

                  mUrlConverter = urlConverter;
            }

            @Override
            public String onExecuteSuccess (K key, ResponseBody response) throws Exception {

                  InputStream inputStream = null;
                  byte[] bytes;
                  try {
                        inputStream = response.byteStream();
                        int length = (int) response.contentLength();

                        bytes = new byte[length];
                        int read = inputStream.read(bytes);
                  } finally {

                        CloseFunction.close(inputStream);
                  }

                  return new String(bytes);
            }

            @Override
            public void onExecuteFailed (K key, int httpCode, ResponseBody errorResponse) {

            }

            @Override
            public String urlFromKey (K key) {

                  return mUrlConverter.urlFromKey(key);
            }
      }
}
