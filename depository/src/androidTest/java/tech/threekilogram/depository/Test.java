package tech.threekilogram.depository;

import com.google.gson.Gson;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import tech.threekilogram.depository.file.converter.FileJsonConverter;
import tech.threekilogram.depository.file.converter.FileStringConverter;
import tech.threekilogram.depository.file.loader.DiskLruLoader;
import tech.threekilogram.depository.file.loader.FileLoader;
import tech.threekilogram.depository.function.encode.Md5;
import tech.threekilogram.depository.function.encode.StringHash;
import tech.threekilogram.depository.function.instance.GsonClient;
import tech.threekilogram.depository.memory.lru.MemoryLruCache;
import tech.threekilogram.depository.memory.map.MemoryList;
import tech.threekilogram.depository.memory.map.MemoryMap;
import tech.threekilogram.depository.net.retrofit.converter.RetrofitJsonConverter;
import tech.threekilogram.depository.net.retrofit.converter.RetrofitStringConverter;
import tech.threekilogram.depository.net.retrofit.loader.RetrofitLoader;

/**
 * @author: Liujin
 * @version: V1.0
 * @date: 2018-08-05
 * @time: 22:14
 */
class Test {

      private static final java.io.File TEMP     = new java.io.File( "Temp" );
      private static final int          MAX_SIZE = 1024 * 1024 * 20;
      private static final String       JSON     = "{\n"
          + "    \"error\": false,\n"
          + "    \"results\": [\n"
          + "        {\n"
          + "            \"_id\": \"5b7102749d2122341d563844\",\n"
          + "            \"createdAt\": \"2018-08-13T12:00:52.458Z\",\n"
          + "            \"desc\": \"2018-08-13\",\n"
          + "            \"publishedAt\": \"2018-08-13T00:00:00.0Z\",\n"
          + "            \"source\": \"api\",\n"
          + "            \"type\": \"福利\",\n"
          + "            \"url\": \"https://ww1.sinaimg.cn/large/0065oQSqly1fu7xueh1gbj30hs0uwtgb.jpg\",\n"
          + "            \"used\": true,\n"
          + "            \"who\": \"lijinshan\"\n"
          + "        }\n"
          + "    ]\n"
          + "}";

      public static void main ( String[] args ) {

            testMemoryList();
      }

      private static void testMemoryList ( ) {

            /* MemoryList 使用数字索引保存/读取数据 */
            MemoryList<String> loader = new MemoryList<>();

            String value = "HelloAndroidModel - 0";
            String value1 = "HelloAndroidModel - 1";
            String value2 = "HelloAndroidModel - 2";

            loader.save( 0, value );
            loader.save( 1, value1 );
            loader.save( 2, value2 );

            int size = loader.size();
            System.out.println( "size: " + size );

            String valueLoad0 = loader.load( 0 );
            System.out.println( "value at 0: " + valueLoad0 );

            boolean containsOf = loader.containsOf( 2 );
            System.out.println( "contains of 2: " + containsOf );

            loader.remove( 2 );
            containsOf = loader.containsOf( 2 );
            System.out.println( "after remove contains of 2: " + containsOf );

            loader.clear();
            size = loader.size();
            System.out.println( "after clear size: " + size );

            List<String> values = new ArrayList<>();
            for( int i = 0; i < 3; i++ ) {

                  values.add( " item " + String.valueOf( i ) );
            }

            loader.saveMore( 3, values );

            String load = loader.load( 3 );
            System.out.println( load );
            String load1 = loader.load( 4 );
            System.out.println( load1 );
            String load2 = loader.load( 5 );
            System.out.println( load2 );

            loader.saveLess( 9, values );
            String load3 = loader.load( 9 );
            System.out.println( load3 );
            String load4 = loader.load( 8 );
            System.out.println( load4 );
            String load5 = loader.load( 7 );
            System.out.println( load5 );
      }

      private static void testRetrofitJson ( ) {

            RetrofitLoader<Bean> loader = new RetrofitLoader<>(
                new RetrofitJsonConverter<Bean>( Bean.class ) );

            final String url = "https://gank.io/api/data/%E7%A6%8F%E5%88%A9/2/1";

            Bean bean = loader.load( url );
            System.out.println( bean.getResults().size() );
            System.out.println( bean.getResults().get( 0 ).url );
      }

      private static void testRetrofitString ( ) {

            RetrofitLoader<String> loader = new RetrofitLoader<>( new RetrofitStringConverter() );

            final String url = "https://gank.io/api/data/%E7%A6%8F%E5%88%A9/2/1";

            String load = loader.load( url );
            System.out.println( load );
      }

      private static void testNetDownloader ( ) {

            RetrofitDowner loader = new RetrofitDowner( TEMP );
            final String url00 = "https://ww1.sinaimg.cn/large/0065oQSqly1fu7xueh1gbj30hs0uwtgb.jpg";
            java.io.File file = loader.load( url00 );
            System.out.println( file );

            java.io.File load = loader.load( url00 );
            System.out.println( load );

            final String url01 = "https://ww1.sinaimg.cn/large/0065oQSqgy1fu39hosiwoj30j60qyq96.jpg";
            java.io.File load1 = loader.load( url01 );
            System.out.println( load1 );
      }

      private static void testDiskJson ( ) {

            try {
                  DiskLruLoader<Bean> loader = new DiskLruLoader<>(
                      TEMP,
                      MAX_SIZE,
                      new FileJsonConverter<Bean>( Bean.class )
                  );

                  String key = "json";

                  Gson gson = GsonClient.INSTANCE;
                  Bean bean = gson.fromJson( JSON, Bean.class );
                  System.out.println( bean.getResults().get( 0 ).url );

                  loader.save( key, bean );

                  Bean load = loader.load( key );
                  System.out.println( load.getResults().get( 0 ).url );

                  java.io.File file = loader.getFile( key );
                  System.out.println( "file: " + file );

                  loader.remove( key );
                  boolean containsOf = loader.containsOf( key );
                  System.out.println( "after remove value exist: " + containsOf );
            } catch(IOException e) {
                  e.printStackTrace();
            }
      }

      private static void testFileJson ( ) {

            FileLoader<Bean> loader = new FileLoader<>(
                TEMP,
                new FileJsonConverter<Bean>( Bean.class )
            );

            String key = "json";

            Gson gson = GsonClient.INSTANCE;
            Bean bean = gson.fromJson( JSON, Bean.class );
            System.out.println( bean.getResults().get( 0 ).url );

            loader.save( key, bean );

            Bean load = loader.load( key );
            System.out.println( load.getResults().get( 0 ).url );

            java.io.File file = loader.getFile( key );
            System.out.println( "file: " + file );

            loader.remove( key );
            boolean containsOf = loader.containsOf( key );
            System.out.println( "after remove value exist: " + containsOf );
      }

      private static void testDiskString ( ) {

            try {
                  DiskLruLoader<String> loader = new DiskLruLoader<>(
                      TEMP,
                      MAX_SIZE,
                      new FileStringConverter()
                  );

                  String key = "key";
                  String value = "曾经沧海难为水";

                  loader.save( key, value );

                  String load = loader.load( key );
                  System.out.println( "save value: " + key + " " + load );

                  java.io.File file = loader.getFile( key );
                  System.out.println( key + " path: " + file );

                  String remove = loader.remove( key );
                  boolean containsOf = loader.containsOf( key );
                  System.out.println( "after remove value exist: " + containsOf );
            } catch(IOException e) {
                  e.printStackTrace();
            }
      }

      private static void testFileString ( ) {

            FileLoader<String> loader = new FileLoader<>( TEMP, new FileStringConverter() );
            String key = "key";
            String value = "曾经沧海难为水";

            loader.save( key, value );

            String load = loader.load( key );
            System.out.println( "save value: " + key + " " + load );

            java.io.File file = loader.getFile( key );
            System.out.println( key + " path: " + file );

            String remove = loader.remove( key );
            boolean containsOf = loader.containsOf( key );
            System.out.println( "after remove value exist: " + containsOf );
      }

      private static void testMemoryLru ( ) {

            /* MemoryList 使用数字索引保存/读取数据 */
            MemoryLruCache<String, String> loader = new MemoryLruCache<>( 3 );

            String value = "HelloAndroidModel - 0";
            String value1 = "HelloAndroidModel - 1";
            String value2 = "HelloAndroidModel - 2";

            String key = "key";
            String key1 = "key1";
            String key2 = "key2";

            loader.save( key, value );
            loader.save( key1, value1 );
            loader.save( key2, value2 );

            int size = loader.size();
            System.out.println( "size: " + size );

            String valueLoad0 = loader.load( key );
            System.out.println( "value at key: " + valueLoad0 );

            boolean containsOf = loader.containsOf( key2 );
            System.out.println( "contains of key2: " + containsOf );

            loader.remove( key2 );
            containsOf = loader.containsOf( key2 );
            System.out.println( "after remove contains of key2: " + containsOf );

            loader.clear();
            size = loader.size();
            System.out.println( "after clear size: " + size );

            loader.save( key, value );
            loader.save( key1, value1 );
            loader.save( key2, value2 );
            loader.save( "extra", "extra" );
            size = loader.size();
            System.out.println( "after add 4 item size: " + size );
            String load = loader.load( key );
            System.out.println( "value at key: " + load );
      }

      private static void testMemoryMap ( ) {

            /* MemoryList 使用数字索引保存/读取数据 */
            MemoryMap<String, String> loader = new MemoryMap<>();

            String value = "HelloAndroidModel - 0";
            String value1 = "HelloAndroidModel - 1";
            String value2 = "HelloAndroidModel - 2";

            String key = "key";
            String key1 = "key1";
            String key2 = "key2";

            loader.save( key, value );
            loader.save( key1, value1 );
            loader.save( key2, value2 );

            int size = loader.size();
            System.out.println( "size: " + size );

            String valueLoad0 = loader.load( key );
            System.out.println( "value at key: " + valueLoad0 );

            boolean containsOf = loader.containsOf( key2 );
            System.out.println( "contains of key2: " + containsOf );

            loader.remove( key2 );
            containsOf = loader.containsOf( key2 );
            System.out.println( "after remove contains of key2: " + containsOf );

            loader.clear();
            size = loader.size();
            System.out.println( "after clear size: " + size );
      }

      private static void testShortName ( ) {

            final String url = "https://gank.io/api/data/Android/10/%d";

            for( int i = 0; i < 10; i++ ) {

                  String s = String.format( url, i );
                  String md5 = Md5.encode( s );
                  String hash = StringHash.hash( s );
                  System.out.println( md5 );
                  System.out.println( hash );
            }
      }

      private static class Bean {

            private boolean           error;
            private List<ResultsBean> results;

            public boolean isError ( ) { return error;}

            public void setError ( boolean error ) { this.error = error;}

            public List<ResultsBean> getResults ( ) { return results;}

            public void setResults ( List<ResultsBean> results ) { this.results = results;}

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

                  public String get_id ( ) { return _id;}

                  public void set_id ( String _id ) { this._id = _id;}

                  public String getCreatedAt ( ) { return createdAt;}

                  public void setCreatedAt ( String createdAt ) { this.createdAt = createdAt;}

                  public String getDesc ( ) { return desc;}

                  public void setDesc ( String desc ) { this.desc = desc;}

                  public String getPublishedAt ( ) { return publishedAt;}

                  public void setPublishedAt (
                      String publishedAt ) { this.publishedAt = publishedAt;}

                  public String getSource ( ) { return source;}

                  public void setSource ( String source ) { this.source = source;}

                  public String getType ( ) { return type;}

                  public void setType ( String type ) { this.type = type;}

                  public String getUrl ( ) { return url;}

                  public void setUrl ( String url ) { this.url = url;}

                  public boolean isUsed ( ) { return used;}

                  public void setUsed ( boolean used ) { this.used = used;}

                  public String getWho ( ) { return who;}

                  public void setWho ( String who ) { this.who = who;}
            }
      }
}
