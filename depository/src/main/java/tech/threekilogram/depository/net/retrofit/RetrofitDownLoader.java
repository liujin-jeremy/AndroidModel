package tech.threekilogram.depository.net.retrofit;

import java.io.File;
import tech.threekilogram.depository.net.retrofit.convert.down.RetrofitFileConverter;

/**
 * 使用 retrofit 从网络下载文件
 *
 * @author liujin
 */
public class RetrofitDownLoader extends RetrofitStreamLoader<String, File> {

      public RetrofitDownLoader ( File dir ) {

            super( new RetrofitFileConverter( dir ) );
      }

      public File getFile ( String url ) {

            return ( (RetrofitFileConverter) mNetConverter ).getFile( url );
      }

      public File getDir ( ) {

            return ( (RetrofitFileConverter) mNetConverter ).getDir();
      }
}
