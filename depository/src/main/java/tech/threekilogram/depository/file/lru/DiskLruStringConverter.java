package tech.threekilogram.depository.file.lru;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import tech.threekilogram.depository.function.NameFunction;

/**
 * @author: Liujin
 * @version: V1.0
 * @date: 2018-07-30
 * @time: 17:17
 */
public class DiskLruStringConverter extends BaseDiskLruCacheConverter<String, String> {

      @Override
      public String fileName (String key) {

            return NameFunction.nameFromMd5(key);
      }

      @Override
      public String toValue (InputStream stream) throws Exception {

            int length = stream.available();
            byte[] bytes = new byte[length];

            int read = stream.read(bytes);
            return new String(bytes);
      }

      @Override
      public void saveValue (OutputStream stream, String value) throws IOException {

            stream.write(value.getBytes());
      }
}
