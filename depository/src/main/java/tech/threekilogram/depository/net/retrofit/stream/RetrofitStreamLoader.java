package tech.threekilogram.depository.net.retrofit.stream;

import okhttp3.ResponseBody;
import retrofit2.Call;
import tech.threekilogram.depository.net.retrofit.BaseRetrofitLoader;
import tech.threekilogram.depository.net.retrofit.RetrofitConverter;

/**
 * 使用 retrofit 从网络获取文件
 *
 * @param <K> key type
 * @param <V> value type
 *
 * @author liujin
 */
public class RetrofitStreamLoader<K, V> extends BaseRetrofitLoader<K, V, StreamService> {

      public RetrofitStreamLoader ( RetrofitConverter<K, V> netConverter ) {

            super( StreamService.class, netConverter );
      }

      @Override
      protected Call<ResponseBody> configService (
          K key, String url, StreamService service ) {

            return service.toGet( url );
      }
}