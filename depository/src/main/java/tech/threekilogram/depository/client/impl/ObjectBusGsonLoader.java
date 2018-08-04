package tech.threekilogram.depository.client.impl;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.util.ArraySet;
import com.example.objectbus.ObjectBusConfig;
import com.example.objectbus.bus.ObjectBus;
import com.example.objectbus.bus.ObjectBusStation;
import com.example.objectbus.message.Messengers;
import com.example.objectbus.message.OnMessageReceiveListener;
import java.io.File;
import java.io.IOException;
import tech.threekilogram.depository.client.AsyncLoader;
import tech.threekilogram.depository.file.FileLoadSupport.ExceptionHandler;
import tech.threekilogram.depository.file.converter.GsonFileConverter;
import tech.threekilogram.depository.file.impl.FileLoader;
import tech.threekilogram.depository.memory.map.MemoryMapLoader;
import tech.threekilogram.depository.net.NetLoader;
import tech.threekilogram.depository.net.retrofit.RetrofitGsonConverter;

/**
 * @author: Liujin
 * @version: V1.0
 * @date: 2018-08-04
 * @time: 9:58
 */
public class ObjectBusGsonLoader<V> implements AsyncLoader<String, V>, OnMessageReceiveListener {

      /* init lib */

      static {

            ObjectBusConfig.init();
      }

      /**
       * 内存缓存
       */
      private MemoryMapLoader<String, V> mMemoryLoader;
      /**
       * 文件缓存
       */
      private FileLoader<String, V>      mFileLoader;
      /**
       * 网络缓存
       */
      private NetLoader<String, V>       mNetLoader;
      /**
       * 临时保存正在加载的key
       */
      private ArraySet<String>           mLoadingKeys;

      /**
       * 创建一个缓存客户端
       *
       * @param memoryLoader 内存
       * @param fileLoader 文件
       * @param netLoader 网络
       */
      public ObjectBusGsonLoader (
          @Nullable MemoryMapLoader<String, V> memoryLoader,
          @Nullable FileLoader<String, V> fileLoader,
          @NonNull NetLoader<String, V> netLoader) {

            mMemoryLoader = memoryLoader;
            mFileLoader = fileLoader;
            mNetLoader = netLoader;
      }

      /**
       * @param cacheDir 缓存文件夹
       * @param valueType value 类型
       */
      public ObjectBusGsonLoader (File cacheDir, Class<V> valueType) {

            mMemoryLoader = new MemoryMapLoader<>();

            mFileLoader = new FileLoader<>(cacheDir, new GsonFileConverter<>(valueType));
            mFileLoader.setExceptionHandler(new FileExceptionHandler());

            mNetLoader = new NetLoader<>(new RetrofitGsonExceptionHandledConverter(valueType));

            mLoadingKeys = new ArraySet<>();
      }

      @Override
      public void prepareValue (final String key) {

            /* 防止重复加载请求 */

            if(mLoadingKeys.contains(key)) {
                  return;
            }
            /* record this key now is loading */
            mLoadingKeys.add(key);

            /* from memory */

            if(mMemoryLoader != null) {

                  if(loadFromMemory(key)) {
                        return;
                  }
            }

            /* back ground thread pool do this */

            final ObjectBus objectBus = ObjectBusStation.callNewBus();
            objectBus.toUnder(new Runnable() {

                  @Override
                  public void run () {

                        /* from file */

                        if(mFileLoader != null) {

                              if(loadFromFile(key, objectBus)) {
                                    return;
                              }
                        }

                        /* from net */

                        loadFromNet(key, objectBus);
                  }
            }).run();
      }

      private boolean loadFromMemory (String key) {

            V load = mMemoryLoader.load(key);
            if(load != null) {
                  Messengers.send(LOAD_FROM_MEMORY, load, ObjectBusGsonLoader.this);
                  mLoadingKeys.remove(key);
                  return true;
            }
            return false;
      }

      private boolean loadFromFile (String key, ObjectBus objectBus) {

            V load = mFileLoader.load(key);
            if(load != null) {

                  /* get from file */

                  Messengers.send(LOAD_FROM_FILE, load, ObjectBusGsonLoader.this);
                  mLoadingKeys.remove(key);
                  ObjectBusStation.recycle(objectBus);
                  return true;
            }
            return false;
      }

      private void loadFromNet (String key, ObjectBus bus) {

            V load = mNetLoader.load(key);

            if(load != null) {

                  /* load success */

                  Messengers.send(
                      LOAD_FROM_NET,
                      load,
                      ObjectBusGsonLoader.this
                  );

                  /* save to container */

                  if(mMemoryLoader != null) {

                        mMemoryLoader.save(key, load);
                  }
                  if(mFileLoader != null) {

                        mFileLoader.save(key, load);
                  }
            } else {

                  /* load failed in all way */

                  Messengers.send(LOAD_NOTHING, ObjectBusGsonLoader.this);
            }

            /* remove recorder */

            mLoadingKeys.remove(key);
            ObjectBusStation.recycle(bus);
      }

      @Override
      public void onValuePrepared (int result, V value) {

      }

      // ========================= get result from backThread there =========================

      @SuppressWarnings("unchecked")
      @Override
      public void onReceive (int what, Object extra) {

            onValuePrepared(what, (V) extra);
      }

      @Override
      public void onReceive (int what) {

            onValuePrepared(what, null);
      }

      // ========================= 内部类 =========================

      private class FileExceptionHandler implements ExceptionHandler<String, V> {

            @Override
            public void onConvertToValue (Exception e, String key) {

                  Messengers.send(LOAD_FROM_FILE_CONVERT_EXCEPTION, ObjectBusGsonLoader.this);
            }

            @Override
            public void onSaveValueToFile (IOException e, String key, V value) {

            }
      }

      private class RetrofitGsonExceptionHandledConverter extends RetrofitGsonConverter<V> {

            public RetrofitGsonExceptionHandledConverter (Class<V> valueType) {

                  super(valueType);
            }

            @Override
            protected void onConnectException (String key, String url, IOException e) {

                  super.onConnectException(key, url, e);
                  Messengers.send(LOAD_NET_CANT_CONNECT, ObjectBusGsonLoader.this);
            }

            @Override
            protected void onConvertException (String key, String url, Exception e) {

                  super.onConvertException(key, url, e);
                  Messengers.send(LOAD_FROM_NET_CONVERT_EXCEPTION, ObjectBusGsonLoader.this);
            }
      }
}
