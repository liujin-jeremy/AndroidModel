package tech.threekilogram.depository.file;

import java.io.IOException;
import tech.threekilogram.depository.file.common.FileLoader;

/**
 * this interface is implemented by user to make {@link FileLoader} work fine
 *
 * @param <V> type of value
 *
 * @author liujin
 */
public interface ValueFileConverter<K, V, M> extends FileNameConverter<K> {

      /**
       * convert a file to value
       *
       * @param file a middle component to convert to value
       *
       * @return value
       *
       * @throws Exception convert exception if cant convert a file to object exception will throw
       */
      V toValue (M file) throws Exception;

      /**
       * to save value
       *
       * @param file a middle component to save
       * @param value to saved value
       *
       * @throws IOException can,t save value to this file
       */
      void saveValue (M file, V value) throws IOException;
}
