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
 * 通过一个{@link String}key 从本地文件系统读取成{@link String}类型的实例
 *
 * @author: Liujin
 * @version: V1.0
 * @date: 2018-07-30
 * @time: 17:17
 */
public class FileStringConverter implements FileConverter<String> {

      @Override
      public String fileName ( String key ) {

            return Md5Function.nameFromMd5( key );
      }

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

                  CloseFunction.close( stream );
            }

            return null;
      }

      @Override
      public void saveValue ( String key, OutputStream outputStream, String value )
          throws IOException {

            try {
                  outputStream.write( value.getBytes() );
            } finally {

                  CloseFunction.close( outputStream );
            }
      }
}
