package tech.threekilogram.depository.net.retrofit.convert.string;

import java.io.InputStream;
import okhttp3.ResponseBody;
import tech.threekilogram.depository.function.CloseFunction;
import tech.threekilogram.depository.net.retrofit.RetrofitConverter;

/**
 * 辅助完成转换工作
 *
 * @author liujin
 */
public abstract class BaseRetrofitStringConverter<K> implements
                                        RetrofitConverter<K, String> {

      @Override
      public String onExecuteSuccess ( K key, ResponseBody response ) throws Exception {

            InputStream inputStream = null;
            byte[] bytes;
            try {
                  inputStream = response.byteStream();
                  int length = (int) response.contentLength();

                  bytes = new byte[ length ];
                  int read = inputStream.read( bytes );
                  return new String( bytes );
            } finally {

                  CloseFunction.close( inputStream );
            }
      }

      @Override
      public void onExecuteFailed ( K key, int httpCode, ResponseBody errorResponse ) {

      }
}


