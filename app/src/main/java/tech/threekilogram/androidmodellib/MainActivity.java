package tech.threekilogram.androidmodellib;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.NavigationView.OnNavigationItemSelectedListener;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.MenuItem;
import android.widget.FrameLayout;
import tech.threekilogram.androidmodellib.test.MemoryMapFragment;

/**
 * @author liujin
 */
public class MainActivity extends AppCompatActivity {

      private FrameLayout    mContainer;
      private NavigationView mNavigationView;
      private DrawerLayout   mDrawer;

      @Override
      protected void onCreate ( Bundle savedInstanceState ) {

            super.onCreate( savedInstanceState );
            setContentView( R.layout.activity_main );
            initView();
      }

      private void initView ( ) {

            mDrawer = findViewById( R.id.drawer );
            mNavigationView = findViewById( R.id.navigationView );
            mContainer = findViewById( R.id.container );

            mNavigationView.setNavigationItemSelectedListener( new MainMenuClickListener() );
      }

      private class MainMenuClickListener implements OnNavigationItemSelectedListener {

            @Override
            public boolean onNavigationItemSelected ( @NonNull MenuItem item ) {

                  switch( item.getItemId() ) {
                        case R.id.menu00:
                              changeFragment( MemoryMapFragment.newInstance() );
                              break;
                        default:
                              break;
                  }

                  mDrawer.closeDrawer( Gravity.START );
                  return true;
            }
      }

      private void changeFragment ( Fragment fragment ) {

            getSupportFragmentManager().beginTransaction()
                                       .replace( R.id.container, fragment )
                                       .commit();
      }
}

