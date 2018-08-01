package tech.threekilogram.depository.net;

/**
 * convert a key to string url ,use this url to get a response then convert response to value
 *
 * @param <K> key to convert a string url
 * @param <T> response type ,response is from net
 * @param <V> final value type
 *
 * @author liujin
 */
public interface NetMapper<K, T, V> {

      /**
       * convert a key to url
       *
       * @param key key to get a value
       *
       * @return url to get value
       */
      String keyToUrl (K key);

      /**
       * when status code is not in (200~300) call this method
       *
       * @param errorCode HTTP status code. when didn't get a valuable response
       */
      void errorCode (int errorCode);

      /**
       * convert a response to value
       *
       * @param response response from net
       *
       * @return value
       */
      V responseToValue (T response);
}
