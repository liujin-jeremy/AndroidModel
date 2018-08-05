package tech.threekilogram.depository;

import java.io.File;
import java.io.IOException;
import tech.threekilogram.depository.file.converter.StringFileConverter;
import tech.threekilogram.depository.file.impl.DiskLruCacheLoader;
import tech.threekilogram.depository.function.Md5Function;

/**
 * @author: Liujin
 * @version: V1.0
 * @date: 2018-08-05
 * @time: 14:01
 */
public class Test {

      public static void main (String[] args) {

            File file = new File("Temp");
            try {
                  DiskLruCacheLoader<String, String> loader =
                      new DiskLruCacheLoader<>(
                          file,
                          1024 * 1024 * 10,
                          new StringFileConverter()
                      );

                  String key = "HelloLruCache";

                  String s = Md5Function.nameFromMd5(key);
                  System.out.println(s);
                  s = s + ".0";
                  File temp = new File(file, s);

                  System.out.println(temp.exists());

                  loader.save(key, "HelloWorld");
            } catch(IOException e) {
                  e.printStackTrace();
            }
      }
}
