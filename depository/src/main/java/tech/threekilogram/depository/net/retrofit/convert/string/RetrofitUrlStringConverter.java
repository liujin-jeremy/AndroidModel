package tech.threekilogram.depository.net.retrofit.convert.string;

/**
 * 辅助完成转换工作,适用于使用一个Url作为key的情况
 *
 * @author liujin
 */
public class RetrofitUrlStringConverter extends BaseRetrofitStringConverter<String> {

      @Override
      public String urlFromKey ( String key ) {

            return key;
      }
}
