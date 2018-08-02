package tech.threekilogram.depository.file.lru;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import tech.threekilogram.depository.file.ValueFileConverter;

/**
 * adapter类,用于将中间类型{@link Cloneable}类转为{@link InputStream}和{@link OutputStream},该类一般用于{@link
 * DiskLruCacheLoader}转换工作
 *
 * @author: Liujin
 * @version: V1.0
 * @date: 2018-07-30
 * @time: 18:22
 */
public abstract class BaseDiskLruCacheConverter<K, V> implements
                                                      ValueFileConverter<K, V, Cloneable> {

      @Override
      public V toValue (Cloneable file) throws Exception {

            return toValue(((InputStream) file));
      }

      @Override
      public void saveValue (Cloneable file, V value) throws IOException {

            saveValue((OutputStream) file, value);
      }

      /**
       * 工具方法简化编程
       *
       * @param stream stream
       *
       * @return value value
       *
       * @throws Exception exception
       */
      public abstract V toValue (InputStream stream) throws Exception;

      /**
       * 工具方法简化编程
       *
       * @param stream stream
       * @param value value value
       *
       * @throws IOException exception
       */
      public abstract void saveValue (OutputStream stream, V value) throws IOException;
}
