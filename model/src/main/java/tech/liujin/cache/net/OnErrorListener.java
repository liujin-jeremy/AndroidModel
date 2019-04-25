package tech.liujin.cache.net;

/**
 * @author Liujin 2018-10-25:22:00
 */

import java.io.IOException;

/**
 * 当网络异常时的监听
 *
 * @author liujin
 */
public interface OnErrorListener {

      /**
       * 当从网络下载的文件转换时的异常
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
       * @param url key
       * @param httpCode http code
       */
      void onNullResource ( String url, int httpCode );
}
