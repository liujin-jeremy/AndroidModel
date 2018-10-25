package tech.threekilogram.model.converter;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import tech.threekilogram.model.util.io.Close;

/**
 * @author Liujin 2018-10-25:13:40
 */
public class InputStreamConverter implements StreamConverter<InputStream> {

      @Override
      public InputStream from ( InputStream inputStream ) {

            return inputStream;
      }

      @Override
      public void to ( OutputStream outputStream, InputStream value ) {

            byte[] temp = new byte[ 64 ];
            int len = 0;

            try {
                  while( ( len = value.read( temp ) ) != -1 ) {
                        outputStream.write( temp, 0, len );
                  }
            } catch(IOException e) {
                  e.printStackTrace();
            } finally {
                  Close.close( value );
                  Close.close( outputStream );
            }
      }
}
