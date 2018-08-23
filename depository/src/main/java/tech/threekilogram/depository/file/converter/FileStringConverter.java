package tech.threekilogram.depository.file.converter;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import tech.threekilogram.depository.file.BaseFileConverter;
import tech.threekilogram.depository.file.FileConverter;
import tech.threekilogram.depository.file.loader.FileLoader;
import tech.threekilogram.depository.function.Close;

/**
 * {@link FileConverter} 的一种实现,需要和{@link FileLoader}配合使用;
 * 通过一个{@link String}key 从本地文件系统读取成{@link String}类型的实例
 *
 * @author: Liujin
 * @version: V1.0
 * @date: 2018-07-30
 * @time: 17:17
 */
public class FileStringConverter extends BaseFileConverter<String> {

      @Override
      public String toValue ( String key, InputStream stream ) throws Exception {

            int length = stream.available();
            byte[] bytes = new byte[ length ];

            try {
                  int read = stream.read( bytes );
                  return new String( bytes );
            } catch(IOException e) {

                  e.printStackTrace();
            } finally {

                  Close.close( stream );
            }

            return null;
      }

      @Override
      public void saveValue ( String key, OutputStream outputStream, String value )
          throws IOException {

            try {
                  outputStream.write( value.getBytes() );
            } finally {

                  Close.close( outputStream );
            }
      }
}
