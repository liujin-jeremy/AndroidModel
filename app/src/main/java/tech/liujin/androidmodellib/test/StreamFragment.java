package tech.liujin.androidmodellib.test;

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
import android.widget.TextView;
import com.threekilogram.objectbus.Threads;
import tech.liujin.cache.stream.StreamLoader;
import tech.liujin.androidmodellib.R;
import tech.liujin.androidmodellib.bean.GankCategoryBean;

/**
 * @author Liujin 2018-10-26:9:38
 */
public class StreamFragment extends Fragment implements OnClickListener {

      private static final String TAG = StreamFragment.class.getSimpleName();

      private Button    mString;
      private Button    mJson;
      private Button    mBitmap;
      private TextView  mTextView3;
      private ImageView mImageView5;

      public static StreamFragment newInstance ( ) {

            Bundle args = new Bundle();

            StreamFragment fragment = new StreamFragment();
            fragment.setArguments( args );
            return fragment;
      }

      @Nullable
      @Override
      public View onCreateView (
          @NonNull LayoutInflater inflater, @Nullable ViewGroup container,
          @Nullable Bundle savedInstanceState ) {

            return inflater.inflate( R.layout.fragment_stream, container, false );
      }

      private void initView ( @NonNull final View itemView ) {

            mString = itemView.findViewById( R.id.string );
            mString.setOnClickListener( this );
            mJson = itemView.findViewById( R.id.json );
            mJson.setOnClickListener( this );
            mBitmap = itemView.findViewById( R.id.bitmap );
            mBitmap.setOnClickListener( this );
            mTextView3 = itemView.findViewById( R.id.textView3 );
            mImageView5 = itemView.findViewById( R.id.imageView5 );
      }

      @Override
      public void onViewCreated ( @NonNull View view, @Nullable Bundle savedInstanceState ) {

            super.onViewCreated( view, savedInstanceState );
            initView( view );
      }

      @Override
      public void onClick ( View v ) {

            final String url = "https://gank.io/api/data/%E7%A6%8F%E5%88%A9/1/10";

            switch( v.getId() ) {
                  case R.id.string:
                        Threads.COMPUTATION.execute( ( ) -> {
                              String string = StreamLoader.loadStringFromNet( url );
                              mTextView3.post( ( ) -> mTextView3.setText( string ) );
                        } );
                        break;
                  case R.id.json:
                        Threads.COMPUTATION.execute( ( ) -> {
                              GankCategoryBean bean = StreamLoader
                                  .loadJsonFromNet( url, GankCategoryBean.class );
                              mTextView3.post( ( ) -> mTextView3.setText( bean.toString() ) );
                        } );
                        break;
                  case R.id.bitmap:
                        Threads.COMPUTATION.execute( ( ) -> {
                              Bitmap bean = StreamLoader
                                  .loadBitmapFromNet(
                                      "https://ww1.sinaimg.cn/large/0065oQSqgy1fu39hosiwoj30j60qyq96.jpg",
                                      500, 500
                                  );
                              mImageView5.post( ( ) -> {
                                    mImageView5.setImageBitmap( bean );
                                    Log.e(
                                        TAG,
                                        "onClick : " + bean.getWidth() + " " + bean.getHeight()
                                    );
                              } );
                        } );
                        break;
                  default:
                        break;
            }
      }
}
