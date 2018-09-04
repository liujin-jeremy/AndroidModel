package tech.threekilogram.depository.net.retrofit.loader;

import static tech.threekilogram.depository.function.StringEncoder.HASH;
import static tech.threekilogram.depository.function.StringEncoder.MD5;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import tech.threekilogram.depository.function.Close;
import tech.threekilogram.depository.function.FileCache;
import tech.threekilogram.depository.function.StringEncoder;
import tech.threekilogram.depository.instance.RetrofitClient;

/**
 * 下载器
 *
 * @author: Liujin
 * @version: V1.0
 * @date: 2018-09-03
 * @time: 10:11
 */
public class DownLoader {

      /**
       * retrofit 客户端
       */
      private static Retrofit mRetrofit = RetrofitClient.INSTANCE;
      /**
       * 创建的service
       */
      private static StreamService sService;
      /**
       * 辅助将url转为file name
       */
      private static StringEncoder sNameConverter = new StringEncoder();
      /**
       * 缓存file对象
       */
      private static FileCache     sFileCache     = new FileCache();
      /**
       * 监听下载进度
       */
      private static OnDownLoadUpdateListener sOnDownLoadUpdateListener;
      /**
       * 下载时发生异常的监听
       */
      private static OnExceptionListener      sOnExceptionListener;
      /**
       * 下载时没有该资源的异常
       */
      private static OnNoResourceListener     sOnNoResourceListener;

      /**
       * 设置下载进度监听
       */
      public static void setOnDownLoadUpdateListener (
          OnDownLoadUpdateListener onDownLoadUpdateListener ) {

            sOnDownLoadUpdateListener = onDownLoadUpdateListener;
      }

      /**
       * 获取设置的下载进度监听
       */
      public static OnDownLoadUpdateListener getOnDownLoadUpdateListener ( ) {

            return sOnDownLoadUpdateListener;
      }

      /**
       * 获取设置的异常监听
       */
      public static OnExceptionListener getOnExceptionListener ( ) {

            return sOnExceptionListener;
      }

      /**
       * 设置异常监听
       */
      public static void setOnExceptionListener (
          OnExceptionListener onExceptionListener ) {

            sOnExceptionListener = onExceptionListener;
      }

      /**
       * 获取设置的没有资源监听
       */
      public static OnNoResourceListener getOnNoResourceListener ( ) {

            return sOnNoResourceListener;
      }

      /**
       * 设置没有资源监听
       */
      public static void setOnNoResourceListener (
          OnNoResourceListener onNoResourceListener ) {

            sOnNoResourceListener = onNoResourceListener;
      }

      /**
       * 设置文件保存名字采用较短的还是较长的
       */
      public static void setShortFileName ( boolean shortFileName ) {

            if( shortFileName ) {
                  sNameConverter.setMode( HASH );
            } else {
                  sNameConverter.setMode( MD5 );
            }
      }

      /**
       * 下载到文件夹
       *
       * @param dir 文件夹
       * @param url url
       *
       * @return file down loaded , or null if exception
       */
      public static File down ( File dir, String url ) {

            /* 2. 制造一个call对象 */
            if( sService == null ) {

                  sService = mRetrofit.create( StreamService.class );
            }
            Call<ResponseBody> call = sService.toGet( url );

            /* 3. 执行call */
            try {
                  Response<ResponseBody> response = call.execute();

                  /* 4. 如果成功获得数据 */
                  if( response.isSuccessful() ) {

                        ResponseBody responseBody = response.body();

                        /* 5. 转换数据 */
                        assert responseBody != null;
                        return saveToFile(
                            dir,
                            url,
                            responseBody.byteStream(),
                            responseBody.contentLength()
                        );
                  } else {

                        /* 4. 连接到网络,但是没有获取到数据 */
                        if( sOnNoResourceListener != null ) {
                              sOnNoResourceListener.onExecuteFailed( dir, url, response.code() );
                        }
                  }
            } catch(IOException e) {

                  /* 4. 没有连接到网络 */
                  e.printStackTrace();
                  if( sOnExceptionListener != null ) {
                        sOnExceptionListener.onConnectException( dir, url, e );
                  }
            }

            return null;
      }

      private static File saveToFile (
          File dir,
          String url,
          InputStream value,
          long length ) {

            File file = sFileCache.get( url );
            if( file == null ) {
                  String name = sNameConverter.encode( url );
                  file = new File( dir, name );
                  sFileCache.put( url, file );
            }

            FileOutputStream outputStream = null;
            try {
                  outputStream = new FileOutputStream( file );
            } catch(FileNotFoundException e) {
                  e.printStackTrace();
                  if( sOnExceptionListener != null ) {
                        sOnExceptionListener.onFileNotFoundException( dir, url, e );
                  }
                  return null;
            }

            try {

                  OnDownLoadUpdateListener onProgressUpdateListener = sOnDownLoadUpdateListener;
                  byte[] bytes = new byte[ 128 ];
                  int len = 0;
                  long write = 0;

                  while( ( len = value.read( bytes ) ) != -1 ) {

                        outputStream.write( bytes, 0, len );

                        if( onProgressUpdateListener != null ) {

                              write += len;
                              onProgressUpdateListener.onProgressUpdate( dir, url, length, write );
                        }
                  }
            } catch(IOException e) {
                  e.printStackTrace();

                  /* 通知没有文件异常 */
                  if( sOnExceptionListener != null ) {
                        sOnExceptionListener.onIOException( dir, url, e );
                  }
                  file = null;
            } finally {

                  Close.close( value );
                  Close.close( outputStream );
            }

            return file;
      }

      public static File getFile ( File dir, String url ) {

            File file = sFileCache.get( url );
            if( file == null ) {
                  String name = sNameConverter.encode( url );
                  file = new File( dir, name );
                  sFileCache.put( url, file );
            }
            return file;
      }

      /**
       * 监听下载进度
       */
      public interface OnDownLoadUpdateListener {

            /**
             * 回调监听
             *
             * @param dir dir
             * @param url url
             * @param total 数据总长
             * @param current 当前下载长度
             */
            void onProgressUpdate ( File dir, String url, long total, long current );
      }

      /**
       * 使用该类处理网络异常
       */
      public interface OnExceptionListener {

            /**
             * 无法连接网络
             *
             * @param dir dir
             * @param url url
             * @param e exception exception
             */
            void onConnectException ( File dir, String url, IOException e );

            /**
             * 没有该文件异常
             *
             * @param dir dir
             * @param url url
             * @param e e
             */
            void onFileNotFoundException ( File dir, String url, FileNotFoundException e );

            /**
             * io 异常
             *
             * @param dir dir
             * @param url url
             * @param e e
             */
            void onIOException ( File dir, String url, IOException e );
      }

      /**
       * 当从网络获取资源响应码不在{200~300}之间时的处理
       */
      public interface OnNoResourceListener {

            /**
             * 没有成功获取数据的回调
             * <p>
             * when cant get a correct response {response not in 200~300} return failed
             *
             * @param dir dir
             * @param url key
             * @param httpCode http code
             */
            void onExecuteFailed ( File dir, String url, int httpCode );
      }
}
