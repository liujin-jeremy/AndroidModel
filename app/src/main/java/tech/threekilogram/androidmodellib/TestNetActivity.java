package tech.threekilogram.androidmodellib;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import java.io.File;
import tech.threekilogram.depository.net.retrofit.RetrofitStreamLoader;

/**
 * @author liujin
 */
public class TestNetActivity extends AppCompatActivity {

      private RetrofitStreamLoader<String, File>             mDownLoader;
      private RetrofitStreamLoader<String, String>           mStringLoader;
      private RetrofitStreamLoader<String, GankCategoryBean> mJsonLoader;

      @Override
      protected void onCreate ( Bundle savedInstanceState ) {

            super.onCreate( savedInstanceState );
            setContentView( R.layout.activity_test_net );
      }

      public void down ( View view ) {

      }
}
