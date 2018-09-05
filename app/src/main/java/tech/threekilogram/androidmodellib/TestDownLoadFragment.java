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
import tech.threekilogram.depository.net.retrofit.down.Downer;

/**
 * @author: Liujin
 * @version: V1.0
 * @date: 2018-09-03
 * @time: 12:17
 */
public class TestDownLoadFragment extends Fragment implements OnClickListener {

      private static final String TAG = TestDownLoadFragment.class.getSimpleName();

      private Button mDownToEx;
      private Button mDeleteEx;
      private Button mDown;
      private Button mDelete;

      public static TestDownLoadFragment newInstance ( ) {

            return new TestDownLoadFragment();
      }

      @Nullable
      @Override
      public View onCreateView (
          @NonNull LayoutInflater inflater, @Nullable ViewGroup container,
          @Nullable Bundle savedInstanceState ) {

            return inflater.inflate( R.layout.fragment_down_loader, container, false );
      }

      @Override
      public void onViewCreated ( @NonNull View view, @Nullable Bundle savedInstanceState ) {

            super.onViewCreated( view, savedInstanceState );
            initView( view );
      }

      private void initView ( @NonNull final View itemView ) {

            mDownToEx = (Button) itemView.findViewById( R.id.downToEx );
            mDownToEx.setOnClickListener( this );
            mDeleteEx = (Button) itemView.findViewById( R.id.deleteEx );
            mDeleteEx.setOnClickListener( this );
            mDown = (Button) itemView.findViewById( R.id.down );
            mDown.setOnClickListener( this );
            mDelete = (Button) itemView.findViewById( R.id.delete );
            mDelete.setOnClickListener( this );
      }

      @Override
      public void onClick ( View v ) {

            switch( v.getId() ) {
                  case R.id.downToEx:
                        downToEx();
                        break;
                  case R.id.deleteEx:
                        deleteEx();
                        break;
                  case R.id.down:
                        down();
                        break;
                  case R.id.delete:
                        delete();
                        break;
                  default:
                        break;
            }
      }

      private void delete ( ) {

            final String url = "http://p4.so.qhmsg.com/t01b07c46919f693549.jpg";
            final File dir = getContext().getCacheDir();
            final File file = new File( dir, Downer.hashUrl( url ) );

            Log.e( TAG, "delete : " + file + " " + file.exists() );
            file.delete();
      }

      private void down ( ) {

            final String url = "http://p4.so.qhmsg.com/t01b07c46919f693549.jpg";
            final File dir = getContext().getCacheDir();
            final File file = new File( dir, Downer.hashUrl( url ) );

            PoolExecutor.execute( new Runnable() {

                  @Override
                  public void run ( ) {

                        Log.e( TAG, "run : " + url );
                        File down = Downer.downloadTo( file, url );
                        Log.e( TAG, "run : " + down );
                  }
            } );
      }

      private void deleteEx ( ) {

            final String url = "http://p2.so.qhimgs1.com/t014a39bd9c52ac5ab2.jpg";
            final File dir = getContext().getExternalFilesDir( "downLoader" );
            final File file = new File( dir, Downer.hashUrl( url ) );

            Log.e( TAG, "deleteEx : " + file + " " + file.exists() );
            file.delete();
      }

      private void downToEx ( ) {

            final String url = "http://p2.so.qhimgs1.com/t014a39bd9c52ac5ab2.jpg";
            final File dir = getContext().getExternalFilesDir( "downLoader" );
            final File file = new File( dir, Downer.hashUrl( url ) );

            PoolExecutor.execute( new Runnable() {

                  @Override
                  public void run ( ) {

                        Log.e( TAG, "run : " + url );
                        File down = Downer.downloadTo( file, url );
                        Log.e( TAG, "run : " + down );
                  }
            } );
      }
}
