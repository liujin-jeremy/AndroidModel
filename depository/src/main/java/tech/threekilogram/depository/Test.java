package tech.threekilogram.depository;

import com.google.gson.Gson;
import java.io.File;
import java.util.List;
import tech.threekilogram.depository.file.converter.GsonFileConverter;
import tech.threekilogram.depository.file.converter.StringFileConverter;
import tech.threekilogram.depository.file.impl.DiskLruCacheLoader;
import tech.threekilogram.depository.file.impl.FileLoader;
import tech.threekilogram.depository.global.GsonClient;
import tech.threekilogram.depository.memory.lru.MemoryLruCacheLoader;
import tech.threekilogram.depository.memory.map.MemoryMapLoader;
import tech.threekilogram.depository.net.NetLoader;
import tech.threekilogram.depository.net.retrofit.RetrofitGsonConverter;
import tech.threekilogram.depository.net.retrofit.RetrofitStringConverter;

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

      public static final String JSON = "{\n"
          + "    \"error\": false,\n"
          + "    \"results\": [\n"
          + "        {\n"
          + "            \"_id\": \"5b6151509d21225206860f08\",\n"
          + "            \"createdAt\": \"2018-08-01T14:21:04.556Z\",\n"
          + "            \"desc\": \"2018-08-01\",\n"
          + "            \"publishedAt\": \"2018-08-01T00:00:00.0Z\",\n"
          + "            \"source\": \"api\",\n"
          + "            \"type\": \"福利\",\n"
          + "            \"url\": \"https://ww1.sinaimg.cn/large/0065oQSqly1ftu6gl83ewj30k80tites.jpg\",\n"
          + "            \"used\": true,\n"
          + "            \"who\": \"lijinshan\"\n"
          + "        },\n"
          + "        {\n"
          + "            \"_id\": \"5b60356a9d212247776a2e0e\",\n"
          + "            \"createdAt\": \"2018-07-31T18:09:46.825Z\",\n"
          + "            \"desc\": \"2018-07-31\",\n"
          + "            \"publishedAt\": \"2018-07-31T00:00:00.0Z\",\n"
          + "            \"source\": \"api\",\n"
          + "            \"type\": \"福利\",\n"
          + "            \"url\": \"http://ww1.sinaimg.cn/large/0065oQSqgy1ftt7g8ntdyj30j60op7dq.jpg\",\n"
          + "            \"used\": true,\n"
          + "            \"who\": \"lijinshan\"\n"
          + "        },\n"
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
          + "        }\n"
          + "    ]\n"
          + "}";

      public static void main (String[] args) {

            testNetLoaderGson();
      }

      private static void testNetLoaderGson () {

            NetLoader<String, JsonBean> loader = new NetLoader<>(
                new RetrofitGsonConverter<>(JsonBean.class));
            final String url = "https://gank.io/api/data/%E7%A6%8F%E5%88%A9/10/1";
            JsonBean jsonBean = loader.load(url);

            System.out.println(jsonBean.getResults().size());
      }

      private static void testNetLoaderString () {

            NetLoader<String, String> loader = new NetLoader<>(new RetrofitStringConverter());
            final String url = "https://gank.io/api/data/%E7%A6%8F%E5%88%A9/10/1";
            String load = loader.load(url);

            System.out.println(load);
      }

      private static void testGsonLru () {

            Gson gson = GsonClient.INSTANCE;
            JsonBean jsonBean = gson.fromJson(JSON, JsonBean.class);

            DiskLruCacheLoader<String, JsonBean> loader = new DiskLruCacheLoader<>(
                TEMP, 1024 * 1024,
                new GsonFileConverter<JsonBean>(JsonBean.class)
            );

            final String gank = "gank";
            loader.save(gank, jsonBean);

            boolean containsOf = loader.containsOf(gank);
            System.out.println("contains of : " + containsOf);

            JsonBean bean = loader.load(gank);
            System.out.println("size : " + bean.getResults().size());
      }

      private static void testGsonFile () {

            Gson gson = GsonClient.INSTANCE;
            JsonBean jsonBean = gson.fromJson(JSON, JsonBean.class);

            FileLoader<String, JsonBean> loader = new FileLoader<>(
                TEMP,
                new GsonFileConverter<JsonBean>(JsonBean.class)
            );

            final String gank = "gank";
            loader.save(gank, jsonBean);

            boolean containsOf = loader.containsOf(gank);
            System.out.println("contains of : " + containsOf);

            File file = loader.getFile(gank);
            System.out.println("path : " + file.getAbsolutePath());

            JsonBean bean = loader.load(gank);
            System.out.println("size : " + bean.getResults().size());
      }

      private static void testFileLoader () {

            FileLoader<String, String> loader = new FileLoader<>(TEMP, new StringFileConverter());

            final String key = "HelloLoader";
            final String msg = "HelloLoader:this is a msg";

            loader.save(key, msg);

            File file = loader.getFile(key);
            System.out.println("file path: " + file.getAbsolutePath() + " " + file.exists());

            boolean containsOf = loader.containsOf(key);
            System.out.println("contains of : " + containsOf);

            String load = loader.load(key);
            System.out.println("load --> " + key + " : " + load);

            String remove = loader.remove(key);
            String value = loader.load(key);
            System.out.println("after remove value : " + value);
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

      private static void testLruFileLoader () {

            final int size = 1024 * 1024;
            DiskLruCacheLoader<String, String> loader = new DiskLruCacheLoader<>(
                TEMP, size, new StringFileConverter());

            final String key = "HelloLoader";
            final String msg = "HelloLoader:this is a msg to DiskLru";

            loader.save(key, msg);

            boolean containsOf = loader.containsOf(key);
            System.out.println("contains of : " + containsOf);

            String load = loader.load(key);
            System.out.println("load --> " + key + " : " + load);

            String remove = loader.remove(key);
            String value = loader.load(key);
            System.out.println("after remove value : " + value);
      }

      public static class JsonBean {

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
}
