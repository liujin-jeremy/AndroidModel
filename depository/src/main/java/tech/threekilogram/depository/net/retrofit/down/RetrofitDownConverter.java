package tech.threekilogram.depository.net.retrofit.down;

import android.support.v4.util.ArrayMap;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import okhttp3.ResponseBody;
import tech.threekilogram.depository.file.BaseFileConverter;
import tech.threekilogram.depository.file.BaseFileLoader;
import tech.threekilogram.depository.file.loader.DiskLruLoader;
import tech.threekilogram.depository.file.loader.FileLoader;
import tech.threekilogram.depository.function.io.Close;
import tech.threekilogram.depository.net.retrofit.converter.ResponseBodyConverter;
import tech.threekilogram.depository.net.retrofit.down.RetrofitDowner.OnDownloadUpdateListener;

/**
 * 辅助{@link RetrofitDowner}将一个响应流保存到文件系统
 *
 * @author liujin
 */
class RetrofitDownConverter implements ResponseBodyConverter<File> {

      /**
       * 下载进度监听
       */
      OnDownloadUpdateListener mOnDownloadUpdateListener;
      /**
       * 下载文件夹
       */
      private File                          mDir;
      /**
       * 保存文件
       */
      private BaseFileLoader<InputStream>   mFileLoader;
      /**
       * converter
       */
      private FileStreamConverter           mFileStreamConverter;
      /**
       * 临时保存信息,用于{@link #mOnDownloadUpdateListener}回调时使用
       */
      private ArrayMap<InputStream, Holder> mHolders;

      /**
       * @param dir 指定保存文件夹
       */
      RetrofitDownConverter ( File dir ) {

            mDir = dir;
            mFileStreamConverter = new FileStreamConverter();
            mFileLoader = new FileLoader<>( dir, mFileStreamConverter );
      }

      /**
       * @param dir 保存文件夹
       * @param maxSize 该文件夹最大大小
       */
      RetrofitDownConverter ( File dir, long maxSize ) throws IOException {

            mDir = dir;
            mFileStreamConverter = new FileStreamConverter();
            mFileLoader = new DiskLruLoader<>( dir, maxSize, mFileStreamConverter );
      }

      @Override
      public File onExecuteSuccess (
          String url, ResponseBody response )
          throws Exception {

            InputStream inputStream = response.byteStream();
            if( mOnDownloadUpdateListener != null ) {
                  if( mHolders == null ) {
                        mHolders = new ArrayMap<>();
                  }
                  mHolders.put( inputStream, new Holder( url, response.contentLength() ) );
            }
            mFileLoader.save( url, inputStream );

            return getFileLoader().getFile( url );
      }

      File getDir ( ) {

            return mDir;
      }

      BaseFileLoader<InputStream> getFileLoader ( ) {

            return mFileLoader;
      }

      /**
       * 辅助将流保存到文件
       */
      private class FileStreamConverter extends BaseFileConverter<InputStream> {

            @Override
            public String fileName ( String key ) {

                  return mNameConverter.encode( key );
            }

            /**
             * empty method
             *
             * @param stream stream from {@link File} get by {@link #fileName(String)}
             */
            @Override
            public InputStream from ( InputStream stream ) {

                  return null;
            }

            @Override
            public void to ( OutputStream outputStream, InputStream value ) {

                  if( mOnDownloadUpdateListener != null ) {

                        Holder holder = mHolders.remove( value );
                        if( holder != null ) {
                              readWithListener(
                                  outputStream,
                                  value,
                                  holder.mUrl,
                                  holder.mContentLength,
                                  mOnDownloadUpdateListener
                              );
                        } else {

                              read( outputStream, value );
                        }
                  } else {

                        read( outputStream, value );
                  }
            }

            private void readWithListener (
                OutputStream outputStream,
                InputStream value,
                String url,
                long total,
                OnDownloadUpdateListener listener ) {

                  try {

                        byte[] bytes = new byte[ 64 ];
                        int len = 0;
                        long alreadyRead = 0;
                        while( ( len = value.read( bytes ) ) != -1 ) {

                              outputStream.write( bytes, 0, len );
                              alreadyRead += len;
                              listener.onProgressUpdate( url, total, alreadyRead );
                        }

                        listener.onFinished( url );
                  } catch(IOException e) {
                        e.printStackTrace();
                  } finally {

                        Close.close( value );
                        Close.close( outputStream );
                  }
            }

            private void read ( OutputStream outputStream, InputStream value ) {

                  try {

                        byte[] bytes = new byte[ 64 ];
                        int len = 0;
                        while( ( len = value.read( bytes ) ) != -1 ) {

                              outputStream.write( bytes, 0, len );
                        }
                  } catch(IOException e) {
                        e.printStackTrace();
                  } finally {

                        Close.close( value );
                        Close.close( outputStream );
                  }
            }
      }

      /**
       * 临时保存一组数据
       */
      private class Holder {

            private String mUrl;
            private long   mContentLength;

            Holder ( String url, long contentLength ) {

                  mUrl = url;
                  mContentLength = contentLength;
            }
      }
}
