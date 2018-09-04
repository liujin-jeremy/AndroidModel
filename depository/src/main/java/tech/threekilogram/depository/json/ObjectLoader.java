package tech.threekilogram.depository.json;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import tech.threekilogram.depository.function.Close;
import tech.threekilogram.depository.function.StringEncoder;
import tech.threekilogram.depository.instance.RetrofitClient;
import tech.threekilogram.depository.net.BaseNetLoader.OnNetExceptionListener;
import tech.threekilogram.depository.net.BaseNetLoader.OnNoResourceListener;
import tech.threekilogram.depository.net.retrofit.loader.StreamService;

/**
 * 静态的从网络加载对象
 *
 * @author: Liujin
 * @version: V1.0
 * @date: 2018-09-03
 * @time: 16:55
 */
public class ObjectLoader {

      /**
       * retrofit 客户端
       */
      private static Retrofit mRetrofit = RetrofitClient.INSTANCE;
      /**
       * 创建的service
       */
      private static StreamService                  sService;
      /**
       * 异常处理助手
       */
      private static OnNetExceptionListener<String> sOnNetExceptionListener;
      /**
       * 没有该资源助手
       */
      private static OnNoResourceListener           sOnNoResourceListener;
      /**
       * encode string
       */
      private static StringEncoder                  sEncoder;

      /**
       * 设置网络异常监听
       */
      public static void setOnNetExceptionListener (
          OnNetExceptionListener<String> onNetExceptionListener ) {

            sOnNetExceptionListener = onNetExceptionListener;
      }

      /**
       * 获取设置的网络异常监听
       */
      public static OnNetExceptionListener<String> getOnNetExceptionListener ( ) {

            return sOnNetExceptionListener;
      }

      /**
       * 设置没有资源监听
       */
      public static void setOnNoResourceListener (
          OnNoResourceListener onNoResourceListener ) {

            sOnNoResourceListener = onNoResourceListener;
      }

      /**
       * 获取设置的没有资源监听
       */
      public static OnNoResourceListener getOnNoResourceListener ( ) {

            return sOnNoResourceListener;
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
      public static <V> V loadFromNet ( String url, JsonConverter<V> converter ) {

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
                              if( sOnNetExceptionListener != null ) {
                                    sOnNetExceptionListener.onConvertException( url, e );
                              }
                        }
                  } else {

                        /* 连接到网络,但是没有获取到数据 */
                        if( sOnNoResourceListener != null ) {
                              sOnNoResourceListener.onExecuteFailed( url, response.code() );
                        }
                  }
            } catch(IOException e) {

                  /* 没有连接到网络 */
                  e.printStackTrace();
                  if( sOnNetExceptionListener != null ) {
                        sOnNetExceptionListener.onConnectException( url, e );
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
      public static <V> V loadFromFile ( File file, JsonConverter<V> converter ) {

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
       */
      public static <V> void toFile ( File file, V v, Class<V> type ) {

            toFile( file, v, new GsonConverter<V>( type ) );
      }

      /**
       * 转换对象到文件
       */
      public static <V> void toFile ( File file, V v, JsonConverter<V> converter ) {

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
}
