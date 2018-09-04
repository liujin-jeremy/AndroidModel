package tech.threekilogram.depository.cache.json;

import com.google.gson.Gson;
import com.google.gson.stream.JsonWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.List;
import tech.threekilogram.depository.function.instance.GsonClient;
import tech.threekilogram.depository.function.io.Close;

/**
 * {@link JsonConverter}的gson实现版本
 *
 * @author: Liujin
 * @version: V1.0
 * @date: 2018-08-19
 * @time: 20:07
 */
public class GsonConverter<V> implements JsonConverter<V> {

      /**
       * gson
       */
      protected static Gson sGson = GsonClient.INSTANCE;
      /**
       * value 类型
       */
      protected Class<V> mValueType;

      public GsonConverter ( Class<V> valueType ) {

            mValueType = valueType;
      }

      /**
       * 该方法需要用户自己实现,转换流为一组bean对象
       *
       * @param inputStream stream
       */
      @Override
      public List<V> fromArray ( InputStream inputStream ) {

            return null;
      }

      @Override
      public V from ( InputStream inputStream ) {

            Reader reader = null;
            try {

                  reader = new InputStreamReader( inputStream );
                  return sGson.fromJson( reader, mValueType );
            } finally {

                  Close.close( reader );
                  Close.close( inputStream );
            }
      }

      @Override
      public void to ( OutputStream outputStream, V value ) {

            Writer writer = null;
            JsonWriter jsonWriter = null;

            try {

                  writer = new OutputStreamWriter( outputStream );
                  jsonWriter = new JsonWriter( writer );

                  sGson.toJson( value, mValueType, jsonWriter );
            } finally {

                  Close.close( jsonWriter );
                  Close.close( writer );
                  Close.close( outputStream );
            }
      }
}
