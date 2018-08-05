package tech.threekilogram.depository;

import java.io.File;
import java.io.IOException;
import tech.threekilogram.depository.file.converter.FileStringConverter;
import tech.threekilogram.depository.file.impl.DiskLruCacheLoader;
import tech.threekilogram.depository.file.impl.FileLoader;
import tech.threekilogram.depository.memory.lru.MemoryLruCacheLoader;
import tech.threekilogram.depository.memory.map.MemoryMapLoader;

/**
 * @author: Liujin
 * @version: V1.0
 * @date: 2018-08-05
 * @time: 22:14
 */
public class Test {

      private static final File TEMP = new File("Temp");

      public static void main (String[] args) {


      }

      private static void testDiskLruFileLoader () {

            DiskLruCacheLoader<String, String> loader = null;
            try {
                  loader = new DiskLruCacheLoader<>(
                      TEMP, 1024 * 1024 * 50, new FileStringConverter());

                  String key = "Hello";

                  loader.save(key, "World");

                  String load = loader.load(key);
                  System.out.println(load);

                  String remove = loader.remove(key);
                  System.out.println(remove);

                  boolean containsOf = loader.containsOf(key);
                  System.out.println(containsOf);
            } catch(IOException e) {
                  e.printStackTrace();
            }
      }

      private static void testFileLoader () {

            FileLoader<String, String> loader = new FileLoader<>(TEMP, new FileStringConverter());

            String key = "Hello";

            loader.save(key, "World");
            File file = loader.getFile(key);
            System.out.println(file.getAbsolutePath());
            String load = loader.load(key);
            System.out.println(load);

            String remove = loader.remove(key);
            System.out.println(remove);
            file = loader.getFile(key);
            System.out.println(file.getAbsolutePath());

            boolean containsOf = loader.containsOf(key);
            System.out.println(containsOf);
      }

      private static void testMemoryLru () {

            MemoryLruCacheLoader<String, String> loader = new MemoryLruCacheLoader<>();

            String key0 = "key0";
            String key1 = "key1";
            String key2 = "key2";

            String value0 = "value0";
            String value1 = "value1";
            String value2 = "value2";

            loader.save(key0, value0);
            loader.save(key1, value1);
            loader.save(key2, value2);

            int size = loader.size();
            System.out.println(size);

            String load = loader.load(key0);
            System.out.println(load);

            String remove = loader.remove(key0);
            System.out.println(remove);

            boolean b = loader.containsOf(key0);
            System.out.println(b);

            loader.save(key0, value0);
            int size1 = loader.size();
            System.out.println(size1);

            loader.clear();
            int size2 = loader.size();
            System.out.println(size2);
      }

      private static void testMemoryMap () {

            MemoryMapLoader<String, String> loader = new MemoryMapLoader<>();

            String key0 = "key0";
            String key1 = "key1";
            String key2 = "key2";

            String value0 = "value0";
            String value1 = "value1";
            String value2 = "value2";

            loader.save(key0, value0);
            loader.save(key1, value1);
            loader.save(key2, value2);

            int size = loader.size();
            System.out.println(size);

            String load = loader.load(key0);
            System.out.println(load);

            String remove = loader.remove(key0);
            System.out.println(remove);

            boolean b = loader.containsOf(key0);
            System.out.println(b);

            loader.save(key0, value0);
            int size1 = loader.size();
            System.out.println(size1);

            loader.clear();
            int size2 = loader.size();
            System.out.println(size2);
      }
}
