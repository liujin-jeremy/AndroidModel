package tech.threekilogram.model.util.encode;

import static tech.threekilogram.model.util.encode.EncodeMode.DEFAULT;
import static tech.threekilogram.model.util.encode.EncodeMode.HASH;
import static tech.threekilogram.model.util.encode.EncodeMode.MD5;

import android.support.annotation.IntDef;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author Liujin 2018-10-08:10:19
 */
@Retention(RetentionPolicy.SOURCE)
@IntDef(value = { MD5, HASH, DEFAULT })
public @interface EncodeMode {

      public static final int MD5     = 11;
      public static final int HASH    = 12;
      public static final int DEFAULT = 13;
}
