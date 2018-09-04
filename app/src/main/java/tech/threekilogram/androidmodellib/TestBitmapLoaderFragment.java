package tech.threekilogram.androidmodellib;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import com.threekilogram.objectbus.executor.PoolExecutor;
import tech.threekilogram.depository.cache.bitmap.BitmapLoader;
import tech.threekilogram.messengers.Messengers;
import tech.threekilogram.messengers.OnMessageReceiveListener;

/**
 * @author: Liujin
 * @version: V1.0
 * @date: 2018-08-25
 * @time: 9:10
 */
public class TestBitmapLoaderFragment extends Fragment implements OnClickListener,
                                                                  OnMessageReceiveListener {

      private static final String TAG = TestBitmapLoaderFragment.class.getSimpleName();

      public static TestBitmapLoaderFragment newInstance ( ) {

            TestBitmapLoaderFragment fragment = new TestBitmapLoaderFragment();
            return fragment;
      }

      private BitmapLoader mLoader;
      private Button       mLoad;
      private ImageView    mImage;
      private int          mUrlIndex;
      private String[] mBitmaps = {
          "http://ww1.sinaimg.cn/mw690/692a6bbcgw1f4fz7s830fj20gg0o00y5.jpg",
          "http://ww1.sinaimg.cn/mw690/692a6bbcgw1f4fz6g6wppj20ms0xp13n.jpg",
          "http://ww3.sinaimg.cn/mw690/81309c56jw1f4sx4ybttdj20ku0vd0ym.jpg",
          "http://ww2.sinaimg.cn/large/610dc034jw1f4mi70ns1bj20i20vedkh.jpg",
          "http://ww1.sinaimg.cn/large/610dc034jw1f4kron1wqaj20ia0rf436.jpg",
          "http://ww2.sinaimg.cn/large/610dc034gw1f4hvgpjjapj20ia0ur0vr.jpg",
          "http://ww3.sinaimg.cn/large/610dc034gw1f4fkmatcvdj20hs0qo78s.jpg",
          "http://ww1.sinaimg.cn/large/610dc034jw1f4d4iji38kj20sg0izdl1.jpg",
          "http://ww3.sinaimg.cn/large/610dc034jw1f48mxqcvkvj20lt0pyaed.jpg",
          "http://ww2.sinaimg.cn/large/610dc034jw1f454lcdekoj20dw0kumzj.jpg",
          };

      @Nullable
      @Override
      public View onCreateView (
          @NonNull LayoutInflater inflater,
          @Nullable ViewGroup container,
          @Nullable Bundle savedInstanceState ) {

            return inflater.inflate( R.layout.fragment_bitmap, container, false );
      }

      @Override
      public void onViewCreated ( @NonNull View view, @Nullable Bundle savedInstanceState ) {

            super.onViewCreated( view, savedInstanceState );
            initView( view );

            ScreenSize.init( getContext() );

            mLoader = new BitmapLoader(
                (int) Runtime.getRuntime().maxMemory() >> 3,
                getContext().getExternalFilesDir( "bitmap" )
            );
            mLoader.configBitmap( ScreenSize.getWidth(), ScreenSize.getHeight() );
      }

      private void initView ( @NonNull final View itemView ) {

            mLoad = (Button) itemView.findViewById( R.id.load );
            mLoad.setOnClickListener( this );
            mImage = (ImageView) itemView.findViewById( R.id.image );
      }

      @Override
      public void onClick ( View v ) {

            switch( v.getId() ) {
                  case R.id.load:

                        final int i = mUrlIndex % mBitmaps.length;
                        Bitmap bitmap = mLoader.loadFromMemory( mBitmaps[ i ] );
                        if( bitmap != null ) {
                              mImage.setImageBitmap( bitmap );
                              mUrlIndex++;
                              Log.e( TAG, "onClick : loadFromNet from net " + mBitmaps[ i ] );
                              return;
                        }

                        PoolExecutor.execute( new Runnable() {

                              @Override
                              public void run ( ) {

                                    Bitmap bitmap = mLoader.loadFromFile( mBitmaps[ i ] );
                                    if( bitmap == null ) {
                                          bitmap = mLoader.loadFromNet( mBitmaps[ i ] );
                                          Log.e(
                                              TAG, "run : loadFromNet from net " + mBitmaps[ i ] );
                                    } else {
                                          Log.e(
                                              TAG, "run : loadFromNet from file " + mBitmaps[ i ] );
                                    }
                                    mUrlIndex++;
                                    Messengers.send( 11, bitmap, TestBitmapLoaderFragment.this );
                              }
                        } );
                        break;
                  default:
                        break;
            }
      }

      @Override
      public void onReceive ( int what, Object extra ) {

            if( what == 11 ) {
                  mImage.setImageBitmap( (Bitmap) extra );
            }
      }
}
