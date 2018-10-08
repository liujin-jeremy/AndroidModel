package tech.threekilogram.depository.cache.bitmap;

/**
 * @author Liujin 2018-10-08:8:58
 */

import static tech.threekilogram.depository.cache.bitmap.ScaleMode.MATCH_HEIGHT;
import static tech.threekilogram.depository.cache.bitmap.ScaleMode.MATCH_SIZE;
import static tech.threekilogram.depository.cache.bitmap.ScaleMode.MATCH_WIDTH;
import static tech.threekilogram.depository.cache.bitmap.ScaleMode.SAMPLE;
import static tech.threekilogram.depository.cache.bitmap.ScaleMode.SAMPLE_MAX;
import static tech.threekilogram.depository.cache.bitmap.ScaleMode.SRC_ARGB;
import static tech.threekilogram.depository.cache.bitmap.ScaleMode.SRC_RGB;

import android.support.annotation.IntDef;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 配置缩放模式
 */
@IntDef({ SAMPLE, SAMPLE_MAX, MATCH_WIDTH, MATCH_HEIGHT, MATCH_SIZE, SRC_RGB, SRC_ARGB })
@Retention(RetentionPolicy.SOURCE)
@Target(value = { ElementType.FIELD, ElementType.PARAMETER, ElementType.METHOD })
public @interface ScaleMode {

      /**
       * 缩放模式:等比例缩放至最接近宽高,缩放后图片尺寸不会小于设置的值
       */
      public static final int SAMPLE       = 1;
      /**
       * 缩放模式:等比例缩放至最接近宽高,缩放后图片尺寸均会小于设置的值
       */
      public static final int SAMPLE_MAX   = 2;
      /**
       * 缩放模式:等比例缩放至匹配宽度
       */
      public static final int MATCH_WIDTH  = 3;
      /**
       * 缩放模式:等比例缩放至匹配高度
       */
      public static final int MATCH_HEIGHT = 4;
      /**
       * 缩放模式:等比例缩放至匹配宽高
       */
      public static final int MATCH_SIZE   = 5;
      /**
       * 加载原图,使用rgb_565格式
       */
      public static final int SRC_RGB      = 6;
      /**
       * 加载原图,使用ARGB_8888格式
       */
      public static final int SRC_ARGB     = 7;
}
