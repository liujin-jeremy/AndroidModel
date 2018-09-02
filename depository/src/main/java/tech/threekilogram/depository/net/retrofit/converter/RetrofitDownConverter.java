package tech.threekilogram.depository.net.retrofit.converter;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import okhttp3.ResponseBody;
import tech.threekilogram.depository.file.BaseFileConverter;
import tech.threekilogram.depository.file.BaseFileLoader;
import tech.threekilogram.depository.file.loader.DiskLruLoader;
import tech.threekilogram.depository.file.loader.FileLoader;
import tech.threekilogram.depository.function.Close;
import tech.threekilogram.depository.net.retrofit.BaseRetrofitConverter;
import tech.threekilogram.depository.net.retrofit.loader.RetrofitDowner;

/**
 * 辅助{@link RetrofitDowner}将一个响应流保存到文件系统
 *
 * @author liujin
 */
public class RetrofitDownConverter extends BaseRetrofitConverter<File> {

      /**
       * 下载文件夹
       */
      private File                        mDir;
      /**
       * 保存文件
       */
      private BaseFileLoader<InputStream> mFileLoader;
      /**
       * converter
       */
      private FileStreamConverter         mFileStreamConverter;

      /**
       * @param dir 指定保存文件夹
       */
      public RetrofitDownConverter ( File dir ) {

            mDir = dir;
            mFileStreamConverter = new FileStreamConverter();
            mFileLoader = new FileLoader<>( dir, mFileStreamConverter );
      }

      /**
       * @param dir 保存文件夹
       * @param maxSize 该文件夹最大大小
       */
      public RetrofitDownConverter ( File dir, long maxSize ) throws IOException {

            mDir = dir;
            mFileStreamConverter = new FileStreamConverter();
            mFileLoader = new DiskLruLoader<>( dir, maxSize, mFileStreamConverter );
      }

      @Override
      public File onExecuteSuccess ( String key, ResponseBody response ) throws Exception {

            InputStream inputStream = response.byteStream();
            mFileStreamConverter.setLength( response.contentLength() );
            mFileLoader.save( key, inputStream );

            return mFileLoader.getFile( key );
      }

      public File getDir ( ) {

            return mDir;
      }

      public BaseFileLoader<InputStream> getFileLoader ( ) {

            return mFileLoader;
      }

      /**
       * 获取进度监听
       *
       * @return 进度监听
       */
      public OnProgressUpdateListener getOnProgressUpdateListener ( ) {

            return mFileStreamConverter.mOnProgressUpdateListener;
      }

      /**
       * 设置进度监听
       *
       * @param updateListener 监听
       */
      public void setOnProgressUpdateListener ( OnProgressUpdateListener updateListener ) {

            mFileStreamConverter.mOnProgressUpdateListener = updateListener;
      }

      /**
       * 辅助将流保存到文件
       */
      private class FileStreamConverter extends BaseFileConverter<InputStream> {

            /**
             * 监听下载进度
             */
            private OnProgressUpdateListener mOnProgressUpdateListener;
            /**
             * 流长度
             */
            private long                     mLength;

            /**
             * every {@link #saveValue(String, OutputStream, InputStream)}must set length
             */
            void setLength ( long length ) {

                  mLength = length;
            }

            @Override
            public String fileName ( String key ) {

                  return mKeyNameConverter.encodeToName( key );
            }

            /**
             * empty method
             *
             * @param key key
             * @param stream stream from {@link java.io.File} get by {@link #fileName(String)}
             */
            @Override
            public InputStream toValue ( String key, InputStream stream ) throws Exception {

                  return null;
            }

            @Override
            public void saveValue ( String key, OutputStream outputStream, InputStream value )
                throws IOException {

                  try {

                        OnProgressUpdateListener onProgressUpdateListener = mOnProgressUpdateListener;
                        byte[] bytes = new byte[ 128 ];
                        int len = 0;
                        long write = 0;
                        long length = mLength;

                        while( ( len = value.read( bytes ) ) != -1 ) {

                              outputStream.write( bytes, 0, len );

                              if( onProgressUpdateListener != null ) {

                                    write += len;
                                    onProgressUpdateListener
                                        .onProgressUpdate( key, length, write );
                              }
                        }
                  } finally {

                        Close.close( value );
                        Close.close( outputStream );
                  }
            }
      }

      /**
       * 监视下载进度
       */
      @SuppressWarnings("AlibabaAbstractClassShouldStartWithAbstractNaming")
      public interface OnProgressUpdateListener {

            /**
             * 回调监听
             *
             * @param key key
             * @param total 数据总长
             * @param current 当前下载长度
             */
            void onProgressUpdate ( String key, long total, long current );
      }
}
