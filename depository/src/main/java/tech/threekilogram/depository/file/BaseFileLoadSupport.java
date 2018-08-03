package tech.threekilogram.depository.file;

/**
 * 辅助类,提供成员变量,及其设置方法
 *
 * @author: Liujin
 * @version: V1.0
 * @date: 2018-08-01
 * @time: 12:01
 */
public abstract class BaseFileLoadSupport<K, V> implements FileLoadSupport<K, V> {

      /**
       * 处理发生的异常
       */
      protected ExceptionHandler<K, V> mExceptionHandler;

      /**
       * 该值会影响 {@link #save(Object, Object)} 和 {@link #remove(Object)}的返回值, 如果是{@link
       * FileLoadSupport#SAVE_STRATEGY_COVER},那么直接返回null,不回去解析文件为value, 如果是{@link
       * FileLoadSupport#SAVE_STRATEGY_RETURN_OLD},那么 {@link #save(Object,
       * Object)}会去尝试解析file为value,然后返回该value
       */
      protected int mSaveStrategy = FileLoadSupport.SAVE_STRATEGY_COVER;

      /**
       * 设置当保存文件时,如果文件已经存在怎么处理, 如果是{@link FileLoadSupport#SAVE_STRATEGY_COVER}那么 {@link
       * #save(Object, Object)}直接返回null,不会去尝试解析file为value, 如果是{@link FileLoadSupport#SAVE_STRATEGY_RETURN_OLD}那么
       * {@link #save(Object, Object)}会去尝试解析file为value,然后返回该值,同时保存新的value到该文件
       *
       * @param saveStrategy 策略
       */
      public void setSaveStrategy (@SaveStrategyValue int saveStrategy) {

            mSaveStrategy = saveStrategy;
      }

      public void setExceptionHandler (
          ExceptionHandler<K, V> exceptionHandler) {

            mExceptionHandler = exceptionHandler;
      }
}
