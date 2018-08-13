package tech.threekilogram.depository.net.retrofit.convert.down;

/**
 * @author: Liujin
 * @version: V1.0
 * @date: 2018-08-05
 * @time: 23:11
 */

import java.io.File;
import tech.threekilogram.depository.net.retrofit.service.RetrofitConverter;

/**
 * base 接口,缓存下载文件到文件夹,该类适用于将一个网络响应缓存到文件夹,有实现类
 * {@link BaseRetrofitDiskLruConverter},{@link BaseRetrofitFileConverter},
 * {@link RetrofitDiskLruConverter},{@link RetrofitFileConverter}
 *
 * @author liujin
 */
public abstract class AbstractRetrofitDownLoadConverter<K> implements
                                                           RetrofitConverter<K, File> {

      /**
       * 下载文件夹
       */
      @SuppressWarnings("WeakerAccess")
      protected File mDir;

      @SuppressWarnings("WeakerAccess")
      public AbstractRetrofitDownLoadConverter ( File dir ) {

            mDir = dir;
      }

      public File getDir ( ) {

            return mDir;
      }

      /**
       * get a file from this key
       *
       * @param key key
       *
       * @return file by this key, may be not exist
       */
      public abstract File getFile ( K key );
}
