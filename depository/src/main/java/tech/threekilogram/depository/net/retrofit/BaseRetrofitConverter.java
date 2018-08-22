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

      public BaseRetrofitConverter ( ) { }

      /**
       * @param urlConverter 辅助转换url,从key中
       */
      public BaseRetrofitConverter ( KeyUrlConverter urlConverter ) {

            mUrlConverter = urlConverter;
      }

      @Override
      public String urlFromKey ( String key ) {

            if( mUrlConverter != null ) {

                  return mUrlConverter.urlFromKey( key );
            }

            return key;
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