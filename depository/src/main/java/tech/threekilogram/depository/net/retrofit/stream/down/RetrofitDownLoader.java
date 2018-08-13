package tech.threekilogram.depository.net.retrofit.stream.down;

import java.io.File;
import java.io.IOException;
import tech.threekilogram.depository.net.retrofit.stream.RetrofitStreamLoader;

/**
 * 使用 retrofit 从网络下载文件,保存到指定文件夹
 *
 * @author liujin
 */
public class RetrofitDownLoader extends RetrofitStreamLoader<File> {

      /**
       * @param dir 保存文件夹
       */
      public RetrofitDownLoader ( File dir ) {

            super( new RetrofitDownConverter( dir ) );
      }

      /**
       * @param dir 保存文件夹
       * @param maxSize 文件夹最大大小
       *
       * @throws IOException 创建文件夹失败
       */
      public RetrofitDownLoader ( File dir, int maxSize ) throws IOException {

            super( new RetrofitDownConverter( dir, maxSize ) );
      }

      private RetrofitDownConverter getConverter ( ) {

            return (RetrofitDownConverter) mNetConverter;
      }

      /**
       * @param url 获取保存的文件
       *
       * @return 文件
       */
      public File getFile ( String url ) {

            return getConverter().getFile( url );
      }

      /**
       * @return 保存文件夹
       */
      public File getDir ( ) {

            return getConverter().getDir();
      }

      @Override
      public File load ( String key ) {

            RetrofitDownConverter converter = getConverter();
            File file = converter.getFile( key );

            if( file != null && file.exists() ) {

                  return file;
            }

            return super.load( key );
      }
}
