package tech.threekilogram.depository.global;

import retrofit2.Retrofit;

/**
 * @author: Liujin
 * @version: V1.0
 * @date: 2018-07-31
 * @time: 10:26
 */
public class RetrofitClient {

      /**
       * this baseUrl means nothing, all service use {@link retrofit2.http.Url} params to get url
       * path
       */
      public static Retrofit INSTANCE = new Retrofit
          .Builder()
          .baseUrl("https://github.com/")
          .build();
}
