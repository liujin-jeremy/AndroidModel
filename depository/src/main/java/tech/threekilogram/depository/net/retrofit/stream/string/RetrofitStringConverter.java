package tech.threekilogram.depository.net.retrofit.stream.string;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import okhttp3.ResponseBody;
import tech.threekilogram.depository.function.CloseFunction;
import tech.threekilogram.depository.net.retrofit.BaseRetrofitConverter;

/**
 * 辅助完成转换工作,适用于使用一个Url作为key的情况
 *
 * @author liujin
 */
public class RetrofitStringConverter extends BaseRetrofitConverter<String> {

      @Override
      public String onExecuteSuccess ( String key, ResponseBody response ) throws Exception {

            InputStream inputStream = null;
            try {

                  StringBuilder builder = new StringBuilder();

                  inputStream = response.byteStream();
                  BufferedReader reader = new BufferedReader(
                      new InputStreamReader( inputStream ) );

                  String line = null;
                  while( ( line = reader.readLine() ) != null ) {

                        builder.append( line );
                  }

                  return builder.toString();
            } finally {

                  CloseFunction.close( inputStream );
            }
      }

      @Override
      public void onExecuteFailed ( String key, int httpCode, ResponseBody errorResponse ) {

      }
}
