package tech.threekilogram.androidmodellib.beauty;

import android.util.Log;
import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import tech.threekilogram.androidmodellib.GankCategoryBean;
import tech.threekilogram.depository.file.FileLoader;
import tech.threekilogram.depository.file.GsonFileMapper;
import tech.threekilogram.depository.memory.MemoryListLoader;
import tech.threekilogram.depository.net.retrofit.get.RetrofitGetLoader;
import tech.threekilogram.depository.net.retrofit.get.RetrofitStringMapper;

/**
 * @author: Liujin
 * @version: V1.0
 * @date: 2018-07-31
 * @time: 18:11
 */
public class BeautyManager {

      private static final String TAG = BeautyManager.class.getSimpleName();

      private WeakReference<BeautyActivity> mBeautyActivityRef;
      private MemoryListLoader<GankCategoryBean> mMemoryListLoader = new MemoryListLoader<>();
      private FileLoader<String, GankCategoryBean> mFileLoader;
      private RetrofitGetLoader<String, String> mRetrofitGetLoader =
          new RetrofitGetLoader<>(new RetrofitStringMapper());

      public static BeautyManager getInstance () {

            return SingletonHolder.INSTANCE;
      }

      private BeautyManager () {

      }

      void bind (BeautyActivity activity) {

            if(mFileLoader == null) {
                  File file = activity.getApplicationContext().getExternalFilesDir("gank");
                  GsonFileMapper<GankCategoryBean> fileMapper = new GsonFileMapper<>(
                      file, GankCategoryBean.class);
                  mFileLoader = new FileLoader<>(fileMapper);
            }

            mBeautyActivityRef = new WeakReference<>(activity);
      }

      void loadTodayBeauty () {

            final int todayIndex = 1;
            String url = GankBeautyUrl.getPageUrl(1);
            GankCategoryBean gankCategoryBean = mMemoryListLoader.loadFromMemory(0);
            if(gankCategoryBean == null) {
                  try {
                        gankCategoryBean = mFileLoader.loadFromFile(url);

                        if(gankCategoryBean == null) {

                              String loadFromNet = mRetrofitGetLoader.loadFromNet(url);
                              if(loadFromNet != null) {

                                    Log.e(TAG, "loadTodayBeauty : from net finished");
                              }
                        }
                  } catch(IOException e) {
                        e.printStackTrace();
                  }
            }
      }

      // ========================= 内部类 =========================

      private static class SingletonHolder {

            private static final BeautyManager INSTANCE = new BeautyManager();
      }
}
