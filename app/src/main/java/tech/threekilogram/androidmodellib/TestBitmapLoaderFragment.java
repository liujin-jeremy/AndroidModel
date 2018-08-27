package tech.threekilogram.androidmodellib;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import com.threekilogram.objectbus.executor.PoolThreadExecutor;
import tech.threekilogram.depository.bitmap.BitmapLoader;
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
          "http://ww4.sinaimg.cn/large/610dc034jw1f4vmdn2f5nj20kq0rm755.jpg",
          "http://ww1.sinaimg.cn/mw690/692a6bbcgw1f4fz7s830fj20gg0o00y5.jpg",
          "http://ww1.sinaimg.cn/mw690/692a6bbcgw1f4fz6g6wppj20ms0xp13n.jpg",
          "http://ww3.sinaimg.cn/mw690/81309c56jw1f4sx4ybttdj20ku0vd0ym.jpg",
          "http://ww4.sinaimg.cn/mw690/9844520fjw1f4fqrpw1fvj21911wlb2b.jpg",
          "http://ww4.sinaimg.cn/mw690/9844520fjw1f4fqribdg1j21911w0kjn.jpg",
          "http://ww4.sinaimg.cn/large/610dc034jw1f4nog8tjfrj20eg0mgab7.jpg",
          "http://ww2.sinaimg.cn/large/610dc034jw1f4mi70ns1bj20i20vedkh.jpg",
          "http://ww1.sinaimg.cn/large/610dc034jw1f4kron1wqaj20ia0rf436.jpg",
          "http://ww2.sinaimg.cn/large/610dc034gw1f4hvgpjjapj20ia0ur0vr.jpg",
          "http://ac-OLWHHM4o.clouddn.com/4063qegYjlC8nx6uEqxV0kT3hn6hdqJqVWPKpdrS",
          "http://ww3.sinaimg.cn/large/610dc034gw1f4fkmatcvdj20hs0qo78s.jpg",
          "http://ac-OLWHHM4o.clouddn.com/DPCY44vIYPjVPKNzfHjMdXd9bk27q0i1X2nIaO8Z",
          "http://ww1.sinaimg.cn/large/610dc034jw1f4d4iji38kj20sg0izdl1.jpg",
          "http://ww4.sinaimg.cn/large/610dc034jw1f49s6i5pg7j20go0p043b.jpg",
          "http://ww3.sinaimg.cn/large/610dc034jw1f48mxqcvkvj20lt0pyaed.jpg",
          "http://ww4.sinaimg.cn/large/610dc034jw1f47gspphiyj20ia0rf76w.jpg",
          "http://ww4.sinaimg.cn/large/610dc034jw1f46bsdcls2j20sg0izac0.jpg",
          "http://ww2.sinaimg.cn/large/610dc034jw1f454lcdekoj20dw0kumzj.jpg",
          "http://ww4.sinaimg.cn/large/610dc034jw1f41lxgc3x3j20jh0tcn14.jpg"
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
                        PoolThreadExecutor.execute( new Runnable() {

                              @Override
                              public void run ( ) {

                                    int i = mUrlIndex % mBitmaps.length;
                                    Bitmap bitmap = mLoader.load( mBitmaps[ i ] );
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
