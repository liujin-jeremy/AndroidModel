package tech.threekilogram.depository.net.retrofit.convert.string;

import java.io.InputStream;
import okhttp3.ResponseBody;
import tech.threekilogram.depository.function.CloseFunction;
import tech.threekilogram.depository.net.UrlConverter;
import tech.threekilogram.depository.net.retrofit.service.RetrofitConverter;

/**
 * 辅助完成转换工作
 *
 * @author liujin
 */
public class RetrofitStringConverter<K> implements
                                        RetrofitConverter<K, String> {

      @SuppressWarnings("WeakerAccess")
      protected UrlConverter<K> mUrlConverter;

      public RetrofitStringConverter ( UrlConverter<K> urlConverter ) {

            mUrlConverter = urlConverter;
      }

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

      @Override
      public String urlFromKey ( K key ) {

            return mUrlConverter.urlFromKey( key );
      }
}


