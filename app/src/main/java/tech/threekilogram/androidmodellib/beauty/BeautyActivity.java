package tech.threekilogram.androidmodellib.beauty;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import tech.threekilogram.androidmodellib.R;

/**
 * @author liujin
 */
public class BeautyActivity extends AppCompatActivity {

      private static final String TAG = BeautyActivity.class.getSimpleName();
      private RecyclerView          mRecycler;
      private BeautyRecyclerAdapter mAdapter;

      public static void start (Context context) {

            Intent starter = new Intent(context, BeautyActivity.class);
            context.startActivity(starter);
      }

      @Override
      protected void onCreate (Bundle savedInstanceState) {

            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_beauty);
            initView();

            BeautyManager.getInstance().bind(this);
      }

      private void initView () {

            mRecycler = findViewById(R.id.recycler);
            mRecycler.setLayoutManager(new LinearLayoutManager(this));
            mAdapter = new BeautyRecyclerAdapter();
            mRecycler.setAdapter(mAdapter);

            mRecycler.post(new Runnable() {

                  @Override
                  public void run () {

                        int width = mRecycler.getWidth();
                        BeautyManager.getInstance().setBitmapWidth(width);
                        BeautyManager.getInstance().loadBitmap(1);
                  }
            });
      }
}
