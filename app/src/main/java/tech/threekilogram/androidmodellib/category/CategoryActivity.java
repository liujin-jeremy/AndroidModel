package tech.threekilogram.androidmodellib.category;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import tech.threekilogram.androidmodellib.R;

/**
 * @author liujin
 */
public class CategoryActivity extends AppCompatActivity {


      public static void start (Context context) {

            Intent starter = new Intent(context, CategoryActivity.class);
            context.startActivity(starter);
      }

      @Override
      protected void onCreate (Bundle savedInstanceState) {

            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_category);
            initView();
      }

      private void initView () {

      }

}
