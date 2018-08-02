package tech.threekilogram.depository.file.lru;

import com.google.gson.Gson;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import tech.threekilogram.depository.function.CloseFunction;
import tech.threekilogram.depository.function.NameFunction;
import tech.threekilogram.depository.global.GsonClient;

/**
 * @author: Liujin
 * @version: V1.0
 * @date: 2018-07-30
 * @time: 17:17
 */
public class DiskLruGsonConverter<T> extends DiskLruCacheConverter<String, T> {

      private static Gson sGson = GsonClient.INSTANCE;
      private Class<T> mValueType;

      /**
       * @param valueType type of value
       */
      public DiskLruGsonConverter (Class<T> valueType) {

            this.mValueType = valueType;
      }

      @Override
      public String fileName (String url) {

            return NameFunction.nameFromMd5(url);
      }

      @Override
      public T toValue (InputStream stream) throws Exception {

            Reader reader = null;
            try {
                  reader = new InputStreamReader(stream);
                  return sGson.fromJson(reader, mValueType);
            } finally {

                  CloseFunction.close(reader);
            }
      }

      @Override
      public void saveValue (OutputStream stream, T value) throws IOException {

            Writer writer = null;
            JsonWriter jsonWriter = null;

            try {

                  writer = new OutputStreamWriter(stream);
                  jsonWriter = new JsonWriter(writer);

                  sGson.toJson(value, mValueType, jsonWriter);
            } finally {

                  CloseFunction.close(jsonWriter);
                  CloseFunction.close(writer);
            }
      }
}
