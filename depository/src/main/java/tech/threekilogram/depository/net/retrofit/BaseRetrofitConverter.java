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

      @Override
      public String urlFromKey ( String key ) {

            return key;
      }
}
