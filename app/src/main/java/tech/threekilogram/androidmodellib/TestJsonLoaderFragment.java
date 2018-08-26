package tech.threekilogram.androidmodellib;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import tech.threekilogram.depository.json.GsonConverter;
import tech.threekilogram.depository.json.JsonLoader;

/**
 * @author: Liujin
 * @version: V1.0
 * @date: 2018-08-25
 * @time: 11:12
 */
public class TestJsonLoaderFragment extends Fragment implements OnClickListener, OnClickListener {

      private Button                  mLoadMore;
      private RecyclerView            mRecycler;
      private JsonLoader<ResultsBean> mJsonLoader;
      private Button                  mSaveMore;

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
      }

      private void initView ( @NonNull final View itemView ) {

            mLoadMore = (Button) itemView.findViewById( R.id.loadMore );
            mLoadMore.setOnClickListener( this );
            mRecycler = (RecyclerView) itemView.findViewById( R.id.recycler );
            mRecycler.setLayoutManager( new LinearLayoutManager( getContext() ) );
            mSaveMore = (Button) itemView.findViewById( R.id.saveMore );
            mSaveMore.setOnClickListener( this );
      }

      @Override
      public void onClick ( View v ) {

            switch( v.getId() ) {
                  case R.id.loadMore:
                        break;
                  case R.id.saveMore:
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

      private class Adapter extends RecyclerView.Adapter {

            @NonNull
            @Override
            public ViewHolder onCreateViewHolder ( @NonNull ViewGroup parent, int viewType ) {

                  return null;
            }

            @Override
            public void onBindViewHolder ( @NonNull ViewHolder holder, int position ) {

            }

            @Override
            public int getItemCount ( ) {

                  return 0;
            }
      }

      private class Holder extends ViewHolder {

            public Holder ( View itemView ) {

                  super( itemView );
            }
      }
}
