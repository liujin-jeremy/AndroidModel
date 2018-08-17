package tech.threekilogram.depository.net.retrofit.loader;

import java.io.File;
import java.io.IOException;
import tech.threekilogram.depository.file.converter.FileStreamConverter.OnProgressUpdateListener;
import tech.threekilogram.depository.net.retrofit.converter.RetrofitDownConverter;

/**
 * 使用 retrofit 从网络下载文件,保存到指定文件夹
 *
 * @author liujin
 */
public class RetrofitDowner extends RetrofitLoader<File> {

      /**
       * @param dir 保存文件夹
       */
      public RetrofitDowner ( File dir ) {

            super( new RetrofitDownConverter( dir ) );
      }

      /**
       * @param dir 保存文件夹
       * @param maxSize 文件夹最大大小
       *
       * @throws IOException 创建文件夹失败
       */
      public RetrofitDowner ( File dir, int maxSize ) throws IOException {

            super( new RetrofitDownConverter( dir, maxSize ) );
      }

      public RetrofitDowner ( RetrofitDownConverter netConverter ) {

            super( netConverter );
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

      public void removeFile ( String key ) {

            getConverter().removeFile( key );
      }

      /**
       * 加载文件如果本地文件存在将返回本地,否则返回从网络下载
       *
       * @param url key
       *
       * @return file
       */
      @Override
      public File load ( String url ) {

            RetrofitDownConverter converter = getConverter();
            File file = converter.getFile( url );

            if( file != null && file.exists() ) {

                  return file;
            }

            return super.load( url );
      }

      public OnProgressUpdateListener getOnProgressUpdateListener ( ) {

            return getConverter().getOnProgressUpdateListener();
      }

      public void setOnProgressUpdateListener (
          OnProgressUpdateListener onProgressUpdateListener ) {

            getConverter().setOnProgressUpdateListener( onProgressUpdateListener );
      }
}
