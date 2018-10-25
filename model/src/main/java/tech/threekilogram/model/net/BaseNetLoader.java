package tech.threekilogram.model.net;

import java.io.IOException;
import tech.threekilogram.model.Loader;

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
      protected OnErrorListener    mErrorListener;
      /**
       * 辅助完成响应到value的转换
       */
      protected NetConverter<V, P> mNetConverter;

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

      protected NetConverter<V, P> getNetConverter ( ) {

            return mNetConverter;
      }

      /**
       * 当网络异常时的监听
       */
      public interface OnErrorListener {

            /**
             * 当从网络下载的文件转换时的异常{@link NetConverter#onExecuteSuccess(String, Object)}
             *
             * @param url url
             * @param e exception exception
             */
            void onConvertException ( String url, Exception e );

            /**
             * 无法连接网络
             *
             * @param url url
             * @param e exception exception
             */
            void onConnectException ( String url, IOException e );

            /**
             * 没有成功获取数据的回调
             * <p>
             * when cant get a correct response {response not in 200~300} return failed
             *
             * @param key key
             * @param httpCode http code
             */
            void onNullResource ( String key, int httpCode );
      }
}
