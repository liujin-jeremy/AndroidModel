package tech.threekilogram.depository.net.retrofit.converter;

import okhttp3.ResponseBody;
import tech.threekilogram.depository.cache.json.GsonConverter;
import tech.threekilogram.depository.cache.json.JsonConverter;

/**
 * 将网络响应转换为json bean对象
 *
 * @author liujin
 */
public class RetrofitJsonConverter<V> implements ResponseBodyConverter<V> {

      @SuppressWarnings("WeakerAccess")
      protected JsonConverter<V> mConverter;

      public RetrofitJsonConverter ( Class<V> valueType ) {

            mConverter = new GsonConverter<>( valueType );
      }

      public RetrofitJsonConverter ( JsonConverter<V> converter ) {

            mConverter = converter;
      }

      @Override
      public V onExecuteSuccess (
          String url, ResponseBody response )
          throws Exception {

            return mConverter.from( response.byteStream() );
      }
}