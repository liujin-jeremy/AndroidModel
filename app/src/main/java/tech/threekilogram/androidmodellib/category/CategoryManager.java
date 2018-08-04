package tech.threekilogram.androidmodellib.category;

import android.util.Log;
import java.io.File;
import java.lang.ref.WeakReference;
import tech.threekilogram.androidmodellib.GankCategoryBean;
import tech.threekilogram.androidmodellib.gankurl.GankUrl;
import tech.threekilogram.depository.client.OnValuePreparedListener;
import tech.threekilogram.depository.client.impl.ObjectBusGsonLoader;

/**
 * @author: Liujin
 * @version: V1.0
 * @date: 2018-08-04
 * @time: 13:33
 */
public class CategoryManager {

      private static final String TAG = CategoryManager.class.getSimpleName();
      private WeakReference<CategoryActivity>       mReference;
      private ObjectBusGsonLoader<GankCategoryBean> mLoader;

      private CategoryManager () {}

      public static CategoryManager getInstance () {

            return SingletonHolder.INSTANCE;
      }

      public void bind (CategoryActivity activity) {

            mReference = new WeakReference<>(activity);

            if(mLoader == null) {
                  File gank = activity.getApplicationContext().getExternalFilesDir("gank");
                  Log.e(TAG, "bind : path : " + gank.getAbsolutePath());
                  mLoader = new ObjectBusGsonLoader<>(gank, GankCategoryBean.class);
                  mLoader.setOnValuePreparedListener(new ValuePrepared());
            }
      }

      public void load (int page) {

            String androidPageUrl = GankUrl.getAndroidPageUrl(page);
            mLoader.prepareValue(androidPageUrl);
      }

      // ========================= 内部类 =========================

      private static class SingletonHolder {

            private static final CategoryManager INSTANCE = new CategoryManager();
      }

      private class ValuePrepared implements OnValuePreparedListener<GankCategoryBean> {

            @Override
            public void onValuePrepared (int result, GankCategoryBean value) {

                  Log.e(TAG, "onValuePrepared : " + result + " " + value.toString());
                  if(result < 100) {
                        CategoryActivity categoryActivity = mReference.get();
                        if(categoryActivity != null) {
                              categoryActivity.addCategory(value.getResults());
                        }
                  }
            }
      }
}
