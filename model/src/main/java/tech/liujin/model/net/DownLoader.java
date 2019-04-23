package tech.liujin.model.net;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import tech.threekilogram.model.util.FileHelper;
import tech.threekilogram.model.util.encode.EncodeMode;
import tech.threekilogram.model.util.instance.NetClient;
import tech.threekilogram.model.util.io.Close;
import tech.threekilogram.model.util.io.FileClear;

/**
 * @author Liujin 2018-10-25:17:46
 */
public class DownLoader {

      private OnErrorListener mOnErrorListener;
      private FileHelper      mFileHelper;

      public DownLoader ( File dir ) {

            mFileHelper = new FileHelper( dir );
      }

      public void setOnErrorListener (
          OnErrorListener onErrorListener ) {

            mOnErrorListener = onErrorListener;
      }

      public OnErrorListener getOnErrorListener ( ) {

            return mOnErrorListener;
      }

      public void setUrlEncodeMode ( @EncodeMode int mode ) {

            mFileHelper.setMode( mode );
      }

      protected File executeUrl ( String url, OnProgressUpdateListener updateListener ) {

            /* 执行call */
            try {
                  Request request = new Request.Builder()
                      .url( url )
                      .build();
                  Response response = NetClient.OKHTTP.newCall( request ).execute();

                  /* 如果成功获得数据 */
                  if( response.isSuccessful() ) {

                        ResponseBody responseBody = response.body();

                        try {

                              /* 转换数据 */
                              assert responseBody != null;
                              if( updateListener == null ) {

                                    return readResponse( url, responseBody );
                              } else {
                                    return readResponse( url, responseBody, updateListener );
                              }
                        } catch(Exception e) {

                              e.printStackTrace();
                              /* 转换异常 */
                              if( mOnErrorListener != null ) {
                                    mOnErrorListener.onConvertException( url, e );
                              }
                        }
                  } else {

                        /* 连接到网络,但是没有获取到数据 */
                        if( mOnErrorListener != null ) {
                              mOnErrorListener.onNullResource( url, response.code() );
                        }
                  }
            } catch(IOException e) {

                  /* 没有连接到网络 */
                  e.printStackTrace();
                  if( mOnErrorListener != null ) {
                        mOnErrorListener.onConnectException( url, e );
                  }
            }

            return null;
      }

      protected File readResponse (
          String url, ResponseBody responseBody ) {

            byte[] temp = new byte[ 256 ];
            int len = 0;

            FileOutputStream outputStream = null;
            InputStream inputStream = responseBody.byteStream();

            try {
                  File file = mFileHelper.getFile( url );
                  outputStream = new FileOutputStream( file );

                  while( ( len = inputStream.read( temp ) ) != -1 ) {
                        outputStream.write( temp, 0, len );
                  }

                  return file;
            } catch(IOException e) {
                  e.printStackTrace();
            } finally {
                  Close.close( inputStream );
                  Close.close( outputStream );
            }

            return null;
      }

      protected File readResponse (
          String url, ResponseBody responseBody, OnProgressUpdateListener updateListener ) {

            byte[] temp = new byte[ 64 ];
            int len = 0;
            int read = 0;
            long total = responseBody.contentLength();

            FileOutputStream outputStream = null;
            InputStream inputStream = responseBody.byteStream();

            try {
                  File file = mFileHelper.getFile( url );
                  outputStream = new FileOutputStream( file );

                  while( ( len = inputStream.read( temp ) ) != -1 ) {
                        outputStream.write( temp, 0, len );
                        read += len;
                        updateListener.onUpdate( url, total, read );
                  }

                  return file;
            } catch(IOException e) {
                  e.printStackTrace();
            } finally {
                  Close.close( inputStream );
                  Close.close( outputStream );
            }

            return null;
      }

      public File down ( String url ) {

            File file = mFileHelper.getFile( url );
            if( file.exists() ) {
                  return file;
            }

            return executeUrl( url, null );
      }

      public File down ( String url, OnProgressUpdateListener updateListener ) {

            File file = mFileHelper.getFile( url );
            if( file.exists() ) {
                  return file;
            }

            return executeUrl( url, updateListener );
      }

      public File getFile ( String url ) {

            return mFileHelper.getFile( url );
      }

      public File getDir ( ) {

            return mFileHelper.getDir();
      }

      public void clear ( ) {

            FileClear.clearFile( getDir() );
      }

      public interface OnProgressUpdateListener {

            /**
             * 下载进度更新
             *
             * @param url url
             * @param total 数据总长
             * @param readLength 已经读取的数据
             */
            void onUpdate ( String url, long total, long readLength );
      }
}
