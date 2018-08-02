package tech.threekilogram.depository.file;

/**
 * @author: Liujin
 * @version: V1.0
 * @date: 2018-08-02
 * @time: 9:31
 */
public interface FileNameConverter<K> {

      /**
       * to get a file from key
       *
       * @param key key to map a file
       *
       * @return file name defined by this key, not a path
       */
      String fileName (K key);
}
