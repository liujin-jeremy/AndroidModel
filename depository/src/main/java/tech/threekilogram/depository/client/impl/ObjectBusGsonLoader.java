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
import tech.threekilogram.depository.file.BaseFileLoadSupport;
import tech.threekilogram.depository.file.FileLoadSupport.ExceptionHandler;
import tech.threekilogram.depository.file.converter.GsonFileConverter;
import tech.threekilogram.depository.file.impl.DiskLruCacheLoader;
import tech.threekilogram.depository.file.impl.FileLoader;
import tech.threekilogram.depository.function.StringKeyFunction;
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
public class ObjectBusGsonLoader<K, V> implements AsyncLoader<K> {

      static {

            /* 初始化线程池 */
            ObjectBusConfig.init();
      }

      /**
       * 用于将K类型转为string类型key
       */
      private StringKeyFunction<K>           mKeyFunction;
      /**
       * 内存缓存
       */
      private MemoryLoadSupport<String, V>   mMemoryLoader;
      /**
       * 文件缓存
       */
      private BaseFileLoadSupport<String, V> mFileLoader;
      /**
       * 网络缓存
       */
      private NetLoader<String, V>           mNetLoader;
      /**
       * 用户监听,当 value 准备好之后在主线程回调
       */
      private OnValuePreparedListener<V>     mOnValuePreparedListener;

      /**
       * 线程调度
       */
      private OnMessageReceiveListener mOnMessageReceiveListener = new MessageReceiver();

      /**
       * 创建一个缓存客户端,只有网络缓存,可以通过{@link #setMemoryLoader(MemoryLoadSupport)}
       * {@link #setFileLoader(FileLoader)}配置其他缓存
       *
       * @param netLoader 网络
       */
      public ObjectBusGsonLoader (
          @NonNull StringKeyFunction<K> keyFunction,
          @NonNull NetLoader<String, V> netLoader) {

            initFields(keyFunction, null, null, netLoader);
      }

      private void initFields (
          StringKeyFunction<K> keyFunction,
          MemoryLoadSupport<String, V> memory,
          BaseFileLoadSupport<String, V> file,
          NetLoader<String, V> net) {

            mKeyFunction = keyFunction;

            mMemoryLoader = memory;

            mFileLoader = file;
            if(mFileLoader.getExceptionHandler() == null) {
                  mFileLoader.setExceptionHandler(new FileExceptionHandler());
            }

            mNetLoader = net;
      }

      /**
       * @param keyFunction get url from key
       * @param memorySize 内存中最多缓存多少
       * @param cacheDir 文件缓存文件夹
       * @param cacheMaxSize cache dir 文件夹最大大小
       * @param valueType value 类型
       */
      public ObjectBusGsonLoader (
          StringKeyFunction<K> keyFunction,
          int memorySize,
          File cacheDir,
          int cacheMaxSize,
          Class<V> valueType) {

            initFields(
                keyFunction,
                new MemoryLruCacheLoader<String, V>(memorySize),
                new DiskLruCacheLoader<String, V>(
                    cacheDir, cacheMaxSize, new GsonFileConverter<V>(valueType)),
                new NetLoader<>(new RetrofitGsonExceptionHandledConverter(valueType))
            );
      }

      // ========================= get/set =========================

      public StringKeyFunction<K> getKeyFunction () {

            return mKeyFunction;
      }

      public void setKeyFunction (StringKeyFunction<K> keyFunction) {

            mKeyFunction = keyFunction;
      }

      public void setMemoryLoader (
          MemoryLoadSupport<String, V> memoryLoader) {

            mMemoryLoader = memoryLoader;
      }

      public MemoryLoadSupport<String, V> getMemoryLoader () {

            return mMemoryLoader;
      }

      public BaseFileLoadSupport<String, V> getFileLoader () {

            return mFileLoader;
      }

      public void setFileLoader (
          FileLoader<String, V> fileLoader) {

            mFileLoader = fileLoader;
      }

      public void setNetLoader (NetLoader<String, V> netLoader) {

            mNetLoader = netLoader;
      }

      public NetLoader<String, V> getNetLoader () {

            return mNetLoader;
      }

      public void setOnValuePreparedListener (
          OnValuePreparedListener<V> onValuePreparedListener) {

            mOnValuePreparedListener = onValuePreparedListener;
      }

      public OnValuePreparedListener<V> getOnValuePreparedListener () {

            return mOnValuePreparedListener;
      }

      // ========================= impl =========================

      @Override
      public void prepareValue (final K key) {

            final String stringKey = mKeyFunction.stringKey(key);

            /* from memory */

            if(loadFromMemory(stringKey)) {
                  return;
            }


            /* back ground thread pool do this */

            final ObjectBus objectBus = ObjectBusStation.callNewBus();
            objectBus.toUnder(new Runnable() {

                  @Override
                  public void run () {

                        /* from file */

                        if(loadFromFile(stringKey)) {
                              ObjectBusStation.recycle(objectBus);
                              return;
                        }

                        /* from net */

                        loadFromNet(stringKey);
                        ObjectBusStation.recycle(objectBus);
                  }
            }).run();
      }

      /**
       * 从内存读取
       */
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

      /**
       * 从文件读取
       */
      private boolean loadFromFile (String key) {

            if(mFileLoader == null) {
                  return false;
            }

            V value = mFileLoader.load(key);
            if(value != null) {

                  /* get from file */

                  Messengers.send(LOAD_FROM_FILE, value, mOnMessageReceiveListener);

                  if(mMemoryLoader != null) {
                        mMemoryLoader.save(key, value);
                  }
                  return true;
            }
            return false;
      }

      /**
       * 从网络读取
       */
      private void loadFromNet (String key) {

            V value = mNetLoader.load(key);

            if(value != null) {

                  /* value success */

                  Messengers.send(
                      LOAD_FROM_NET,
                      value,
                      mOnMessageReceiveListener
                  );

                  /* save to container */

                  if(mMemoryLoader != null) {

                        mMemoryLoader.save(key, value);
                  }
                  if(mFileLoader != null) {

                        mFileLoader.save(key, value);
                  }
            } else {

                  /* value failed in all way */

                  Messengers.send(LOAD_NOTHING, mOnMessageReceiveListener);
            }
      }

      /**
       * 只从内存尝试读取
       */
      public void loadFromMemoryOnly (K key) {

            if(mMemoryLoader == null) {
                  return;
            }

            String stringKey = mKeyFunction.stringKey(key);
            loadFromMemory(stringKey);
      }

      /**
       * 只从文件尝试读取
       */
      public void loadFromFileOnly (final K key) {

            if(mFileLoader == null) {
                  return;
            }

            final String stringKey = mKeyFunction.stringKey(key);

            final ObjectBus objectBus = ObjectBusStation.callNewBus();
            objectBus.toUnder(new Runnable() {

                  @Override
                  public void run () {

                        /* from file */
                        loadFromFile(stringKey);
                        ObjectBusStation.recycle(objectBus);
                  }
            }).run();
      }

      /**
       * 只从文件尝试读取
       */
      public void loadFromNetOnly (final K key) {

            final String stringKey = mKeyFunction.stringKey(key);
            final ObjectBus objectBus = ObjectBusStation.callNewBus();
            objectBus.toUnder(new Runnable() {

                  @Override
                  public void run () {

                        /* from net */

                        loadFromNet(stringKey);
                        ObjectBusStation.recycle(objectBus);
                  }
            }).run();
      }

      /**
       * 当尝试完所有方法之后回调这个方法,通知监听
       */
      private void onValuePrepared (int result, @Nullable V value) {

            if(mOnValuePreparedListener != null) {
                  mOnValuePreparedListener.onValuePrepared(result, value);
            }
      }

      // ========================= 内部类 =========================

      /**
       * 线程调度
       */
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

      /**
       * 处理 file exception
       */
      private class FileExceptionHandler implements ExceptionHandler<String, V> {

            @Override
            public void onConvertToValue (Exception e, String key) {

                  Messengers.send(LOAD_FROM_FILE_CONVERT_EXCEPTION, mOnMessageReceiveListener);
            }

            @Override
            public void onSaveValueToFile (IOException e, String key, V value) {

            }
      }

      /**
       * 处理网络 exception
       */
      private class RetrofitGsonExceptionHandledConverter extends RetrofitGsonConverter<V> {

            RetrofitGsonExceptionHandledConverter (Class<V> valueType) {

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
