package tech.threekilogram.androidmodellib.beauty;

import java.lang.ref.WeakReference;

/**
 * @author: Liujin
 * @version: V1.0
 * @date: 2018-07-31
 * @time: 18:11
 */
public class BeautyManager {

      private static final String TAG = BeautyManager.class.getSimpleName();

      private WeakReference<BeautyActivity> mBeautyActivityRef;

      public static BeautyManager getInstance () {

            return SingletonHolder.INSTANCE;
      }

      private BeautyManager () {

      }

      void bind (BeautyActivity activity) {



            mBeautyActivityRef = new WeakReference<>(activity);
      }

      void loadTodayBeauty () {

            final int todayIndex = 1;
            String url = GankBeautyUrl.getPageUrl(1);

      }

      // ========================= 内部类 =========================

      private static class SingletonHolder {

            private static final BeautyManager INSTANCE = new BeautyManager();
      }
}
