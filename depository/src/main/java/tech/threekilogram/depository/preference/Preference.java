package tech.threekilogram.depository.preference;

import android.content.Context;
import android.content.SharedPreferences;
import java.util.Set;
import tech.threekilogram.depository.Container;

/**
 * 使用{@link SharedPreferences}保存数据
 *
 * @author: Liujin
 * @version: V1.0
 * @date: 2018-08-13
 * @time: 13:35
 */
public class Preference implements Container<String, String> {

      private SharedPreferences mPreferences;

      public Preference ( Context context, String name ) {

            mPreferences = context.getSharedPreferences( name, Context.MODE_PRIVATE );
      }

      @Override
      public String save ( String key, String value ) {

            mPreferences.edit().putString( key, value ).apply();
            return null;
      }

      @Override
      public String remove ( String key ) {

            mPreferences.edit().remove( key ).apply();
            return null;
      }

      @Override
      public boolean containsOf ( String key ) {

            return mPreferences.contains( key );
      }

      public void save ( String key, boolean value ) {

            mPreferences.edit().putBoolean( key, value ).apply();
      }

      public void save ( String key, int value ) {

            mPreferences.edit().putInt( key, value ).apply();
      }

      public void save ( String key, float value ) {

            mPreferences.edit().putFloat( key, value ).apply();
      }

      public void save ( String key, long value ) {

            mPreferences.edit().putLong( key, value ).apply();
      }

      public void save ( String key, Set<String> value ) {

            mPreferences.edit().putStringSet( key, value ).apply();
      }

      @Override
      public String load ( String key ) {

            return getString( key );
      }

      public String getString ( String key ) {

            return mPreferences.getString( key, null );
      }

      public boolean getBoolean ( String key ) {

            return mPreferences.getBoolean( key, false );
      }

      public int getInt ( String key ) {

            return mPreferences.getInt( key, 0 );
      }

      public float getFloat ( String key ) {

            return mPreferences.getFloat( key, 0f );
      }

      public long getLong ( String key ) {

            return mPreferences.getLong( key, 0 );
      }

      public Set<String> getStringSet ( String key ) {

            return mPreferences.getStringSet( key, null );
      }
}
