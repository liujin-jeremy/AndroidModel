package tech.threekilogram.depository.file.lru;

/**
 * @author: Liujin
 * @version: V1.0
 * @date: 2018-07-30
 * @time: 18:22
 */
public interface DiskLruCacheMapper<K, V> {

      /**
       * change a key to string
       *
       * @param key key to string
       *
       * @return key of string
       */
      String stringKey (K key);

      /**
       * convert a byte array to value
       *
       * @param dataFromFile data read from file
       *
       * @return value of this file
       */
      V toValue (byte[] dataFromFile);

      /**
       * to convert a value to byte[] to save
       *
       * @param value value to save
       *
       * @return data of value
       */
      byte[] toFile (V value);
}
