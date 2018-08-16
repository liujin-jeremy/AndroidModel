package tech.threekilogram.depository.client;

import tech.threekilogram.depository.Container;

/**
 * @author: Liujin
 * @version: V1.0
 * @date: 2018-08-16
 * @time: 21:44
 */
public class Bitmap implements Container<String, android.graphics.Bitmap> {

      @Override
      public android.graphics.Bitmap save ( String key, android.graphics.Bitmap value ) {

            return null;
      }

      @Override
      public android.graphics.Bitmap remove ( String key ) {

            return null;
      }

      @Override
      public boolean containsOf ( String key ) {

            return false;
      }

      @Override
      public android.graphics.Bitmap load ( String key ) {

            return null;
      }
}
