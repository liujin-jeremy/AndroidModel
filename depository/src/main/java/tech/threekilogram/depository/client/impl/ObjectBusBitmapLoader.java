package tech.threekilogram.depository.client.impl;

import static tech.threekilogram.depository.client.OnValuePreparedListener.LOAD_FROM_FILE;
import static tech.threekilogram.depository.client.OnValuePreparedListener.LOAD_FROM_MEMORY;
import static tech.threekilogram.depository.client.OnValuePreparedListener.LOAD_FROM_NET;
import static tech.threekilogram.depository.client.OnValuePreparedListener.LOAD_FROM_NET_CONVERT_EXCEPTION;
import static tech.threekilogram.depository.client.OnValuePreparedListener.LOAD_NET_CANT_CONNECT;
import static tech.threekilogram.depository.client.OnValuePreparedListener.LOAD_NOTHING;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import com.example.objectbus.ObjectBusConfig;
import com.example.objectbus.bus.ObjectBus;
import com.example.objectbus.bus.ObjectBusStation;
import com.example.objectbus.message.Messengers;
import com.example.objectbus.message.OnMessageReceiveListener;
import java.io.File;
import java.io.IOException;
import tech.threekilogram.depository.client.AsyncLoader;
import tech.threekilogram.depository.client.OnValuePreparedListener;
import tech.threekilogram.depository.function.Md5Function;
import tech.threekilogram.depository.memory.lru.MemoryLruCacheLoader;
import tech.threekilogram.depository.memory.lru.size.BitmapSize;
import tech.threekilogram.depository.net.NetLoader;
import tech.threekilogram.depository.net.retrofit.RetrofitDownLoadConverter;

/**
 * @author: Liujin
 * @version: V1.0
 * @date: 2018-08-04
 * @time: 9:58
 */
public class ObjectBusBitmapLoader implements AsyncLoader<String> {

      /* init lib */

      static {

            ObjectBusConfig.init();
      }

      /**
       * memory
       */
      private MemoryLruCacheLoader<String, Bitmap> mMemoryLoader;

      /**
       * file
       */
      private File mDir;

      /**
       * 网络
       */
      private NetLoader<String, File> mNetLoader;

      /**
       * 线程调度
       */
      private OnMessageReceiveListener mOnMessageReceiveListener = new MessageReceiver();

      /**
       * convert file to bitmap
       */
      private OnBitmapFilePreparedListener<String> mOnBitmapFilePreparedListener;

      private OnValuePreparedListener<Bitmap> mOnValuePreparedListener;

      public ObjectBusBitmapLoader (File dir) {

            long memorySize = Runtime.getRuntime().totalMemory() >> 3;
            mMemoryLoader = new MemoryLruCacheLoader<>(
                (int) memorySize,
                new BitmapSize<String>()
            );
            mDir = dir;
            mNetLoader = new NetLoader<>(new RetrofitDownExceptionHandledConverter(dir));
      }

      public ObjectBusBitmapLoader (
          MemoryLruCacheLoader<String, Bitmap> memoryLruCacheLoader, File dir) {

            mMemoryLoader = memoryLruCacheLoader;
            mDir = dir;
            mNetLoader = new NetLoader<>(new RetrofitDownExceptionHandledConverter(dir));
      }

      public void setOnBitmapFilePreparedListener (
          OnBitmapFilePreparedListener<String> onBitmapFilePreparedListener) {

            mOnBitmapFilePreparedListener = onBitmapFilePreparedListener;
      }

      @Override
      public void prepareValue (final String key) {

            if(loadFromMemory(key)) {
                  return;
            }

            final ObjectBus bus = ObjectBusStation.callNewBus();
            bus.toUnder(new Runnable() {

                  @Override
                  public void run () {

                        if(loadFromFile(key)) {
                              ObjectBusStation.recycle(bus);
                              return;
                        }

                        loadFromNet(key);
                        ObjectBusStation.recycle(bus);
                  }
            }).run();
      }

      private boolean loadFromMemory (String key) {

            if(mMemoryLoader == null) {
                  return false;
            }

            Bitmap load = mMemoryLoader.load(key);
            if(load != null) {
                  Messengers.send(LOAD_FROM_MEMORY, load, mOnMessageReceiveListener);
                  return true;
            }
            return false;
      }

      private boolean loadFromFile (String key) {

            String name = Md5Function.nameFromMd5(key);
            File file = new File(mDir, name);

            if(file.exists()) {
                  Messengers.send(LOAD_FROM_FILE, file, mOnMessageReceiveListener);
            }

            return false;
      }

      private void loadFromNet (String key) {

            File file = mNetLoader.load(key);

            if(file.exists()) {

                  if(mOnBitmapFilePreparedListener != null) {

                        Bitmap bitmap = mOnBitmapFilePreparedListener
                            .onBitmapFilePrepared(key, file);
                        Messengers.send(LOAD_FROM_NET, bitmap, mOnMessageReceiveListener);
                  } else {

                        Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                        Messengers.send(LOAD_FROM_NET, bitmap, mOnMessageReceiveListener);
                  }
            } else {

                  Messengers.send(LOAD_NOTHING, mOnMessageReceiveListener);
            }
      }

      private void onBitmapPrepared (int result, Bitmap bitmap) {

            if(mOnValuePreparedListener != null) {
                  mOnValuePreparedListener.onValuePrepared(result, bitmap);
            }
      }

      // ========================= 内部类 =========================

      private class MessageReceiver implements OnMessageReceiveListener {

            @SuppressWarnings("unchecked")
            @Override
            public void onReceive (int what, Object extra) {

                  if(what == LOAD_FROM_MEMORY) {
                        onBitmapPrepared(LOAD_FROM_MEMORY, (Bitmap) extra);
                  }
            }

            @Override
            public void onReceive (int what) {

                  onBitmapPrepared(what, null);
            }
      }

      private class RetrofitDownExceptionHandledConverter extends RetrofitDownLoadConverter {

            public RetrofitDownExceptionHandledConverter (File dir) {

                  super(dir);
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
