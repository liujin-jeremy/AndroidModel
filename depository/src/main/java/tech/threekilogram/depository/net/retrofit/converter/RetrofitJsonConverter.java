package tech.threekilogram.depository.net.retrofit.converter;

import okhttp3.ResponseBody;
import tech.threekilogram.depository.cache.json.GsonConverter;
import tech.threekilogram.depository.cache.json.JsonConverter;
import tech.threekilogram.depository.net.retrofit.BaseRetrofitConverter;

/**
 * 辅助类,适用于使用一个Url作为key的情况
 *
 * @author liujin
 */
public class RetrofitJsonConverter<V> extends BaseRetrofitConverter<V> {

      @SuppressWarnings("WeakerAccess")
      protected JsonConverter<V> mConverter;

      public RetrofitJsonConverter ( Class<V> valueType ) {

            mConverter = new GsonConverter<>( valueType );
      }

      public RetrofitJsonConverter ( JsonConverter<V> converter ) {

            mConverter = converter;
      }

      @Override
      public V onExecuteSuccess ( String key, ResponseBody response ) throws Exception {

            return mConverter.from( response.byteStream() );
      }
}
