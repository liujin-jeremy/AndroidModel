package tech.threekilogram.depository.net;

import android.support.v4.util.ArraySet;

/**
 * 记录正在加载的url
 *
 * @author: Liujin
 * @version: V1.0
 * @date: 2018-08-27
 * @time: 11:28
 */
public class LoadingUrls {

      /**
       * 正在加载的url集合
       */
      private final ArraySet<String> mUrlLoadings = new ArraySet<>();

      public boolean isLoading ( String url ) {

            /* 如果正在加载该url,那么不加载它 */
            synchronized(mUrlLoadings) {

                  if( mUrlLoadings.contains( url ) ) {
                        return true;
                  } else {
                        mUrlLoadings.add( url );
                        return false;
                  }
            }
      }

      public void removeLoadingUrl ( String url ) {

            /* 加载完成 */
            synchronized(mUrlLoadings) {
                  mUrlLoadings.remove( url );
            }
      }
}
