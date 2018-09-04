package tech.threekilogram.depository.file;

import tech.threekilogram.depository.function.encode.StringEncoder;
import tech.threekilogram.depository.function.encode.StringEncoder.EncodeMode;

/**
 * {@link FileConverter}简单实现,完成key到file name的转换工作
 *
 * @param <V> 从文件中获取的数据类型
 *
 * @author liujin
 */
public abstract class BaseFileConverter<V> implements FileConverter<V> {

      /**
       * 辅助转换{@link #fileName(String)}中的key为合法字符
       */
      protected StringEncoder mNameConverter = new StringEncoder();

      /**
       * 设置{@link #fileName(String)}采用何种转换方式
       */
      public void setMode ( @EncodeMode int mode ) {

            mNameConverter.setMode( mode );
      }

      @Override
      public String fileName ( String key ) {

            return mNameConverter.encode( key );
      }
}
