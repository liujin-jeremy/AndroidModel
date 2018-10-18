package tech.threekilogram.model.file;

import android.support.annotation.IntDef;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import tech.threekilogram.model.Container;

/**
 * 文件缓存的基本实现类,用于从文件系统中保存恢复对象,需要配合{@link FileConverter}使用
 *
 * @author: Liujin
 * @version: V1.0
 * @date: 2018-08-01
 * @time: 12:01
 */
public abstract class BaseFileLoader<V> implements Container<String, V> {

      /**
       * 保存文件策略,使用该策略会直接覆盖旧的文件,不会读取已经存在的key对应的文件
       */
      @SuppressWarnings("WeakerAccess")
      public static final int SAVE_STRATEGY_COVER      = 0;
      /**
       * 保存文件策略,使用该策略会先读取key对应的旧的文件之后再覆盖文件
       */
      @SuppressWarnings("WeakerAccess")
      public static final int SAVE_STRATEGY_RETURN_OLD = 1;

      /**
       * 该值会影响 {@link #save(Object, Object)} 和 {@link #remove(Object)}的返回值, 如果是{@link
       * #SAVE_STRATEGY_COVER},那么直接返回null,不回去解析文件为value, 如果是{@link
       * #SAVE_STRATEGY_RETURN_OLD},那么 {@link #save(Object,
       * Object)}会去尝试解析file为value,然后返回该value
       */
      protected int                               mSaveStrategy = SAVE_STRATEGY_COVER;
      /**
       * 辅助该类完成stream到{@link V}的转换工作
       */
      protected FileConverter<V>                  mConverter;
      /**
       * 处理发生的异常
       */
      protected OnFileConvertExceptionListener<V> mOnFileConvertExceptionListener;

      /**
       * 读取保存策略
       *
       * @return 策略
       */
      public int getSaveStrategy ( ) {

            return mSaveStrategy;
      }

      /**
       * 设置当保存文件时,如果文件已经存在怎么处理, 如果是{@link #SAVE_STRATEGY_COVER}那么 {@link
       * #save(Object, Object)}直接返回null,不会去尝试解析file为value, 如果是{@link #SAVE_STRATEGY_RETURN_OLD}那么
       * {@link #save(Object, Object)}会去尝试解析file为value,然后返回该值,同时保存新的value到该文件,同样会影响{@link
       * #remove(Object)}
       *
       * @param saveStrategy 策略
       */
      public void setSaveStrategy ( @SaveStrategyValue int saveStrategy ) {

            mSaveStrategy = saveStrategy;
      }

      /**
       * 保存策略的值类型
       */
      @Retention(RetentionPolicy.SOURCE)
      @SuppressWarnings("WeakerAccess")
      @IntDef(value = { SAVE_STRATEGY_COVER,
                        SAVE_STRATEGY_RETURN_OLD })
      public @interface SaveStrategyValue { }

      /**
       * @return 异常处理类, 或者null
       */
      public OnFileConvertExceptionListener<V> getOnFileConvertExceptionListener ( ) {

            return mOnFileConvertExceptionListener;
      }

      /**
       * 设置异常处理类
       *
       * @param onFileConvertExceptionListener 异常处理类
       */
      public void setOnFileConvertExceptionListener (
          OnFileConvertExceptionListener<V> onFileConvertExceptionListener ) {

            mOnFileConvertExceptionListener = onFileConvertExceptionListener;
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
       * 获取转换器
       *
       * @return 设置的转换器
       */
      public FileConverter<V> getConverter ( ) {

            return mConverter;
      }

      /**
       * handle exception
       */
      public interface OnFileConvertExceptionListener<V> {

            /**
             * a exception occur at {@link FileConverter#from(InputStream)} will
             * call this
             *
             * @param e exception
             * @param key which key occur
             */
            void onFileToValue ( Exception e, String key );

            /**
             * a exception occur at {@link FileConverter#to(OutputStream, Object)}
             * will call
             * this
             *
             * @param e exception
             * @param key key
             * @param value to save
             */
            void onValueToFile ( IOException e, String key, V value );
      }
}
