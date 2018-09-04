package tech.threekilogram.depository.cache.json;

import java.io.InputStream;
import java.util.List;
import tech.threekilogram.depository.StreamConverter;

/**
 * json 转换为bean对象接口,一般可以使用{@link GsonConverter},根据需要实现{@link GsonConverter#fromArray(InputStream)}
 *
 * @author: Liujin
 * @version: V1.0
 * @date: 2018-08-17
 * @time: 14:16
 */
public interface JsonConverter<V> extends StreamConverter<V> {

      /**
       * 将一个stream转换为json对象列表,该方法对应{@link JsonLoader#loadListFromNet(String)}
       *
       * @param inputStream stream
       *
       * @return json bean
       */
      List<V> fromArray ( InputStream inputStream );
}
