package tech.threekilogram.depository.file;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import tech.threekilogram.depository.file.loader.DiskLru;
import tech.threekilogram.depository.file.loader.File;

/**
 * 该接口用于辅助{@link File} 和{@link DiskLru}
 * 正常工作,
 * 用于将一个键转为{@link V}类型的值
 * <p>
 * this interface is implemented by user to make {@link File} work fine
 *
 * @param <V> type of value
 *
 * @author liujin
 */
public interface FileConverter<V> {

      /**
       * 根据key返回文件名字,文件名字不能包含一些特殊字符,最好只包含数字字母
       * to get a file from key
       *
       * @param key key to map a file
       *
       * @return file encodeToName defined by this key, not a path
       */
      String fileName ( String key );

      /**
       * 将一个stream转换为指定类型的类实例
       * <p>
       * convert a file to value
       *
       * @param key key
       * @param stream stream from {@link java.io.File} get by {@link #fileName(String)}
       *
       * @return value
       *
       * @throws Exception convert exception if cant convert a file to object exception will throw
       */
      V toValue ( String key, InputStream stream ) throws Exception;

      /**
       * 将一个指定类型的类实例保存到输出流
       * <p>
       * to save value
       *
       * @param key key
       * @param outputStream stream from {@link java.io.File} get by {@link #fileName(String)}
       * @param value to saved value
       *
       * @throws IOException can,t save value to this file
       */
      void saveValue ( String key, OutputStream outputStream, V value ) throws IOException;
}
