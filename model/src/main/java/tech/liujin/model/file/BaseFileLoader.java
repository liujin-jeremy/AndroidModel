package tech.liujin.model.file;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import tech.threekilogram.model.ConverterLoader;
import tech.threekilogram.model.converter.StreamConverter;
import tech.threekilogram.model.util.encode.EncodeMode;
import tech.threekilogram.model.util.encode.StringEncoder;

/**
 * 文件缓存的基本实现类,用于从文件系统中保存恢复对象,需要配合{@link StreamConverter}使用
 *
 * @author: Liujin
 * @version: V1.0
 * @date: 2018-08-01
 * @time: 12:01
 */
public abstract class BaseFileLoader<V> implements ConverterLoader<String, V> {

      protected StreamConverter<V> mConverter;
      /**
       * 处理发生的异常
       */
      protected OnErrorListener<V> mOnErrorListener;
      @EncodeMode
      private   int                mMode = EncodeMode.HASH;

      public BaseFileLoader ( StreamConverter<V> converter ) {

            mConverter = converter;
      }

      /**
       * 设置文件名字转化模式
       */
      public void setMode ( @EncodeMode int mode ) {

            mMode = mode;
      }

      @EncodeMode
      public int getMode ( ) {

            return mMode;
      }

      /**
       * 获取一个合法名字
       */
      public String encodeKey ( String key ) {

            return StringEncoder.encode( key, mMode );
      }

      /**
       * @return 异常处理类, 或者null
       */
      public OnErrorListener<V> getOnErrorListener ( ) {

            return mOnErrorListener;
      }

      /**
       * 设置异常处理类
       *
       * @param onErrorListener 异常处理类
       */
      public void setOnErrorListener (
          OnErrorListener<V> onErrorListener ) {

            mOnErrorListener = onErrorListener;
      }

      /**
       * 根据key返回文件,文件可能不存在,{@link File#exists()}可能返回false
       *
       * @param url key
       *
       * @return 文件(可能不存在), 需要自己判断一下
       */
      public abstract File getFile ( String url );

      /**
       * 保存一个值
       *
       * @param url key
       * @param value value
       */
      public abstract void save ( String url, V value );

      /**
       * 保存一个值
       *
       * @param url key
       * @param value value
       * @param converter converter
       */
      public abstract void save ( String url, V value, StreamConverter<V> converter );

      /**
       * remove the value to this key
       *
       * @param url remove the value at key
       *
       * @return if key exist remove value at key , or null returned
       */
      public abstract void remove ( String url );

      /**
       * test contains a value or not
       *
       * @param url contains of a value to this url
       *
       * @return true contains this value
       */
      public abstract boolean containsOf ( String url );

      /**
       * 清空
       */
      public abstract void clear ( );

      /**
       * 加载
       *
       * @param url url
       *
       * @return value
       */
      @Override
      public abstract V load ( String url );

      /**
       * 加载
       *
       * @param url url
       * @param converter converter
       *
       * @return value
       */
      @Override
      public abstract V load ( String url, StreamConverter<V> converter );

      /**
       * handle exception
       */
      public interface OnErrorListener<V> {

            /**
             * a exception occur at {@link StreamConverter#from(InputStream)} will
             * call this
             *
             * @param e exception
             * @param key which key occur
             */
            void onLoadFromFile ( Exception e, String key );

            /**
             * a exception occur at {@link StreamConverter#to(OutputStream, Object)}
             * will call
             * this
             *
             * @param e exception
             * @param key key
             * @param value to save
             */
            void onSaveToFile ( IOException e, String key, V value );
      }
}
