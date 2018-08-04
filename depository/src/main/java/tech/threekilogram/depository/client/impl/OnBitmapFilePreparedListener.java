package tech.threekilogram.depository.client.impl;

import android.graphics.Bitmap;
import java.io.File;

/**
 * @author: Liujin
 * @version: V1.0
 * @date: 2018-08-04
 * @time: 17:07
 */
public interface OnBitmapFilePreparedListener<K> {

      /**
       * decode a bitmap from file
       *
       * @param key key
       * @param file file
       *
       * @return bitmap
       */
      Bitmap onBitmapFilePrepared (K key, File file);
}
