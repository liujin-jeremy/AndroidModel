package tech.liujin.model.net;

import tech.liujin.model.ConverterLoader;
import tech.liujin.model.converter.StreamConverter;

/**
 * 网络加载接口,给接口发送一个网络请求,然后使用{@link StreamConverter}将网络请求转换为需要的数据类型
 *
 * @param <V> 期望获取的数据类型
 *
 * @author liujin
 */
public abstract class BaseNetLoader<V> implements ConverterLoader<String, V> {

      protected StreamConverter<V> mConverter;
      /**
       * 异常处理助手
       */
      protected OnErrorListener    mErrorListener;

      public BaseNetLoader ( StreamConverter<V> converter ) {

            mConverter = converter;
      }

      /**
       * 加载url对应对象
       *
       * @param url url
       *
       * @return value
       */
      @Override
      public abstract V load ( String url );

      /**
       * 加载url对应的对象
       *
       * @param url url
       * @param converter converter converter
       *
       * @return value
       */
      @Override
      public abstract V load ( String url, StreamConverter<V> converter );

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
