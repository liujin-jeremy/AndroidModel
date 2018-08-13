package tech.threekilogram.depository.net.retrofit.convert.down;

import java.io.File;

/**
 * 使用{@link com.jakewharton.disklrucache.DiskLruCache} 保存下载的文件,适用于使用一个Url作为key的情况
 *
 * @author liujin
 */
public class RetrofitDiskLruConverter extends BaseRetrofitDiskLruConverter<String> {

      public RetrofitDiskLruConverter ( File dir, int maxSize ) {

            super( dir, maxSize );
      }

      @Override
      public String urlFromKey ( String key ) {

            return key;
      }
}
