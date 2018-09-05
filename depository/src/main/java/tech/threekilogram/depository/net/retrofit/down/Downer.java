package tech.threekilogram.depository.net.retrofit.down;

import android.support.annotation.Nullable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import tech.threekilogram.depository.function.encode.Md5;
import tech.threekilogram.depository.function.encode.StringHash;
import tech.threekilogram.depository.function.instance.RetrofitClient;
import tech.threekilogram.depository.function.io.Close;
import tech.threekilogram.depository.function.io.FileCache;
import tech.threekilogram.depository.net.retrofit.loader.StreamService;

/**
 * 一个通用下载器,相对于{@link RetrofitDowner}使用更直接,可以直接使用静态方法
 *
 * @author: Liujin
 * @version: V1.0
 * @date: 2018-09-03
 * @time: 10:11
 */
public class Downer {

      /**
       * retrofit 客户端
       */
      private static Retrofit mRetrofit = RetrofitClient.INSTANCE;
      /**
       * 创建的service
       */
      private static StreamService sService;
      /**
       * 缓存file对象
       */
      private static FileCache sFileCache = new FileCache();
      /**
       * 监听下载进度
       */
      private static OnDownloadUpdateListener sOnDownloadUpdateListener;
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
      public static void setOnDownloadUpdateListener (
          OnDownloadUpdateListener onDownloadUpdateListener ) {

            sOnDownloadUpdateListener = onDownloadUpdateListener;
      }

      /**
       * 获取设置的下载进度监听
       */
      public static OnDownloadUpdateListener getOnDownloadUpdateListener ( ) {

            return sOnDownloadUpdateListener;
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
       * 下载到指定文件
       *
       * @param file 文件
       * @param url url
       *
       * @return 该url对应文件, 或者null保存失败
       */
      @Nullable
      public static File downloadTo ( File file, String url ) {

            if( file != null && file.exists() ) {

                  if( sOnDownloadUpdateListener != null ) {
                        sOnDownloadUpdateListener.onFinished( file, url );
                  }
                  return file;
            }

            /* 制造一个call对象 */
            if( sService == null ) {

                  sService = mRetrofit.create( StreamService.class );
            }
            Call<ResponseBody> call = sService.toGet( url );

            /* 执行call */
            try {
                  Response<ResponseBody> response = call.execute();

                  /* 如果成功获得数据 */
                  if( response.isSuccessful() ) {

                        ResponseBody responseBody = response.body();

                        /* 转换数据 */
                        assert responseBody != null;
                        return saveToFile(
                            url,
                            file,
                            responseBody.byteStream(),
                            responseBody.contentLength()
                        );
                  } else {

                        /* 连接到网络,但是没有获取到数据 */
                        if( sOnNoResourceListener != null ) {
                              sOnNoResourceListener.onExecuteFailed( file, url, response.code() );
                        }
                  }
            } catch(IOException e) {

                  /* 没有连接到网络 */
                  e.printStackTrace();
                  if( sOnExceptionListener != null ) {
                        sOnExceptionListener.onConnectException( file, url, e );
                  }
            }

            return null;
      }

      private static File saveToFile (
          String url,
          File file,
          InputStream value,
          long length ) {

            FileOutputStream outputStream = null;
            try {
                  outputStream = new FileOutputStream( file );
            } catch(FileNotFoundException e) {
                  e.printStackTrace();
                  if( sOnExceptionListener != null ) {
                        sOnExceptionListener.onFileNotFoundException( file, url, e );
                  }
                  return null;
            }

            try {

                  OnDownloadUpdateListener onProgressUpdateListener = sOnDownloadUpdateListener;
                  byte[] bytes = new byte[ 64 ];
                  int len = 0;
                  long write = 0;

                  while( ( len = value.read( bytes ) ) != -1 ) {

                        outputStream.write( bytes, 0, len );

                        if( onProgressUpdateListener != null ) {

                              write += len;
                              onProgressUpdateListener.onProgressUpdate( file, url, length, write );
                        }
                  }

                  if( onProgressUpdateListener != null ) {
                        onProgressUpdateListener.onFinished( file, url );
                  }
            } catch(IOException e) {
                  e.printStackTrace();

                  /* 通知没有文件异常 */
                  if( sOnExceptionListener != null ) {
                        sOnExceptionListener.onIOException( file, url, e );
                  }
                  file = null;
            } finally {

                  Close.close( value );
                  Close.close( outputStream );
            }

            return file;
      }

      /**
       * 根据一个url获取一个文件
       *
       * @param dir 文件夹
       * @param url 文件url,将会转为文件名字
       *
       * @return 位于文件夹下的文件
       */
      public static File getFileByHash ( File dir, String url ) {

            return new File( dir, StringHash.hash( url ) );
      }

      /**
       * 根据一个url获取一个文件
       *
       * @param dir 文件夹
       * @param url 文件url,将会转为文件名字
       *
       * @return 位于文件夹下的文件
       */
      public static File getFileByMd5 ( File dir, String url ) {

            return new File( dir, Md5.encode( url ) );
      }

      /**
       * 监听下载进度
       */
      public interface OnDownloadUpdateListener {

            /**
             * 回调监听
             *
             * @param file file
             * @param url url
             * @param total 数据总长
             * @param current 当前下载长度
             */
            void onProgressUpdate ( File file, String url, long total, long current );

            /**
             * 下载完成回调
             *
             * @param file file
             * @param url url
             */
            void onFinished ( File file, String url );
      }

      /**
       * 使用该类处理网络异常
       */
      public interface OnExceptionListener {

            /**
             * 无法连接网络
             *
             * @param file file
             * @param url url
             * @param e exception exception
             */
            void onConnectException ( File file, String url, IOException e );

            /**
             * 没有该文件异常
             *
             * @param file file
             * @param url url
             * @param e e
             */
            void onFileNotFoundException ( File file, String url, FileNotFoundException e );

            /**
             * io 异常
             *
             * @param file file
             * @param url url
             * @param e e
             */
            void onIOException ( File file, String url, IOException e );
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
             * @param file file
             * @param url key
             * @param httpCode http code
             */
            void onExecuteFailed ( File file, String url, int httpCode );
      }
}