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
import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author: Liujin
 * @version: V1.0
 * @date: 2018-08-23
 * @time: 19:51
 */
public class TestDownFragment extends Fragment implements OnClickListener {

      private static final String TAG = TestDownFragment.class.getSimpleName();

      private Button mDown;
      private Button mDelete;
      private AtomicBoolean mIsRunning = new AtomicBoolean();
      private Button mGetFile;
      private Button mDiskDown;
      private Button mDiskDelete;
      private Button mDiskGet;

      public static TestDownFragment newInstance ( ) {

            return new TestDownFragment();
      }

      private RetrofitDowner mRetrofitDowner;
      private RetrofitDowner mRetrofitDiskDowner;

      @Nullable
      @Override
      public View onCreateView (
          @NonNull LayoutInflater inflater, @Nullable ViewGroup container,
          @Nullable Bundle savedInstanceState ) {

            return inflater.inflate( R.layout.fragment_down, container, false );
      }

      @Override
      public void onViewCreated ( @NonNull View view, @Nullable Bundle savedInstanceState ) {

            super.onViewCreated( view, savedInstanceState );
            initView( view );
            mRetrofitDowner = new RetrofitDowner(
                getActivity().getExternalFilesDir( "downloadToDir" ) );

            try {
                  mRetrofitDiskDowner = new RetrofitDowner(
                      getActivity().getExternalFilesDir( "downloadToDir" ),
                      10 * 1024 * 1024
                  );
            } catch(IOException e) {
                  e.printStackTrace();
            }
      }

      private void initView ( @NonNull final View itemView ) {

            mDown = itemView.findViewById( R.id.down );
            mDown.setOnClickListener( this );
            mDelete = (Button) itemView.findViewById( R.id.delete );
            mDelete.setOnClickListener( this );
            mGetFile = (Button) itemView.findViewById( R.id.getFile );
            mGetFile.setOnClickListener( this );
            mDiskDown = (Button) itemView.findViewById( R.id.diskDown );
            mDiskDown.setOnClickListener( this );
            mDiskDelete = (Button) itemView.findViewById( R.id.diskDelete );
            mDiskDelete.setOnClickListener( this );
            mDiskGet = (Button) itemView.findViewById( R.id.diskGet );
            mDiskGet.setOnClickListener( this );
      }

      @Override
      public void onClick ( View v ) {

            switch( v.getId() ) {
                  case R.id.down:
                        down();
                        break;
                  case R.id.delete:
                        delete();
                        break;
                  case R.id.getFile:
                        getFile();
                        break;
                  case R.id.diskDown:
                        diskDown();
                        break;
                  case R.id.diskDelete:
                        diskDelete();
                        break;
                  case R.id.diskGet:
                        diskGetFile();
                        break;
                  default:
                        break;
            }
      }

      private void getFile ( ) {

            File file = mRetrofitDowner.getFile(
                "https://dn-shimo-attachment.qbox.me/ozBkk9m3DJ0h4yDc/wanghouyusheng.mp3" );

            Log.e( TAG, "getFile : " + file );
      }

      private void delete ( ) {

            mRetrofitDowner.removeFile(
                "https://dn-shimo-attachment.qbox.me/ozBkk9m3DJ0h4yDc/wanghouyusheng.mp3" );
            Log.e( TAG, "delete : finished" );
      }

      private void down ( ) {

            if( mIsRunning.get() ) {
                  return;
            }

            mIsRunning.set( true );

            PoolExecutor.execute( new Runnable() {

                  @Override
                  public void run ( ) {

                        File file = mRetrofitDowner.load(
                            "https://dn-shimo-attachment.qbox.me/ozBkk9m3DJ0h4yDc/wanghouyusheng.mp3" );

                        Log.e( TAG, "run : " + file.exists() + " " + file );
                        mIsRunning.set( false );
                  }
            } );
      }

      private void diskGetFile ( ) {

            File file = mRetrofitDiskDowner.getFile(
                "https://dn-shimo-attachment.qbox.me/ozBkk9m3DJ0h4yDc/wanghouyusheng.mp3" );

            Log.e( TAG, "getFile : " + file );
      }

      private void diskDelete ( ) {

            mRetrofitDiskDowner.removeFile(
                "https://dn-shimo-attachment.qbox.me/ozBkk9m3DJ0h4yDc/wanghouyusheng.mp3" );
            Log.e( TAG, "delete : finished" );
      }

      private void diskDown ( ) {

            if( mIsRunning.get() ) {
                  return;
            }

            mIsRunning.set( true );

            PoolExecutor.execute( new Runnable() {

                  @Override
                  public void run ( ) {

                        File file = mRetrofitDiskDowner.load(
                            "https://dn-shimo-attachment.qbox.me/ozBkk9m3DJ0h4yDc/wanghouyusheng.mp3" );

                        Log.e( TAG, "run : " + file.exists() + " " + file );
                        mIsRunning.set( false );
                  }
            } );
      }
}
