package tech.threekilogram.depository.file;

import java.io.File;
import java.io.IOException;

/**
 * this interface is implemented by user to make {@link FileLoadSupport} work
 *
 * @param <V> type of value
 *
 * @author liujin
 */
public interface FileMapper<K, V> {

      /**
       * to get a file from key
       *
       * @param key key to map a file {@link File#isFile()} must return true
       *
       * @return file to this key
       */
      File keyToFile (K key);

      /**
       * convert a file to value
       *
       * @param file file to convert
       *
       * @return value
       *
       * @throws IOException exception
       */
      V fileToValue (File file) throws IOException;

      /**
       * convert a value to byte array to write to file
       *
       * @param key key to get file
       * @param value value to write
       *
       * @throws IOException exception
       */
      void writeToFile (K key, V value) throws IOException;


}
