package tech.threekilogram.depository.net;

import android.support.v4.util.ArraySet;

/**
 * 记录正在加载的url,防止重复加载该url
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

      /**
       * 测试是否正在加载该url,当加载完成后记得删除{@link #removeLoadingUrl(String)}
       *
       * @param url 需要测试的url
       *
       * @return true:正在加载url
       */
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
