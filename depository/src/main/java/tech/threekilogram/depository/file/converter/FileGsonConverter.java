package tech.threekilogram.depository.file.converter;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import tech.threekilogram.depository.file.BaseFileConverter;
import tech.threekilogram.depository.file.FileConverter;
import tech.threekilogram.depository.json.GsonConverter;

/**
 * {@link FileConverter} 的一种实现
 * 通过一个{@link String}key 从本地文件系统读取成{@link T}类型的实例
 * <p>
 * 该类用于从文件中保存json和解析json对象
 *
 * @param <T> 想要获取的类型
 *
 * @author liujin
 */
public class FileGsonConverter<T> extends BaseFileConverter<T> {

      private GsonConverter<T> mGsonConverter;

      /**
       * 传入value类型
       *
       * @param valueType type of value
       */
      public FileGsonConverter ( Class<T> valueType ) {

            mGsonConverter = new GsonConverter<>( valueType );
      }

      @Override
      public T toValue ( String key, InputStream stream ) throws Exception {

            return mGsonConverter.fromJson( stream );
      }

      @Override
      public void saveValue ( String key, OutputStream stream, T value ) throws IOException {

            mGsonConverter.toJson( stream, value );
      }
}
