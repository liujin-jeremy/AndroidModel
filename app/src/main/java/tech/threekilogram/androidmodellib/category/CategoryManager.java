package tech.threekilogram.androidmodellib.category;

import java.lang.ref.WeakReference;
import tech.threekilogram.androidmodellib.GankCategoryBean;
import tech.threekilogram.androidmodellib.gankurl.GankUrl;
import tech.threekilogram.depository.client.OnValuePreparedListener;

/**
 * @author: Liujin
 * @version: V1.0
 * @date: 2018-08-04
 * @time: 13:33
 */
public class CategoryManager {

      private static final String TAG = CategoryManager.class.getSimpleName();
      private WeakReference<CategoryActivity> mReference;

      private CategoryManager () {}

      public static CategoryManager getInstance () {

            return SingletonHolder.INSTANCE;
      }

      public void bind (CategoryActivity activity) {

            mReference = new WeakReference<>(activity);
      }

      public void load (int page) {

            String androidPageUrl = GankUrl.getAndroidPageUrl(page);
      }

      // ========================= 内部类 =========================

      private static class SingletonHolder {

            private static final CategoryManager INSTANCE = new CategoryManager();
      }

      private class ValuePrepared implements OnValuePreparedListener<GankCategoryBean> {

            @Override
            public void onValuePrepared (int result, GankCategoryBean value) {

                  if(result > 0 && value != null) {

                  }
            }
      }
}
