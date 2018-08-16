package tech.threekilogram.depository.file.converter;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import tech.threekilogram.depository.file.BaseFileConverter;
import tech.threekilogram.depository.file.FileConverter;
import tech.threekilogram.depository.file.loader.File;
import tech.threekilogram.depository.function.Close;

/**
 * {@link FileConverter} 的一种实现,需要和{@link File}配合使用;
 *
 * @author: Liujin
 * @version: V1.0
 * @date: 2018-07-30
 * @time: 17:17
 */
public class FileStreamConverter extends BaseFileConverter<InputStream> {

      @Override
      public String fileName ( String key ) {

            return mKeyNameConverter.encodeToName( key );
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

                  Close.close( value );
                  Close.close( outputStream );
            }
      }
}
