package tech.threekilogram.depository.file.converter;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import tech.threekilogram.depository.file.BaseFileConverter;
import tech.threekilogram.depository.file.FileConverter;
import tech.threekilogram.depository.function.io.Close;

/**
 * {@link FileConverter} 的一种实现,需要和{@link tech.threekilogram.depository.file.BaseFileLoader}配合使用;
 * 从本地文件系统读取成{@link String}类型的实例
 *
 * @author: Liujin
 * @version: V1.0
 * @date: 2018-07-30
 * @time: 17:17
 */
public class FileStringConverter extends BaseFileConverter<String> {

      @Override
      public String from ( InputStream stream ) {

            try {

                  int length = stream.available();
                  byte[] bytes = new byte[ length ];
                  int read = stream.read( bytes );
                  return new String( bytes, 0, read );
            } catch(IOException e) {

                  e.printStackTrace();
            } finally {

                  Close.close( stream );
            }

            return null;
      }

      @Override
      public void to ( OutputStream outputStream, String value ) {

            try {

                  outputStream.write( value.getBytes() );
            } catch(IOException e) {

                  e.printStackTrace();
            } finally {

                  Close.close( outputStream );
            }
      }
}
