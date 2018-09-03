package tech.threekilogram.depository.json;

import java.io.InputStream;

/**
 * @author: Liujin
 * @version: V1.0
 * @date: 2018-09-03
 * @time: 17:10
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
}
