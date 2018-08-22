package tech.threekilogram.androidmodellib;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import com.threekilogram.objectbus.executor.PoolThreadExecutor;
import java.io.File;
import tech.threekilogram.depository.bitmap.BitmapConverter;
import tech.threekilogram.depository.bitmap.BitmapLoader;
import tech.threekilogram.depository.bitmap.BitmapLoader.OnLoadFinishedListener;

/**
 * @author liujin
 */
public class TestBitmapLoaderActivity extends AppCompatActivity {

      private static final String TAG = TestBitmapLoaderActivity.class.getSimpleName();
      private TestBitmapLoader mBitmapLoader;
      private ImageView        mImageView;

      private String[] mUrls = {
          "http://ww1.sinaimg.cn/large/0065oQSqly1fsysqszneoj30hi0pvqb7.jpg",
          "http://ww1.sinaimg.cn/large/0065oQSqly1fswhaqvnobj30sg14hka0.jpg",
          "http://ww1.sinaimg.cn/large/0065oQSqly1fsq9iq8ttrj30k80q9wi4.jpg",
          "http://7xi8d6.com1.z0.glb.clouddn.com/2017-04-16-17934400_1738549946443321_2924146161843437568_n.jpg",
          "http://7xi8d6.com1.z0.glb.clouddn.com/2017-04-14-17881962_1329090457138411_8289893708619317248_n.jpg",
          "http://7xi8d6.com1.z0.glb.clouddn.com/2017-04-13-17882785_926451654163513_7725522121023029248_n.jpg",
          "http://7xi8d6.com1.z0.glb.clouddn.com/2017-04-12-17662441_1675934806042139_7236493360834281472_n.jpg",
          "http://7xi8d6.com1.z0.glb.clouddn.com/2017-04-09-17586558_426275167734768_6312107349515436032_n.jpg",
          "http://7xi8d6.com1.z0.glb.clouddn.com/2017-04-07-17817932_274034076387428_5240190736292380672_n.jpg",
          "http://7xi8d6.com1.z0.glb.clouddn.com/2017-04-06-17493825_1061197430652762_1457834104966873088_n.jpg",
          "http://7xi8d6.com1.z0.glb.clouddn.com/2017-04-04-17438270_1418311001574160_8728796670000627712_n.jpg",
          "http://7xi8d6.com1.z0.glb.clouddn.com/2017-03-29-17267498_392021674501739_8632065627013513216_n.jpg",
          "http://7xi8d6.com1.z0.glb.clouddn.com/2017-03-28-17494350_115579865647756_2448160714821468160_n.jpg",
          "http://7xi8d6.com1.z0.glb.clouddn.com/2017-03-26-17495078_643307445877569_4485136026821459968_n.jpg",
          "http://7xi8d6.com1.z0.glb.clouddn.com/2017-03-24-17438359_1470934682925012_1066984844010979328_n.jpg",
          "http://7xi8d6.com1.z0.glb.clouddn.com/2017-03-23-17265820_645330569008169_4543676027339014144_n.jpg",
          "http://7xi8d6.com1.z0.glb.clouddn.com/2017-03-22-17332868_1929305090624552_8918542166154805248_n.jpg",
          "http://7xi8d6.com1.z0.glb.clouddn.com/2017-03-21-17268102_763630507137257_3620762734536163328_n%20-1-.jpg",
          "http://7xi8d6.com1.z0.glb.clouddn.com/2017-03-20-17333300_1680707251945881_2009298023053524992_n.jpg",
          "http://7xi8d6.com1.z0.glb.clouddn.com/2017-02-15-16464434_414363458902323_3665858302006263808_n.jpg",
          "http://7xi8d6.com1.z0.glb.clouddn.com/2017-02-14-16123260_755771577930478_8918176595718438912_n.jpg",
          "http://7xi8d6.com1.z0.glb.clouddn.com/2017-02-13-16464498_1247940031909047_2763412151866490880_n.jpg",
          "http://7xi8d6.com1.z0.glb.clouddn.com/2017-02-07-032924.jpg"
      };

      private int mIndex;

      public static void start ( Context context ) {

            Intent starter = new Intent( context, TestBitmapLoaderActivity.class );
            context.startActivity( starter );
      }

      @Override
      protected void onCreate ( Bundle savedInstanceState ) {

            ScreenSize.init( this );

            super.onCreate( savedInstanceState );
            setContentView( R.layout.activity_bitmap );
            initView();

            long maxMemory = Runtime.getRuntime().maxMemory();
            Log.e( TAG, "onCreate : " + ( maxMemory >> 3 ) );
            mBitmapLoader = new TestBitmapLoader(
                (int) maxMemory >> 3,
                getExternalFilesDir( "beauty" )
            );
            mBitmapLoader.configBitmap(
                ScreenSize.getWidth(),
                ScreenSize.getHeight(),
                BitmapConverter.MATCH_WIDTH
            );
            mBitmapLoader.setOnLoadFinishedListener( new OnLoadFinishedListener() {

                  @Override
                  public void onFinished ( String url, Bitmap bitmap ) {

                        if( bitmap == null ) {

                              Toast.makeText(
                                  TestBitmapLoaderActivity.this,
                                  "null bitmap",
                                  Toast.LENGTH_SHORT
                              ).show();
                        } else {

                              Log.e( TAG, "onFinished : " + bitmap.getWidth() );
                              mImageView.setImageBitmap( bitmap );
                        }
                  }
            } );
      }

      private void initView ( ) {

            mImageView = findViewById( R.id.imageView );
      }

      public void load ( View view ) {

            mBitmapLoader
                .load( mUrls[ 0 ] );
      }

      public void memory ( View view ) {

            int memorySize = mBitmapLoader.memorySize();
            Log.e( TAG, "memory : " + memorySize );
      }

      public void next ( View view ) {

            mIndex++;
            mBitmapLoader.load( mUrls[ mIndex % mUrls.length ] );
      }

      public void prev ( View view ) {

            mIndex--;
            if( mIndex < 0 ) {
                  mIndex = 0;
            }
            mBitmapLoader.load( mUrls[ mIndex % mUrls.length ] );
      }

      private class TestBitmapLoader extends BitmapLoader {

            public TestBitmapLoader ( int maxMemorySize, File cacheDir ) {

                  super( maxMemorySize, cacheDir );
            }

            @Override
            protected void asyncLoad ( AsyncLoadRunnable runnable ) {

                  PoolThreadExecutor.execute( runnable );
            }
      }
}