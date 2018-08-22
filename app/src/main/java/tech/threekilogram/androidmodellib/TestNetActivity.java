package tech.threekilogram.androidmodellib;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import com.threekilogram.objectbus.bus.ObjectBus;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;
import tech.threekilogram.depository.json.GsonConverter;
import tech.threekilogram.depository.json.JsonLoader;
import tech.threekilogram.messengers.Messengers;
import tech.threekilogram.messengers.OnMessageReceiveListener;

/**
 * @author liujin
 */
public class TestNetActivity extends AppCompatActivity implements OnMessageReceiveListener {

      private static final String TAG = TestNetActivity.class.getSimpleName();
      private ObjectBus mObjectBus;
      private JsonLoader<ResultsBean> mJsonLoader;
      private int mStartPage = 10;
      private int mMorePage;
      private int mLessPage;

      public static void start ( Context context ) {

            Intent starter = new Intent( context, TestNetActivity.class );
            context.startActivity( starter );
      }

      @Override
      protected void onCreate ( Bundle savedInstanceState ) {

            super.onCreate( savedInstanceState );
            setContentView( R.layout.activity_test_net );

            mObjectBus = ObjectBus.newList();
            mJsonLoader = new JsonLoader<>(
                getExternalCacheDir(), new TestGsonConverter( ResultsBean.class ),
                90
            );
            /* 一般在app退出时清空,此处只是示例 */
            try {
                  mJsonLoader.clearFileCache();
            } catch(IOException e) {
                  e.printStackTrace();
            }
            /* 配置内存中最大保存数据量 */
            mJsonLoader.setMemoryMaxCount( 64 );
      }

      public void loadMore ( View view ) {

            if( mJsonLoader.isLoading() ) {
                  return;
            }

            mObjectBus.toPool( new Runnable() {

                  @Override
                  public void run ( ) {

                        int pageOff = mMorePage++;
                        int page = mStartPage + pageOff;

                        final String url =
                            "https://gank.io/api/data/%E7%A6%8F%E5%88%A9/10/" + page;
                        mJsonLoader.loadMore( url );
                  }
            } ).run();
      }

      public void loadLess ( View view ) {

            if( mJsonLoader.isLoading() ) {
                  return;
            }

            mObjectBus.toPool( new Runnable() {

                  @Override
                  public void run ( ) {

                        int pageOff = --mLessPage;
                        int page = mStartPage + pageOff;

                        if( page < 1 ) {

                              mLessPage++;
                              Messengers.send( 12, TestNetActivity.this );
                              return;
                        }

                        final String url =
                            "https://gank.io/api/data/%E7%A6%8F%E5%88%A9/10/" + page;
                        mJsonLoader.loadLess( url );
                  }
            } ).run();
      }

      public void cacheMax ( View view ) {

            int maxIndex = mJsonLoader.getCacheMaxIndex();
            ResultsBean resultsBean = mJsonLoader.get( maxIndex );

            Log.e( TAG, "cacheMax : index: " + maxIndex );
            Log.e( TAG, "cacheMax : " + resultsBean );
      }

      public void cacheMin ( View view ) {

            int minIndex = mJsonLoader.getCacheMinIndex();
            ResultsBean resultsBean = mJsonLoader.get( minIndex );

            Log.e( TAG, "cacheMin : index: " + minIndex );
            Log.e( TAG, "cacheMin : " + resultsBean );
      }

      public void cacheCount ( View view ) {

            int cacheCount = mJsonLoader.getCacheCount();
            Log.e( TAG, "cacheCount : " + cacheCount );
      }

      public void memoryCount ( View view ) {

            int memorySize = mJsonLoader.getMemorySize();
            Log.e( TAG, "memoryCount : " + memorySize );
      }

      @Override
      public void onReceive ( int what, Object extra ) {

            if( what == 12 ) {
                  Toast.makeText(
                      TestNetActivity.this,
                      "已经加载到最少",
                      Toast.LENGTH_SHORT
                  ).show();
            }
      }

      // ========================= 辅助类 =========================

      /**
       * convert net stream to json bean list
       */
      private class TestGsonConverter extends GsonConverter<ResultsBean> {

            public TestGsonConverter ( Class<ResultsBean> valueType ) {

                  super( valueType );
            }

            @Override
            public List<ResultsBean> fromJsonArray ( InputStream inputStream ) {

                  Reader reader = new InputStreamReader( inputStream );
                  GankCategoryBean bean = sGson.fromJson( reader, GankCategoryBean.class );

                  return bean.getResults();
            }
      }
}
