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
import java.util.List;
import tech.threekilogram.depository.function.Doing;
import tech.threekilogram.depository.json.GsonConverter;
import tech.threekilogram.depository.json.JsonLoader;

/**
 * @author: Liujin
 * @version: V1.0
 * @date: 2018-08-25
 * @time: 11:12
 */
public class TestJsonLoaderFragment extends Fragment implements OnClickListener {

      private static final String TAG = TestJsonLoaderFragment.class.getSimpleName();

      private int   mDayIndex;
      private Doing mDoing;

      private Button mHistory;
      private Button mHistorySize;
      private Button mMore10Bean;
      private Button mLoad1More;
      private Button mAddIndex;
      private Button mSubIndex;
      private Button mIndexUrl;
      private Button mMemoryContains;
      private Button mMemoryLoad;
      private Button mFileContains;
      private Button mFileLoad;

      public static TestJsonLoaderFragment newInstance ( ) {

            Bundle args = new Bundle();

            TestJsonLoaderFragment fragment = new TestJsonLoaderFragment();
            fragment.setArguments( args );
            return fragment;
      }

      private JsonLoader<GankHistoryBean> mHistoryLoader;
      private JsonLoader<GankDayBean>     mDayLoader;

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

            mHistoryLoader = new JsonLoader<>(
                new GsonConverter<GankHistoryBean>( GankHistoryBean.class )
            );

            File jsonFile = getContext().getExternalFilesDir( "jsonFile" );
            mDayLoader = new JsonLoader<>(
                jsonFile,
                new GsonConverter<GankDayBean>( GankDayBean.class )
            );

            mDoing = new Doing();
      }

      private void initView ( @NonNull final View itemView ) {

            mHistory = (Button) itemView.findViewById( R.id.history );
            mHistory.setOnClickListener( this );
            mHistorySize = (Button) itemView.findViewById( R.id.historySize );
            mHistorySize.setOnClickListener( this );
            mMore10Bean = (Button) itemView.findViewById( R.id.more10Bean );
            mMore10Bean.setOnClickListener( this );
            mLoad1More = (Button) itemView.findViewById( R.id.load1More );
            mLoad1More.setOnClickListener( this );
            mAddIndex = (Button) itemView.findViewById( R.id.addIndex );
            mAddIndex.setOnClickListener( this );
            mSubIndex = (Button) itemView.findViewById( R.id.subIndex );
            mSubIndex.setOnClickListener( this );
            mIndexUrl = (Button) itemView.findViewById( R.id.indexUrl );
            mIndexUrl.setOnClickListener( this );
            mMemoryContains = (Button) itemView.findViewById( R.id.memoryContains );
            mMemoryContains.setOnClickListener( this );
            mMemoryLoad = (Button) itemView.findViewById( R.id.memoryLoad );
            mMemoryLoad.setOnClickListener( this );
            mFileContains = (Button) itemView.findViewById( R.id.fileContains );
            mFileContains.setOnClickListener( this );
            mFileLoad = (Button) itemView.findViewById( R.id.fileLoad );
            mFileLoad.setOnClickListener( this );
      }

      @Override
      public void onClick ( View v ) {

            switch( v.getId() ) {

                  case R.id.history:
                        loadHistory();
                        break;
                  case R.id.historySize:
                        historySize();
                        break;
                  case R.id.more10Bean:
                        loadMore10Days();
                        break;
                  case R.id.load1More:
                        load1More();
                        break;
                  case R.id.addIndex:
                        addIndex();
                        break;
                  case R.id.subIndex:
                        subIndex();
                        break;
                  case R.id.indexUrl:
                        indexUrl();
                        break;
                  case R.id.memoryContains:
                        memoryContainsOf();
                        break;
                  case R.id.memoryLoad:
                        memoryLoad();
                        break;
                  case R.id.fileContains:
                        fileContainsOf();
                        break;
                  case R.id.fileLoad:
                        fileLoad();
                        break;
                  default:
                        break;
            }
      }

      private void fileLoad ( ) {

            String dayUrl = getDayUrl();
            GankDayBean dayBean = mDayLoader.loadFromFile( dayUrl );
            if( dayBean != null ) {
                  mDayLoader.saveToMemory( dayUrl, dayBean );
                  Log.e( TAG, "fileLoad : " + dayUrl + " " + dayBean.getResults().get福利().get( 0 )
                                                                    .getUrl() );
            } else {

                  Log.e( TAG, "fileLoad : " + "without in file ;" + dayUrl );
            }
      }

      private void fileContainsOf ( ) {

            String url = getDayUrl();
            boolean b = mDayLoader.containsOfFile( url );

            Log.e( TAG, "fileContainsOf : " + url + " " + b );
      }

      private void memoryLoad ( ) {

            String dayUrl = getDayUrl();
            GankDayBean dayBean = mDayLoader.loadFromMemory( dayUrl );
            if( dayBean != null ) {

                  Log.e( TAG, "memoryLoad : " + dayUrl + " " + dayBean.getResults().get福利().get( 0 )
                                                                      .getUrl() );
            } else {

                  Log.e( TAG, "memoryLoad : without in memory ;" + dayUrl );
            }
      }

      private void memoryContainsOf ( ) {

            String dayUrl = getDayUrl();
            boolean b = mDayLoader.containsOfMemory( dayUrl );

            Log.e( TAG, "memoryContainsOf : " + dayUrl + " " + b );
      }

      private void indexUrl ( ) {

            int size = getSize();
            if( size == 0 ) {
                  Log.e( TAG, "indexUrl : without history" );
                  return;
            }

            String url = getDayUrl();
            Log.e( TAG, "indexUrl : " + url );
      }

      private String getDayUrl ( ) {

            GankHistoryBean load = mHistoryLoader.loadFromMemory( GankUrl.historyUrl() );
            String history = load.getResults().get( mDayIndex );
            return GankUrl.dayUrl( history );
      }

      private void subIndex ( ) {

            mDayIndex--;
            if( mDayIndex < 0 ) {
                  mDayIndex = 0;
            }
            Log.e( TAG, "subIndex : " + mDayIndex );
      }

      private void addIndex ( ) {

            mDayIndex++;
            int size = getSize();
            if( mDayIndex > size - 1 ) {
                  mDayIndex = 0;
            }
            Log.e( TAG, "addIndex : " + mDayIndex );
      }

      private void load1More ( ) {

            PoolExecutor.execute( new Runnable() {

                  @Override
                  public void run ( ) {

                        int size = getSize();
                        if( size > 0 ) {

                              GankHistoryBean historyBean = mHistoryLoader
                                  .loadFromMemory( GankUrl.historyUrl() );

                              List<String> histories = historyBean.getResults();
                              int index = 0;
                              while( index < histories.size() ) {

                                    String history = histories.get( index );
                                    String url = GankUrl.dayUrl( history );

                                    boolean containsOf = mDayLoader.containsOf( url );

                                    Log.e( TAG, "run : containsOf " + containsOf + " " + url );

                                    if( !containsOf ) {
                                          if( mDoing.isRunning( url ) ) {
                                                return;
                                          }
                                          GankDayBean dayBean = mDayLoader.loadFromNet( url );
                                          mDoing.remove( url );
                                          if( dayBean != null ) {
                                                mDayLoader.saveToMemory( url, dayBean );
                                                return;
                                          }
                                    }
                                    index++;
                              }

                              Log.e( TAG, "run : load 1 more finished" );
                        }
                  }
            } );
      }

      private void loadMore10Days ( ) {

            PoolExecutor.execute( new Runnable() {

                  @Override
                  public void run ( ) {

                        int size = getSize();
                        if( size > 0 ) {

                              GankHistoryBean historyBean = mHistoryLoader
                                  .loadFromMemory( GankUrl.historyUrl() );

                              List<String> histories = historyBean.getResults();
                              int index = 0;
                              int count = 0;
                              while( index < histories.size() ) {

                                    String history = histories.get( index );
                                    String url = GankUrl.dayUrl( history );

                                    boolean containsOf = mDayLoader.containsOf( url );

                                    Log.e( TAG, "run : containsOf " + containsOf + " " + url );

                                    if( !containsOf ) {
                                          if( mDoing.isRunning( url ) ) {
                                                return;
                                          }
                                          GankDayBean dayBean = mDayLoader.loadFromNet( url );
                                          mDoing.remove( url );
                                          if( dayBean != null ) {
                                                mDayLoader.saveToMemory( url, dayBean );
                                                count++;

                                                if( count >= 10 ) {
                                                      return;
                                                }
                                          }
                                    }
                                    index++;
                              }

                              Log.e( TAG, "run : load 10 more finished" );
                        }
                  }
            } );
      }

      private void historySize ( ) {

            int size = getSize();
            Log.e( TAG, "historySize : " + size );
      }

      private int getSize ( ) {

            GankHistoryBean gankHistoryBean = mHistoryLoader
                .loadFromMemory( GankUrl.historyUrl() );

            return gankHistoryBean == null ? 0 : gankHistoryBean.getResults().size();
      }

      private void loadHistory ( ) {

            PoolExecutor.execute( new Runnable() {

                  @Override
                  public void run ( ) {

                        String url = GankUrl.historyUrl();
                        if( mDoing.isRunning( url ) ) {
                              return;
                        }
                        GankHistoryBean gankHistoryBean = mHistoryLoader
                            .loadFromNet( url );
                        mDoing.remove( url );

                        mHistoryLoader
                            .saveToMemory( url, gankHistoryBean );

                        boolean error = gankHistoryBean.isError();
                        Log.e( TAG, "run : error: " + error );
                        List<String> results = gankHistoryBean.getResults();
                        Log.e(
                            TAG,
                            "run : results: " + results.size() + " " + results.get( 0 ) + " "
                                + results.get( results.size() - 1 )
                        );
                  }
            } );
      }
}
