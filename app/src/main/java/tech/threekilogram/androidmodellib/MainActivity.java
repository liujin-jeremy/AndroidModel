package tech.threekilogram.androidmodellib;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

/**
 * @author liujin
 */
public class MainActivity extends AppCompatActivity {

      @Override
      protected void onCreate ( Bundle savedInstanceState ) {

            super.onCreate( savedInstanceState );
            setContentView( R.layout.activity_main );
      }

      public void toTestMemory ( View view ) {

            TestMemoryActivity.start( this );
      }

      public void toTestFile ( View view ) {

            TestFileActivity.start( this );
      }
}
