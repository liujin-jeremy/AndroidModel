package tech.threekilogram.depository.instance;

import com.google.gson.Gson;

/**
 * @author: Liujin
 * @version: V1.0
 * @date: 2018-08-01
 * @time: 8:50
 */
public class GsonClient {

      public static final Gson INSTANCE = new Gson();

      public static <V> V fromJson ( String json, Class<V> type ) {

            return INSTANCE.fromJson( json, type );
      }
}
