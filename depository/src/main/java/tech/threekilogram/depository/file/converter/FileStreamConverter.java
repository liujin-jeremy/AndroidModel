package tech.threekilogram.depository.file.converter;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import tech.threekilogram.depository.file.FileConverter;
import tech.threekilogram.depository.file.impl.FileLoader;
import tech.threekilogram.depository.function.CloseFunction;
import tech.threekilogram.depository.function.Md5Function;

/**
 * {@link FileConverter} 的一种实现,需要和{@link FileLoader}配合使用;
 *
 * @author: Liujin
 * @version: V1.0
 * @date: 2018-07-30
 * @time: 17:17
 */
public class FileStreamConverter implements FileConverter<InputStream> {

      @Override
      public String fileName ( String key ) {

            return Md5Function.nameFromMd5( key );
      }

      @Override
      public InputStream toValue ( String key, InputStream stream ) throws Exception {

            return stream;
      }

      @Override
      public void saveValue ( String key, OutputStream outputStream, InputStream value )
          throws IOException {

            try {
                  byte[] bytes = new byte[ 128 ];
                  int len = 0;
                  while( ( len = value.read( bytes ) ) != -1 ) {

                        outputStream.write( bytes, 0, len );
                  }
            } finally {

                  CloseFunction.close( value );
                  CloseFunction.close( outputStream );
            }
      }
}
