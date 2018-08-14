package tech.threekilogram.depository.net.retrofit;

import okhttp3.ResponseBody;
import tech.threekilogram.depository.net.NetConverter;

/**
 * 辅助{@link BaseRetrofitLoader}完成从key到网络响应{@link ResponseBody}再到value的转换
 *
 * @param <V> value 类型
 *
 * @author liujin
 */
public abstract class BaseRetrofitConverter<V> implements NetConverter<V, ResponseBody> {

      protected KeyUrlConverter mUrlConverter;

      @Override
      public String urlFromKey ( String key ) {

            if( mUrlConverter != null ) {

                  return mUrlConverter.urlFromKey( key );
            }

            return key;
      }

      public KeyUrlConverter getUrlConverter ( ) {

            return mUrlConverter;
      }

      public void setUrlConverter ( KeyUrlConverter urlConverter ) {

            mUrlConverter = urlConverter;
      }

      /**
       * 定制url生成规则
       */
      public interface KeyUrlConverter {

            /**
             * 获取Url
             *
             * @param key key
             *
             * @return url
             */
            String urlFromKey ( String key );
      }
}