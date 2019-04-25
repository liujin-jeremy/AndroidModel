package tech.liujin.androidmodellib.test;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
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
import com.threekilogram.bitmapreader.BitmapReader;
import com.threekilogram.objectbus.Threads;
import java.io.File;
import tech.liujin.cache.net.DownLoader;
import tech.liujin.cache.net.Downer;
import tech.liujin.cache.util.encode.StringHash;
import tech.liujin.androidmodellib.R;
import tech.liujin.androidmodellib.util.FileManager;

/**
 * @author Liujin 2018-10-25:22:15
 */
public class DownLoadFragment extends Fragment implements OnClickListener {

      private static final String TAG = DownLoadFragment.class.getSimpleName();

      private Button    mDownLoad;
      private ImageView mImageView2;
      private Button    mDownLoad2;
      private ImageView mImageView3;

      private DownLoader mDownLoader;

      public static DownLoadFragment newInstance ( ) {

            Bundle args = new Bundle();

            DownLoadFragment fragment = new DownLoadFragment();
            fragment.setArguments( args );
            return fragment;
      }

      @Nullable
      @Override
      public View onCreateView (
          @NonNull LayoutInflater inflater, @Nullable ViewGroup container,
          @Nullable Bundle savedInstanceState ) {

            return inflater.inflate( R.layout.fragment_down, container, false );
      }

      private void initView ( @NonNull final View itemView ) {

            mDownLoad = itemView.findViewById( R.id.downLoad );
            mDownLoad.setOnClickListener( this );
            mImageView2 = itemView.findViewById( R.id.imageView2 );
            mDownLoad2 = itemView.findViewById( R.id.downLoad2 );
            mDownLoad2.setOnClickListener( this );
            mImageView3 = itemView.findViewById( R.id.imageView3 );
      }

      @Override
      public void onViewCreated ( @NonNull View view, @Nullable Bundle savedInstanceState ) {

            super.onViewCreated( view, savedInstanceState );
            initView( view );

            mDownLoader = new DownLoader( FileManager.getDownloads() );
      }

      @Override
      public void onClick ( View v ) {

            switch( v.getId() ) {
                  case R.id.downLoad:
                        Threads.COMPUTATION.execute( ( ) -> {
                              File down = mDownLoader.down(
                                  "http://7xi8d6.com1.z0.glb.clouddn.com/2017-10-31-nozomisasaki_official_31_10_2017_10_49_17_24.jpg" );

                              Bitmap bitmap = BitmapReader.read( down, Config.ARGB_8888 );
                              v.post( ( ) -> {

                                    mImageView2.setImageBitmap( bitmap );
                                    Log.e( TAG, "onClick : " + down + " " + down.exists() );
                              } );
                        } );
                        break;
                  case R.id.downLoad2:
                        Threads.COMPUTATION.execute( ( ) -> {

                              String url = "http://7xi8d6.com1.z0.glb.clouddn.com/20171027114026_v8VFwP_joanne_722_27_10_2017_11_40_17_370.jpeg";
                              File down = Downer.down(
                                  url,
                                  new File( FileManager.getDocuments(), StringHash.hash( url ) )
                              );

                              Bitmap bitmap = BitmapReader.read( down, Config.RGB_565 );
                              v.post( ( ) -> {

                                    mImageView3.setImageBitmap( bitmap );
                                    Log.e( TAG, "onClick : " + down + " " + down.exists() );
                              } );
                        } );
                        break;
                  default:
                        break;
            }
      }
}
