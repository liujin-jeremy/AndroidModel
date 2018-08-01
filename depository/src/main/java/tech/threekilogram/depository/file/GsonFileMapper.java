package tech.threekilogram.depository.file;

import com.google.gson.Gson;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import tech.threekilogram.depository.function.NameFunction;

/**
 * @author: Liujin
 * @version: V1.0
 * @date: 2018-07-30
 * @time: 17:17
 */
public class GsonFileMapper<T> implements FileMapper<String, T> {

      private static Gson sGson = new Gson();
      private File     mDir;
      private Class<T> mValueType;

      /**
       * @param dir 文件夹用于保存json string
       * @param valueType type of value
       */
      public GsonFileMapper (File dir, Class<T> valueType) {

            this.mValueType = valueType;
            mDir = dir;
      }

      /**
       * @param url from this url to get json
       *
       * @return file to save json string
       */
      @Override
      public File keyToFile (String url) {

            return new File(mDir, NameFunction.nameFromMd5(url));
      }

      @Override
      public void writeToFile (String key, T value) throws IOException {

            File file = keyToFile(key);
            if(file.exists()) {
                  file.delete();
            }

            FileOutputStream outputStream = null;
            try {

                  String s = sGson.toJson(value, mValueType);
                  outputStream = new FileOutputStream(file);
                  outputStream.write(s.getBytes());
            } finally {

                  if(outputStream != null) {

                        outputStream.close();
                  }
            }
      }



      @Override
      public T fileToValue (File file) throws IOException {

            if(file.exists()) {

                  FileInputStream inputStream = null;
                  Reader reader = null;
                  try {
                        inputStream = new FileInputStream(file);
                        reader = new InputStreamReader(inputStream);
                        return sGson.fromJson(reader, mValueType);
                  } finally {

                        if(reader != null) {
                              try {
                                    reader.close();
                              } catch(IOException e) {
                                    e.printStackTrace();
                              }
                        }

                        if(inputStream != null) {
                              try {
                                    inputStream.close();
                              } catch(IOException e) {
                                    e.printStackTrace();
                              }
                        }
                  }
            }

            return null;
      }
}
