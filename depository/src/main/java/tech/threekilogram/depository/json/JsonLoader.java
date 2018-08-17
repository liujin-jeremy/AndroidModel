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
       * 当前index
       */
      protected int mCurrentIndex = 0;
      /**
       * memory 最多保存数目
       */
      protected int mMemoryCount  = 50;

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
