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
                              changeFragment( TestMemoryListFragment.newInstance() );
                              break;

                        case R.id.menu01:
                              changeFragment( TestMemoryBitmapFragment.newInstance() );
                              break;

                        case R.id.menu02:
                              changeFragment( TestFileLoaderFragment.newInstance() );
                              break;

                        case R.id.menu03:
                              changeFragment( TestDiskLoaderFragment.newInstance() );
                              break;

                        case R.id.menu04:
                              changeFragment( TestDownFragment.newInstance() );
                              break;

                        case R.id.menu05:
                              changeFragment( TestRetrofitLoaderFragment.newInstance() );
                              break;

                        case R.id.menu06:
                              changeFragment( TestBitmapLoaderFragment.newInstance() );
                              break;

                        case R.id.menu07:
                              changeFragment( TestJsonLoaderFragment.newInstance() );
                              break;

                        case R.id.menu08:
                              changeFragment( TestDownLoadFragment.newInstance() );
                              break;

                        case R.id.menu09:
                              changeFragment( TestObjectLoaderFragment.newInstance() );
                              break;

                        case R.id.menu10:
                              changeFragment( TestStreamStringLoader.newInstance() );
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

