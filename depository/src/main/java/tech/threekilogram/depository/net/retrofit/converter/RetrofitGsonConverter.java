package tech.threekilogram.depository.net.retrofit.converter;

import okhttp3.ResponseBody;
import tech.threekilogram.depository.json.GsonConverter;
import tech.threekilogram.depository.net.retrofit.BaseRetrofitConverter;

/**
 * 辅助类,适用于使用一个Url作为key的情况
 *
 * @author liujin
 */
public class RetrofitGsonConverter<V> extends BaseRetrofitConverter<V> {

      @SuppressWarnings("WeakerAccess")
      protected GsonConverter<V> mGsonConverter;

      public RetrofitGsonConverter ( Class<V> valueType ) {

            mGsonConverter = new GsonConverter<>( valueType );
      }

      @Override
      public V onExecuteSuccess ( String key, ResponseBody response ) throws Exception {

            return mGsonConverter.from( response.byteStream() );
      }
}
