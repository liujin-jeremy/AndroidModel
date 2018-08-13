package tech.threekilogram.depository.net.retrofit.stream.down;

import java.io.File;
import java.io.IOException;
import tech.threekilogram.depository.net.retrofit.stream.RetrofitStreamLoader;

/**
 * 使用 retrofit 从网络下载文件
 *
 * @author liujin
 */
public class RetrofitDownLoader extends RetrofitStreamLoader<String, File> {

      /**
       * @param dir 保存文件夹
       */
      public RetrofitDownLoader ( File dir ) {

            super( new RetrofitDownConverter( dir ) );
      }

      /**
       * @param dir 保存文件夹
       * @param maxSize 文件夹最大大小
       */
      public RetrofitDownLoader ( File dir, int maxSize ) throws IOException {

            super( new RetrofitDownConverter( dir, maxSize ) );
      }

      /**
       * @param url 获取保存的文件
       *
       * @return 文件
       */
      public File getFile ( String url ) {

            return ( (RetrofitDownConverter) mNetConverter ).getFile( url );
      }

      /**
       * @return 保存文件夹
       */
      public File getDir ( ) {

            return ( (RetrofitDownConverter) mNetConverter ).getDir();
      }
}
