package tech.threekilogram.depository.net.retrofit.stream.down;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import okhttp3.ResponseBody;
import tech.threekilogram.depository.file.BaseFileLoader;
import tech.threekilogram.depository.file.FileConverter;
import tech.threekilogram.depository.file.impl.DiskLruCacheLoader;
import tech.threekilogram.depository.file.impl.FileLoader;
import tech.threekilogram.depository.function.CloseFunction;
import tech.threekilogram.depository.function.Md5Function;
import tech.threekilogram.depository.net.retrofit.RetrofitConverter;

/**
 * 下载文件到一个文件夹下
 *
 * @author liujin
 */
public class RetrofitDownConverter implements RetrofitConverter<String, File> {

      /**
       * 下载文件夹
       */
      private File                                mDir;
      /**
       * 保存文件
       */
      private BaseFileLoader<String, InputStream> mFileLoader;
      /**
       * 转换流
       */
      private FileStreamConverter                 mConverter;

      public RetrofitDownConverter ( File dir ) {

            mDir = dir;
            mConverter = new FileStreamConverter();
            mFileLoader = new FileLoader<>( dir, mConverter );
      }

      public RetrofitDownConverter ( File dir, int maxSize ) throws IOException {

            mDir = dir;
            mConverter = new FileStreamConverter();
            mFileLoader = new DiskLruCacheLoader<>( dir, maxSize, mConverter );
      }

      @Override
      public String urlFromKey ( String key ) {

            return key;
      }

      @Override
      public File onExecuteSuccess ( String key, ResponseBody response ) throws Exception {

            InputStream inputStream = response.byteStream();
            int length = (int) response.contentLength();

            mConverter.setLength( length );
            mFileLoader.save( key, inputStream );

            return mFileLoader.getFile( key );
      }

      @Override
      public void onExecuteFailed ( String key, int httpCode, ResponseBody errorResponse ) {

      }

      public File getDir ( ) {

            return mDir;
      }

      public File getFile ( String key ) {

            return mFileLoader.getFile( key );
      }

      /**
       * 将网络响应保存到文件
       */
      private class FileStreamConverter implements FileConverter<String, InputStream> {

            private int mLength;

            void setLength ( int length ) {

                  this.mLength = length;
            }

            @Override
            public String fileName ( String key ) {

                  return Md5Function.nameFromMd5( key );
            }

            @Override
            public InputStream toValue ( String key, InputStream stream ) throws Exception {

                  return null;
            }

            @Override
            public void saveValue ( String key, OutputStream outputStream, InputStream value )
                throws IOException {

                  final int limit = 512;

                  if( mLength <= limit ) {

                        byte[] bytes = new byte[ mLength ];
                        int read = value.read( bytes );
                        outputStream.write( bytes, 0, read );
                  } else {

                        byte[] bytes = new byte[ 128 ];
                        int len = 0;

                        while( ( len = value.read( bytes ) ) != -1 ) {

                              outputStream.write( bytes, 0, len );
                        }
                  }

                  CloseFunction.close( value );
                  CloseFunction.close( outputStream );
            }
      }
}
