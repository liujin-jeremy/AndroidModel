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

      /**
       * 转换stream为指定格式的bitmap
       */
      private BitmapConverter mBitmapConverter;
      /**
       * 缓存文件夹
       */
      private File            mDir;

      public FileBitmapConverter ( File dir, BitmapConverter bitmapConverter ) {

            mBitmapConverter = bitmapConverter;
            mDir = dir;
      }

      @Override
      public Bitmap toValue ( String key, InputStream stream ) throws Exception {

            return mBitmapConverter.read( getFile( key ) );
      }

      private File getFile ( String key ) {

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
