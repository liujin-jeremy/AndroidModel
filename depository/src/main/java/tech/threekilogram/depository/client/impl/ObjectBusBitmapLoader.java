package tech.threekilogram.depository.client.impl;

import static tech.threekilogram.depository.client.OnValuePreparedListener.LOAD_FROM_MEMORY;

import android.graphics.Bitmap;
import com.example.objectbus.ObjectBusConfig;
import com.example.objectbus.message.Messengers;
import com.example.objectbus.message.OnMessageReceiveListener;
import tech.threekilogram.depository.client.AsyncLoader;
import tech.threekilogram.depository.client.OnValuePreparedListener;
import tech.threekilogram.depository.memory.lru.MemoryLruCacheLoader;
import tech.threekilogram.depository.memory.lru.size.BitmapSize;

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

      private MemoryLruCacheLoader<String, Bitmap> mMemoryLoader;
      /**
       * 用户监听
       */
      private OnValuePreparedListener<Bitmap>      mOnValuePreparedListener;
      /**
       * 线程调度
       */
      private OnMessageReceiveListener mOnMessageReceiveListener = new MessageReceiver();

      public ObjectBusBitmapLoader (
          MemoryLruCacheLoader<String, Bitmap> memoryLruCacheLoader) {

            mMemoryLoader = memoryLruCacheLoader;
      }

      public ObjectBusBitmapLoader () {

            long memorySize = Runtime.getRuntime().totalMemory() >> 3;
            mMemoryLoader = new MemoryLruCacheLoader<>(
                (int) memorySize,
                new BitmapSize<String>()
            );
      }

      public void setOnValuePreparedListener (
          OnValuePreparedListener<Bitmap> onValuePreparedListener) {

            mOnValuePreparedListener = onValuePreparedListener;
      }

      @Override
      public void prepareValue (String key) {

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

            return false;
      }

      private void loadFromNet (String key) {

      }

      private void onValuePrepared (int result, Bitmap value) {

            if(mOnValuePreparedListener != null) {
                  mOnValuePreparedListener.onValuePrepared(result, value);
            }
      }

      // ========================= 内部类 =========================

      private class MessageReceiver implements OnMessageReceiveListener {

            @SuppressWarnings("unchecked")
            @Override
            public void onReceive (int what, Object extra) {

                  onValuePrepared(what, (Bitmap) extra);
            }

            @Override
            public void onReceive (int what) {

                  onValuePrepared(what, null);
            }
      }
}
