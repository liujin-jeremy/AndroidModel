package tech.threekilogram.depository.net;

/**
 * @author: Liujin
 * @version: V1.0
 * @date: 2018-08-01
 * @time: 19:32
 */
public interface OnNetErrorListener<K> {

      /**
       * call this when get a response then convert response to a value occur a exception
       *
       * @param key key
       * @param e exception
       */
      void onResponseConvertException (K key, Exception e);

      /**
       * 发生异常时回调
       *
       * @param key key
       * @param e 异常
       */
      void onConnectException (K key, Exception e);

      /**
       * 响应不是200~300回调
       *
       * @param key key
       * @param code 响应数字
       */
      void onErrorCode (K key, int code);
}
