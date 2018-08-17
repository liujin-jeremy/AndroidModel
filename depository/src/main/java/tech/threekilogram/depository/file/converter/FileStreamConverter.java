package tech.threekilogram.depository.file.converter;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import tech.threekilogram.depository.file.BaseFileConverter;
import tech.threekilogram.depository.file.FileConverter;
import tech.threekilogram.depository.file.loader.FileContainer;
import tech.threekilogram.depository.function.Close;

/**
 * {@link FileConverter} 的一种实现,需要和{@link FileContainer}配合使用;
 *
 * @author: Liujin
 * @version: V1.0
 * @date: 2018-07-30
 * @time: 17:17
 */
public class FileStreamConverter extends BaseFileConverter<InputStream> {

      protected OnProgressUpdateListener mOnProgressUpdateListener;

      @Override
      public String fileName ( String key ) {

            return mKeyNameConverter.encodeToName( key );
      }

      @Override
      public InputStream toValue ( String key, InputStream stream ) throws Exception {

            return stream;
      }

      @Override
      public void saveValue ( String key, OutputStream outputStream, InputStream value )
          throws IOException {

            try {

                  byte[] bytes = new byte[ 128 ];
                  int len = 0;

                  while( ( len = value.read( bytes ) ) != -1 ) {

                        outputStream.write( bytes, 0, len );

                        if( mOnProgressUpdateListener != null ) {

                              mOnProgressUpdateListener.updateLength( key, len );
                        }
                  }
            } finally {

                  Close.close( value );
                  Close.close( outputStream );
            }
      }

      public OnProgressUpdateListener getOnProgressUpdateListener ( ) {

            return mOnProgressUpdateListener;
      }

      public void setOnProgressUpdateListener (
          OnProgressUpdateListener onProgressUpdateListener ) {

            mOnProgressUpdateListener = onProgressUpdateListener;
      }

      /**
       * 监视进度
       */
      @SuppressWarnings("AlibabaAbstractClassShouldStartWithAbstractNaming")
      public static abstract class OnProgressUpdateListener {

            /**
             * 总长度
             */
            protected int mLength;
            /**
             * 当前下载长度
             */
            protected int mCurrentLength;

            /**
             * 设置总长度
             */
            public void setLength ( int length ) {

                  mLength = length;
            }

            void updateLength ( String key, int newLength ) {

                  mCurrentLength += newLength;
                  onProgressUpdate( key, mLength, mCurrentLength );
            }

            /**
             * 回调监听
             *
             * @param key key
             * @param total 文件总长
             * @param current 当前下载长度
             */
            protected abstract void onProgressUpdate ( String key, int total, int current );
      }
}
