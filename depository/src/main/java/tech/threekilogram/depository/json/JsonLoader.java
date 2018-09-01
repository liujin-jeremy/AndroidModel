package tech.threekilogram.depository.json;

import android.support.v4.util.ArrayMap;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import okhttp3.ResponseBody;
import tech.threekilogram.depository.file.BaseFileConverter;
import tech.threekilogram.depository.file.BaseFileLoader;
import tech.threekilogram.depository.file.loader.FileLoader;
import tech.threekilogram.depository.memory.map.MemoryList;
import tech.threekilogram.depository.net.LoadingUrls;
import tech.threekilogram.depository.net.retrofit.BaseRetrofitConverter;
import tech.threekilogram.depository.net.retrofit.loader.RetrofitLoader;

/**
 * @author liujin
 */
@SuppressWarnings("WeakerAccess")
public class JsonLoader<V> {

      /**
       * 内存
       */
      protected MemoryList<V>           mMemoryList;
      /**
       * 文件
       */
      protected BaseFileLoader<V>       mFileContainer;
      /**
       * 网络
       */
      protected RetrofitLoader<List<V>> mRetrofitLoader;
      /**
       * 辅助将流转换为json bean
       */
      protected JsonConverter<V>        mJsonConverter;
      /**
       * 记录所有正在加载的url
       */
      protected       LoadingUrls          mLoadingUrls     = new LoadingUrls();
      /**
       * 管理APP运行期间所有缓存的索引
       */
      protected       CachedIndexes        mIndexes         = new CachedIndexes();
      /**
       * 临时保存需要需要写入文件的bean
       */
      protected final ArrayMap<Integer, V> mWillCacheToFile = new ArrayMap<>();
      /**
       * true : 正在缓存文件
       */
      protected final AtomicBoolean        mIsCacheToFile   = new AtomicBoolean();
      /**
       * 内存中数据多少监听,当达到阈值时回调
       */
      protected OnMemorySizeTooLargeListener mOnMemorySizeTooLargeListener;

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
                new JsonRetrofitListConverter()
            );
      }

      /**
       * 从内存和本地缓存文件中测试是否包含该key对应的数据缓存
       *
       * @param index index
       *
       * @return true:有该key对应的缓存,否则没有
       */
      public boolean containsOf ( int index ) {

            if( mMemoryList.containsOf( index ) ) {
                  return true;
            }

            return mFileContainer.containsOf( String.valueOf( index ) );
      }

      /**
       * 从网络加载一组数据
       *
       * @param url url
       *
       * @return 数据
       */
      public List<V> loadMore ( String url ) {

            if( mLoadingUrls.isLoading( url ) ) {
                  return null;
            }
            List<V> list = mRetrofitLoader.load( url );
            mLoadingUrls.removeLoadingUrl( url );
            return list;
      }

      /**
       * 保存一组数据
       *
       * @param index 起始索引
       * @param list 数据
       */
      public void saveMore ( int index, List<V> list ) {

            mMemoryList.saveMore( index, list );

            mIndexes.updateIndex( index, index + list.size() - 1 );

            notifyMemorySizeListener();
      }

      /**
       * 从网络加载更多数据
       *
       * @param index 加载完成后以该索引为起点保存数据
       * @param url url
       */
      public void loadMore ( int index, String url ) {

            List<V> list = loadMore( url );

            if( list == null || list.size() == 0 ) {
                  return;
            }
            saveMore( index, list );
      }

      private void notifyMemorySizeListener ( ) {

            if( mOnMemorySizeTooLargeListener != null ) {

                  int size = mMemoryList.size();
                  int maxMemorySize = mOnMemorySizeTooLargeListener.getMaxMemorySize();
                  if( size >= maxMemorySize ) {

                        mOnMemorySizeTooLargeListener.onMemorySizeTooLarge( size );
                  }
            }
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
       * 获取程序运行期间所有缓存数据中最小的index.{@link #loadMore(int, String)}
       *
       * @return index
       */
      public int getCachedMin ( ) {

            return mIndexes.getMinIndex();
      }

      /**
       * 获取程序运行期间所有缓存数据中最大的index.{@link #loadMore(int, String)}
       *
       * @return index
       */
      public int getCachedMax ( ) {

            return mIndexes.getMaxIndex();
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
       * 返回内存中数据数量
       *
       * @return 数据量
       */
      public int getMemorySize ( ) {

            return mMemoryList.size();
      }

      /**
       * 测试方法,开发时没用
       */
      private void printMemory ( ) {

            int size = mMemoryList.size();
            ArrayMap<Integer, V> container = mMemoryList.container();
            for( int i = 0; i < container.size(); i++ ) {
                  Integer key = container.keyAt( i );
                  V v = container.valueAt( i );
            }
      }

      /**
       * 压缩内存,将内存中数据缓存到本地缓存,同时在内存中删除,
       * 如果本地已经有缓存文件那么不会覆盖它,清除缓存文件请使用{@link #clearFile(int)}{@link #clearAllFile()}
       *
       * @param start 内存中保留数据最小索引
       * @param end 内存中保留数据最大索引,不包含该索引[start,end)
       */
      @SuppressWarnings("UnnecessaryLocalVariable")
      public void trimMemory ( int start, int end ) {

            ArrayMap<Integer, V> container = mMemoryList.container();
            ArrayMap<Integer, V> willCacheToFile = mWillCacheToFile;

            for( int i = 0; i < container.size(); i++ ) {

                  Integer key = container.keyAt( i );
                  if( key < start || key >= end ) {

                        V v = container.removeAt( i );
                        synchronized(mWillCacheToFile) {
                              willCacheToFile.put( key, v );
                        }
                        i--;
                  }
            }

            notifyCacheFile();
      }

      /**
       * 压缩内存,仅将指定索引数据缓存到本地缓存,同时在内存中删除
       *
       * @param index 需要缓存索引
       */
      public void trimMemory ( int index ) {

            V v = mMemoryList.remove( index );
            if( v != null ) {

                  synchronized(mWillCacheToFile) {
                        mWillCacheToFile.put( index, v );
                  }
                  notifyCacheFile();
            }
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
            BaseFileLoader<V> fileContainer = mFileContainer;

            while( willCacheToFile.size() > 0 ) {

                  Integer key = null;
                  V v = null;

                  synchronized(mWillCacheToFile) {

                        key = willCacheToFile.keyAt( 0 );
                        v = willCacheToFile.removeAt( 0 );
                  }

                  String s = key.toString();
                  File file = fileContainer.getFile( s );
                  if( !file.exists() ) {
                        fileContainer.save( s, v );
                  }
            }

            mIsCacheToFile.set( false );
      }

      /**
       * 从缓存文件夹加载缓存
       *
       * @param index 索引
       *
       * @return 缓存值
       */
      public V loadFile ( int index ) {

            String key = String.valueOf( index );
            V v = mFileContainer.load( key );
            if( v != null ) {

                  mMemoryList.save( index, v );
                  mIndexes.updateIndex( index, index );
                  notifyMemorySizeListener();
                  return v;
            }
            return null;
      }

      /**
       * 清除指定索引缓存
       */
      public void clearFile ( int index ) {

            mFileContainer.remove( String.valueOf( index ) );
      }

      /**
       * 清除所有文件缓存
       */
      public void clearAllFile ( ) {

            try {
                  mFileContainer.clear();
            } catch(IOException e) {
                  e.printStackTrace();
            }
      }

      /**
       * 清除所有缓存包括内存/文件
       */
      public void clearCache ( ) {

            mMemoryList.clear();
            try {
                  mFileContainer.clear();
            } catch(IOException e) {
                  e.printStackTrace();
            }
            mIndexes.clear();
      }

      /**
       * 清除所有缓存,仅保留指定区间数据[start,end)
       *
       * @param start 起始索引
       * @param end 结束索引
       */
      public void clearCache ( int start, int end ) {

            if( start > end ) {
                  return;
            }

            CachedIndexes indexes = mIndexes;
            indexes.clear();

            ArrayMap<Integer, V> temp = new ArrayMap<>();
            for( int i = start; i < end; i++ ) {

                  V v = loadMemory( i );
                  if( v != null ) {

                        temp.put( i, v );
                        indexes.updateIndex( i, i );
                  } else {

                        v = loadFile( i );
                        if( v != null ) {
                              temp.put( i, v );
                              indexes.updateIndex( i, i );
                        }
                  }
            }

            mMemoryList.clear();
            try {
                  mFileContainer.clear();
            } catch(IOException e) {
                  e.printStackTrace();
            }

            mMemoryList.saveAll( temp );
      }

      public void setOnMemorySizeTooLargeListener (
          OnMemorySizeTooLargeListener onMemorySizeTooLargeListener ) {

            mOnMemorySizeTooLargeListener = onMemorySizeTooLargeListener;
      }

      public OnMemorySizeTooLargeListener getOnMemorySizeTooLargeListener ( ) {

            return mOnMemorySizeTooLargeListener;
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

            private int count ( ) {

                  ArrayList<Node> nodes = mNodes;
                  int result = 0;
                  for( Node node : nodes ) {
                        result += node.count();
                  }

                  return result;
            }

            private int getMinIndex ( ) {

                  if( mNodes.size() == 0 ) {
                        return -1;
                  }

                  return mNodes.get( 0 ).low;
            }

            private int getMaxIndex ( ) {

                  if( mNodes.size() == 0 ) {
                        return -1;
                  }

                  return mNodes.get( mNodes.size() - 1 ).high;
            }

            private synchronized void clear ( ) {

                  mNodes.clear();
            }
      }

      /**
       * 内存中数据大小监听
       */
      public interface OnMemorySizeTooLargeListener {

            /**
             * 配置内存中数据最大数量
             *
             * @return 内存最大数据
             */
            int getMaxMemorySize ( );

            /**
             * 当内存中数据
             *
             * @param memorySize 当前内存中数据大小
             */
            void onMemorySizeTooLarge ( int memorySize );
      }
}
