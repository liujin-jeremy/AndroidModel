package tech.threekilogram.depository.net;

/**
 * convert a key to string url ,use this url to get a response then convert response to value
 *
 * @param <K> key to convert a string url
 *
 * @author liujin
 */
public interface UrlFactory<K> {

      /**
       * convert a key to url
       *
       * @param key key to get a value
       *
       * @return url to get value
       */
      String url (K key);
}
