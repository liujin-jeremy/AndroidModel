package tech.threekilogram.androidmodellib.category;

import android.util.Log;
import java.io.File;
import java.lang.ref.WeakReference;
import tech.threekilogram.androidmodellib.GankCategoryBean;
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
            }
      }

      private static class SingletonHolder {

            private static final CategoryManager INSTANCE = new CategoryManager();
      }
}
