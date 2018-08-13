package tech.threekilogram.depository.net.retrofit.convert.down;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import okhttp3.ResponseBody;
import tech.threekilogram.depository.function.CloseFunction;
import tech.threekilogram.depository.function.Md5Function;

/**
 * 下载文件到一个文件夹下
 *
 * @param <K> key
 *
 * @author liujin
 */
public abstract class BaseRetrofitFileConverter<K> extends
                                                   AbstractRetrofitDownLoadConverter<K> {

      public BaseRetrofitFileConverter ( File dir ) {

            super( dir );
      }

      @Override
      public File onExecuteSuccess ( K key, ResponseBody response ) throws Exception {

            File file = getFile( key );

            FileOutputStream outputStream = new FileOutputStream( file );

            InputStream inputStream = response.byteStream();
            int length = (int) response.contentLength();

            final int limit = 512;

            if( length <= limit ) {

                  byte[] bytes = new byte[ length ];
                  int read = inputStream.read( bytes );
                  outputStream.write( bytes, 0, read );
            } else {

                  byte[] bytes = new byte[ 128 ];
                  int len = 0;

                  while( ( len = inputStream.read( bytes ) ) != -1 ) {

                        outputStream.write( bytes, 0, len );
                  }
            }

            CloseFunction.close( inputStream );
            CloseFunction.close( outputStream );

            return file;
      }

      @Override
      public File getFile ( K key ) {

            String url = urlFromKey( key );
            String name = Md5Function.nameFromMd5( url );
            return new File( mDir, name );
      }

      @Override
      public void onExecuteFailed ( K key, int httpCode, ResponseBody errorResponse ) {

      }
}
