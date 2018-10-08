package tech.threekilogram.depository.file;

import tech.threekilogram.depository.function.encode.EncodeMode;
import tech.threekilogram.depository.function.encode.StringEncoder;

/**
 * {@link FileConverter}简单实现,完成key到file name的转换工作
 *
 * @param <V> 从文件中获取的数据类型
 *
 * @author liujin
 */
public abstract class BaseFileConverter<V> implements FileConverter<V> {

      @EncodeMode
      private int mMode = EncodeMode.HASH;

      public void setMode ( @EncodeMode int mode ) {

            mMode = mode;
      }

      @EncodeMode
      public int getMode ( ) {

            return mMode;
      }

      @Override
      public String fileName ( String key ) {

            return StringEncoder.encode( key, mMode );
      }
}
