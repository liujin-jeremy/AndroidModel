package tech.threekilogram.depository.client;

import android.support.annotation.IntDef;

/**
 * listen for value load finished
 *
 * @param <V> value type
 *
 * @author liujin
 */
public interface OnValuePreparedListener<V> {

      /**
       * load from memory success
       */
      public static final int LOAD_FROM_MEMORY                 = 11;
      /**
       * load from file success
       */
      public static final int LOAD_FROM_FILE                   = 13;
      /**
       * load from net success
       */
      public static final int LOAD_FROM_NET                    = 15;
      /**
       * load failed in all ways
       */
      public static final int LOAD_NOTHING                     = 7;
      /**
       * value is in file but cant convert to value
       */
      public static final int LOAD_FROM_FILE_CONVERT_EXCEPTION = 911;
      public static final int LOAD_NET_CANT_CONNECT            = 913;
      public static final int LOAD_FROM_NET_CONVERT_EXCEPTION  = 915;

      /**
       * to notify value prepared
       *
       * @param result result code
       * @param value value load maybe null if load failed
       */
      void onValuePrepared (@LoadResult int result, V value);

      @IntDef({LOAD_FROM_MEMORY,
               LOAD_FROM_FILE,
               LOAD_FROM_NET,
               LOAD_NOTHING,
               LOAD_FROM_FILE_CONVERT_EXCEPTION,
               LOAD_NET_CANT_CONNECT,
               LOAD_FROM_NET_CONVERT_EXCEPTION})
      public @interface LoadResult {}
}
