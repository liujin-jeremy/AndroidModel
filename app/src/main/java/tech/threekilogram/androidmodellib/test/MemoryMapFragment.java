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
import android.widget.EditText;
import android.widget.TextView;
import tech.threekilogram.androidmodellib.R;
import tech.threekilogram.model.memory.map.MemoryMap;

/**
 * @author Liujin 2018-10-25:14:31
 */
public class MemoryMapFragment extends Fragment implements OnClickListener {

      private MemoryMap<String, String> mMemoryMap;
      private EditText                  mEditKey;
      private EditText                  mEditValue;
      private Button                    mAdd;
      private Button                    mGet;
      private Button                    mDelete;
      private Button                    mAll;
      private TextView                  mTextView;

      public static MemoryMapFragment newInstance ( ) {

            Bundle args = new Bundle();

            MemoryMapFragment fragment = new MemoryMapFragment();
            fragment.setArguments( args );
            return fragment;
      }

      @Nullable
      @Override
      public View onCreateView (
          @NonNull LayoutInflater inflater, @Nullable ViewGroup container,
          @Nullable Bundle savedInstanceState ) {

            return inflater.inflate( R.layout.fragment_memory_map, container, false );
      }

      @Override
      public void onViewCreated ( @NonNull View view, @Nullable Bundle savedInstanceState ) {

            super.onViewCreated( view, savedInstanceState );
            initView( view );
            mMemoryMap = new MemoryMap<>();
      }

      private void initView ( @NonNull final View itemView ) {

            mEditKey = itemView.findViewById( R.id.editKey );
            mEditValue = itemView.findViewById( R.id.editValue );
            mAdd = itemView.findViewById( R.id.add );
            mAdd.setOnClickListener( this );
            mGet = itemView.findViewById( R.id.get );
            mGet.setOnClickListener( this );
            mDelete = itemView.findViewById( R.id.delete );
            mDelete.setOnClickListener( this );
            mAll = itemView.findViewById( R.id.all );
            mAll.setOnClickListener( this );
            mTextView = itemView.findViewById( R.id.textView );
      }

      private String getKey ( ) {

            String s = mEditKey.getText().toString();
            return s == null ? "null" : s;
      }

      private String getValue ( ) {

            String s = mEditValue.getText().toString();
            return s == null ? "null" : s;
      }

      @Override
      public void onClick ( View v ) {

            String key;
            String value;

            key = getKey();
            value = getValue();

            switch( v.getId() ) {
                  case R.id.add:

                        mMemoryMap.save( key, value );
                        setText( "add : " + key + " : " + value );
                        break;
                  case R.id.get:
                        String load = mMemoryMap.get( key );
                        setText( "get " + key + " : " + load );
                        break;
                  case R.id.delete:
                        String remove = mMemoryMap.remove( key );
                        setText( "remove " + key + " : " + remove );
                        break;
                  case R.id.all:
                        int size = mMemoryMap.size();
                        setText( "all size " + size );
                        break;
                  default:
                        break;
            }
      }

      private void setText ( String msg ) {

            mTextView.setText( msg );
      }
}
