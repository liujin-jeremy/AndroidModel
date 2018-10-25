package tech.threekilogram.model.converter;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.Charset;
import tech.threekilogram.model.util.io.Close;

/**
 * @author Liujin 2018-10-25:13:30
 */
public class StringConverter implements StreamConverter<String> {

      private Charset mCharset;

      public StringConverter ( ) {

            mCharset = Charset.forName( "UTF-8" );
      }

      public StringConverter ( Charset charset ) {

            mCharset = charset;
      }

      @Override
      public String from ( InputStream inputStream ) {

            StringBuilder builder = new StringBuilder();
            try {

                  InputStreamReader reader = new InputStreamReader( inputStream, mCharset );
                  char[] chars = new char[ 32 ];
                  int len = 0;
                  while( ( len = reader.read( chars, 0, chars.length ) ) != -1 ) {
                        builder.append( chars, 0, len );
                  }
                  return builder.toString();
            } catch(IOException e) {
                  e.printStackTrace();
            } finally {

                  Close.close( inputStream );
            }

            return null;
      }

      @Override
      public void to ( OutputStream outputStream, String value ) {

            try {
                  outputStream.write( value.getBytes() );
            } catch(IOException e) {
                  e.printStackTrace();
            } finally {
                  Close.close( outputStream );
            }
      }
}
