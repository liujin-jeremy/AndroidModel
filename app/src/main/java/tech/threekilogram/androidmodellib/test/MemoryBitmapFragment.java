package tech.threekilogram.androidmodellib.test;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.threekilogram.objectbus.Threads;
import tech.liujin.model.memory.lru.MemoryBitmap;
import tech.threekilogram.androidmodellib.R;

/**
 * @author Liujin 2018-10-25:15:01
 */
public class MemoryBitmapFragment extends Fragment {

      private TextView             mTextMemorySize;
      private ImageView            mImageView;
      private MemoryBitmap<String> mMemoryBitmap;

      private int[] mRes = {
          R.drawable.a117,
          R.drawable.a120,
          R.drawable.a131,
          R.drawable.a152,
          R.drawable.a209,
          R.drawable.a224,
          R.drawable.a264,
          R.drawable.a281,
          R.drawable.a282,
          R.drawable.a283,
          R.drawable.a285,
          R.drawable.a286,
          R.drawable.be10
      };

      private int mIndex;

      public static MemoryBitmapFragment newInstance ( ) {

            Bundle args = new Bundle();

            MemoryBitmapFragment fragment = new MemoryBitmapFragment();
            fragment.setArguments( args );
            return fragment;
      }

      @Nullable
      @Override
      public View onCreateView (
          @NonNull LayoutInflater inflater, @Nullable ViewGroup container,
          @Nullable Bundle savedInstanceState ) {

            return inflater.inflate( R.layout.fragment_memory_bitmap, container, false );
      }

      @Override
      public void onViewCreated ( @NonNull View view, @Nullable Bundle savedInstanceState ) {

            super.onViewCreated( view, savedInstanceState );
            initView( view );
            mMemoryBitmap = new MemoryBitmap<>(
                (int) ( Runtime.getRuntime().maxMemory() >> 4 ) );
      }

      private void initView ( @NonNull final View itemView ) {

            mTextMemorySize = itemView.findViewById( R.id.textMemorySize );
            mImageView = itemView.findViewById( R.id.imageView );
            mImageView.setOnClickListener( new ImageClickListener() );
      }

      private class ImageClickListener implements OnClickListener {

            @Override
            public void onClick ( View v ) {

                  Threads.COMPUTATION.execute( ( ) -> {

                        int i = mRes[ mIndex % mRes.length ];
                        Bitmap bitmap = BitmapFactory.decodeResource( getResources(), i );
                        mMemoryBitmap.save( String.valueOf( mIndex ), bitmap );
                        mIndex++;

                        v.post( ( ) -> {

                              ( (ImageView) v ).setImageBitmap( bitmap );
                              mTextMemorySize.setText(
                                  "当前内存 : " + mMemoryBitmap.size() * 1f / 1024 / 1024 + " mb" );
                        } );
                  } );
            }
      }
}
