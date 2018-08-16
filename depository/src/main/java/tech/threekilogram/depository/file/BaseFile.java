package tech.threekilogram.depository.file;

import android.support.annotation.IntDef;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import tech.threekilogram.depository.Container;

/**
 * 辅助类,提供成员变量,及其设置方法
 *
 * @author: Liujin
 * @version: V1.0
 * @date: 2018-08-01
 * @time: 12:01
 */
public abstract class BaseFile<V> implements Container<String, V> {

      /**
       * 保存文件策略,会直接覆盖旧的文件,不会读取,如果旧的文件存在的话
       */
      @SuppressWarnings("WeakerAccess")
      public static final int SAVE_STRATEGY_COVER      = 0;
      /**
       * 保存文件策略,先读取旧的文件之后再覆盖旧的文件,如果旧的文件存在的话
       */
      @SuppressWarnings("WeakerAccess")
      public static final int SAVE_STRATEGY_RETURN_OLD = 1;
      /**
       * 该值会影响 {@link #save(Object, Object)} 和 {@link #remove(Object)}的返回值, 如果是{@link
       * #SAVE_STRATEGY_COVER},那么直接返回null,不回去解析文件为value, 如果是{@link
       * #SAVE_STRATEGY_RETURN_OLD},那么 {@link #save(Object,
       * Object)}会去尝试解析file为value,然后返回该value
       */
      protected           int mSaveStrategy            = SAVE_STRATEGY_COVER;

      /**
       * 读取保存策略
       *
       * @return 策略
       */
      public int getSaveStrategy ( ) {

            return mSaveStrategy;
      }

      /**
       * 处理发生的异常
       */
      protected ExceptionHandler<V> mExceptionHandler;

      /**
       * 设置当保存文件时,如果文件已经存在怎么处理, 如果是{@link #SAVE_STRATEGY_COVER}那么 {@link
       * #save(Object, Object)}直接返回null,不会去尝试解析file为value, 如果是{@link #SAVE_STRATEGY_RETURN_OLD}那么
       * {@link #save(Object, Object)}会去尝试解析file为value,然后返回该值,同时保存新的value到该文件
       *
       * @param saveStrategy 策略
       */
      public void setSaveStrategy ( @SaveStrategyValue int saveStrategy ) {

            mSaveStrategy = saveStrategy;
      }

      /**
       * @return 异常处理类, 或者null
       */
      public ExceptionHandler<V> getExceptionHandler ( ) {

            return mExceptionHandler;
      }

      /**
       * 设置异常处理类
       *
       * @param exceptionHandler 异常处理类
       */
      public void setExceptionHandler (
          ExceptionHandler<V> exceptionHandler ) {

            mExceptionHandler = exceptionHandler;
      }

      /**
       * 根据key返回文件
       *
       * @param key key
       *
       * @return 文件
       */
      public abstract File getFile ( String key );

      /**
       * 保存策略的值类型
       */
      @Retention(RetentionPolicy.SOURCE)
      @SuppressWarnings("WeakerAccess")
      @IntDef(value = { SAVE_STRATEGY_COVER,
                        SAVE_STRATEGY_RETURN_OLD })
      public static @interface SaveStrategyValue { }

      /**
       * handle exception
       */
      public interface ExceptionHandler<V> {

            /**
             * a exception occur at {@link FileConverter#toValue(String, InputStream)} will
             * call this
             *
             * @param e exception
             * @param key which key occur
             */
            void onConvertToValue ( Exception e, String key );

            /**
             * a exception occur at {@link FileConverter#saveValue(String, OutputStream, Object)}
             * will call
             * this
             *
             * @param e exception
             * @param key key
             * @param value to save
             */
            void onSaveValueToFile ( IOException e, String key, V value );
      }
}
