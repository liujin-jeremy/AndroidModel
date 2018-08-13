package tech.threekilogram.depository.net.retrofit.convert.down;

import java.io.File;

/**
 * 下载文件到一个文件夹下,适用于使用一个Url作为key的情况
 *
 * @author liujin
 */
public class RetrofitFileConverter extends
                                   BaseRetrofitFileConverter<String> {

      public RetrofitFileConverter ( File dir ) {

            super( dir );
      }

      @Override
      public String urlFromKey ( String key ) {

            return key;
      }
}
