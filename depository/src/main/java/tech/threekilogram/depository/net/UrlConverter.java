package tech.threekilogram.depository.net;

/**
 * @author: Liujin
 * @version: V1.0
 * @date: 2018-08-05
 * @time: 13:35
 */
public interface UrlConverter<K> {

      /**
       * 从一个key返回一个Url
       *
       * @param key key
       *
       * @return url to get value
       */
      String urlFromKey (K key);
}
