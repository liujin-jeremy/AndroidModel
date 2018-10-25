package tech.threekilogram.model.converter;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author Liujin 2018-10-25:13:40
 */
public class SimpleConverter implements StreamConverter<InputStream> {

      @Override
      public InputStream from ( InputStream inputStream ) {

            return inputStream;
      }

      @Override
      public void to ( OutputStream outputStream, InputStream value ) {

      }
}
