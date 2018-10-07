package tech.threekilogram.depository.stream;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import tech.threekilogram.depository.cache.json.ObjectLoader;
import tech.threekilogram.depository.file.converter.FileStringConverter;
import tech.threekilogram.depository.net.retrofit.converter.RetrofitStringConverter;
import tech.threekilogram.depository.net.retrofit.loader.RetrofitLoader;

/**
 * 从网络
 *
 * @author Liujin 2018-10-07:19:56
 */
public class StreamLoader {

      private static RetrofitLoader<String> sRetrofitStringLoader;
      private static FileStringConverter    sFileStringConverter;

      private StreamLoader ( ) { }

      /**
       * 从网络加载string
       *
       * @param url url
       *
       * @return url 对应的 string
       */
      public static String loadStringFromNet ( String url ) {

            if( sRetrofitStringLoader == null ) {
                  sRetrofitStringLoader = new RetrofitLoader<>(
                      new RetrofitStringConverter()
                  );
            }
            return sRetrofitStringLoader.load( url );
      }

      /**
       * 保存一个string到文件
       *
       * @param data string 数据
       * @param file string 需要保存到的文件
       */
      public static void saveStringToFile ( String data, File file ) {

            if( sFileStringConverter == null ) {
                  sFileStringConverter = new FileStringConverter();
            }

            try {
                  FileOutputStream outputStream = new FileOutputStream( file );
                  sFileStringConverter.to( outputStream, data );
            } catch(FileNotFoundException e) {
                  e.printStackTrace();
            }
      }

      /**
       * 从文件加载string
       *
       * @param file string 文件
       *
       * @return file 对应 string
       */
      public static String loadStringFromFile ( File file ) {

            if( sFileStringConverter == null ) {
                  sFileStringConverter = new FileStringConverter();
            }

            if( file.exists() && file.isFile() ) {

                  try {
                        FileInputStream inputStream = new FileInputStream( file );
                        return sFileStringConverter.from( inputStream );
                  } catch(FileNotFoundException e) {
                        e.printStackTrace();
                  }
            }
            return null;
      }

      /**
       * 从网络加载json
       *
       * @param url url
       * @param beanClass json bean class
       *
       * @return url 对应的 json bean
       */
      public static <V> V loadJsonFromNet ( String url, Class<V> beanClass ) {

            return ObjectLoader.loadFromNet( url, beanClass );
      }

      /**
       * 保存一个json到文件
       *
       * @param beanClass json bean class
       * @param data string 数据
       * @param file string 需要保存到的文件
       */
      public static <V> void saveJsonToFile ( File file, Class<V> beanClass, V data ) {

            ObjectLoader.toFile( file, data, beanClass );
      }

      /**
       * 从网络加载json
       *
       * @param file json 对应 file
       * @param beanClass json bean class
       *
       * @return url 对应的 json bean
       */
      public static <V> V loadJsonFromFile ( File file, Class<V> beanClass ) {

            return ObjectLoader.loadFromFile( file, beanClass );
      }
}
