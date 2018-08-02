package tech.threekilogram.depository.net;

/**
 * @author: Liujin
 * @version: V1.0
 * @date: 2018-08-02
 * @time: 9:03
 */
public class UrlKeyFactory implements UrlFactory<String> {

      @Override
      public String url (String key) {

            return key;
      }
}
