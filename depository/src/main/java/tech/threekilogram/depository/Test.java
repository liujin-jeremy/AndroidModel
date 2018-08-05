package tech.threekilogram.depository;

import java.io.File;
import java.io.IOException;
import java.util.List;
import tech.threekilogram.depository.file.converter.FileStringConverter;
import tech.threekilogram.depository.file.impl.DiskLruCacheLoader;
import tech.threekilogram.depository.file.impl.FileLoader;
import tech.threekilogram.depository.memory.lru.MemoryLruCacheLoader;
import tech.threekilogram.depository.memory.map.MemoryMapLoader;
import tech.threekilogram.depository.net.retrofit.RetrofitStreamLoader;
import tech.threekilogram.depository.net.retrofit.convert.down.RetrofitUrlDiskLruConverter;
import tech.threekilogram.depository.net.retrofit.convert.down.RetrofitUrlFileConverter;
import tech.threekilogram.depository.net.retrofit.convert.json.RetrofitUrlGsonConverter;
import tech.threekilogram.depository.net.retrofit.convert.string.RetrofitUrlStringConverter;

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

      private static void testDiskRetrofit () {

            RetrofitStreamLoader<String, File> loader = new RetrofitStreamLoader<>(
                new RetrofitUrlDiskLruConverter(TEMP, 1024 * 1024 * 50));

            String url = "https://gank.io/api/data/%E7%A6%8F%E5%88%A9/10/1";

            File load = loader.load(url);

            System.out.println(load.getAbsolutePath());
      }

      private static void testFileRetrofit () {

            RetrofitStreamLoader<String, File> loader = new RetrofitStreamLoader<>(
                new RetrofitUrlFileConverter(TEMP));

            String url = "https://gank.io/api/data/%E7%A6%8F%E5%88%A9/10/1";

            File load = loader.load(url);

            System.out.println(load.getAbsolutePath());
      }

      private static void testJson () {

            String url = "https://gank.io/api/data/%E7%A6%8F%E5%88%A9/10/1";

            RetrofitStreamLoader<String, Bean> loader = new RetrofitStreamLoader<>(
                new RetrofitUrlGsonConverter<Bean>(Bean.class));

            Bean load = loader.load(url);

            System.out.println(load.getResults().size());
            System.out.println(load.getResults().get(0).url);
      }

      private static void testRetrofitString () {

            RetrofitStreamLoader<String, String> loader = new RetrofitStreamLoader<>(
                new RetrofitUrlStringConverter());

            String url = "http://gank.io/api/today ";

            String value = loader.load(url);

            System.out.println(value);
      }

      private static class Bean {

            private boolean           error;
            private List<ResultsBean> results;

            public boolean isError () { return error;}

            public void setError (boolean error) { this.error = error;}

            public List<ResultsBean> getResults () { return results;}

            public void setResults (List<ResultsBean> results) { this.results = results;}

            public static class ResultsBean {

                  private String  _id;
                  private String  createdAt;
                  private String  desc;
                  private String  publishedAt;
                  private String  source;
                  private String  type;
                  private String  url;
                  private boolean used;
                  private String  who;

                  public String get_id () { return _id;}

                  public void set_id (String _id) { this._id = _id;}

                  public String getCreatedAt () { return createdAt;}

                  public void setCreatedAt (String createdAt) { this.createdAt = createdAt;}

                  public String getDesc () { return desc;}

                  public void setDesc (String desc) { this.desc = desc;}

                  public String getPublishedAt () { return publishedAt;}

                  public void setPublishedAt (String publishedAt) { this.publishedAt = publishedAt;}

                  public String getSource () { return source;}

                  public void setSource (String source) { this.source = source;}

                  public String getType () { return type;}

                  public void setType (String type) { this.type = type;}

                  public String getUrl () { return url;}

                  public void setUrl (String url) { this.url = url;}

                  public boolean isUsed () { return used;}

                  public void setUsed (boolean used) { this.used = used;}

                  public String getWho () { return who;}

                  public void setWho (String who) { this.who = who;}
            }
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
