package tech.threekilogram.depository.net.retrofit.loader;

import java.io.File;
import java.io.IOException;
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
      public RetrofitDowner ( File dir, long maxSize ) throws IOException {

            super( new RetrofitDownConverter( dir, maxSize ) );
      }

      public RetrofitDowner ( RetrofitDownConverter converter ) {

            super( converter );
      }

      RetrofitDownConverter getConverter ( ) {

            return (RetrofitDownConverter) mNetConverter;
      }

      /**
       * @param url 获取该url对应的文件,文件可能不存在{@link File#exists()}
       *
       * @return 文件
       */
      public File getFile ( String url ) {

            return getConverter().getFileLoader().getFile( url );
      }

      /**
       * @return 保存文件夹
       */
      public File getDir ( ) {

            return getConverter().getDir();
      }

      /**
       * 删除该url对应的文件
       *
       * @param url url
       */
      public void removeFile ( String url ) {

            getConverter().getFileLoader().remove( url );
      }

      public void clearFile ( ) {

            try {
                  getConverter().getFileLoader().clear();
            } catch(IOException e) {
                  e.printStackTrace();
            }
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
            File file = converter.getFileLoader().getFile( url );

            if( file != null && file.exists() ) {

                  OnProgressUpdateListener listener = getOnProgressUpdateListener();
                  if( listener != null ) {
                        listener.onProgressUpdate( url, file.length(), file.length() );
                  }
                  return file;
            }

            return super.load( url );
      }

      /**
       * {@link RetrofitDownConverter#getOnProgressUpdateListener()}
       */
      public OnProgressUpdateListener getOnProgressUpdateListener ( ) {

            return getConverter().getOnProgressUpdateListener();
      }

      /**
       * {@link RetrofitDownConverter#setOnProgressUpdateListener(OnProgressUpdateListener)}
       *
       * @param onProgressUpdateListener 进度监听
       */
      public void setOnProgressUpdateListener (
          OnProgressUpdateListener onProgressUpdateListener ) {

            getConverter().setOnProgressUpdateListener( onProgressUpdateListener );
      }

      /**
       * 监视下载进度
       */
      @SuppressWarnings("AlibabaAbstractClassShouldStartWithAbstractNaming")
      public interface OnProgressUpdateListener {

            /**
             * 回调监听
             *
             * @param key key
             * @param total 数据总长
             * @param current 当前下载长度
             */
            void onProgressUpdate ( String key, long total, long current );
      }
}
