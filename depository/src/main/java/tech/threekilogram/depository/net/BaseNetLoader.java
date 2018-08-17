package tech.threekilogram.depository.net;

import java.io.IOException;
import tech.threekilogram.depository.Loader;

/**
 * @author: Liujin
 * @version: V1.0
 * @date: 2018-08-17
 * @time: 14:40
 */
public abstract class BaseNetLoader<V> implements Loader<String, V> {

      /**
       * 异常处理助手
       */
      protected NetExceptionHandler<String> mExceptionHandler;
      /**
       * 没有该资源助手
       */
      protected NoResourceHandler           mNoResourceHandler;

      /**
       * 获取设置的异常处理类
       *
       * @return 设置的异常处理类
       */
      public NetExceptionHandler<String> getExceptionHandler ( ) {

            return mExceptionHandler;
      }

      /**
       * 设置异常处理类
       *
       * @param exceptionHandler 异常处理类
       */
      public void setExceptionHandler (
          NetExceptionHandler<String> exceptionHandler ) {

            mExceptionHandler = exceptionHandler;
      }

      /**
       * 获取设置的没有该资源处理器
       */
      public NoResourceHandler getNoResourceHandler ( ) {

            return mNoResourceHandler;
      }

      /**
       * 设置没有该资源处理类
       *
       * @param noResourceHandler 没有该资源处理器
       */
      public void setNoResourceHandler (
          NoResourceHandler noResourceHandler ) {

            mNoResourceHandler = noResourceHandler;
      }

      /**
       * 使用该类处理网络异常
       */
      public interface NetExceptionHandler<K> {

            /**
             * 当从网络下载的文件转换时的异常{@link NetConverter#onExecuteSuccess(String, Object)}
             *
             * @param key key key
             * @param url url url from {@link NetConverter#urlFromKey(String)}
             * @param e exception exception
             */
            void onConvertException ( K key, java.lang.String url, Exception e );

            /**
             * 无法连接网络
             *
             * @param key key key
             * @param url url url from {@link NetConverter#urlFromKey(String)}
             * @param e exception exception
             */
            void onConnectException ( K key, java.lang.String url, IOException e );
      }

      /**
       * 当从网络获取资源响应码不在{200~300}之间时的处理
       */
      public interface NoResourceHandler {

            /**
             * 没有成功获取数据的回调
             * <p>
             * when cant get a correct response {response not in 200~300} return failed
             *
             * @param key key
             * @param httpCode http code
             */
            void onExecuteFailed ( String key, int httpCode );
      }
}
