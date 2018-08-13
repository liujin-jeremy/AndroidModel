package tech.threekilogram.depository.net.retrofit.convert.string;

import java.io.InputStream;
import okhttp3.ResponseBody;
import tech.threekilogram.depository.function.CloseFunction;
import tech.threekilogram.depository.net.retrofit.service.RetrofitConverter;

/**
 * 辅助完成转换工作,适用于使用一个Url作为key的情况
 *
 * @author liujin
 */
public class RetrofitUrlStringConverter implements
                                        RetrofitConverter<String, String> {

      @Override
      public String onExecuteSuccess ( String key, ResponseBody response ) throws Exception {

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
      public void onExecuteFailed ( String key, int httpCode, ResponseBody errorResponse ) {

      }

      @Override
      public String urlFromKey ( String key ) {

            return key;
      }
}
