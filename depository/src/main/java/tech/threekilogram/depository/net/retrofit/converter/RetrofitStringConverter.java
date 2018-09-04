package tech.threekilogram.depository.net.retrofit.converter;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import okhttp3.ResponseBody;
import tech.threekilogram.depository.function.io.Close;

/**
 * 将网络响应转换为string对象
 *
 * @author liujin
 */
public class RetrofitStringConverter implements ResponseBodyConverter<String> {

      @Override
      public String onExecuteSuccess (
          String url, ResponseBody response )
          throws Exception {

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

                  Close.close( inputStream );
            }
      }
}
