package tech.threekilogram.depository;

import android.support.v4.util.ArrayMap;
import android.support.v4.util.LruCache;
import com.google.gson.Gson;
import com.jakewharton.disklrucache.DiskLruCache;
import com.jakewharton.disklrucache.DiskLruCache.Editor;
import com.jakewharton.disklrucache.DiskLruCache.Snapshot;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import tech.threekilogram.depository.file.FileLoader;
import tech.threekilogram.depository.file.GsonFileMapper;
import tech.threekilogram.depository.file.StringByteFileMapper;
import tech.threekilogram.depository.file.StringStringFileMapper;
import tech.threekilogram.depository.file.lru.DiskLruCacheLoader;
import tech.threekilogram.depository.file.lru.DiskLruStringStringMapper;
import tech.threekilogram.depository.memory.MemoryListLoader;
import tech.threekilogram.depository.memory.MemoryLruCacheLoader;
import tech.threekilogram.depository.memory.MemoryMapLoader;
import tech.threekilogram.depository.net.NetMapper;
import tech.threekilogram.depository.net.retrofit.download.RetrofitDownLoader;
import tech.threekilogram.depository.net.retrofit.get.RetrofitGetLoader;
import tech.threekilogram.depository.net.retrofit.get.RetrofitStringMapper;

/**
 * @author: Liujin
 * @version: V1.0
 * @date: 2018-07-29
 * @time: 21:50
 */
public class TestLoader {

      public static void main (String[] args) {

            File dir = new File("gson");
            if(!dir.exists()) {
                  dir.mkdirs();
            }

            FileLoader<String, GankCategoryBean> loader = new FileLoader<>(
                new GsonFileMapper<>(dir, GankCategoryBean.class)
            );

            Gson gson = new Gson();
            GankCategoryBean gankCategoryBean = gson.fromJson(JSON, GankCategoryBean.class);

            int size = gankCategoryBean.results.size();
            System.out.println(size);

            final String key = "https://gank.io/api/data/%E7%A6%8F%E5%88%A9/10/1";
            try {
                  loader.writeToFile(key, gankCategoryBean);
                  boolean b = loader.fileExist(key);
                  System.out.println("write success : " + b);

                  GankCategoryBean loadFromFile = loader.loadFromFile(key);
                  System.out.println(loadFromFile.results.size());
            } catch(IOException e) {
                  e.printStackTrace();
            }
      }

      public static class GankCategoryBean {

            private boolean           error;
            private List<ResultsBean> results;

            public boolean isError () {

                  return error;
            }

            public void setError (boolean error) {

                  this.error = error;
            }

            public List<ResultsBean> getResults () {

                  return results;
            }

            public void setResults (List<ResultsBean> results) {

                  this.results = results;
            }

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

                  public String get_id () {

                        return _id;
                  }

                  public void set_id (String _id) {

                        this._id = _id;
                  }

                  public String getCreatedAt () {

                        return createdAt;
                  }

                  public void setCreatedAt (String createdAt) {

                        this.createdAt = createdAt;
                  }

                  public String getDesc () {

                        return desc;
                  }

                  public void setDesc (String desc) {

                        this.desc = desc;
                  }

                  public String getPublishedAt () {

                        return publishedAt;
                  }

                  public void setPublishedAt (String publishedAt) {

                        this.publishedAt = publishedAt;
                  }

                  public String getSource () {

                        return source;
                  }

                  public void setSource (String source) {

                        this.source = source;
                  }

                  public String getType () {

                        return type;
                  }

                  public void setType (String type) {

                        this.type = type;
                  }

                  public String getUrl () {

                        return url;
                  }

                  public void setUrl (String url) {

                        this.url = url;
                  }

                  public boolean isUsed () {

                        return used;
                  }

                  public void setUsed (boolean used) {

                        this.used = used;
                  }

                  public String getWho () {

                        return who;
                  }

                  public void setWho (String who) {

                        this.who = who;
                  }
            }
      }

      private static void testDownLoad () {

            File dir = new File("retrofit");
            if(!dir.exists()) {
                  boolean mkdirs = dir.mkdirs();
                  System.out.println("mkdirs : " + mkdirs);
            }

            File file = new File(dir, "image");

            RetrofitDownLoader<String, File> loader = new RetrofitDownLoader<>(
                file,
                new NetMapper<String, File, File>() {

                      @Override
                      public String keyToUrl (String key) {

                            return key;
                      }

                      @Override
                      public void errorCode (int errorCode) {

                            System.out.println(errorCode);
                      }

                      @Override
                      public File responseToValue (File response) {

                            return response;
                      }
                }
            );

            try {
                  File fromNet = loader.loadFromNet(
                      "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1533015251323&di=21c583689e08e2cee65c5d4f34614727&imgtype=0&src=http%3A%2F%2Fe.hiphotos.baidu.com%2Fimage%2Fpic%2Fitem%2F42a98226cffc1e17461390ed4690f603728de9ba.jpg");
                  System.out.println(fromNet);
            } catch(IOException e) {
                  e.printStackTrace();
            }
      }

      private static void testNet () {

            RetrofitGetLoader<String, String> loader =
                new RetrofitGetLoader<>(new RetrofitStringMapper());

            try {
                  String s = loader.loadFromNet("https://gank.io/api/today");
                  System.out.println(s);
            } catch(IOException e) {
                  e.printStackTrace();
            }
      }

      private static void testDiskStringString () {

            try {
                  File file = new File("Disk");
                  long maxSize = 10 * 1024 * 1024;
                  DiskLruCacheLoader<String, String> diskLruCacheLoader = new DiskLruCacheLoader<>(
                      file,
                      maxSize,
                      new DiskLruStringStringMapper()
                  );

                  final String key = "HelloWorld".toLowerCase();

                  diskLruCacheLoader.writeToFile(key, "Hello world diskLruCache");
                  boolean fileExist = diskLruCacheLoader.fileExist(key);
                  System.out.println(fileExist);

                  String value = diskLruCacheLoader.loadFromFile(key);
                  System.out.println(value);
            } catch(IOException e) {
                  e.printStackTrace();
            }
      }

      private static void testDiskLruCache () {

            File file = new File("Disk");
            long maxSize = 10 * 1024 * 1024;
            try {
                  DiskLruCache diskLruCache = DiskLruCache.open(file, 1, 1, maxSize);
                  Editor editor = diskLruCache.edit("file0");
                  OutputStream outputStream = editor.newOutputStream(0);
                  String hello = "Hello disk Lru cache";
                  outputStream.write(hello.getBytes());
                  outputStream.close();
                  editor.commit();

                  Snapshot snapshot = diskLruCache.get("file0");
                  InputStream inputStream = snapshot.getInputStream(0);
                  int available = inputStream.available();
                  byte[] bytes = new byte[available];
                  inputStream.read(bytes);
                  String result = new String(bytes);
                  System.out.println(result);
            } catch(IOException e) {
                  e.printStackTrace();
            }
      }

      private static void testStringString () {

            FileLoader<String, String> loader = new FileLoader<>(
                new StringStringFileMapper());

            String data = "Hello World";

            try {
                  loader.writeToFile("Test", data);
                  boolean test = loader.fileExist("Test");
                  System.out.println("save success : " + test);
                  String result = loader.loadFromFile("Test");
                  System.out.println(result);

                  boolean world = loader.fileExist("World");
                  System.out.println("World key exist :" + world);
                  String worlds = loader.loadFromFile("World");
                  System.out.println(worlds);
            } catch(IOException e) {
                  e.printStackTrace();
            }
      }

      private static void testStringByte () {

            FileLoader<String, byte[]> loader = new FileLoader<>(
                new StringByteFileMapper());

            String data = "Hello World";

            try {
                  loader.writeToFile("Test", data.getBytes());
                  boolean test = loader.fileExist("Test");
                  System.out.println("save success : " + test);
                  byte[] bytes = loader.loadFromFile("Test");
                  String result = new String(bytes);
                  System.out.println(result);

                  boolean world = loader.fileExist("World");
                  System.out.println("World key exist :" + world);
                  byte[] worlds = loader.loadFromFile("World");
                  System.out.println(worlds);
            } catch(IOException e) {
                  e.printStackTrace();
            }
      }

      private static void readFile () {

            File file = new File("Temp");
            File dataSave = new File(file, "data");
            FileInputStream inputStream = null;
            try {
                  inputStream = new FileInputStream(dataSave);
                  byte[] bytes = new byte[(int) dataSave.length()];
                  System.out.println("data length : " + dataSave.length());
                  int read = inputStream.read(bytes);
                  System.out.println("read : " + read);
                  inputStream.close();
                  String result = new String(bytes);
                  System.out.println(result);
            } catch(IOException e) {
                  e.printStackTrace();
            }
      }

      private static void writeFile () {

            File file = new File("Temp");
            if(!file.exists()) {
                  boolean mkdirs = file.mkdirs();
                  if(mkdirs) {

                        System.out.println("mk dirs success");
                  } else {

                        System.out.println("mk dirs failed");
                  }
            }

            String data = "该方法的描述是这样的："
                + "返回可以不受阻塞地从此文件输入流中读取的字节数 "
                + "如上，由于是从网络中获取数据，由于存在着网络延迟等因素，"
                + "所以也就不难理解 两次输出不一致了 "
                + "当然，如果是读取本地文件的话，这个方法返回的数据大小一般是真实的。"
                + "因此，如何使用以及要不要用这个方法，得根据具体的场景";

            File dataSave = new File(file, "data");
            try {
                  FileOutputStream outputStream = new FileOutputStream(dataSave);
                  outputStream.write(data.getBytes());
                  outputStream.close();

                  long length = dataSave.length();
                  System.out.println("file length: " + length + " ; data length: " + data.length());
            } catch(IOException e) {
                  e.printStackTrace();
            }
      }

      public static void testLruLoader () {

            MemoryLruCacheLoader<String, Person> loader = new MemoryLruCacheLoader<>(12);

            Person person = new Person("Liu", "Jin", 28);
            Person person1 = new Person("Liu", "Qing", 29);
            Person person2 = new Person("Wu", "Xiong", 28);

            final String key = "Person";
            final String key1 = "Person1";
            final String key2 = "Person2";

            loader.putInToMemory(key, person);
            loader.putInToMemory(key1, person1);
            loader.putInToMemory(key2, person2);

            Person newPerson2 = new Person("Sun", "Qing", 29);
            Person personOld2 = loader.putInToMemory(key2, newPerson2);
            System.out.println("old value is : " + personOld2);

            LruCache<String, Person> container = loader.container();
            System.out.println("container is : " + container + " size is : " + container.size());

            System.out.println(
                "contains a value to  " + key2 + " : " + loader.containsOfKey(key2));

            final String emptyKey = "Empty";

            System.out.println(
                "contains a value to  " + emptyKey + " : " + loader
                    .containsOfKey(emptyKey));

            Person person3 = loader.loadFromMemory(key2);
            System.out.println("value to " + key2 + " is : " + person3);
            Person person4 = loader.loadFromMemory(emptyKey);
            System.out.println("value to Empty is : " + person4);

            Person person5 = loader.removeFromMemory(key2);
            System.out.println("removed value to " + key2 + " is : " + person5);

            loader.clear();
            System.out.println("after clear " + container.size());
      }

      public static void testMapLoader () {

            MemoryMapLoader<String, Person> loader = new MemoryMapLoader<>();

            Person person = new Person("Liu", "Jin", 28);
            Person person1 = new Person("Liu", "Qing", 29);
            Person person2 = new Person("Wu", "Xiong", 28);

            final String key = "Person";
            final String key1 = "Person1";
            final String key2 = "Person2";

            loader.putInToMemory(key, person);
            loader.putInToMemory(key1, person1);
            loader.putInToMemory(key2, person2);

            Person newPerson2 = new Person("Sun", "Qing", 29);
            Person personOld2 = loader.putInToMemory(key2, newPerson2);
            System.out.println("old value is : " + personOld2);

            ArrayMap<String, Person> container = loader.container();
            System.out.println("container is : " + container + " size is : " + container.size());

            System.out.println(
                "contains a value to  " + key2 + " : " + loader.containsOfKey(key2));

            final String emptyKey = "Empty";

            System.out.println(
                "contains a value to  " + emptyKey + " : " + loader
                    .containsOfKey(emptyKey));

            Person person3 = loader.loadFromMemory(key2);
            System.out.println("value to " + key2 + " is : " + person3);
            Person person4 = loader.loadFromMemory(emptyKey);
            System.out.println("value to Empty is : " + person4);

            Person person5 = loader.removeFromMemory(key2);
            System.out.println("removed value to " + key2 + " is : " + person5);

            loader.clear();
            System.out.println("after clear " + container.size());
      }

      public static class Person {

            final String firstName;
            final String lastName;
            final int    age;

            public Person (String firstName, String lastName, int age) {

                  this.firstName = firstName;
                  this.lastName = lastName;
                  this.age = age;
            }

            @Override
            public String toString () {

                  return "Person{" +
                      "firstName='" + firstName + '\'' +
                      ", lastName='" + lastName + '\'' +
                      ", age=" + age +
                      '}';
            }
      }

      public static void testListLoader () {

            MemoryListLoader<String> loader = new MemoryListLoader<>();

            String item = "Hello";
            String item1 = "World";
            String item2 = "Memory";

            loader.putInToMemory(0, item);
            loader.putInToMemory(1, item1);
            loader.putInToMemory(2, item2);

            item2 = "NewMemory";
            String oldValue = loader.putInToMemory(2, item2);
            System.out.println("old value is : " + oldValue);

            ArrayList<String> container = loader.container();
            System.out.println("container is : " + container + " size is : " + container.size());

            System.out.println("contains a value to 5 : " + loader.containsOfKey(5));
            System.out.println("contains a value to 2 : " + loader.containsOfKey(2));

            String value = loader.loadFromMemory(2);
            System.out.println("value to 2 is : " + value);
            value = loader.loadFromMemory(5);
            System.out.println("value to 5 is : " + value);

            String remove = loader.removeFromMemory(2);
            System.out.println("removed value to 2 is : " + remove);

            loader.clear();
            System.out.println("after clear " + container.size());
      }

      private static final String JSON = "{\n"
          + "    \"error\": false,\n"
          + "    \"results\": [\n"
          + "        {\n"
          + "            \"_id\": \"5b5e93499d21220fc64181a9\",\n"
          + "            \"createdAt\": \"2018-07-30T12:25:45.937Z\",\n"
          + "            \"desc\": \"2018-07-30\",\n"
          + "            \"publishedAt\": \"2018-07-30T00:00:00.0Z\",\n"
          + "            \"source\": \"web\",\n"
          + "            \"type\": \"福利\",\n"
          + "            \"url\": \"https://ww1.sinaimg.cn/large/0065oQSqgy1ftrrvwjqikj30go0rtn2i.jpg\",\n"
          + "            \"used\": true,\n"
          + "            \"who\": \"lijinshanmx\"\n"
          + "        },\n"
          + "        {\n"
          + "            \"_id\": \"5b50107f421aa917a31c0565\",\n"
          + "            \"createdAt\": \"2018-07-19T12:15:59.226Z\",\n"
          + "            \"desc\": \"2018-07-19\",\n"
          + "            \"publishedAt\": \"2018-07-19T00:00:00.0Z\",\n"
          + "            \"source\": \"web\",\n"
          + "            \"type\": \"福利\",\n"
          + "            \"url\": \"https://ww1.sinaimg.cn/large/0065oQSqly1ftf1snjrjuj30se10r1kx.jpg\",\n"
          + "            \"used\": true,\n"
          + "            \"who\": \"lijinshanmx\"\n"
          + "        },\n"
          + "        {\n"
          + "            \"_id\": \"5b4eaae4421aa93aa99bee16\",\n"
          + "            \"createdAt\": \"2018-07-18T11:14:55.648Z\",\n"
          + "            \"desc\": \"2018-07-18\",\n"
          + "            \"publishedAt\": \"2018-07-18T00:00:00.0Z\",\n"
          + "            \"source\": \"web\",\n"
          + "            \"type\": \"福利\",\n"
          + "            \"url\": \"https://ww1.sinaimg.cn/large/0065oQSqly1ftdtot8zd3j30ju0pt137.jpg\",\n"
          + "            \"used\": true,\n"
          + "            \"who\": \"lijinshanmx\"\n"
          + "        },\n"
          + "        {\n"
          + "            \"_id\": \"5b481d01421aa90bba87b9ae\",\n"
          + "            \"createdAt\": \"2018-07-13T11:31:13.266Z\",\n"
          + "            \"desc\": \"2018-07-13\",\n"
          + "            \"publishedAt\": \"2018-07-13T00:00:00.0Z\",\n"
          + "            \"source\": \"web\",\n"
          + "            \"type\": \"福利\",\n"
          + "            \"url\": \"http://ww1.sinaimg.cn/large/0073sXn7ly1ft82s05kpaj30j50pjq9v.jpg\",\n"
          + "            \"used\": true,\n"
          + "            \"who\": \"lijinshanmx\"\n"
          + "        },\n"
          + "        {\n"
          + "            \"_id\": \"5b456f5d421aa92fc4eebe48\",\n"
          + "            \"createdAt\": \"2018-07-11T10:45:49.246Z\",\n"
          + "            \"desc\": \"2018-07-11\",\n"
          + "            \"publishedAt\": \"2018-07-11T00:00:00.0Z\",\n"
          + "            \"source\": \"web\",\n"
          + "            \"type\": \"福利\",\n"
          + "            \"url\": \"https://ww1.sinaimg.cn/large/0065oQSqly1ft5q7ys128j30sg10gnk5.jpg\",\n"
          + "            \"used\": true,\n"
          + "            \"who\": \"lijinshanmx\"\n"
          + "        },\n"
          + "        {\n"
          + "            \"_id\": \"5b441f06421aa92fccb520a2\",\n"
          + "            \"createdAt\": \"2018-07-10T10:50:46.379Z\",\n"
          + "            \"desc\": \"2018-07-10\",\n"
          + "            \"publishedAt\": \"2018-07-10T00:00:00.0Z\",\n"
          + "            \"source\": \"web\",\n"
          + "            \"type\": \"福利\",\n"
          + "            \"url\": \"https://ww1.sinaimg.cn/large/0065oQSqgy1ft4kqrmb9bj30sg10fdzq.jpg\",\n"
          + "            \"used\": true,\n"
          + "            \"who\": \"lijinshanmx\"\n"
          + "        },\n"
          + "        {\n"
          + "            \"_id\": \"5b42d1aa421aa92d1cba2918\",\n"
          + "            \"createdAt\": \"2018-07-09T11:08:26.162Z\",\n"
          + "            \"desc\": \"2018-07-09\",\n"
          + "            \"publishedAt\": \"2018-07-09T00:00:00.0Z\",\n"
          + "            \"source\": \"web\",\n"
          + "            \"type\": \"福利\",\n"
          + "            \"url\": \"http://ww1.sinaimg.cn/large/0065oQSqly1ft3fna1ef9j30s210skgd.jpg\",\n"
          + "            \"used\": true,\n"
          + "            \"who\": \"lijinshanmx\"\n"
          + "        },\n"
          + "        {\n"
          + "            \"_id\": \"5b3ed2d5421aa91cfe803e35\",\n"
          + "            \"createdAt\": \"2018-07-06T10:24:21.907Z\",\n"
          + "            \"desc\": \"2018-07-06\",\n"
          + "            \"publishedAt\": \"2018-07-06T00:00:00.0Z\",\n"
          + "            \"source\": \"web\",\n"
          + "            \"type\": \"福利\",\n"
          + "            \"url\": \"http://ww1.sinaimg.cn/large/0065oQSqly1fszxi9lmmzj30f00jdadv.jpg\",\n"
          + "            \"used\": true,\n"
          + "            \"who\": \"lijinshanmx\"\n"
          + "        },\n"
          + "        {\n"
          + "            \"_id\": \"5b3d883f421aa906e5b3c6f1\",\n"
          + "            \"createdAt\": \"2018-07-05T10:53:51.361Z\",\n"
          + "            \"desc\": \"2018-07-05\",\n"
          + "            \"publishedAt\": \"2018-07-05T00:00:00.0Z\",\n"
          + "            \"source\": \"web\",\n"
          + "            \"type\": \"福利\",\n"
          + "            \"url\": \"http://ww1.sinaimg.cn/large/0065oQSqly1fsysqszneoj30hi0pvqb7.jpg\",\n"
          + "            \"used\": true,\n"
          + "            \"who\": \"lijinshanmx\"\n"
          + "        },\n"
          + "        {\n"
          + "            \"_id\": \"5b3ae394421aa906e7db029b\",\n"
          + "            \"createdAt\": \"2018-07-03T10:46:44.112Z\",\n"
          + "            \"desc\": \"2018-07-03\",\n"
          + "            \"publishedAt\": \"2018-07-03T00:00:00.0Z\",\n"
          + "            \"source\": \"web\",\n"
          + "            \"type\": \"福利\",\n"
          + "            \"url\": \"http://ww1.sinaimg.cn/large/0065oQSqly1fswhaqvnobj30sg14hka0.jpg\",\n"
          + "            \"used\": true,\n"
          + "            \"who\": \"lijinshanmx\"\n"
          + "        }\n"
          + "    ]\n"
          + "}";
}
