package tech.threekilogram.depository.net.retrofit.convert.string;

/**
 * @author: Liujin
 * @version: V1.0
 * @date: 2018-08-05
 * @time: 23:23
 */

import java.io.InputStream;
import okhttp3.ResponseBody;
import tech.threekilogram.depository.function.CloseFunction;
import tech.threekilogram.depository.net.NetConverter;
import tech.threekilogram.depository.net.UrlConverter;

/**
 * 辅助完成转换工作
 */
public class RetrofitStringConverter<K> implements
                                        NetConverter<K, String, ResponseBody> {

      protected UrlConverter<K> mUrlConverter;

      public RetrofitStringConverter (UrlConverter<K> urlConverter) {

            mUrlConverter = urlConverter;
      }

      @Override
      public String onExecuteSuccess (K key, ResponseBody response) throws Exception {

            InputStream inputStream = null;
            byte[] bytes;
            try {
                  inputStream = response.byteStream();
                  int length = (int) response.contentLength();

                  bytes = new byte[length];
                  int read = inputStream.read(bytes);
            } finally {

                  CloseFunction.close(inputStream);
            }

            return new String(bytes);
      }

      @Override
      public void onExecuteFailed (K key, int httpCode, ResponseBody errorResponse) {

      }

      @Override
      public String urlFromKey (K key) {

            return mUrlConverter.urlFromKey(key);
      }
}


