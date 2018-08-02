package tech.threekilogram.depository;

import java.io.File;
import tech.threekilogram.depository.memory.lru.MemoryLruCacheLoader;
import tech.threekilogram.depository.memory.map.MemoryMapLoader;

/**
 * test this lib
 */
class Test {

      /* create a dir to save temp data */

      private static final File TEMP = new File("Temp");

      static {
            if(!TEMP.exists()) {
                  boolean mkdirs = TEMP.mkdirs();
            } else {
                  if(TEMP.isFile()) {
                        boolean delete = TEMP.delete();
                        boolean mkdirs = TEMP.mkdirs();
                  }
            }
      }

      public static void main (String[] args) {

      }

      private static void testMemoryLru () {

            MemoryLruCacheLoader<String, String> loader = new MemoryLruCacheLoader<>(3);

            int size = loader.size();
            System.out.println("init size " + size);

            final String key0 = "name";
            final String value0 = "LiuQing";
            final String key1 = "poet";
            final String value1 = "曾经沧海难为水,除却巫山不是云";
            final String key2 = "msg";
            final String value2 = "Hello Loader";

            final String keyEmpty = "key99";

            loader.save(key0, value0);
            loader.save(key1, value1);
            loader.save(key2, value2);

            System.out.println("saved size : " + loader.size());

            boolean containsOf = loader.containsOf(keyEmpty);
            System.out.println("containsOf " + keyEmpty + " : " + containsOf);

            String s = loader.load(key1);
            System.out.println("load " + key1 + " : " + s);

            final String key3 = "extra";
            final String value3 = "this is extra msg";

            loader.save(key3, value3);

            String load = loader.load(key0);
            System.out.println("after put 4 item load " + key0 + " : " + load);
            load = loader.load(key1);
            System.out.println("after put 4 item load " + key1 + " : " + load);
            load = loader.load(key2);
            System.out.println("after put 4 item load " + key2 + " : " + load);
            load = loader.load(key3);
            System.out.println("after put 4 item load " + key3 + " : " + load);

            final String valueAnotherPoet = "取次花丛懒回顾,半缘修道半缘君";
            String save = loader.save(key1, valueAnotherPoet);
            System.out.println("original value : " + key1 + " : " + save);
            System.out.println("new saved value : " + loader.load(key1));

            String remove = loader.remove(key3);
            System.out.println("removed : " + key3 + " : " + remove);

            loader.clear();
            System.out.println("clear size : " + loader.size());
      }

      private static void testMemoryMap () {

            MemoryMapLoader<String, String> loader = new MemoryMapLoader<>();

            int size = loader.size();
            System.out.println("init size " + size);

            final String key0 = "name";
            final String value0 = "LiuQing";
            final String key1 = "poet";
            final String value1 = "曾经沧海难为水,除却巫山不是云";
            final String key2 = "msg";
            final String value2 = "Hello Loader";

            final String keyEmpty = "key99";

            loader.save(key0, value0);
            loader.save(key1, value1);
            loader.save(key2, value2);

            System.out.println("saved size : " + loader.size());

            boolean containsOf = loader.containsOf(keyEmpty);
            System.out.println("containsOf " + keyEmpty + " : " + containsOf);

            String s = loader.load(key1);
            System.out.println("load " + key1 + " : " + s);

            final String valueAnotherPoet = "取次花丛懒回顾,半缘修道半缘君";
            String save = loader.save(key1, valueAnotherPoet);
            System.out.println("original value : " + key1 + " : " + save);
            System.out.println("new saved value : " + loader.load(key1));

            String remove = loader.remove(key1);
            System.out.println("removed : " + key1 + " : " + remove);

            loader.clear();
            System.out.println("clear size : " + loader.size());
      }
}
