package tech.threekilogram.depository.file;

import tech.threekilogram.depository.key.NameConverter;
import tech.threekilogram.depository.key.NameConverter.EncodeMode;

/**
 * @author: Liujin
 * @version: V1.0
 * @date: 2018-08-16
 * @time: 21:30
 */
public abstract class BaseFileConverter<V> implements FileConverter<V> {

      protected NameConverter mNameConverter = new NameConverter();

      /**
       * 设置{@link #fileName(String)}采用较短名字还是长名字
       */
      public void setMode ( @EncodeMode int mode ) {

            mNameConverter.setMode( mode );
      }

      @Override
      public String fileName ( String key ) {

            return mNameConverter.encodeToName( key );
      }
}
