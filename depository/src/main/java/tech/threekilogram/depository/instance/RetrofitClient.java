package tech.threekilogram.depository.instance;

import java.io.File;
import okhttp3.Cache;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;

/**
 * @author: Liujin
 * @version: V1.0
 * @date: 2018-07-31
 * @time: 10:26
 */
public class RetrofitClient {

      public static final Retrofit INSTANCE = new Retrofit
          .Builder()
          .baseUrl( "https://github.com/" )
          .build();

      public static Retrofit instance ( File file, long maxSize ) {

            Cache cache = new Cache( file, maxSize );
            OkHttpClient client = new OkHttpClient.Builder().cache( cache ).build();

            return new Retrofit
                .Builder()
                .client( client )
                .baseUrl( "https://github.com/" )
                .build();
      }
}
