package tech.threekilogram.model.net.responsebody;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import okhttp3.ResponseBody;
import tech.threekilogram.model.function.io.Close;

/**
 * 将网络响应转换为string对象
 *
 * @author liujin
 */
public class BodyStringConverter implements ResponseBodyConverter<String> {

      private Charset mCharset;

      public BodyStringConverter ( ) {

            mCharset = Charset.forName( "UTF-8" );
      }

      public BodyStringConverter ( Charset charset ) {

            mCharset = charset;
      }

      @Override
      public String onExecuteSuccess (
          String url, ResponseBody response )
          throws Exception {

            InputStream inputStream = null;
            try {

                  StringBuilder builder = new StringBuilder();

                  inputStream = response.byteStream();
                  InputStreamReader reader = new InputStreamReader( inputStream, mCharset );
                  char[] chars = new char[ 32 ];
                  int len = 0;
                  while( ( len = reader.read( chars, 0, chars.length ) ) != -1 ) {
                        builder.append( chars, 0, len );
                  }
                  return builder.toString();
            } finally {

                  Close.close( inputStream );
            }
      }
}
