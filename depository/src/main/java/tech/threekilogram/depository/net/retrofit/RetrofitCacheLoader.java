package tech.threekilogram.depository.net.retrofit;

import java.io.File;
import tech.threekilogram.depository.net.retrofit.convert.down.RetrofitDiskLruConverter;

/**
 * 使用 retrofit 从网络缓存文件
 *
 * @author liujin
 */
public class RetrofitCacheLoader extends RetrofitStreamLoader<String, File> {

      public RetrofitCacheLoader ( File dir, int maxSize ) {

            super( new RetrofitDiskLruConverter( dir, maxSize ) );
      }

      public File getFile ( String url ) {

            return ( (RetrofitDiskLruConverter) mNetConverter ).getFile( url );
      }

      public File getDir ( ) {

            return ( (RetrofitDiskLruConverter) mNetConverter ).getDir();
      }
}
