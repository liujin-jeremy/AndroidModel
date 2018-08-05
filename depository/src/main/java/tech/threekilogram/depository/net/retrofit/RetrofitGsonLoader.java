package tech.threekilogram.depository.net.retrofit;

import com.google.gson.Gson;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import okhttp3.ResponseBody;
import retrofit2.Call;
import tech.threekilogram.depository.function.CloseFunction;
import tech.threekilogram.depository.global.GsonClient;
import tech.threekilogram.depository.net.retrofit.service.StreamService;

/**
 * @author: Liujin
 * @version: V1.0
 * @date: 2018-08-02
 * @time: 14:57
 */
@SuppressWarnings("WeakerAccess")
public class RetrofitGsonLoader<V> extends BaseRetrofitLoader<String, V, StreamService> {

      protected Gson mGson = GsonClient.INSTANCE;
      protected Class<V> mValueType;

      public RetrofitGsonLoader (Class<V> valueType) {

            super(StreamService.class);
            mValueType = valueType;
      }

      @Override
      protected String urlFromKey (String key) {

            return key;
      }

      @Override
      protected Call<ResponseBody> configService (
          String key, String url, StreamService service) {

            return service.toGet(url);
      }

      @Override
      protected V onExecuteSuccess (String key, ResponseBody response) throws Exception {

            InputStream inputStream = response.byteStream();
            Reader reader = new InputStreamReader(inputStream);

            V v = mGson.fromJson(reader, mValueType);

            CloseFunction.close(reader);
            CloseFunction.close(inputStream);

            return v;
      }
}
