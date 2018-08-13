package tech.threekilogram.depository.file;

import android.support.annotation.IntDef;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import tech.threekilogram.depository.ContainerLoader;

/**
 * 辅助类,提供成员变量,及其设置方法
 *
 * @author: Liujin
 * @version: V1.0
 * @date: 2018-08-01
 * @time: 12:01
 */
public abstract class BaseFileLoader<K, V> implements ContainerLoader<K, V> {

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
       * 设置当保存文件时,如果文件已经存在怎么处理, 如果是{@link #SAVE_STRATEGY_COVER}那么 {@link
       * #save(Object, Object)}直接返回null,不会去尝试解析file为value, 如果是{@link #SAVE_STRATEGY_RETURN_OLD}那么
       * {@link #save(Object, Object)}会去尝试解析file为value,然后返回该值,同时保存新的value到该文件
       *
       * @param saveStrategy 策略
       */
      public void setSaveStrategy (@SaveStrategyValue int saveStrategy) {

            mSaveStrategy = saveStrategy;
      }

      /**
       * 处理发生的异常
       */
      protected ExceptionHandler<K, V> mExceptionHandler;

      @SuppressWarnings("WeakerAccess")
      @IntDef(value = {SAVE_STRATEGY_COVER,
                       SAVE_STRATEGY_RETURN_OLD})
      public static @interface SaveStrategyValue {}

      /**
       * handle exception
       */
      public interface ExceptionHandler<K, V> {

            /**
             * a exception occur at {@link FileConverter#toValue(Object, InputStream)} will
             * call this
             *
             * @param e exception
             * @param key which key occur
             */
            void onConvertToValue (Exception e, K key);

            /**
             * a exception occur at {@link FileConverter#saveValue(Object, OutputStream, Object)}
             * will call
             * this
             *
             * @param e exception
             * @param key key
             * @param value to save
             */
            void onSaveValueToFile (IOException e, K key, V value);
      }

      public int getSaveStrategy () {

            return mSaveStrategy;
      }

      public void setExceptionHandler (
          ExceptionHandler<K, V> exceptionHandler) {

            mExceptionHandler = exceptionHandler;
      }

      public ExceptionHandler<K, V> getExceptionHandler () {

            return mExceptionHandler;
      }

      public abstract File getFile ( K key );
}
