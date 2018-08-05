package tech.threekilogram.depository.net.retrofit;

import okhttp3.ResponseBody;
import retrofit2.Call;
import tech.threekilogram.depository.net.NetConverter;
import tech.threekilogram.depository.net.retrofit.service.StreamService;

/**
 * @author: Liujin
 * @version: V1.0
 * @date: 2018-08-05
 * @time: 23:09
 */
public class RetrofitStreamLoader<K, V> extends BaseRetrofitLoader<K, V, StreamService> {

      public RetrofitStreamLoader (NetConverter<K, V, ResponseBody> netConverter) {

            super(StreamService.class, netConverter);
      }

      @Override
      protected Call<ResponseBody> configService (
          K key, String url, StreamService service) {

            return service.toGet(url);
      }
}
