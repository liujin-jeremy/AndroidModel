package tech.threekilogram.depository.memory.lru;

import android.graphics.Bitmap;
import tech.threekilogram.depository.memory.lru.size.BitmapSize;

/**
 * @author: Liujin
 * @version: V1.0
 * @date: 2018-08-15
 * @time: 18:13
 */
public class MemoryBitmapLoader<K> extends MemoryLruCacheLoader<K, Bitmap> {

      public MemoryBitmapLoader ( int maxSize ) {

            super( maxSize, new BitmapSize<K>() );
      }
}
