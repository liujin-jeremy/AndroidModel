package tech.threekilogram.model.net;

import tech.threekilogram.model.Loader;
import tech.threekilogram.model.converter.StreamConverter;

/**
 * 网络加载接口,给接口发送一个网络请求,然后使用{@link StreamConverter}将网络请求转换为需要的数据类型
 *
 * @param <V> 期望获取的数据类型
 *
 * @author liujin
 */
public abstract class BaseNetLoader<V> implements Loader<String, V> {

      protected StreamConverter<V> mConverter;
      /**
       * 异常处理助手
       */
      protected OnErrorListener    mErrorListener;

      public BaseNetLoader ( StreamConverter<V> converter ) {

            mConverter = converter;
      }

      /**
       * 获取设置的异常处理类
       *
       * @return 设置的异常处理类
       */
      public OnErrorListener getErrorListener ( ) {

            return mErrorListener;
      }

      /**
       * 设置异常处理类
       *
       * @param errorListener 异常处理类
       */
      public void setErrorListener (
          OnErrorListener errorListener ) {

            mErrorListener = errorListener;
      }
}
