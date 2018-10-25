package tech.threekilogram.model.container.file;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import tech.threekilogram.model.container.Container;
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
public abstract class BaseFileLoader<V> implements Container<String, V> {

      /**
       * 辅助该类完成stream到{@link V}的转换工作
       */
      protected StreamConverter<V> mConverter;
      /**
       * 处理发生的异常
       */
      protected OnErrorListener<V> mOnErrorListener;
      @EncodeMode
      private   int                mMode = EncodeMode.HASH;

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
       * @param key key
       *
       * @return 文件(可能不存在), 需要自己判断一下
       */
      public abstract File getFile ( String key );

      /**
       * 清空缓存文件
       */
      public abstract void clear ( );

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
