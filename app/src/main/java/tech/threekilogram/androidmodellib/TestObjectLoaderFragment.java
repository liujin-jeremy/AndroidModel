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
import tech.threekilogram.depository.json.GsonConverter;
import tech.threekilogram.depository.json.ObjectLoader;

/**
 * @author: Liujin
 * @version: V1.0
 * @date: 2018-09-03
 * @time: 17:32
 */
public class TestObjectLoaderFragment extends Fragment implements OnClickListener {

      private static final String TAG = TestObjectLoaderFragment.class.getSimpleName();

      private Button mHistory;
      private Button mDay;
      private Button mCategory;

      public static TestObjectLoaderFragment newInstance ( ) {

            TestObjectLoaderFragment fragment = new TestObjectLoaderFragment();
            return fragment;
      }

      @Nullable
      @Override
      public View onCreateView (
          @NonNull LayoutInflater inflater, @Nullable ViewGroup container,
          @Nullable Bundle savedInstanceState ) {

            return inflater.inflate( R.layout.fragment_object_loader, container, false );
      }

      @Override
      public void onViewCreated ( @NonNull View view, @Nullable Bundle savedInstanceState ) {

            super.onViewCreated( view, savedInstanceState );
            initView( view );
      }

      private void initView ( @NonNull final View itemView ) {

            mHistory = (Button) itemView.findViewById( R.id.history );
            mHistory.setOnClickListener( this );
            mDay = (Button) itemView.findViewById( R.id.day );
            mDay.setOnClickListener( this );
            mCategory = (Button) itemView.findViewById( R.id.category );
            mCategory.setOnClickListener( this );
      }

      @Override
      public void onClick ( View v ) {

            switch( v.getId() ) {
                  case R.id.history:
                        history();
                        break;
                  case R.id.day:
                        day();
                        break;
                  case R.id.category:
                        category();
                        break;
                  default:
                        break;
            }
      }

      private void category ( ) {

            PoolExecutor.execute( new Runnable() {

                  @Override
                  public void run ( ) {

                        GankCategoryBean categoryBean = ObjectLoader.load(
                            "https://gank.io/api/data/%E7%A6%8F%E5%88%A9/10/1",
                            new GsonConverter<>( GankCategoryBean.class )
                        );

                        Log.e( TAG, "run : " + categoryBean.getResults().size() + " " + categoryBean
                            .getResults().get( 0 ).getUrl() );
                  }
            } );
      }

      private void day ( ) {

            PoolExecutor.execute( new Runnable() {

                  @Override
                  public void run ( ) {

                        GankDayBean dayBean = ObjectLoader.load(
                            "https://gank.io/api/day/2015/08/07",
                            new GsonConverter<>( GankDayBean.class )
                        );

                        String url = dayBean.getResults().get福利().get( 0 ).getUrl();
                        Log.e( TAG, "run : " + url );
                  }
            } );
      }

      private void history ( ) {

            PoolExecutor.execute( new Runnable() {

                  @Override
                  public void run ( ) {

                        GankHistoryBean historyBean = ObjectLoader.load(
                            "https://gank.io/api/day/history",
                            new GsonConverter<>( GankHistoryBean.class )
                        );

                        int size = historyBean.getResults().size();
                        Log.e( TAG, "history : " + size );
                  }
            } );
      }
}
