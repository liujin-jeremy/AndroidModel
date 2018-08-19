package tech.threekilogram.depository.json;

import com.google.gson.Gson;
import com.google.gson.stream.JsonWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.List;
import tech.threekilogram.depository.function.Close;
import tech.threekilogram.depository.instance.GsonClient;

/**
 * @author: Liujin
 * @version: V1.0
 * @date: 2018-08-19
 * @time: 20:07
 */
public class GsonConverter<V> implements JsonConverter<V> {

      /**
       * gson
       */
      private static Gson sGson = GsonClient.INSTANCE;
      /**
       * value 类型
       */
      private Class<V> mValueType;

      public GsonConverter ( Class<V> valueType ) {

            mValueType = valueType;
      }

      @Override
      public List<V> fromJsonArray ( InputStream inputStream ) {

            return null;
      }

      @Override
      public V fromJson ( InputStream inputStream ) {

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
      public void toJson ( OutputStream outputStream, V value ) {

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
