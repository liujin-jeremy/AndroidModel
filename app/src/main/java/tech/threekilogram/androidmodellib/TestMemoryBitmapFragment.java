package tech.threekilogram.androidmodellib;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.bitmapreader.BitmapReader;
import tech.threekilogram.depository.memory.lru.MemoryBitmap;

/**
 * @author: Liujin
 * @version: V1.0
 * @date: 2018-08-22
 * @time: 20:49
 */
public class TestMemoryBitmapFragment extends Fragment implements OnClickListener {

      public static TestMemoryBitmapFragment newInstance ( ) {

            return new TestMemoryBitmapFragment();
      }

      private Button               mSave;
      private ImageView            mImageView;
      private MemoryBitmap<String> mMemoryBitmap;

      private int[] resources = {
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
          R.drawable.a286
      };

      private int      index;
      private TextView mMemorySize;

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

            mMemoryBitmap = new MemoryBitmap<>( 10 * 1024 * 1024 );
      }

      private void initView ( @NonNull final View itemView ) {

            mSave = itemView.findViewById( R.id.save );
            mImageView = itemView.findViewById( R.id.imageView );
            mSave.setOnClickListener( this );
            mMemorySize = itemView.findViewById( R.id.cacheCount );
            mMemorySize.setText(
                "memory size: " +
                    ( Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory() )
            );
      }

      @Override
      public void onClick ( View v ) {

            switch( v.getId() ) {
                  case R.id.save:
                        save();
                        break;
                  default:
                        break;
            }
      }

      private void save ( ) {

            if( !mMemoryBitmap.containsOf( String.valueOf( index ) ) ) {

                  int bitmapIndex = index % resources.length;

                  Bitmap bitmap = BitmapReader
                      .matchWidth(
                          mImageView.getContext(),
                          resources[ bitmapIndex ],
                          mImageView.getWidth()
                      );

                  mMemoryBitmap.save( String.valueOf( bitmapIndex ), bitmap );
                  mImageView.setImageBitmap( bitmap );
                  index++;
            } else {

                  Bitmap load = mMemoryBitmap.load( String.valueOf( index % resources.length ) );
                  mImageView.setImageBitmap( load );
            }

            mMemorySize.setText( "memory size: " + mMemoryBitmap.size() );
      }
}
