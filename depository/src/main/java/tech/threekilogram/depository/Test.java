package tech.threekilogram.depository;

import java.io.File;
import java.util.List;
import tech.threekilogram.depository.memory.lru.MemoryLruCacheLoader;
import tech.threekilogram.depository.memory.map.MemoryListLoader;
import tech.threekilogram.depository.memory.map.MemoryMapLoader;

/**
 * @author: Liujin
 * @version: V1.0
 * @date: 2018-08-05
 * @time: 22:14
 */
class Test {

      private static final File TEMP = new File( "Temp" );

      public static void main ( String[] args ) {

      }

      private static void testMemoryLru ( ) {

            /* MemoryListLoader 使用数字索引保存/读取数据 */
            MemoryLruCacheLoader<String, String> loader = new MemoryLruCacheLoader<>( 3 );

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

            /* MemoryListLoader 使用数字索引保存/读取数据 */
            MemoryMapLoader<String, String> loader = new MemoryMapLoader<>();

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

      private static void testMemoryList ( ) {

            /* MemoryListLoader 使用数字索引保存/读取数据 */
            MemoryListLoader<String> loader = new MemoryListLoader<>();

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
