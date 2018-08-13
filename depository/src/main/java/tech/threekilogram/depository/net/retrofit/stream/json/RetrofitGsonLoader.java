package tech.threekilogram.depository.net.retrofit.stream.json;

import tech.threekilogram.depository.net.retrofit.stream.RetrofitStreamLoader;

/**
 * 从网络读取json对象
 *
 * @author: Liujin
 * @version: V1.0
 * @date: 2018-08-13
 * @time: 18:19
 */
public class RetrofitGsonLoader<V> extends RetrofitStreamLoader<V> {

      public RetrofitGsonLoader ( Class<V> valueType ) {

            super( new RetrofitGsonConverter<V>( valueType ) );
      }
}
