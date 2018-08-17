package tech.threekilogram.androidmodellib;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import java.io.IOException;
import tech.threekilogram.depository.file.BaseFileContainer;
import tech.threekilogram.depository.file.converter.FileStringConverter;
import tech.threekilogram.depository.file.loader.DiskLruContainer;
import tech.threekilogram.depository.file.loader.FileContainer;

/**
 * @author liujin
 */
public class TestFileActivity extends AppCompatActivity {

      private final String key   = "HelloFile";
      private final String value = "Hello Android Model";

      private FileContainer<String>     mFileContainerLoader;
      private DiskLruContainer<String>  mDiskLruContainerLoader;
      private BaseFileContainer<String> mLoader;

      private TextView mTextView;

      public static void start ( Context context ) {

            Intent starter = new Intent( context, TestFileActivity.class );
            context.startActivity( starter );
      }

      @Override
      protected void onCreate ( Bundle savedInstanceState ) {

            super.onCreate( savedInstanceState );
            setContentView( R.layout.activity_test_file );
            initView();

            mFileContainerLoader = new FileContainer<>(
                getExternalCacheDir(), new FileStringConverter() );
            try {
                  mDiskLruContainerLoader = new DiskLruContainer<>(
                      getCacheDir(),
                      1024 * 1024 * 5,
                      new FileStringConverter()
                  );
            } catch(IOException e) {
                  e.printStackTrace();
            }

            mLoader = mFileContainerLoader;
      }

      private void initView ( ) {

            mTextView = (TextView) findViewById( R.id.textView );
      }

      public void save ( View view ) {

            mLoader.save( key, value );
            show( "save to : " + mLoader.getFile( key ) );
      }

      private void show ( String text ) {

            mTextView.setText( text );
      }

      public void load ( View view ) {

            String load = mLoader.load( key );
            show( "load : " + load );
      }

      public void delete ( View view ) {

            mLoader.remove( key );
            java.io.File file = mLoader.getFile( key );
            show( "remove : " + file + " exist : " + file.exists() );
      }

      public void change ( View view ) {

            if( mLoader == mFileContainerLoader ) {

                  mLoader = mDiskLruContainerLoader;
            } else {

                  mLoader = mFileContainerLoader;
            }
      }
}
