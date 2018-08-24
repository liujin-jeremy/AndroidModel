package tech.threekilogram.depository.net.retrofit.loader;

import android.content.Context;
import java.io.File;
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
public class RetrofitLoader<V> extends BaseRetrofitLoader<V, StreamService> {

      /**
       * {@link BaseRetrofitLoader#BaseRetrofitLoader(Context, Class, BaseRetrofitConverter)}
       */
      public RetrofitLoader ( Context context, BaseRetrofitConverter<V> netConverter ) {

            super( context, StreamService.class, netConverter );
      }

      /**
       * {@link BaseRetrofitLoader#BaseRetrofitLoader(File, long, Class, BaseRetrofitConverter)}
       */
      public RetrofitLoader (
          File cache,
          long maxFileSize,
          BaseRetrofitConverter<V> netConverter ) {

            super( cache, maxFileSize, StreamService.class, netConverter );
      }

      @Override
      protected Call<ResponseBody> configService (
          String url,
          StreamService service ) {

            return service.toGet( url );
      }
}