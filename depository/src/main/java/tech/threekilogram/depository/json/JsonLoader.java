package tech.threekilogram.depository.json;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import okhttp3.ResponseBody;
import tech.threekilogram.depository.file.BaseFileContainer;
import tech.threekilogram.depository.file.BaseFileConverter;
import tech.threekilogram.depository.file.loader.DiskLruContainer;
import tech.threekilogram.depository.file.loader.FileContainer;
import tech.threekilogram.depository.memory.map.MemoryList;
import tech.threekilogram.depository.net.retrofit.BaseRetrofitConverter;
import tech.threekilogram.depository.net.retrofit.loader.RetrofitLoader;

/**
 * @author: Liujin
 * @version: V1.0
 * @date: 2018-08-17
 * @time: 13:48
 */
public class JsonLoader<V> {

      /**
       * 内存
       */
      protected MemoryList<V>           mMemoryList;
      /**
       * 文件
       */
      protected BaseFileContainer<V>    mFileContainer;
      /**
       * 网络
       */
      protected RetrofitLoader<List<V>> mRetrofitLoader;
      /**
       * 辅助将流转换为json bean
       */
      protected JsonConverter<V>        mJsonConverter;

      /**
       * 已缓存数量
       */
      protected int mCacheCount;
      /**
       * memory 最多保存数目
       */
      protected int mMemoryCount = 100;
      /**
       * 内存中保存的数据最小的index
       */
      protected int mMixIndex    = 0;
      /**
       * 内存中保存的数据最大的index
       */
      protected int mMaxIndex    = 0;
      /**
       * 是否正在加载
       */
      private transient boolean isLoading;

      public JsonLoader ( File dir, JsonConverter<V> jsonConverter ) {

            mJsonConverter = jsonConverter;

            mMemoryList = new MemoryList<>();

            mFileContainer = new FileContainer<>(
                dir,
                new JsonFileConverter()
            );

            mRetrofitLoader = new RetrofitLoader<>(
                new JsonRetrofitListConverter()
            );
      }

      public JsonLoader (
          File dir,
          int maxFileSize,
          JsonConverter<V> jsonConverter ) throws IOException {

            mJsonConverter = jsonConverter;

            mMemoryList = new MemoryList<>();

            mFileContainer = new DiskLruContainer<>(
                dir,
                maxFileSize,
                new JsonFileConverter()
            );

            mRetrofitLoader = new RetrofitLoader<>(
                new JsonRetrofitListConverter()
            );
      }

      public void setStartIndex ( int index ) {

            mMixIndex = index;
            mMixIndex = index;
      }

      public void setMemoryCount ( int memoryCount ) {

            mMemoryCount = memoryCount;
      }

      /**
       * 加载更多数据
       *
       * @param url url
       */
      public void loadMore ( String url ) {

            isLoading = true;
            List<V> load = mRetrofitLoader.load( url );
            mMemoryList.saveMore( mMaxIndex + 1, load );
            int size = load.size();
            mMixIndex += size;
            mCacheCount += size;
            isLoading = false;
      }

      /**
       * 加载更少数据
       *
       * @param url url
       */
      public void loadLess ( String url ) {

            isLoading = true;
            List<V> load = mRetrofitLoader.load( url );
            mMemoryList.saveLess( mMixIndex - 1, load );
            int size = load.size();
            mMixIndex -= size;
            mCacheCount += size;
            isLoading = false;
      }

      private void reSize ( boolean fromStart ) {

            int size = mMemoryList.size();
            if( size <= mMemoryCount ) {
                  return;
            }

            if( fromStart ) {

                  int limit = mMixIndex + mMemoryCount;

                  for( int i = 0; i < size; i++ ) {

                        int index = mMixIndex + i;

                        V load = mMemoryList.load( index );

                        if( index > limit ) {

                              String key = String.valueOf( index );
                              V remove = mFileContainer.remove( key );
                              mFileContainer.save( key, remove );
                        } else {

                              if( load == null ) {
                                    String key = String.valueOf( index );
                                    V v = mFileContainer.load( key );
                                    mMemoryList.save( index, v );
                              }
                        }
                  }

                  mMaxIndex -= size;
            } else {

                  int limit = mMaxIndex - mMemoryCount;

                  for( int i = 0; i < size; i++ ) {

                        int index = mMixIndex + i;

                        V load = mMemoryList.load( index );

                        if( index >= limit ) {

                              if( load == null ) {
                                    String key = String.valueOf( index );
                                    V v = mFileContainer.load( key );
                                    mMemoryList.save( index, v );
                              }
                        } else {

                              String key = String.valueOf( index );
                              V remove = mFileContainer.remove( key );
                              mFileContainer.save( key, remove );
                        }
                  }

                  mMixIndex += size;
            }
      }

      /**
       * @return true : 正在加载
       */
      public boolean isLoading ( ) {

            return isLoading;
      }

      public V get ( int index ) {

            V load = mMemoryList.load( index );
            if( load == null ) {

                  load = mFileContainer.load( String.valueOf( index ) );
            }

            return null;
      }

      /**
       * 辅助保存json为文件
       */
      private class JsonFileConverter extends BaseFileConverter<V> {

            @Override
            public String fileName ( String key ) {

                  return mKeyNameConverter.encodeToName( key );
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
}
