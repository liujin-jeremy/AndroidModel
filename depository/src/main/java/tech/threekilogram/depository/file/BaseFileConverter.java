package tech.threekilogram.depository.file;

import tech.threekilogram.depository.key.KeyNameConverter;
import tech.threekilogram.depository.key.KeyNameConverter.EncodeMode;

/**
 * @author: Liujin
 * @version: V1.0
 * @date: 2018-08-16
 * @time: 21:30
 */
public abstract class BaseFileConverter<V> implements FileConverter<V> {

      protected KeyNameConverter mKeyNameConverter = new KeyNameConverter();

      /**
       * 设置{@link #fileName(String)}采用较短名字还是长名字
       */
      public void setMode ( @EncodeMode int mode ) {

            mKeyNameConverter.setMode( mode );
      }

      @Override
      public String fileName ( String key ) {

            return mKeyNameConverter.encodeToName( key );
      }
}
