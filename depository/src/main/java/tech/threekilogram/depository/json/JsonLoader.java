package tech.threekilogram.depository.json;

import android.support.v4.util.ArrayMap;
import android.support.v4.util.ArraySet;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import okhttp3.ResponseBody;
import tech.threekilogram.depository.file.BaseFileConverter;
import tech.threekilogram.depository.file.loader.FileLoader;
import tech.threekilogram.depository.memory.map.MemoryList;
import tech.threekilogram.depository.net.retrofit.BaseRetrofitConverter;
import tech.threekilogram.depository.net.retrofit.loader.RetrofitLoader;

/**
 * @author liujin
 */
public abstract class JsonLoader<V> {

      private static final String TAG = JsonLoader.class.getSimpleName();

      /**
       * 内存
       */
      protected MemoryList<V>           mMemoryList;
      /**
       * 文件
       */
      protected FileLoader<V>           mFileContainer;
      /**
       * 网络
       */
      protected RetrofitLoader<List<V>> mRetrofitLoader;
      /**
       * 辅助将流转换为json bean
       */
      protected JsonConverter<V>        mJsonConverter;
      /**
       * 管理APP运行期间所有缓存的索引
       */
      protected       CachedIndexes        mIndexes              = new CachedIndexes();
      /**
       * 缓存文件过期策略,一旦过期就重新创建
       */
      protected       CacheExpiredStrategy mCacheExpiredStrategy = new DefaultCacheExpiredStrategy();
      /**
       * 正在加载的url集合
       */
      protected final ArraySet<String>     mUrlLoadings          = new ArraySet<>();
      /**
       * 临时保存需要需要写入文件的bean
       */
      protected final ArrayMap<Integer, V> mWillCacheToFile      = new ArrayMap<>();
      /**
       * true : 正在缓存文件
       */
      protected final AtomicBoolean        mIsCacheToFile        = new AtomicBoolean();

      /**
       * @param dir 缓存文件夹
       * @param jsonConverter 转换流为bean对象
       */
      public JsonLoader ( File dir, JsonConverter<V> jsonConverter ) {

            mMemoryList = new MemoryList<>();

            mFileContainer = new FileLoader<>(
                dir,
                new JsonFileConverter()
            );

            mJsonConverter = jsonConverter;
            mRetrofitLoader = new RetrofitLoader<>(
                dir,
                100 * 1024 * 1024,
                new JsonRetrofitListConverter()
            );
      }

      /**
       * 设置缓存文件过期策略,如果没有设置,默认使用{@link DefaultCacheExpiredStrategy}
       */
      public void setCacheExpiredStrategy (
          CacheExpiredStrategy cacheExpiredStrategy ) {

            mCacheExpiredStrategy = cacheExpiredStrategy;
      }

      /**
       * 获取设置的缓存文件过期策略
       */
      public CacheExpiredStrategy getCacheExpiredStrategy ( ) {

            return mCacheExpiredStrategy;
      }

      /**
       * 从内存和本地缓存文件中测试是否包含该key对应的数据缓存,缓存过期将会认为没有该key缓存
       *
       * @param index index
       *
       * @return true:有该key对应的缓存,否则没有
       */
      public boolean containsOf ( int index ) {

            if( mMemoryList.containsOf( index ) ) {
                  return true;
            }

            File file = mFileContainer.getFile( String.valueOf( index ) );
            return file.exists() && mCacheExpiredStrategy.testCacheFile( file );
      }

      /**
       * 从网络加载更多数据
       *
       * @param index 加载完成后以该索引为起点保存数据
       * @param url url
       */
      public void loadMore ( int index, String url ) {

            /* 如果正在加载该url,那么不加载它 */
            synchronized(mUrlLoadings) {

                  if( mUrlLoadings.contains( url ) ) {
                        return;
                  } else {
                        mUrlLoadings.add( url );
                  }
            }

            List<V> list = mRetrofitLoader.load( url );

            /* 加载完成 */
            synchronized(mUrlLoadings) {
                  mUrlLoadings.remove( url );
            }

            if( list == null || list.size() == 0 ) {
                  return;
            }
            mMemoryList.saveMore( index, list );

            mIndexes.updateIndex( index, index + list.size() );
      }

      /**
       * 获取所有缓存数据数量
       *
       * @return 缓存数据数量
       */
      public int getCachedCount ( ) {

            return mIndexes.count();
      }

      /**
       * 从内存中读取
       *
       * @param index 索引
       *
       * @return 值
       */
      public V loadMemory ( int index ) {

            return mMemoryList.load( index );
      }

      /**
       * 压缩内存,将内存中数据缓存到本地缓存
       *
       * @param start 内存中保留数据最小索引
       * @param end 内存中保留数据最大索引,不包含该索引[start,end)
       */
      public void trimMemory ( int start, int end ) {

            ArrayMap<Integer, V> container = mMemoryList.container();
            ArrayMap<Integer, V> willCacheToFile = mWillCacheToFile;

            for( int i = 0; i < container.size(); i++ ) {

                  Integer key = container.keyAt( i );
                  if( key >= start && key < end ) {

                        V v = container.removeAt( i );
                        synchronized(mWillCacheToFile) {
                              willCacheToFile.put( key, v );
                        }
                  }

                  if( key >= end ) {
                        return;
                  }
            }

            notifyCacheFile();
      }

      /**
       * 通知缓存文件到本地
       */
      private void notifyCacheFile ( ) {

            if( mIsCacheToFile.get() ) {
                  return;
            }

            mIsCacheToFile.set( true );
            ArrayMap<Integer, V> willCacheToFile = mWillCacheToFile;
            FileLoader<V> fileContainer = mFileContainer;

            while( willCacheToFile.size() > 0 ) {

                  Integer key = null;
                  V v = null;

                  synchronized(mWillCacheToFile) {

                        key = willCacheToFile.keyAt( 0 );
                        v = willCacheToFile.removeAt( 0 );
                  }

                  String s = key.toString();
                  File file = fileContainer.getFile( s );
                  if( mCacheExpiredStrategy.testCacheFile( file ) ) {
                        fileContainer.save( s, v );
                  }
            }

            mIsCacheToFile.set( false );
      }

      /**
       * 从缓存文件夹加载缓存,如果缓存过期{@link CacheExpiredStrategy}那么返回null
       *
       * @param index 索引
       *
       * @return 缓存值
       */
      public V loadFile ( int index ) {

            String key = String.valueOf( index );
            File file = mFileContainer.getFile( key );

            if( !mCacheExpiredStrategy.testCacheFile( file ) ) {
                  return mFileContainer.load( key );
            }

            return null;
      }

// ========================= 辅助转换 =========================

      /**
       * 辅助保存json为文件
       */
      private class JsonFileConverter extends BaseFileConverter<V> {

            @Override
            public String fileName ( String key ) {

                  return key;
            }

            @Override
            public V toValue ( String key, InputStream stream ) throws Exception {

                  return mJsonConverter.fromJson( stream );
            }

            @Override
            public void saveValue ( String key, OutputStream outputStream, V value )
                throws IOException {

                  mJsonConverter.toJson( outputStream, value );
            }
      }

      /**
       * 辅助将网络资源流转为json bean list
       */
      private class JsonRetrofitListConverter extends BaseRetrofitConverter<List<V>> {

            @Override
            public List<V> onExecuteSuccess ( String key, ResponseBody response ) throws Exception {

                  return mJsonConverter.fromJsonArray( response.byteStream() );
            }
      }

// ========================= 缓存策略 =========================

      /**
       * 缓存过期策略
       */
      public interface CacheExpiredStrategy {

            /**
             * 测试缓存文件是否过期
             *
             * @param cache 缓存文件
             *
             * @return true 过期了
             */
            boolean testCacheFile ( File cache );
      }

      /**
       * 文件默认缓存策略,永不过期,如果需要清除缓存,在app启动时
       */
      public static class DefaultCacheExpiredStrategy implements CacheExpiredStrategy {

            /**
             * @param cache 缓存文件
             */
            @Override
            public boolean testCacheFile ( File cache ) {

                  return false;
            }
      }

      /**
       * 文件缓存策略,使用时间
       */
      public static class CacheExpiredStrategyTime implements CacheExpiredStrategy {

            /**
             * 过期时间
             */
            private long mExpiredTime;

            public CacheExpiredStrategyTime ( long expiredTime ) {

                  mExpiredTime = expiredTime;
            }

            /**
             * @param cache 缓存文件
             */
            @Override
            public boolean testCacheFile ( File cache ) {

                  long l = System.currentTimeMillis();
                  long lastModified = cache.lastModified();

                  return l - lastModified > mExpiredTime;
            }
      }

      // ========================= 记录所有已缓存数据索引 =========================

      private static class CachedIndexes {

            private ArrayList<Node> mNodes = new ArrayList<>();

            private class Node {

                  int low;
                  int high;

                  Node ( int low, int high ) {

                        this.low = low;
                        this.high = high;
                  }

                  int count ( ) {

                        return high - low + 1;
                  }
            }

            private synchronized void updateIndex ( int low, int high ) {

                  if( high < low ) {
                        int temp = high;
                        high = low;
                        low = temp;
                  }

                  ArrayList<Node> nodes = mNodes;

                  if( nodes.size() == 0 ) {
                        nodes.add( new Node( low, high ) );
                        return;
                  }

                  for( int i = 0; i < nodes.size(); i++ ) {

                        Node node = nodes.get( i );
                        int nodeLow = node.low;
                        int nodeHigh = node.high;

                        if( high < nodeLow ) {
                              mNodes.add( i, new Node( low, high ) );
                              return;
                        }

                        if( high <= nodeHigh ) {

                              if( low < nodeLow ) {
                                    node.low = low;
                                    reRange( i );
                                    return;
                              } else {
                                    return;
                              }
                        }

                        /* 以下high>nodeHigh恒为true */

                        if( low < nodeLow ) {
                              node.low = low;
                              if( high > nodeHigh ) {
                                    node.high = high;
                              }
                              reRange( i );
                        }

                        if( low <= nodeHigh ) {
                              if( high > nodeHigh ) {
                                    node.high = high;
                                    reRange( i );
                                    return;
                              } else {
                                    return;
                              }
                        }
                  }

                  mNodes.add( new Node( low, high ) );
            }

            private int count ( ) {

                  ArrayList<Node> nodes = mNodes;
                  int result = 0;
                  for( Node node : nodes ) {
                        result += node.count();
                  }

                  return result;
            }

            private void reRange ( int starIndex ) {

                  ArrayList<Node> nodes = mNodes;

                  if( starIndex == 0 ) {
                        return;
                  }

                  for( int i = starIndex; i < nodes.size(); i++ ) {

                        Node prev = nodes.get( i - 1 );
                        Node node = nodes.get( i );

                        if( node.low <= prev.high + 1 ) {

                              if( prev.high < node.high ) {
                                    prev.high = node.high;
                              }
                              nodes.remove( i );
                              i--;
                        }

                        try {
                              node = nodes.get( i );
                              Node next = nodes.get( i + 1 );
                              if( node.high + 1 < next.low ) {
                                    break;
                              }
                        } catch(Exception e) {

                              /* do nothing */
                        }
                  }
            }

            public static void main ( String[] args ) {

                  String s = String.valueOf( 5 );
                  Integer index = 5;
                  String s1 = index.toString();

                  System.out.println( s + " " + s1 );
            }
      }
}
