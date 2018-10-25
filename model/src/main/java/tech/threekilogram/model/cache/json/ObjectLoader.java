package tech.threekilogram.model.cache.json;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import tech.threekilogram.model.StreamConverter;
import tech.threekilogram.model.function.encode.Md5;
import tech.threekilogram.model.function.encode.StringHash;
import tech.threekilogram.model.function.instance.NetClient;
import tech.threekilogram.model.function.io.Close;
import tech.threekilogram.model.net.BaseNetLoader.OnErrorListener;
import tech.threekilogram.model.net.retrofit.loader.StreamService;

/**
 * 简单的从流中加载对象
 *
 * @author: Liujin
 * @version: V1.0
 * @date: 2018-09-03
 * @time: 16:55
 */
public class ObjectLoader {

      private ObjectLoader ( ) { }

      /**
       * retrofit 客户端
       */
      private static Retrofit        mRetrofit = NetClient.RETROFIT;
      /**
       * 创建的service
       */
      private static StreamService   sService;
      /**
       * 异常处理助手
       */
      private static OnErrorListener sErrorListener;

      /**
       * 设置网络异常监听
       */
      public static void setErrorListener (
          OnErrorListener errorListener ) {

            sErrorListener = errorListener;
      }

      /**
       * 获取设置的网络异常监听
       */
      public static OnErrorListener getErrorListener ( ) {

            return sErrorListener;
      }

      /**
       * 从网络加载一个对象
       *
       * @param url url
       * @param type object type
       * @param <V> 需要获取的对象类型
       *
       * @return 转换后的对象 or null if exception
       */
      public static <V> V loadFromNet ( String url, Class<V> type ) {

            return loadFromNet( url, new GsonConverter<>( type ) );
      }

      /**
       * 从网络加载一个对象
       *
       * @param url url
       * @param converter 辅助将网络流转为对象
       * @param <V> 需要获取的对象类型
       *
       * @return 转换后的对象 or null if exception
       */
      public static <V> V loadFromNet ( String url, StreamConverter<V> converter ) {

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
                        try {
                              return converter.from( responseBody.byteStream() );
                        } catch(Exception e) {
                              e.printStackTrace();

                              /* 转换异常 */
                              if( sErrorListener != null ) {
                                    sErrorListener.onConvertException( url, e );
                              }
                        }
                  } else {

                        /* 连接到网络,但是没有获取到数据 */
                        if( sErrorListener != null ) {
                              sErrorListener.onNullResource( url, response.code() );
                        }
                  }
            } catch(IOException e) {

                  /* 没有连接到网络 */
                  e.printStackTrace();
                  if( sErrorListener != null ) {
                        sErrorListener.onConnectException( url, e );
                  }
            }

            return null;
      }

      /**
       * 从本地文件加载一个对象
       *
       * @param file file
       * @param type object type
       * @param <V> 需要获取的对象类型
       *
       * @return 转换后的对象 or null if exception
       */
      public static <V> V loadFromFile ( File file, Class<V> type ) {

            return loadFromFile( file, new GsonConverter<>( type ) );
      }

      /**
       * 从本地文件加载一个对象
       *
       * @param file file
       * @param converter 辅助将文件流转为对象
       * @param <V> 需要获取的对象类型
       *
       * @return 转换后的对象 or null if exception
       */
      public static <V> V loadFromFile ( File file, StreamConverter<V> converter ) {

            FileInputStream inputStream = null;
            try {
                  inputStream = new FileInputStream( file );
                  return converter.from( inputStream );
            } catch(Exception e) {
                  e.printStackTrace();
            } finally {
                  Close.close( inputStream );
            }
            return null;
      }

      /**
       * 转换对象到文件
       *
       * @param file 该对象将保存到该文件
       * @param v 对象
       * @param type json bean对象
       */
      public static <V> void toFile ( File file, V v, Class<V> type ) {

            toFile( file, v, new GsonConverter<V>( type ) );
      }

      /**
       * 转换对象到文件
       *
       * @param file 该对象将保存到该文件
       * @param v 对象
       * @param converter 辅助转换
       */
      public static <V> void toFile ( File file, V v, StreamConverter<V> converter ) {

            FileOutputStream outputStream = null;
            try {
                  outputStream = new FileOutputStream( file );
                  converter.to( outputStream, v );
            } catch(Exception e) {
                  e.printStackTrace();
            } finally {
                  Close.close( outputStream );
            }
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
}
