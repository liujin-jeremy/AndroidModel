package tech.threekilogram.depository.file.lru;

/**
 * @author: Liujin
 * @version: V1.0
 * @date: 2018-07-30
 * @time: 18:41
 */
public class DiskLruStringStringMapper implements DiskLruCacheMapper<String, String> {

      @Override
      public String stringKey (String key) {

            return key;
      }

      @Override
      public String toValue (byte[] dataFromFile) {

            if(dataFromFile == null) {
                  return null;
            }

            return new String(dataFromFile);
      }

      @Override
      public byte[] toFile (String value) {

            return value.getBytes();
      }
}
