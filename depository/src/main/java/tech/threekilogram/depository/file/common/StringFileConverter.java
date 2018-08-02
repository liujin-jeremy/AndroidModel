package tech.threekilogram.depository.file.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import tech.threekilogram.depository.file.ValueFileConverter;
import tech.threekilogram.depository.function.CloseFunction;
import tech.threekilogram.depository.function.NameFunction;

/**
 * {@link ValueFileConverter} 的一种实现,需要和{@link FileLoader}配合使用;
 * 通过一个{@link String}key 从本地文件系统读取成{@link String}类型的实例
 *
 * @author: Liujin
 * @version: V1.0
 * @date: 2018-07-30
 * @time: 17:17
 */
public class StringFileConverter implements ValueFileConverter<String, String, File> {

      @Override
      public String fileName (String key) {

            return NameFunction.nameFromMd5(key);
      }

      @Override
      public String toValue (File file) throws Exception {

            if(file.exists()) {
                  int length = (int) file.length();
                  byte[] bytes = new byte[length];

                  FileInputStream inputStream = null;
                  try {
                        inputStream = new FileInputStream(file);
                        int read = inputStream.read(bytes);
                        return new String(bytes);
                  } finally {

                        CloseFunction.close(inputStream);
                  }
            }

            return null;
      }

      @Override
      public void saveValue (File file, String value) throws IOException {

            if(file.exists()) {
                  boolean delete = file.delete();
            }

            FileOutputStream outputStream = null;
            try {

                  outputStream = new FileOutputStream(file);
                  outputStream.write(value.getBytes());
            } finally {

                  CloseFunction.close(outputStream);
            }
      }
}
