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
import tech.threekilogram.depository.net.retrofit.converter.RetrofitJsonConverter;
import tech.threekilogram.depository.net.retrofit.converter.RetrofitStringConverter;
import tech.threekilogram.depository.net.retrofit.loader.RetrofitLoader;

/**
 * @author: Liujin
 * @version: V1.0
 * @date: 2018-08-23
 * @time: 21:58
 */
public class TestRetrofitLoaderFragment extends Fragment implements OnClickListener {

      private static final String TAG = TestRetrofitLoaderFragment.class.getSimpleName();

      public static TestRetrofitLoaderFragment newInstance ( ) {

            TestRetrofitLoaderFragment fragment = new TestRetrofitLoaderFragment();
            return fragment;
      }

      private Button                           mJsonLoad;
      private Button                           mStringLoad;
      private RetrofitLoader<String>           mStringLoader;
      private RetrofitLoader<GankCategoryBean> mJsonLoader;

      @Nullable
      @Override
      public View onCreateView (
          @NonNull LayoutInflater inflater, @Nullable ViewGroup container,
          @Nullable Bundle savedInstanceState ) {

            return inflater.inflate( R.layout.fragment_retrofit_loader, container, false );
      }

      @Override
      public void onViewCreated ( @NonNull View view, @Nullable Bundle savedInstanceState ) {

            super.onViewCreated( view, savedInstanceState );
            initView( view );
            mStringLoader = new RetrofitLoader<>( new RetrofitStringConverter() );
            mJsonLoader = new RetrofitLoader<>(
                new RetrofitJsonConverter<>( GankCategoryBean.class )
            );
      }

      private void initView ( @NonNull final View itemView ) {

            mStringLoad = (Button) itemView.findViewById( R.id.stringLoad );
            mStringLoad.setOnClickListener( this );
            mJsonLoad = (Button) itemView.findViewById( R.id.jsonLoad );
            mJsonLoad.setOnClickListener( this );
      }

      @Override
      public void onClick ( View v ) {

            switch( v.getId() ) {
                  case R.id.stringLoad:
                        loadString();
                        break;
                  case R.id.jsonLoad:
                        loadJson();
                        break;
                  default:
                        break;
            }
      }

      private void loadString ( ) {

            PoolExecutor.execute( new Runnable() {

                  @Override
                  public void run ( ) {

                        String load = mStringLoader
                            .load( "https://gank.io/api/data/%E7%A6%8F%E5%88%A9/1/1" );

                        Log.e( TAG, "run : " + load );
                  }
            } );
      }

      private void loadJson ( ) {

            PoolExecutor.execute( new Runnable() {

                  @Override
                  public void run ( ) {

                        GankCategoryBean load = mJsonLoader
                            .load( "https://gank.io/api/data/%E7%A6%8F%E5%88%A9/1/1" );

                        Log.e( TAG, "run : " + load.getResults().get( 0 ) );
                  }
            } );
      }
}
