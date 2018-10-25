package tech.threekilogram.model.net;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import tech.threekilogram.model.net.DownLoader.OnProgressUpdateListener;
import tech.threekilogram.model.util.instance.NetClient;
import tech.threekilogram.model.util.io.Close;

/**
 * 一个通用下载器
 *
 * @author: Liujin
 * @version: V1.0
 * @date: 2018-09-03
 * @time: 10:11
 */
public class Downer {

      private Downer ( ) { }

      private static File executeUrl (
          String url, File file, OnProgressUpdateListener updateListener,
          OnErrorListener errorListener ) {

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

                                    return readResponse( file, responseBody );
                              } else {
                                    return readResponse( url, file, responseBody, updateListener );
                              }
                        } catch(Exception e) {

                              e.printStackTrace();
                              /* 转换异常 */
                              if( errorListener != null ) {
                                    errorListener.onConvertException( url, e );
                              }
                        }
                  } else {

                        /* 连接到网络,但是没有获取到数据 */
                        if( errorListener != null ) {
                              errorListener.onNullResource( url, response.code() );
                        }
                  }
            } catch(IOException e) {

                  /* 没有连接到网络 */
                  e.printStackTrace();
                  if( errorListener != null ) {
                        errorListener.onConnectException( url, e );
                  }
            }

            return null;
      }

      private static File readResponse ( File file, ResponseBody responseBody ) {

            byte[] temp = new byte[ 256 ];
            int len = 0;

            FileOutputStream outputStream = null;
            InputStream inputStream = responseBody.byteStream();

            try {
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

      private static File readResponse (
          String url, File file, ResponseBody responseBody,
          OnProgressUpdateListener updateListener ) {

            byte[] temp = new byte[ 64 ];
            int len = 0;
            int read = 0;
            long total = responseBody.contentLength();

            FileOutputStream outputStream = null;
            InputStream inputStream = responseBody.byteStream();

            try {
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

      public static File down (
          String url,
          File file ) {

            if( file.exists() ) {
                  return file;
            }
            return executeUrl( url, file, null, null );
      }

      public static File down (
          String url,
          File file,
          OnProgressUpdateListener updateListener ) {

            if( file.exists() ) {
                  return file;
            }
            return executeUrl( url, file, updateListener, null );
      }

      public static File down (
          String url,
          File file,
          OnProgressUpdateListener updateListener,
          OnErrorListener listener ) {

            if( file.exists() ) {
                  return file;
            }
            return executeUrl( url, file, updateListener, listener );
      }
}
