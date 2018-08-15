package tech.threekilogram.depository.file.converter;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.support.annotation.IntDef;
import com.example.bitmapreader.BitmapReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import tech.threekilogram.depository.file.FileConverter;
import tech.threekilogram.depository.function.Md5;

/**
 * @author: Liujin
 * @version: V1.0
 * @date: 2018-08-15
 * @time: 18:22
 */
public class FileBitmapConverter implements FileConverter<Bitmap> {

      /**
       * 缩放模式:等比例缩放至最接近{@link #mWidth}和{@link #mHeight},不会小于设置的值
       */
      public static final int SAMPLE       = 1;
      /**
       * 缩放模式:等比例缩放至最接近{@link #mWidth}和{@link #mHeight},均会小于缩放的值
       */
      public static final int SAMPLE_MAX   = 2;
      /**
       * 缩放模式:等比例缩放至匹配{@link #mWidth}
       */
      public static final int MATCH_WIDTH  = 3;
      /**
       * 缩放模式:等比例缩放至匹配{@link #mWidth}
       */
      public static final int MATCH_HEIGHT = 4;
      private int mWidth;
      private int mHeight;
      private int mMode;
      /**
       * 保存格式
       */
      private Bitmap.CompressFormat mCompressFormat  = CompressFormat.PNG;
      /**
       * 保存质量
       */
      private int                   mCompressQuality = 100;

      public void setCompressFormat ( CompressFormat compressFormat ) {

            mCompressFormat = compressFormat;
      }

      public void setCompressQuality ( int compressQuality ) {

            mCompressQuality = compressQuality;
      }

      /**
       * 设置缩放宽度
       *
       * @param width 宽度
       */
      public void setWidth ( int width ) {

            mWidth = width;
      }

      /**
       * 设置缩放高度
       *
       * @param height 高度
       */
      public void setHeight ( int height ) {

            mHeight = height;
      }

      /**
       * 设置缩放模式
       *
       * @param mode 模式
       */
      public void setMode ( @ScaleMode int mode ) {

            mMode = mode;
      }

      @Override
      public String fileName ( String key ) {

            return Md5.md5( key );
      }

      @Override
      public Bitmap toValue ( String key, InputStream stream ) throws Exception {

            /* size is 0 */

            if( mWidth == 0 || mHeight == 0 || mMode == 0 ) {

                  return BitmapFactory.decodeStream( stream );
            }

            if( mMode == SAMPLE ) {
                  return BitmapReader.decodeSampledBitmap( stream, mWidth, mHeight );
            }

            if( mMode == SAMPLE_MAX ) {
                  return BitmapReader.decodeMaxSampledBitmap( stream, mWidth, mHeight );
            }

            if( mMode == MATCH_WIDTH ) {
                  return BitmapReader.decodeBitmapToMatchWidth( stream, mWidth );
            }

            if( mMode == MATCH_HEIGHT ) {
                  return BitmapReader.decodeBitmapToMatchHeight( stream, mHeight );
            }

            return BitmapReader.decodeBitmapToMatchSize( stream, mWidth, mHeight );
      }

      @Override
      public void saveValue (
          String key,
          OutputStream outputStream,
          Bitmap value ) throws IOException {

            value.compress( CompressFormat.PNG, 100, outputStream );
      }

      @IntDef({ SAMPLE, SAMPLE_MAX, MATCH_WIDTH, MATCH_HEIGHT })
      @Retention(RetentionPolicy.SOURCE)
      public @interface ScaleMode { }
}
