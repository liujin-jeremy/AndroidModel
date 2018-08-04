package tech.threekilogram.depository.function;

/**
 * convert a {@link K} to string
 *
 * @param <K> key type
 *
 * @author liujin
 */
public interface StringKeyFunction<K> {

      /**
       * get a string key from {@link K} type
       *
       * @param key key
       *
       * @return string key from key
       */
      String stringKey (K key);
}
