package tech.liujin.androidmodellib.test;

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
import com.threekilogram.objectbus.Threads;
import java.io.File;
import tech.liujin.cache.cache.bitmap.BitmapLoader;
import tech.liujin.androidmodellib.R;
import tech.liujin.androidmodellib.util.FileManager;

/**
 * @author Liujin 2018-10-26:8:13
 */
public class BitmapFragment extends Fragment implements OnClickListener {

      private static final String TAG = BitmapFragment.class.getSimpleName();

      private Button    mNet;
      private Button    mFile;
      private Button    mMemory;
      private Button    mWrap;
      private ImageView mImageView4;

      private BitmapLoader mBitmapLoader;

      public static BitmapFragment newInstance ( ) {

            Bundle args = new Bundle();

            BitmapFragment fragment = new BitmapFragment();
            fragment.setArguments( args );
            return fragment;
      }

      @Nullable
      @Override
      public View onCreateView (
          @NonNull LayoutInflater inflater, @Nullable ViewGroup container,
          @Nullable Bundle savedInstanceState ) {

            return inflater.inflate( R.layout.fragment_bitmap, container, false );
      }

      private void initView ( @NonNull final View itemView ) {

            mNet = itemView.findViewById( R.id.net );
            mNet.setOnClickListener( this );
            mFile = itemView.findViewById( R.id.file );
            mFile.setOnClickListener( this );
            mMemory = itemView.findViewById( R.id.memory );
            mMemory.setOnClickListener( this );
            mWrap = itemView.findViewById( R.id.wrap );
            mWrap.setOnClickListener( this );
            mImageView4 = itemView.findViewById( R.id.imageView4 );
      }

      @Override
      public void onViewCreated ( @NonNull View view, @Nullable Bundle savedInstanceState ) {

            super.onViewCreated( view, savedInstanceState );
            initView( view );
            File picture = FileManager.getPicture();
            mBitmapLoader = new BitmapLoader(
                (int) Runtime.getRuntime().maxMemory() >> 3, picture );
      }

      @Override
      public void onClick ( View v ) {

            final String url = "http://7xi8d6.com1.z0.glb.clouddn.com/20171219224721_wFH5PL_Screenshot.jpeg";

            switch( v.getId() ) {
                  case R.id.net:
                        Threads.COMPUTATION.execute( ( ) -> {

                              Bitmap bitmap = mBitmapLoader.loadFromNet( url );
                              mImageView4.post( ( ) -> {

                                    mImageView4.setImageBitmap( bitmap );
                                    Log.e( TAG, "onClick : " + bitmap.getWidth() + " " + bitmap
                                        .getHeight() );
                              } );
                        } );
                        break;
                  case R.id.file:
                        Threads.COMPUTATION.execute( ( ) -> {

                              Bitmap bitmap = mBitmapLoader.loadFromFile( url, 500, 500 );
                              mImageView4.post( ( ) -> {

                                    mImageView4.setImageBitmap( bitmap );
                                    Log.e( TAG, "onClick : " + bitmap.getWidth() + " " + bitmap
                                        .getHeight() );
                              } );
                        } );
                        break;
                  case R.id.memory:
                        Threads.COMPUTATION.execute( ( ) -> {

                              Bitmap bitmap = mBitmapLoader.loadFromMemory( url );
                              mImageView4.post( ( ) -> {

                                    mImageView4.setImageBitmap( bitmap );
                                    Log.e( TAG, "onClick : " + bitmap.getWidth() + " " + bitmap
                                        .getHeight() );
                              } );
                        } );
                        break;
                  case R.id.wrap:
                        mImageView4.setImageDrawable( null );
                        break;
                  default:
                        break;
            }
      }
}
