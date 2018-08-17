package tech.threekilogram.depository.json;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

/**
 * @author: Liujin
 * @version: V1.0
 * @date: 2018-08-17
 * @time: 14:16
 */
public interface JsonConverter<V> {

      /**
       * 将一个stream转换为json对象
       *
       * @param inputStream stream
       *
       * @return json bean
       */
      List<V> fromJsonArray ( InputStream inputStream );

      /**
       * 将一个流转换为json bean
       *
       * @param inputStream stream
       *
       * @return json bean
       */
      V fromJson ( InputStream inputStream );

      /**
       * 将一个json bean 对象保存到数据流中
       *
       * @param outputStream 数据流
       * @param value json bean
       */
      void toJson ( OutputStream outputStream, V value );
}
