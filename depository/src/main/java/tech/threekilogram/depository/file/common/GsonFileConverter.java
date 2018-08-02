package tech.threekilogram.depository.file.common;

import com.google.gson.Gson;
import com.google.gson.stream.JsonWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import tech.threekilogram.depository.file.ValueFileConverter;
import tech.threekilogram.depository.function.CloseFunction;
import tech.threekilogram.depository.function.NameFunction;
import tech.threekilogram.depository.global.GsonClient;

/**
 * {@link ValueFileConverter} 的一种实现,需要和{@link FileLoader}配合使用;
 * 通过一个{@link String}key 从本地文件系统读取成{@link T}类型的实例
 *
 * @param <T> 想要获取的类型
 *
 * @author liujin
 */
public class GsonFileConverter<T> implements ValueFileConverter<String, T, File> {

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

            return NameFunction.nameFromMd5(key);
      }

      @Override
      public T toValue (File file) throws Exception {

            if(file.exists()) {

                  FileInputStream inputStream = null;
                  Reader reader = null;
                  try {
                        inputStream = new FileInputStream(file);
                        reader = new InputStreamReader(inputStream);
                        return sGson.fromJson(reader, mValueType);
                  } finally {

                        CloseFunction.close(reader);
                        CloseFunction.close(inputStream);
                  }
            }

            return null;
      }

      @Override
      public void saveValue (File file, T value) throws IOException {

            if(file.exists()) {
                  boolean delete = file.delete();
            }

            FileOutputStream outputStream = null;
            Writer writer = null;
            JsonWriter jsonWriter = null;

            try {

                  outputStream = new FileOutputStream(file);
                  writer = new OutputStreamWriter(outputStream);
                  jsonWriter = new JsonWriter(writer);

                  sGson.toJson(value, mValueType, jsonWriter);
            } finally {

                  CloseFunction.close(jsonWriter);
                  CloseFunction.close(writer);
                  CloseFunction.close(outputStream);
            }
      }
}
