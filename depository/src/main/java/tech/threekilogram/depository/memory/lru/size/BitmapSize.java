package tech.threekilogram.depository.memory.lru.size;

import android.graphics.Bitmap;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;

/**
 * @author: Liujin
 * @version: V1.0
 * @date: 2018-08-02
 * @time: 10:20
 */
public class BitmapSize<K> implements ValueSize<K, Bitmap> {

      @Override
      public int sizeOf (K k, Bitmap bitmap) {

            if(VERSION.SDK_INT >= VERSION_CODES.KITKAT) {
                  return bitmap.getAllocationByteCount();
            } else {
                  return bitmap.getByteCount();
            }
      }
}
