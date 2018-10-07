package tech.threekilogram.androidmodellib;

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
import com.threekilogram.objectbus.executor.PoolExecutor;
import java.io.File;
import tech.threekilogram.depository.function.encode.StringHash;
import tech.threekilogram.depository.stream.StreamLoader;

/**
 * @author Liujin 2018-10-07:20:13
 */
public class TestStreamStringLoader extends Fragment implements OnClickListener {

      private static final String TAG = TestStreamStringLoader.class.getSimpleName();

      private Button mButton;
      private Button mSaveToFile;
      private Button mLoadFromFile;
      private String mString;
      private File   mFile;

      public static TestStreamStringLoader newInstance ( ) {

            TestStreamStringLoader fragment = new TestStreamStringLoader();
            return fragment;
      }

      @Nullable
      @Override
      public View onCreateView (
          @NonNull LayoutInflater inflater, @Nullable ViewGroup container,
          @Nullable Bundle savedInstanceState ) {

            return inflater.inflate( R.layout.fragment_stream_string, container, false );
      }

      @Override
      public void onViewCreated ( @NonNull View view, @Nullable Bundle savedInstanceState ) {

            super.onViewCreated( view, savedInstanceState );
            initView( view );
      }

      private void initView ( @NonNull final View itemView ) {

            mButton = itemView.findViewById( R.id.loadFromNet );
            mButton.setOnClickListener( this );
            mSaveToFile = itemView.findViewById( R.id.saveToFile );
            mSaveToFile.setOnClickListener( this );
            mLoadFromFile = itemView.findViewById( R.id.loadFromFile );
            mLoadFromFile.setOnClickListener( this );
      }

      @Override
      public void onClick ( View v ) {

            switch( v.getId() ) {
                  case R.id.loadFromNet:
                        loadFromNet();
                        break;
                  case R.id.saveToFile:
                        saveToFile();
                        break;
                  case R.id.loadFromFile:
                        loadFromFile();
                        break;
                  default:
                        break;
            }
      }

      private void loadFromFile ( ) {

            if( mFile != null ) {
                  String s = StreamLoader.loadStringFromFile( mFile );
                  Log.e( TAG, "loadFromFile : " + s );
            }
      }

      private void saveToFile ( ) {

            if( mString != null ) {
                  File cacheDir = getActivity().getExternalCacheDir();
                  String hash = StringHash.hash( mString );
                  mFile = new File( cacheDir, hash );

                  StreamLoader.saveStringToFile( mString, mFile );
                  Log.e( TAG, "saveToFile : " + mFile );
            }
      }

      private void loadFromNet ( ) {

            PoolExecutor.execute( new Runnable() {

                  @Override
                  public void run ( ) {

                        Log.e( TAG, "run : click" );
                        mString = StreamLoader
                            .loadStringFromNet( "https://gank.io/api/data/%E7%A6%8F%E5%88%A9/1/1" );

                        Log.e( TAG, "run : " + mString );
                  }
            } );
      }
}
