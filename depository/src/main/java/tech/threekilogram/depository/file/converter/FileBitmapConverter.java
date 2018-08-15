package tech.threekilogram.depository.file.converter;

import android.graphics.Bitmap;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import tech.threekilogram.depository.bitmap.BitmapConverter;
import tech.threekilogram.depository.file.FileConverter;
import tech.threekilogram.depository.function.Md5;

/**
 * @author: Liujin
 * @version: V1.0
 * @date: 2018-08-15
 * @time: 18:22
 */
public class FileBitmapConverter implements FileConverter<Bitmap> {

      private BitmapConverter mBitmapConverter;

      public FileBitmapConverter ( BitmapConverter bitmapConverter ) {

            mBitmapConverter = bitmapConverter;
      }

      @Override
      public String fileName ( String key ) {

            return Md5.md5( key );
      }

      @Override
      public Bitmap toValue ( String key, InputStream stream ) throws Exception {

            return mBitmapConverter.read( stream );
      }

      @Override
      public void saveValue (
          String key,
          OutputStream outputStream,
          Bitmap value ) throws IOException {

            mBitmapConverter.write( outputStream, value );
      }
}
