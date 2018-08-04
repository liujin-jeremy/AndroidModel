package tech.threekilogram.depository.client.impl;

import static tech.threekilogram.depository.client.OnValuePreparedListener.LOAD_FROM_FILE;
import static tech.threekilogram.depository.client.OnValuePreparedListener.LOAD_FROM_FILE_CONVERT_EXCEPTION;
import static tech.threekilogram.depository.client.OnValuePreparedListener.LOAD_FROM_MEMORY;
import static tech.threekilogram.depository.client.OnValuePreparedListener.LOAD_FROM_NET;
import static tech.threekilogram.depository.client.OnValuePreparedListener.LOAD_FROM_NET_CONVERT_EXCEPTION;
import static tech.threekilogram.depository.client.OnValuePreparedListener.LOAD_NET_CANT_CONNECT;
import static tech.threekilogram.depository.client.OnValuePreparedListener.LOAD_NOTHING;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.example.objectbus.ObjectBusConfig;
import com.example.objectbus.bus.ObjectBus;
import com.example.objectbus.bus.ObjectBusStation;
import com.example.objectbus.message.Messengers;
import com.example.objectbus.message.OnMessageReceiveListener;
import java.io.File;
import java.io.IOException;
import tech.threekilogram.depository.client.AsyncLoader;
import tech.threekilogram.depository.client.OnValuePreparedListener;
import tech.threekilogram.depository.file.FileLoadSupport.ExceptionHandler;
import tech.threekilogram.depository.file.converter.GsonFileConverter;
import tech.threekilogram.depository.file.impl.FileLoader;
import tech.threekilogram.depository.memory.MemoryLoadSupport;
import tech.threekilogram.depository.memory.lru.MemoryLruCacheLoader;
import tech.threekilogram.depository.net.NetLoader;
import tech.threekilogram.depository.net.retrofit.RetrofitGsonConverter;

/**
 * @author: Liujin
 * @version: V1.0
 * @date: 2018-08-04
 * @time: 9:58
 */
public class ObjectBusGsonLoader<V> implements AsyncLoader<String> {

      /* init lib */

      static {

            ObjectBusConfig.init();
      }

      /**
       * 内存缓存
       */
      private MemoryLoadSupport<String, V> mMemoryLoader;
      /**
       * 文件缓存
       */
      private FileLoader<String, V>        mFileLoader;
      /**
       * 网络缓存
       */
      private NetLoader<String, V>         mNetLoader;

      /**
       * 用户监听
       */
      private OnValuePreparedListener<V> mOnValuePreparedListener;
      /**
       * 线程调度
       */
      private OnMessageReceiveListener mOnMessageReceiveListener = new MessageReceiver();

      /**
       * 创建一个缓存客户端
       *
       * @param memoryLoader 内存
       * @param fileLoader 文件
       * @param netLoader 网络
       */
      public ObjectBusGsonLoader (
          @Nullable MemoryLoadSupport<String, V> memoryLoader,
          @Nullable FileLoader<String, V> fileLoader,
          @NonNull NetLoader<String, V> netLoader) {

            mMemoryLoader = memoryLoader;
            mFileLoader = fileLoader;
            mFileLoader.setExceptionHandler(new FileExceptionHandler());
            mNetLoader = netLoader;
      }

      /**
       * @param cacheDir 缓存文件夹
       * @param valueType value 类型
       */
      public ObjectBusGsonLoader (int memorySize, File cacheDir, Class<V> valueType) {

            mMemoryLoader = new MemoryLruCacheLoader<>(memorySize);

            mFileLoader = new FileLoader<>(cacheDir, new GsonFileConverter<>(valueType));
            mFileLoader.setExceptionHandler(new FileExceptionHandler());

            mNetLoader = new NetLoader<>(new RetrofitGsonExceptionHandledConverter(valueType));
      }

      public MemoryLoadSupport<String, V> getMemoryLoader () {

            return mMemoryLoader;
      }

      public FileLoader<String, V> getFileLoader () {

            return mFileLoader;
      }

      public NetLoader<String, V> getNetLoader () {

            return mNetLoader;
      }

      public OnValuePreparedListener<V> getOnValuePreparedListener () {

            return mOnValuePreparedListener;
      }

      public void setOnValuePreparedListener (
          OnValuePreparedListener<V> onValuePreparedListener) {

            mOnValuePreparedListener = onValuePreparedListener;
      }

      @Override
      public void prepareValue (final String key) {

            /* from memory */

            if(loadFromMemory(key)) {
                  return;
            }


            /* back ground thread pool do this */

            final ObjectBus objectBus = ObjectBusStation.callNewBus();
            objectBus.toUnder(new Runnable() {

                  @Override
                  public void run () {

                        /* from file */

                        if(loadFromFile(key)) {
                              ObjectBusStation.recycle(objectBus);
                              return;
                        }

                        /* from net */

                        loadFromNet(key);
                        ObjectBusStation.recycle(objectBus);
                  }
            }).run();
      }

      private boolean loadFromMemory (String key) {

            if(mMemoryLoader == null) {
                  return false;
            }

            V load = mMemoryLoader.load(key);
            if(load != null) {
                  Messengers.send(LOAD_FROM_MEMORY, load, mOnMessageReceiveListener);
                  return true;
            }
            return false;
      }

      private boolean loadFromFile (String key) {

            if(mFileLoader == null) {
                  return false;
            }

            V load = mFileLoader.load(key);
            if(load != null) {

                  /* get from file */

                  Messengers.send(LOAD_FROM_FILE, load, mOnMessageReceiveListener);

                  if(mMemoryLoader != null) {
                        mMemoryLoader.save(key, load);
                  }
                  return true;
            }
            return false;
      }

      private void loadFromNet (String key) {

            V load = mNetLoader.load(key);

            if(load != null) {

                  /* load success */

                  Messengers.send(
                      LOAD_FROM_NET,
                      load,
                      mOnMessageReceiveListener
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

                  Messengers.send(LOAD_NOTHING, mOnMessageReceiveListener);
            }
      }

      public void loadFromMemoryOnly (String key) {

            if(mMemoryLoader == null) {
                  return;
            }

            loadFromMemory(key);
      }

      public void loadFromFileOnly (final String key) {

            if(mFileLoader == null) {
                  return;
            }

            final ObjectBus objectBus = ObjectBusStation.callNewBus();
            objectBus.toUnder(new Runnable() {

                  @Override
                  public void run () {

                        /* from file */
                        loadFromFile(key);
                        ObjectBusStation.recycle(objectBus);
                  }
            }).run();
      }

      public void loadFromNetOnly (final String key) {

            final ObjectBus objectBus = ObjectBusStation.callNewBus();
            objectBus.toUnder(new Runnable() {

                  @Override
                  public void run () {

                        /* from net */

                        loadFromNet(key);
                        ObjectBusStation.recycle(objectBus);
                  }
            }).run();
      }

      private void onValuePrepared (int result, V value) {

            if(mOnValuePreparedListener != null) {
                  mOnValuePreparedListener.onValuePrepared(result, value);
            }
      }

      // ========================= 内部类 =========================

      private class MessageReceiver implements OnMessageReceiveListener {

            @SuppressWarnings("unchecked")
            @Override
            public void onReceive (int what, Object extra) {

                  onValuePrepared(what, (V) extra);
            }

            @Override
            public void onReceive (int what) {

                  onValuePrepared(what, null);
            }
      }

      private class FileExceptionHandler implements ExceptionHandler<String, V> {

            @Override
            public void onConvertToValue (Exception e, String key) {

                  Messengers.send(LOAD_FROM_FILE_CONVERT_EXCEPTION, mOnMessageReceiveListener);
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
                  Messengers.send(LOAD_NET_CANT_CONNECT, mOnMessageReceiveListener);
            }

            @Override
            protected void onConvertException (String key, String url, Exception e) {

                  super.onConvertException(key, url, e);
                  Messengers.send(LOAD_FROM_NET_CONVERT_EXCEPTION, mOnMessageReceiveListener);
            }
      }
}
