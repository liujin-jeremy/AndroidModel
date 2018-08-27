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
import com.threekilogram.objectbus.executor.PoolThreadExecutor;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import tech.threekilogram.depository.json.GsonConverter;
import tech.threekilogram.depository.json.JsonLoader;
import tech.threekilogram.depository.json.JsonLoader.OnMemorySizeTooLargeListener;

/**
 * @author: Liujin
 * @version: V1.0
 * @date: 2018-08-25
 * @time: 11:12
 */
public class TestJsonLoaderFragment extends Fragment implements OnClickListener {

      private static final String TAG = TestJsonLoaderFragment.class.getSimpleName();
      private Button mLoadMore;
      private Button mCacheCount;
      private Button mCachedMin;
      private Button mCachedMax;
      private Button mPrintMemory;
      private Button mTrim09;

      public static TestJsonLoaderFragment newInstance ( ) {

            Bundle args = new Bundle();

            TestJsonLoaderFragment fragment = new TestJsonLoaderFragment();
            fragment.setArguments( args );
            return fragment;
      }

      private JsonLoader<ResultsBean> mJsonLoader;

      @Nullable
      @Override
      public View onCreateView (
          @NonNull LayoutInflater inflater, @Nullable ViewGroup container,
          @Nullable Bundle savedInstanceState ) {

            return inflater.inflate( R.layout.fragment_json_loader, container, false );
      }

      @Override
      public void onViewCreated ( @NonNull View view, @Nullable Bundle savedInstanceState ) {

            super.onViewCreated( view, savedInstanceState );
            initView( view );

            File jsonFile = getContext().getExternalFilesDir( "jsonFile" );
            mJsonLoader = new JsonLoader<>( jsonFile, new GankGsonConverter( ResultsBean.class ) );

            PoolThreadExecutor.execute( new Runnable() {

                  @Override
                  public void run ( ) {

                        mJsonLoader.clearAllFile();
                  }
            } );
            mJsonLoader.setOnMemorySizeTooLargeListener( new OnMemorySizeTooLargeListener() {

                  @Override
                  public int getMaxMemorySize ( ) {

                        return 50;
                  }

                  @Override
                  public void onMemorySizeTooLarge ( int memorySize ) {

                        Log.e( TAG, "onMemorySizeTooLarge : " + memorySize );
                  }
            } );
      }

      private void initView ( @NonNull final View itemView ) {

            mLoadMore = (Button) itemView.findViewById( R.id.loadMore );
            mLoadMore.setOnClickListener( this );
            mCacheCount = (Button) itemView.findViewById( R.id.cacheCount );
            mCacheCount.setOnClickListener( this );
            mCachedMin = (Button) itemView.findViewById( R.id.cachedMin );
            mCachedMin.setOnClickListener( this );
            mCachedMax = (Button) itemView.findViewById( R.id.cachedMax );
            mCachedMax.setOnClickListener( this );
            mPrintMemory = (Button) itemView.findViewById( R.id.printMemory );
            mPrintMemory.setOnClickListener( this );
            mTrim09 = (Button) itemView.findViewById( R.id.trim09 );
            mTrim09.setOnClickListener( this );
      }

      @Override
      public void onClick ( View v ) {

            switch( v.getId() ) {
                  case R.id.loadMore:

                        PoolThreadExecutor.execute( new Runnable() {

                              @Override
                              public void run ( ) {

                                    int cachedCount = mJsonLoader.getCachedCount();
                                    int index = cachedCount / 10 + 1;
                                    final String url =
                                        "https://gank.io/api/data/%E7%A6%8F%E5%88%A9/10/" + index;

                                    mJsonLoader.loadMore( cachedCount, url );
                                    Log.e( TAG, "run : load more finished " + mJsonLoader
                                        .getCachedCount() );
                              }
                        } );
                        break;
                  case R.id.cacheCount:
                        int memorySize = mJsonLoader.getCachedCount();
                        Log.e( TAG, "onClick : memorySize " + memorySize );
                        break;
                  case R.id.cachedMin:
                        int cachedMin = mJsonLoader.getCachedMin();
                        Log.e( TAG, "onClick : cachedMin " + cachedMin );
                        break;
                  case R.id.cachedMax:
                        int cachedMax = mJsonLoader.getCachedMax();
                        Log.e( TAG, "onClick : cachedMax " + cachedMax );
                        break;
                  case R.id.printMemory:
                        mJsonLoader.printMemory();
                        break;
                  case R.id.trim09:
                        mJsonLoader.trimMemory( 0, 10 );
                        Log.e( TAG, "onClick : trim 0-9 finished" );
                        break;
                  default:
                        break;
            }
      }

      private class GankGsonConverter extends GsonConverter<ResultsBean> {

            public GankGsonConverter ( Class<ResultsBean> valueType ) {

                  super( valueType );
            }

            @Override
            public List<ResultsBean> fromJsonArray ( InputStream inputStream ) {

                  GankCategoryBean bean = sGson
                      .fromJson( new InputStreamReader( inputStream ), GankCategoryBean.class );
                  return bean.getResults();
            }
      }
}
