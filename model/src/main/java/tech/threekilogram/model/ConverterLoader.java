package tech.threekilogram.model;

import tech.threekilogram.model.converter.StreamConverter;

/**
 * @author Liujin 2018-10-25:18:21
 */
public interface ConverterLoader<K, V> {

      V load ( K k, StreamConverter<V> converter );
}
