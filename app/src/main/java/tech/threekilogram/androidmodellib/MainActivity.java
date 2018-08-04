package tech.threekilogram.androidmodellib;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import tech.threekilogram.androidmodellib.beauty.BeautyActivity;
import tech.threekilogram.androidmodellib.category.CategoryActivity;

/**
 * @author liujin
 */
public class MainActivity extends AppCompatActivity {

      @Override
      protected void onCreate (Bundle savedInstanceState) {

            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);
      }

      public void toBeauty (View view) {

            BeautyActivity.start(this);
      }

      public void toCategory (View view) {

            CategoryActivity.start(this);
      }
}
