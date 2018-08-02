package tech.threekilogram.depository.memory.lru.size;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.os.Build.VERSION_CODES;

/**
 * @author: Liujin
 * @version: V1.0
 * @date: 2018-08-02
 * @time: 10:20
 */
public class BitmapSize<K> implements ValueSize<K, Bitmap> {

      @TargetApi(VERSION_CODES.KITKAT)
      @Override
      public int sizeOf (K k, Bitmap bitmap) {

            return bitmap.getAllocationByteCount();
      }
}
