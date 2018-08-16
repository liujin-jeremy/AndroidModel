package tech.threekilogram.androidmodellib;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import tech.threekilogram.depository.memory.Memory;
import tech.threekilogram.depository.memory.lru.MemoryLruCache;
import tech.threekilogram.depository.memory.map.MemoryMap;

/**
 * @author liujin
 */
public class TestMemoryActivity extends AppCompatActivity implements OnClickListener {

      private EditText mEditKey;
      private EditText mEditValue;
      private Button   mSave;
      private Button   mDelete;
      private Button   mContainsOf;
      private Button   mLoad;
      private TextView mMessageText;
      private Button   mButton;

      private Memory<String, String>         mMemoryLoader;
      private MemoryMap<String, String>      mMapLoader;
      private MemoryLruCache<String, String> mLruLoader;
      private Button                         mSize;

      public static void start ( Context context ) {

            Intent starter = new Intent( context, TestMemoryActivity.class );
            context.startActivity( starter );
      }

      @Override
      protected void onCreate ( Bundle savedInstanceState ) {

            super.onCreate( savedInstanceState );
            setContentView( R.layout.activity_test_memory );
            initView();

            mMapLoader = new MemoryMap<>();
            mLruLoader = new MemoryLruCache<>( 3 );
            mMemoryLoader = mMapLoader;
      }

      private void initView ( ) {

            mEditKey = (EditText) findViewById( R.id.editKey );
            mEditValue = (EditText) findViewById( R.id.editValue );
            mSave = (Button) findViewById( R.id.save );
            mSave.setOnClickListener( this );
            mDelete = (Button) findViewById( R.id.delete );
            mDelete.setOnClickListener( this );
            mContainsOf = (Button) findViewById( R.id.containsOf );
            mContainsOf.setOnClickListener( this );
            mMessageText = (TextView) findViewById( R.id.messageText );
            mLoad = (Button) findViewById( R.id.load );
            mLoad.setOnClickListener( this );
            mButton = (Button) findViewById( R.id.change );
            mButton.setOnClickListener( this );
            mSize = (Button) findViewById( R.id.size );
            mSize.setOnClickListener( this );
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
                        contains();
                        break;
                  case R.id.load:
                        load();
                        break;
                  case R.id.change:
                        change();
                        break;
                  case R.id.size:
                        size();
                        break;
                  default:
                        break;
            }
      }

      private void change ( ) {

            if( mMemoryLoader instanceof MemoryMap ) {

                  mMemoryLoader = mLruLoader;
            } else {

                  mMemoryLoader = mMapLoader;
            }
      }

      private void size ( ) {

            int size = mMemoryLoader.size();
            showMessage( String.valueOf( size ) );
      }

      private void save ( ) {

            String key = mEditKey.getText().toString();
            String value = mEditValue.getText().toString();

            if( notNullString( key ) && notNullString( value ) ) {

                  String save = mMemoryLoader.save( key, value );
                  if( save == null ) {

                        showMessage( "save success !!!" );
                  } else {

                        showMessage( "update " + key + " oldValue : " + save );
                  }
            }
      }

      private void delete ( ) {

            String key = mEditKey.getText().toString();

            if( notNullString( key ) ) {

                  String remove = mMemoryLoader.remove( key );
                  if( remove != null ) {

                        showMessage( "remove " + key + " : " + remove );
                  } else {

                        showMessage( "without this key" );
                  }
            }
      }

      private void contains ( ) {

            String key = mEditKey.getText().toString();

            if( notNullString( key ) ) {

                  boolean containsOf = mMemoryLoader.containsOf( key );
                  showMessage( "contains of " + key + " : " + containsOf );
            }
      }

      private void load ( ) {

            String key = mEditKey.getText().toString();

            if( notNullString( key ) ) {

                  String value = mMemoryLoader.load( key );
                  showMessage( "value of " + key + " : " + value );
            }
      }

      private boolean notNullString ( String text ) {

            return !TextUtils.isEmpty( text );
      }

      private void showMessage ( String msg ) {

            mMessageText.setText( msg );
      }
}
