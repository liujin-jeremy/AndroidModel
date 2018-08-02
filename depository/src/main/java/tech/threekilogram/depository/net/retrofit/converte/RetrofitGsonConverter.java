package tech.threekilogram.depository.net.retrofit.converte;

import android.support.annotation.NonNull;
import com.google.gson.Gson;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import tech.threekilogram.depository.global.GsonClient;

/**
 * @author: Liujin
 * @version: V1.0
 * @date: 2018-08-02
 * @time: 8:44
 */
public class RetrofitGsonConverter<K, V> implements RetrofitConverter<K, V> {

      private Gson mGson = GsonClient.INSTANCE;
      private Class<V> mValueType;

      public RetrofitGsonConverter (@NonNull Class<V> valueType) {

            mValueType = valueType;
      }

      @Override
      public V onResponse (K key, long contentLength, InputStream stream) throws Exception {

            Reader reader = new InputStreamReader(stream);
            return mGson.fromJson(reader, mValueType);
      }
}
