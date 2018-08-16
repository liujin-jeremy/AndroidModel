package tech.threekilogram.depository.file;

import tech.threekilogram.depository.key.KeyNameConverter;

/**
 * @author: Liujin
 * @version: V1.0
 * @date: 2018-08-16
 * @time: 21:30
 */
public abstract class BaseFileConverter<V> implements FileConverter<V> {

      protected KeyNameConverter mKeyNameConverter = new KeyNameConverter();

      /**
       * 设置{@link #fileName(String)}采用较短名字还是较长名字
       *
       * @param set true:短名字
       */
      public void shortName ( boolean set ) {

            if( set ) {

                  mKeyNameConverter.setMode( KeyNameConverter.HASH );
            } else {

                  mKeyNameConverter.setMode( KeyNameConverter.MD5 );
            }
      }
}
