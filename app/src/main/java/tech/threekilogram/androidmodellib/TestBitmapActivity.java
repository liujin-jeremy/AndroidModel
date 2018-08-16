package tech.threekilogram.androidmodellib;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import com.threekilogram.objectbus.bus.ObjectBus;
import tech.threekilogram.depository.bitmap.BitmapConverter;
import tech.threekilogram.depository.file.converter.FileBitmapConverter;
import tech.threekilogram.depository.file.loader.File;
import tech.threekilogram.depository.net.retrofit.converter.RetrofitBitmapConverter;
import tech.threekilogram.depository.net.retrofit.loader.RetrofitLoader;

/**
 * @author liujin
 */
public class TestBitmapActivity extends AppCompatActivity {

      private static final String TAG = TestBitmapActivity.class.getSimpleName();
      private ObjectBus               mObjectBus;
      private RetrofitLoader<Bitmap>  mRetrofitLoader;
      private BitmapConverter         mBitmapConverter;
      private RetrofitBitmapConverter mRetrofitBitmapConverter;
      private File<Bitmap>            mFileLoader;

      public static void start ( Context context ) {

            Intent starter = new Intent( context, TestBitmapActivity.class );
            context.startActivity( starter );
      }

      @Override
      protected void onCreate ( Bundle savedInstanceState ) {

            super.onCreate( savedInstanceState );
            setContentView( R.layout.activity_test_bitmap );

            mObjectBus = ObjectBus.newList();

            mBitmapConverter = new BitmapConverter();
            mBitmapConverter.setWidth( 500 );
            mBitmapConverter.setHeight( 500 );
            mBitmapConverter.setMode( BitmapConverter.SAMPLE );

            mRetrofitBitmapConverter = new RetrofitBitmapConverter(
                mBitmapConverter,
                getExternalCacheDir()
            );
            mRetrofitLoader = new RetrofitLoader<>( mRetrofitBitmapConverter );

            mFileLoader = new File<>(
                getExternalCacheDir(),
                new FileBitmapConverter( mBitmapConverter, getExternalCacheDir() )
            );
      }

      public void loadFromNet ( View view ) {

            mObjectBus.toPool( new Runnable() {

                  @Override
                  public void run ( ) {

                        final String url = "https://ww1.sinaimg.cn/large/0065oQSqly1fu7xueh1gbj30hs0uwtgb.jpg";
                        Bitmap bitmap = mRetrofitLoader.load( url );

                        Log.e( TAG, "run : " + bitmap.getWidth() );

                        mFileLoader.save( url, bitmap );
                        Bitmap load = mFileLoader.load( url );

                        Log.e( TAG, "run 02: " + load.getWidth() );
                  }
            } ).run();
      }
}
