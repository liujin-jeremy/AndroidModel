package tech.liujin.cache.util.encode;

import static tech.liujin.cache.util.encode.EncodeMode.HASH;
import static tech.liujin.cache.util.encode.EncodeMode.MD5;

import android.support.annotation.IntDef;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author Liujin 2018-10-08:10:19
 */
@Retention(RetentionPolicy.SOURCE)
@IntDef(value = { MD5, HASH, EncodeMode.DEFAULT })
public @interface EncodeMode {

      /**
       * 使用md5
       */
      public static final int MD5     = 11;
      /**
       * 使用{@link StringHash}
       */
      public static final int HASH    = 12;
      /**
       * 返回原值
       */
      public static final int DEFAULT = 13;
}
