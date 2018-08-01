package tech.threekilogram.depository.client;

import com.google.gson.Gson;
import tech.threekilogram.depository.file.common.FileLoader;
import tech.threekilogram.depository.global.GsonClient;
import tech.threekilogram.depository.memory.MemoryListLoader;
import tech.threekilogram.depository.net.retrofit.download.RetrofitDownLoader;

/**
 * @author: Liujin
 * @version: V1.0
 * @date: 2018-08-01
 * @time: 8:21
 */
public class JsonListLoad<V> implements ClientLoadSupport<String, V> {

      private MemoryListLoader<V>           mMemoryListLoader;
      private FileLoader<String, V>         mFileLoader;
      private RetrofitDownLoader<String, V> mRetrofitDownLoader;
      private Gson                          mGson= GsonClient.INSTANCE;

      public JsonListLoad () {

            mMemoryListLoader = new MemoryListLoader<>();
            //mFileLoader=new FileLoader<>()
      }

      @Override
      public void prepareValue (String key) {

      }

      @Override
      public void onValuePrepared (V value) {

      }
}
