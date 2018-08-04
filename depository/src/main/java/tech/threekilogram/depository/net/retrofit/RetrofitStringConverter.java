package tech.threekilogram.depository.net.retrofit;

import com.google.gson.Gson;
import java.io.InputStream;
import okhttp3.ResponseBody;
import retrofit2.Call;
import tech.threekilogram.depository.function.CloseFunction;
import tech.threekilogram.depository.global.GsonClient;

/**
 * @author: Liujin
 * @version: V1.0
 * @date: 2018-08-02
 * @time: 14:57
 */
@SuppressWarnings("WeakerAccess")
public class RetrofitStringConverter extends BaseRetrofitConverter<String, String, StreamService> {

      protected Gson mGson = GsonClient.INSTANCE;

      public RetrofitStringConverter () {

            super(StreamService.class);
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
      protected String onExecuteSuccess (String key, ResponseBody response) throws Exception {

            InputStream inputStream = response.byteStream();
            int length = (int) response.contentLength();

            byte[] bytes = new byte[length];
            int read = inputStream.read(bytes);

            CloseFunction.close(inputStream);

            return new String(bytes);
      }
}
