package tech.threekilogram.model;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * 该接口用于将一个数据流转换为指定的对象
 *
 * @param <V> 需要转换的数据类型
 *
 * @author liujin
 */
public interface StreamConverter<V> {

      /**
       * 将一个流转换为json bean
       *
       * @param inputStream stream
       *
       * @return json bean
       */
      V from ( InputStream inputStream );

      /**
       * 将一个json bean 对象保存到数据流中
       *
       * @param outputStream 数据流
       * @param value json bean
       */
      void to ( OutputStream outputStream, V value );
}
