package tech.liujin.cache;

import tech.liujin.cache.converter.StreamConverter;

/**
 * @author Liujin 2018-10-25:23:01
 */
public interface ConverterLoader<K, V> extends Loader<K, V> {

      /**
       * 使用指定converter完成转换
       *
       * @param k key
       * @param converter converter
       *
       * @return value
       */
      V load ( K k, StreamConverter<V> converter );
}
