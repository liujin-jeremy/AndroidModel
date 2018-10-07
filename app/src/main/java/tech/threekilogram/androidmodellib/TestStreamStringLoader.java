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
import java.io.File;
import tech.threekilogram.depository.function.encode.StringHash;
import tech.threekilogram.depository.net.retrofit.down.Downer.OnDownloadUpdateListener;
import tech.threekilogram.depository.stream.StreamLoader;

/**
 * @author Liujin 2018-10-07:20:13
 */
public class TestStreamStringLoader extends Fragment implements OnClickListener {

      private static final String TAG = TestStreamStringLoader.class.getSimpleName();

      private Button           mButton;
      private Button           mSaveToFile;
      private Button           mLoadFromFile;
      private String           mString;
      private File             mFile;
      private Button           mNetJson;
      private Button           mSaveJson;
      private Button           mFileJson;
      private GankCategoryBean mBean;
      private File             mJsonFile;
      private Button           mDownLoad;
      private Button           mLoadBitmap;
      private ImageView        mImageView2;

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
            mNetJson = itemView.findViewById( R.id.netJson );
            mNetJson.setOnClickListener( this );
            mSaveJson = itemView.findViewById( R.id.saveJson );
            mSaveJson.setOnClickListener( this );
            mFileJson = itemView.findViewById( R.id.fileJson );
            mFileJson.setOnClickListener( this );
            mDownLoad = itemView.findViewById( R.id.downLoad );
            mDownLoad.setOnClickListener( this );
            mLoadBitmap = itemView.findViewById( R.id.loadBitmap );
            mLoadBitmap.setOnClickListener( this );
            mImageView2 = itemView.findViewById( R.id.imageView2 );
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
                  case R.id.netJson:
                        netJson();
                        break;
                  case R.id.saveJson:
                        saveJson();
                        break;
                  case R.id.fileJson:
                        fileJson();
                        break;
                  case R.id.downLoad:
                        downLoad();
                        break;
                  case R.id.loadBitmap:
                        loadBitmap();
                        break;
                  default:
                        break;
            }
      }

      private void loadBitmap ( ) {

            PoolExecutor.execute( new Runnable() {

                  @Override
                  public void run ( ) {

                        final Bitmap bitmap = StreamLoader.loadBitmapFromNet(
                            "https://ws1.sinaimg.cn/large/0065oQSqly1fvexaq313uj30qo0wldr4.jpg" );
                        mImageView2.post( new Runnable() {

                              @Override
                              public void run ( ) {

                                    mImageView2.setImageBitmap( bitmap );
                              }
                        } );
                  }
            } );
      }

      private void downLoad ( ) {

            PoolExecutor.execute( new Runnable() {

                  @Override
                  public void run ( ) {

                        final File cacheDir = getActivity().getExternalCacheDir();
                        File file = new File( cacheDir, "streamDown" );
                        StreamLoader
                            .downLoad(
                                "https://gank.io/api/data/%E7%A6%8F%E5%88%A9/1/1",
                                file,
                                new OnDownloadUpdateListener() {

                                      @Override
                                      public void onProgressUpdate (
                                          File file, String url, long total, long current ) {

                                            Log.e( TAG, "onProgressUpdate : " + current );
                                      }

                                      @Override
                                      public void onFinished ( File file, String url ) {

                                            Log.e( TAG, "onFinished : " + file );
                                      }
                                }
                            );
                  }
            } );
      }

      private void fileJson ( ) {

            if( mJsonFile != null ) {
                  GankCategoryBean bean = StreamLoader
                      .loadJsonFromFile( mJsonFile, GankCategoryBean.class );
                  Log.e( TAG, "fileJson : " + bean );
            }
      }

      private void saveJson ( ) {

            if( mBean != null ) {

                  File cacheDir = getActivity().getExternalCacheDir();
                  String hash = StringHash.hash( mBean.toString() );
                  mJsonFile = new File( cacheDir, hash + ".json" );
                  StreamLoader.saveJsonToFile( mJsonFile, GankCategoryBean.class, mBean );
                  Log.e( TAG, "saveJson : " + mJsonFile );
            }
      }

      private void netJson ( ) {

            PoolExecutor.execute( new Runnable() {

                  @Override
                  public void run ( ) {

                        mBean = StreamLoader
                            .loadJsonFromNet(
                                "https://gank.io/api/data/%E7%A6%8F%E5%88%A9/1/1",
                                GankCategoryBean.class
                            );
                        Log.e( TAG, "run : " + mBean );
                  }
            } );
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
