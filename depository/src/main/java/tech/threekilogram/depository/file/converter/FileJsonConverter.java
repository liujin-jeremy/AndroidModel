package tech.threekilogram.depository.file.converter;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import tech.threekilogram.depository.file.BaseFileConverter;
import tech.threekilogram.depository.file.FileConverter;
import tech.threekilogram.depository.json.GsonConverter;
import tech.threekilogram.depository.json.JsonConverter;

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
public class FileJsonConverter<T> extends BaseFileConverter<T> {

      private JsonConverter<T> mConverter;

      /**
       * 传入value类型
       *
       * @param valueType type of value
       */
      public FileJsonConverter ( Class<T> valueType ) {

            mConverter = new GsonConverter<>( valueType );
      }

      /**
       * 传入value类型
       *
       * @param converter converter
       */
      public FileJsonConverter ( JsonConverter<T> converter ) {

            mConverter = converter;
      }

      @Override
      public T toValue ( String key, InputStream stream ) throws Exception {

            return mConverter.from( stream );
      }

      @Override
      public void saveValue ( String key, OutputStream stream, T value ) throws IOException {

            mConverter.to( stream, value );
      }
}
