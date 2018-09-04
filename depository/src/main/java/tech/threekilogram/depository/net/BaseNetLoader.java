package tech.threekilogram.depository.net;

import java.io.IOException;
import tech.threekilogram.depository.Loader;

/**
 * 网络加载接口,给接口发送一个网络请求,然后使用{@link NetConverter}将网络请求转换为需要的数据类型
 *
 * @param <V> 期望获取的数据类型
 * @param <P> 网络响应类型
 *
 * @author liujin
 */
public abstract class BaseNetLoader<V, P> implements Loader<String, V> {

      /**
       * 异常处理助手
       */
      protected OnNetExceptionListener<String> mOnNetExceptionListener;
      /**
       * 没有该资源助手
       */
      protected OnNoResourceListener           mOnNoResourceListener;
      /**
       * 辅助完成响应到value的转换
       */
      protected NetConverter<V, P>             mNetConverter;

      /**
       * 构建一个加载器
       *
       * @param netConverter 辅助完成网络响应到值的转换
       */
      protected BaseNetLoader ( NetConverter<V, P> netConverter ) {

            mNetConverter = netConverter;
      }

      /**
       * 获取设置的异常处理类
       *
       * @return 设置的异常处理类
       */
      public OnNetExceptionListener<String> getOnNetExceptionListener ( ) {

            return mOnNetExceptionListener;
      }

      /**
       * 设置异常处理类
       *
       * @param onNetExceptionListener 异常处理类
       */
      public void setOnNetExceptionListener (
          OnNetExceptionListener<String> onNetExceptionListener ) {

            mOnNetExceptionListener = onNetExceptionListener;
      }

      /**
       * 获取设置的没有该资源处理器
       */
      public OnNoResourceListener getOnNoResourceListener ( ) {

            return mOnNoResourceListener;
      }

      /**
       * 设置没有该资源处理类
       *
       * @param onNoResourceListener 没有该资源处理器
       */
      public void setOnNoResourceListener (
          OnNoResourceListener onNoResourceListener ) {

            mOnNoResourceListener = onNoResourceListener;
      }

      /**
       * 使用该类处理网络异常
       */
      /**
       * 当从网络获取资源响应码不在{200~300}之间时的处理
       */
      protected NetConverter<V, P> getNetConverter ( ) {

            return mNetConverter;
      }

      /**
       * 当网络异常时的监听
       */
      public interface OnNetExceptionListener<K> {

            /**
             * 当从网络下载的文件转换时的异常{@link NetConverter#onExecuteSuccess(String, Object)}
             *
             * @param key key key
             * @param e exception exception
             */
            void onConvertException ( K key, Exception e );

            /**
             * 无法连接网络
             *
             * @param key key key
             * @param e exception exception
             */
            void onConnectException ( K key, IOException e );
      }

      /**
       * 当没有资源时的监听
       */
      public interface OnNoResourceListener {

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
