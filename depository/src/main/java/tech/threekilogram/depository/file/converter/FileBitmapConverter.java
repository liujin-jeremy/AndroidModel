package tech.threekilogram.depository.file.converter;

import android.graphics.Bitmap;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import tech.threekilogram.depository.bitmap.BitmapConverter;
import tech.threekilogram.depository.file.BaseFileConverter;

/**
 * 用于{@link File}和{@link Bitmap}相互转换
 *
 * @author: Liujin
 * @version: V1.0
 * @date: 2018-08-15
 * @time: 18:22
 */
public class FileBitmapConverter extends BaseFileConverter<Bitmap> {

      private BitmapConverter mBitmapConverter;
      private File            mDir;

      public FileBitmapConverter ( BitmapConverter bitmapConverter, File dir ) {

            mBitmapConverter = bitmapConverter;
            mDir = dir;
      }

      @Override
      public String fileName ( String key ) {

            return mKeyNameConverter.encodeToName( key );
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
