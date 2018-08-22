package tech.threekilogram.androidmodellib;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/**
 * @author liujin
 */
public class TestBitmapLoaderActivity extends AppCompatActivity {

      public static void start ( Context context ) {

            Intent starter = new Intent( context, TestBitmapLoaderActivity.class );
            context.startActivity( starter );
      }

      @Override
      protected void onCreate ( Bundle savedInstanceState ) {

            super.onCreate( savedInstanceState );
            setContentView( R.layout.activity_bitmap );
      }
}
