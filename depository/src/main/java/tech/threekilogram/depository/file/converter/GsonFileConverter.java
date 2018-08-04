package tech.threekilogram.depository.file.converter;

import com.google.gson.Gson;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import tech.threekilogram.depository.file.ValueFileConverter;
import tech.threekilogram.depository.function.CloseFunction;
import tech.threekilogram.depository.function.Md5Function;
import tech.threekilogram.depository.global.GsonClient;

/**
 * {@link ValueFileConverter} 的一种实现,需要和
 * {@link  tech.threekilogram.depository.file.FileLoadSupport}实现类配合使用;
 * 通过一个{@link String}key 从本地文件系统读取成{@link T}类型的实例
 * <p>
 * 该类用于从文件中保存json和解析json对象
 *
 * @param <T> 想要获取的类型
 *
 * @author liujin
 */
public class GsonFileConverter<T> implements ValueFileConverter<String, T> {

      /**
       * gson
       */
      private static Gson sGson = GsonClient.INSTANCE;
      /**
       * value 类型
       */
      private Class<T> mValueType;

      /**
       * 传入value类型
       *
       * @param valueType type of value
       */
      public GsonFileConverter (Class<T> valueType) {

            this.mValueType = valueType;
      }

      @Override
      public String fileName (String key) {

            /* 默认将该key md5 一下,防止有非法的字符 */

            return Md5Function.nameFromMd5(key);
      }

      @Override
      public T toValue (String key, InputStream stream) throws Exception {

            Reader reader = null;
            try {

                  reader = new InputStreamReader(stream);
                  return sGson.fromJson(reader, mValueType);
            } finally {

                  CloseFunction.close(reader);
                  CloseFunction.close(stream);
            }
      }

      @Override
      public void saveValue (String key, OutputStream stream, T value) throws IOException {

            Writer writer = null;
            JsonWriter jsonWriter = null;

            try {

                  writer = new OutputStreamWriter(stream);
                  jsonWriter = new JsonWriter(writer);

                  sGson.toJson(value, mValueType, jsonWriter);
            } finally {

                  CloseFunction.close(jsonWriter);
                  CloseFunction.close(writer);
                  CloseFunction.close(stream);
            }
      }
}
