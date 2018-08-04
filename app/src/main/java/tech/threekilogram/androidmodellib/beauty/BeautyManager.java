package tech.threekilogram.androidmodellib.beauty;

import android.graphics.Bitmap;
import android.util.Log;
import com.example.bitmapreader.BitmapReader;
import java.io.File;
import java.lang.ref.WeakReference;
import tech.threekilogram.androidmodellib.GankCategoryBean.ResultsBean;
import tech.threekilogram.androidmodellib.gankurl.GankUrl;
import tech.threekilogram.depository.client.bitmap.ObjectBusBitmapLoader;
import tech.threekilogram.depository.client.bitmap.OnBitmapFilePreparedListener;
import tech.threekilogram.depository.client.impl.ObjectBusGsonLoader;

/**
 * @author: Liujin
 * @version: V1.0
 * @date: 2018-07-31
 * @time: 18:11
 */
public class BeautyManager {

      private static final String TAG = BeautyManager.class.getSimpleName();

      private WeakReference<BeautyActivity>    mBeautyActivityRef;
      private ObjectBusBitmapLoader            mBitmapLoader;
      private ObjectBusGsonLoader<ResultsBean> mGsonLoader;

      public static BeautyManager getInstance () {

            return SingletonHolder.INSTANCE;
      }

      private BeautyManager () {

      }

      void bind (BeautyActivity activity) {

            mBeautyActivityRef = new WeakReference<>(activity);

            if(mBitmapLoader == null) {
                  File gankBeauty = activity.getApplicationContext()
                                            .getExternalFilesDir("GankBeauty");

                  Log.e(TAG, "bind : " + gankBeauty.getAbsolutePath());

                  mBitmapLoader = new ObjectBusBitmapLoader(gankBeauty);

                  File gank = activity.getApplicationContext()
                                      .getExternalFilesDir("gank");

                  mGsonLoader = new ObjectBusGsonLoader<>(gank, ResultsBean.class);
            }
      }

      public void setBitmapWidth (final int width) {

            mBitmapLoader.setOnBitmapFilePreparedListener(
                new OnBitmapFilePreparedListener<String>() {

                      @Override
                      public Bitmap onBitmapFilePrepared (String key, File file) {

                            return BitmapReader.decodeBitmapToMatchWidth(file, width);
                      }
                });
      }

      public void loadBitmap (int page) {

            String beautyPageUrl = GankUrl.getBeautyPageUrl(1);
            mGsonLoader.prepareValue(beautyPageUrl);
      }

      // ========================= 内部类 =========================

      private static class SingletonHolder {

            private static final BeautyManager INSTANCE = new BeautyManager();
      }
}
