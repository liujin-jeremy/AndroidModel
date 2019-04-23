package tech.threekilogram.androidmodellib.test;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import com.threekilogram.objectbus.Threads;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import tech.liujin.model.cache.json.JsonLoader;
import tech.threekilogram.androidmodellib.R;
import tech.threekilogram.androidmodellib.bean.GankCategoryBean;
import tech.threekilogram.androidmodellib.bean.ResultsBean;
import tech.threekilogram.androidmodellib.util.FileManager;

/**
 * @author Liujin 2018-10-26:9:19
 */
public class JsonFragment extends Fragment implements OnClickListener {

      private Button                       mNet;
      private Button                       mFile;
      private Button                       mCreateSave;
      private TextView                     mMessage;
      private JsonLoader<GankCategoryBean> mJsonLoader;

      public static JsonFragment newInstance ( ) {

            Bundle args = new Bundle();

            JsonFragment fragment = new JsonFragment();
            fragment.setArguments( args );
            return fragment;
      }

      @Nullable
      @Override
      public View onCreateView (
          @NonNull LayoutInflater inflater,
          @Nullable ViewGroup container,
          @Nullable Bundle savedInstanceState ) {

            return inflater.inflate( R.layout.fragment_json, container, false );
      }

      private void initView ( @NonNull final View itemView ) {

            mNet = itemView.findViewById( R.id.net );
            mNet.setOnClickListener( this );
            mFile = itemView.findViewById( R.id.file );
            mFile.setOnClickListener( this );
            mCreateSave = itemView.findViewById( R.id.createSave );
            mCreateSave.setOnClickListener( this );
            mMessage = itemView.findViewById( R.id.message );
      }

      @Override
      public void onViewCreated ( @NonNull View view, @Nullable Bundle savedInstanceState ) {

            super.onViewCreated( view, savedInstanceState );
            initView( view );
            mJsonLoader = new JsonLoader<>( FileManager.getDocuments(), GankCategoryBean.class );
      }

      @Override
      public void onClick ( View v ) {

            final String url = "https://gank.io/api/data/%E7%A6%8F%E5%88%A9/1/1";

            switch( v.getId() ) {
                  case R.id.net:
                        Threads.COMPUTATION.execute( ( ) -> {

                              GankCategoryBean fromNet = mJsonLoader.loadFromNet( url );
                              mMessage.post( ( ) -> {

                                    setMessage( "from net \n" + fromNet.toString() );
                              } );
                        } );
                        break;
                  case R.id.file:
                        Threads.COMPUTATION.execute( ( ) -> {

                              GankCategoryBean fromNet = mJsonLoader.loadFromFile( url );
                              mMessage.post( ( ) -> {

                                    setMessage( "from file \n" + fromNet.toString() );
                              } );
                        } );
                        break;
                  case R.id.createSave:
                        Threads.COMPUTATION.execute( ( ) -> {

                              GankCategoryBean bean = new GankCategoryBean();
                              bean.setError( true );
                              bean.setResults( new ArrayList<>() );
                              List<ResultsBean> results = bean.getResults();
                              ResultsBean resultsBean = new ResultsBean();
                              resultsBean.set_id( "-1" );
                              resultsBean.setCreatedAt( new Date().toString() );
                              resultsBean.setDesc( "create by me" );
                              resultsBean.setWho( "wuxingc" );
                              results.add( resultsBean );
                              mJsonLoader.saveToFileForce( "createUrl", bean );

                              mMessage.post( ( ) -> {

                                    setMessage( mJsonLoader.getFile( "createUrl" ).toString() );
                              } );
                        } );
                        break;
                  default:
                        break;
            }
      }

      private void setMessage ( String msg ) {

            mMessage.setText( msg );
      }
}
