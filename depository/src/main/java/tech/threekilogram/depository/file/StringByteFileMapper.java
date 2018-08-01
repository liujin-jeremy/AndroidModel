package tech.threekilogram.depository.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @author: Liujin
 * @version: V1.0
 * @date: 2018-07-30
 * @time: 17:17
 */
public class StringByteFileMapper implements FileMapper<String, byte[]> {

      @Override
      public File keyToFile (String key) {

            return new File(key);
      }

      @Override
      public byte[] fileToValue (File file) throws IOException {

            if(file.exists()) {
                  int length = (int) file.length();
                  byte[] bytes = new byte[length];

                  FileInputStream inputStream = null;
                  try {
                        inputStream = new FileInputStream(file);
                        int read = inputStream.read(bytes);
                        inputStream.close();
                        return bytes;
                  } finally {

                        if(inputStream != null) {
                              inputStream.close();
                        }
                  }
            }

            return null;
      }

      @Override
      public void writeToFile (String key, byte[] value) throws IOException {

            File file = keyToFile(key);
            if(file.exists()) {
                  boolean delete = file.delete();
            }

            FileOutputStream outputStream = null;
            try {

                  outputStream = new FileOutputStream(file);
                  outputStream.write(value);
            } finally {

                  if(outputStream != null) {

                        outputStream.close();
                  }
            }
      }
}
