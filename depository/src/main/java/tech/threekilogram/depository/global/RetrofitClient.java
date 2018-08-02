package tech.threekilogram.depository.global;

import retrofit2.Retrofit;
import tech.threekilogram.depository.net.UrlConverter;
import tech.threekilogram.depository.net.retrofit.service.GetService;

/**
 * @author: Liujin
 * @version: V1.0
 * @date: 2018-07-31
 * @time: 10:26
 */
public class RetrofitClient {

      /**
       * this baseUrl means nothing, because real urlPath is from {@link UrlConverter}
       * to {@link GetService}'s params
       */
      public static Retrofit INSTANCE = new Retrofit
          .Builder()
          .baseUrl("https://github.com/")
          .build();
}
