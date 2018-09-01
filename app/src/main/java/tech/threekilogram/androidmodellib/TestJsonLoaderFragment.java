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

      private Button mHistory;
      private Button mHistorySize;
      private Button mMore10Bean;

      private int    mDayIndex;
      private Button mLoad1More;

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
                  default:
                        break;
            }
      }

      private void load1More ( ) {

            PoolExecutor.execute( new Runnable() {

                  @Override
                  public void run ( ) {

                        int size = getSize();
                        if( size > 0 ) {

                              GankHistoryBean historyBean = mHistoryLoader
                                  .loadMemory( GankUrl.historyUrl() );

                              List<String> histories = historyBean.getResults();
                              int index = 0;
                              while( index < histories.size() ) {

                                    String history = histories.get( index );
                                    String url = GankUrl.dayUrl( history );

                                    boolean containsOf = mDayLoader.containsOf( url );

                                    Log.e( TAG, "run : containsOf " + containsOf + " " + url );

                                    if( !containsOf ) {
                                          GankDayBean dayBean = mDayLoader.loadFromNet( url );
                                          if( dayBean != null ) {
                                                mDayLoader.save( url, dayBean );
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
                                  .loadMemory( GankUrl.historyUrl() );

                              List<String> histories = historyBean.getResults();
                              int index = 0;
                              int count = 0;
                              while( index < histories.size() ) {

                                    String history = histories.get( index );
                                    String url = GankUrl.dayUrl( history );

                                    boolean containsOf = mDayLoader.containsOf( url );

                                    Log.e( TAG, "run : containsOf " + containsOf + " " + url );

                                    if( !containsOf ) {
                                          GankDayBean dayBean = mDayLoader.loadFromNet( url );
                                          if( dayBean != null ) {
                                                mDayLoader.save( url, dayBean );
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
                .loadMemory( GankUrl.historyUrl() );

            return gankHistoryBean == null ? 0 : gankHistoryBean.getResults().size();
      }

      private void loadHistory ( ) {

            PoolExecutor.execute( new Runnable() {

                  @Override
                  public void run ( ) {

                        GankHistoryBean gankHistoryBean = mHistoryLoader
                            .loadFromNet( GankUrl.historyUrl() );

                        mHistoryLoader
                            .saveToMemory( GankUrl.historyUrl(), gankHistoryBean );

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
