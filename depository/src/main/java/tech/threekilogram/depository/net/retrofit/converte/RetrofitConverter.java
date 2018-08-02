package tech.threekilogram.depository.net.retrofit.converte;

import java.io.InputStream;
import tech.threekilogram.depository.net.retrofit.loader.RetrofitLoader;

/**
 * @author: Liujin
 * @version: V1.0
 * @date: 2018-08-02
 * @time: 8:34
 */
public interface RetrofitConverter<K, V> {

      /**
       * when {@link RetrofitLoader} get a success response call this to convert stream to value
       *
       * @param key key to this response
       * @param contentLength content length from response
       * @param stream stream from net
       *
       * @return value
       *
       * @throws Exception exception when convert a stream to V
       */
      V onResponse (K key, long contentLength, InputStream stream) throws Exception;
}
