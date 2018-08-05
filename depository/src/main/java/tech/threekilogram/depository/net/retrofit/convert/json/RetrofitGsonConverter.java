package tech.threekilogram.depository.net.retrofit.convert.json;

import com.google.gson.Gson;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import okhttp3.ResponseBody;
import tech.threekilogram.depository.function.CloseFunction;
import tech.threekilogram.depository.global.GsonClient;
import tech.threekilogram.depository.net.NetConverter;
import tech.threekilogram.depository.net.UrlConverter;

/**
 * 辅助完成转换工作,需要完成{@link K} 到 {@link String} url转换,网络响应{@link ResponseBody} 到{@link V}转换
 *
 * @author liujin
 */
public class RetrofitGsonConverter<K, V> implements NetConverter<K, V, ResponseBody> {

      @SuppressWarnings("WeakerAccess")
      protected Gson mGson = GsonClient.INSTANCE;

      @SuppressWarnings("WeakerAccess")
      protected UrlConverter<K> mUrlConverter;
      @SuppressWarnings("WeakerAccess")
      protected Class<V>        mValueType;

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


