package tech.threekilogram.androidmodellib;

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
import tech.threekilogram.depository.memory.map.MemoryList;

/**
 * @author: Liujin
 * @version: V1.0
 * @date: 2018-08-22
 * @time: 20:07
 */
public class TestMemoryListFragment extends Fragment implements OnClickListener {

      private Button             mSave;
      private MemoryList<String> mMemoryList;
      private int                mIndex;
      private int                mDeleteIndex;
      private TextView           mMsg;
      private Button             mDelete;
      private Button             mContainsOf;
      private Button             mLoad;
      private Button             mSize;
      private Button             mClear;

      public static TestMemoryListFragment newInstance ( ) {

            return new TestMemoryListFragment();
      }

      @Nullable
      @Override
      public View onCreateView (
          @NonNull LayoutInflater inflater,
          @Nullable ViewGroup container,
          @Nullable Bundle savedInstanceState ) {

            return inflater.inflate( R.layout.fragment_memory_list, container, false );
      }

      @Override
      public void onViewCreated ( @NonNull View view, @Nullable Bundle savedInstanceState ) {

            super.onViewCreated( view, savedInstanceState );
            initView( view );

            mMemoryList = new MemoryList<>();
      }

      private void initView ( @NonNull final View itemView ) {

            mSave = itemView.findViewById( R.id.save );
            mSave.setOnClickListener( this );
            mMsg = itemView.findViewById( R.id.msg );
            mDelete = itemView.findViewById( R.id.delete );
            mDelete.setOnClickListener( this );
            mContainsOf = itemView.findViewById( R.id.containsOf );
            mContainsOf.setOnClickListener( this );
            mLoad = itemView.findViewById( R.id.load );
            mLoad.setOnClickListener( this );
            mSize = itemView.findViewById( R.id.size );
            mSize.setOnClickListener( this );
            mClear = itemView.findViewById( R.id.clear );
            mClear.setOnClickListener( this );
      }

      @Override
      public void onClick ( View v ) {

            switch( v.getId() ) {
                  case R.id.save:
                        save();
                        break;
                  case R.id.delete:
                        delete();
                        break;
                  case R.id.containsOf:
                        containsOf();
                        break;
                  case R.id.load:
                        load();
                        break;
                  case R.id.size:
                        size();
                        break;
                  case R.id.clear:
                        clear();
                        break;
                  default:
                        break;
            }
      }

      private void clear ( ) {

            mMemoryList.clear();
            setMsgText( "clear" );
      }

      private void save ( ) {

            String value = "value " + mIndex;
            mMemoryList.save( mIndex, value );

            setMsgText( "save new data --> " + mIndex + " : " + value );
            mIndex++;
      }

      private void delete ( ) {

            String remove = mMemoryList.remove( mDeleteIndex );

            if( remove == null ) {

                  setMsgText( "without value --> " + mDeleteIndex );
            } else {

                  setMsgText( "delete value --> " + mDeleteIndex + " " + remove );
                  mDeleteIndex++;
            }
      }

      private void containsOf ( ) {

            boolean contains = mMemoryList.containsOf( mIndex );
            boolean containsOf = mMemoryList.containsOf( mDeleteIndex );

            String msg = "contains of --> " + mIndex + " : " + contains +
                "\n" +
                "contains of --> " + mDeleteIndex + " : " + containsOf;

            setMsgText( msg );
      }

      private void load ( ) {

            String load = mMemoryList.load( mIndex );
            String load1 = mMemoryList.load( mDeleteIndex );

            String msg = "load --> " + mIndex + " : " + load +
                "\n" +
                "load --> " + mDeleteIndex + " : " + load1;

            setMsgText( msg );
      }

      private void size ( ) {

      }

      private void setMsgText ( String msgText ) {

            mMsg.setText( msgText );
      }
}
