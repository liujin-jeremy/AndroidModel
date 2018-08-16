package tech.threekilogram.depository.file.converter;

import android.graphics.Bitmap;
import java.io.File;
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
      private File            mDir;

      public FileBitmapConverter ( BitmapConverter bitmapConverter, File dir ) {

            mBitmapConverter = bitmapConverter;
            mDir = dir;
      }

      @Override
      public String fileName ( String key ) {

            return Md5.md5( key );
      }

      @Override
      public Bitmap toValue ( String key, InputStream stream ) throws Exception {

            return mBitmapConverter.read( getFile( key ) );
      }

      public File getFile ( String key ) {

            String fileName = fileName( key );
            return new File( mDir, fileName );
      }

      @Override
      public void saveValue (
          String key,
          OutputStream outputStream,
          Bitmap value ) throws IOException {

            mBitmapConverter.write( outputStream, value );
      }
}
