package tech.threekilogram.androidmodellib;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.threekilogram.objectbus.bus.ObjectBus;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;
import tech.threekilogram.depository.bitmap.BitmapLoader;
import tech.threekilogram.depository.json.GsonConverter;
import tech.threekilogram.depository.json.JsonLoader;

/**
 * @author liujin
 */
public class TestJsonBitmapActivity extends AppCompatActivity {

      private static final String TAG = TestJsonBitmapActivity.class.getSimpleName();

      private RecyclerView            mRecycler;
      private TestRecyclerAdapter     mAdapter;
      private BitmapLoader            mBitmapLoader;
      private JsonLoader<ResultsBean> mJsonLoader;
      private ObjectBus               mBus;

      public static void start ( Context context ) {

            Intent starter = new Intent( context, TestJsonBitmapActivity.class );
            context.startActivity( starter );
      }

      @Override
      protected void onCreate ( Bundle savedInstanceState ) {

            ScreenSize.init( this );

            super.onCreate( savedInstanceState );
            setContentView( R.layout.activity_test_json_bitmap );
            initView();
      }

      private void initView ( ) {

            mRecycler = (RecyclerView) findViewById( R.id.recycler );
            mRecycler.setLayoutManager( new LinearLayoutManager( getApplicationContext() ) );
            mAdapter = new TestRecyclerAdapter();
            mRecycler.setAdapter( mAdapter );
      }

      private class TestRecyclerAdapter extends RecyclerView.Adapter {

            @NonNull
            @Override
            public ViewHolder onCreateViewHolder (
                @NonNull ViewGroup parent, int viewType ) {

                  return new ImageHolder(
                      getLayoutInflater().inflate(
                          R.layout.recycler_bitmap_item,
                          parent,
                          false
                      )
                  );
            }

            @Override
            public void onBindViewHolder ( @NonNull ViewHolder holder, int position ) {

            }

            @Override
            public int getItemCount ( ) {

                  return 20;
            }
      }

      private class ImageHolder extends ViewHolder {

            private String    mUrl;
            private ImageView mImageView;

            public ImageHolder ( View itemView ) {

                  super( itemView );
                  mImageView = itemView.findViewById( R.id.image );
            }

            void bind ( int position, final String url ) {

                  mUrl = url;
            }
      }

      /**
       * convert net stream to json bean list
       */
      private class TestGsonConverter extends GsonConverter<ResultsBean> {

            public TestGsonConverter ( Class<ResultsBean> valueType ) {

                  super( valueType );
            }

            @Override
            public List<ResultsBean> fromJsonArray ( InputStream inputStream ) {

                  Reader reader = new InputStreamReader( inputStream );
                  GankCategoryBean bean = sGson.fromJson( reader, GankCategoryBean.class );

                  return bean.getResults();
            }
      }
}
