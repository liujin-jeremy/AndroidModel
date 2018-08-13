package tech.threekilogram.depository.net.retrofit.stream;

import okhttp3.ResponseBody;
import retrofit2.Call;
import tech.threekilogram.depository.net.retrofit.BaseRetrofitConverter;
import tech.threekilogram.depository.net.retrofit.BaseRetrofitLoader;

/**
 * 使用 retrofit 从网络获取文件,固定使用{@link StreamService}请求
 *
 * @param <V> value type
 *
 * @author liujin
 */
public class RetrofitStreamLoader<V> extends BaseRetrofitLoader<V, StreamService> {

      public RetrofitStreamLoader ( BaseRetrofitConverter<V> netConverter ) {

            super( StreamService.class, netConverter );
      }

      @Override
      protected Call<ResponseBody> configService (
          String key, String url, StreamService service ) {

            return service.toGet( url );
      }
}