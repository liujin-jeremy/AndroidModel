package tech.threekilogram.depository.file;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import tech.threekilogram.depository.file.impl.DiskLruCacheLoader;
import tech.threekilogram.depository.file.impl.FileLoader;

/**
 * 该接口用于辅助{@link FileLoader} 和{@link DiskLruCacheLoader}
 * 正常工作,
 * 用于将一个{@link K}类型的键转为{@link V}类型的值,其中{@link M} 是转换过程中的中间类型
 * <p>
 * this interface is implemented by user to make {@link FileLoader} work fine
 *
 * @param <V> type of value
 *
 * @author liujin
 */
public interface ValueFileConverter<K, V> extends FileNameConverter<K> {

      /**
       * convert a file to value
       *
       * @param key key
       * @param stream stream
       *
       * @return value
       *
       * @throws Exception convert exception if cant convert a file to object exception will throw
       */
      V toValue (K key, InputStream stream) throws Exception;

      /**
       * to save value
       *
       * @param key key
       * @param outputStream stream
       * @param value to saved value
       *
       * @throws IOException can,t save value to this file
       */
      void saveValue (K key, OutputStream outputStream, V value) throws IOException;
}
