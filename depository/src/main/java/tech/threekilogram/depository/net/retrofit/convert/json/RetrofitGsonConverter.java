package tech.threekilogram.depository.net.retrofit.convert.json;

/**
 * 辅助类,适用于使用一个Url作为key的情况
 *
 * @author liujin
 */
public class RetrofitGsonConverter<V> extends
                                      BaseRetrofitGsonConverter<String, V> {

      public RetrofitGsonConverter ( Class<V> valueType ) {

            super( valueType );
      }

      @Override
      public String urlFromKey ( String key ) {

            return key;
      }
}
