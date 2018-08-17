package tech.threekilogram.depository.net.retrofit.converter;

import com.google.gson.Gson;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import okhttp3.ResponseBody;
import tech.threekilogram.depository.function.Close;
import tech.threekilogram.depository.instance.GsonClient;
import tech.threekilogram.depository.net.retrofit.BaseRetrofitConverter;

/**
 * 辅助类,适用于使用一个Url作为key的情况
 *
 * @author liujin
 */
public class RetrofitGsonConverter<V> extends BaseRetrofitConverter<V> {

      @SuppressWarnings("WeakerAccess")
      protected Gson mGson = GsonClient.INSTANCE;

      @SuppressWarnings("WeakerAccess")
      protected Class<V> mValueType;

      public RetrofitGsonConverter ( Class<V> valueType ) {

            mValueType = valueType;
      }

      @Override
      public V onExecuteSuccess ( String key, ResponseBody response ) throws Exception {

            InputStream inputStream = null;
            Reader reader = null;
            V v = null;

            try {
                  inputStream = response.byteStream();
                  reader = new InputStreamReader( inputStream );

                  v = mGson.fromJson( reader, mValueType );
            } finally {

                  Close.close( reader );
                  Close.close( inputStream );
            }

            return v;
      }
}
