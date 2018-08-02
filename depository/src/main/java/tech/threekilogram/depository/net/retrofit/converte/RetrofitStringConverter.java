package tech.threekilogram.depository.net.retrofit.converte;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

/**
 * @author: Liujin
 * @version: V1.0
 * @date: 2018-08-02
 * @time: 8:44
 */
public class RetrofitStringConverter<K> implements RetrofitConverter<K, String> {

      @Override
      public String onResponse (K key, long contentLength, InputStream stream) throws Exception {

            //1Mb
            final int limitByte = 1024 * 1024;

            if(contentLength <= limitByte) {

                  /* data length is short */

                  int length = (int) contentLength;
                  byte[] data = new byte[length];
                  int read = stream.read(data);
                  return new String(data);
            } else {

                  /* data length is large */

                  StringBuilder builder = new StringBuilder();
                  char[] chars = new char[512];
                  int len = 0;
                  Reader reader = new InputStreamReader(stream);

                  while((len = reader.read(chars)) != -1) {

                        builder.append(chars, 0, len);
                  }

                  return builder.toString();
            }
      }
}
