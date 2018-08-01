package tech.threekilogram.depository.file.lru;

/**
 * @author: Liujin
 * @version: V1.0
 * @date: 2018-07-30
 * @time: 18:41
 */
public class DiskLruStringByteMapper implements DiskLruCacheMapper<String, byte[]> {

      @Override
      public String stringKey (String key) {

            return key;
      }

      @Override
      public byte[] toValue (byte[] dataFromFile) {

            return dataFromFile;
      }

      @Override
      public byte[] toFile (byte[] value) {

            return value;
      }
}
