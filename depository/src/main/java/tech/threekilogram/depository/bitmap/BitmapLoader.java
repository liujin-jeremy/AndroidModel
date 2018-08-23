package tech.threekilogram.depository.bitmap;

import static tech.threekilogram.depository.bitmap.BitmapConverter.MATCH_SIZE;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import java.io.File;
import java.io.IOException;
import tech.threekilogram.depository.bitmap.BitmapConverter.ScaleMode;
import tech.threekilogram.depository.memory.lru.MemoryBitmap;
import tech.threekilogram.depository.net.retrofit.converter.RetrofitDownConverter.OnProgressUpdateListener;
import tech.threekilogram.depository.net.retrofit.loader.RetrofitDowner;
import tech.threekilogram.messengers.Messengers;
import tech.threekilogram.messengers.OnMessageReceiveListener;

/**
 * 缓存bitmap对象
 *
 * @author: Liujin
 * @version: V1.0
 * @date: 2018-08-16
 * @time: 21:44
 */
public class BitmapLoader implements OnMessageReceiveListener {

      private static final int LOAD_SUCCESS = 11;
      private static final int LOAD_FAILED  = 13;

      /**
       * 内存缓存
       */
      protected MemoryBitmap<String>   mMemory;
      /**
       * bitmap 转换
       */
      protected BitmapConverter        mBitmapConverter;
      /**
       * 下载
       */
      protected RetrofitDowner         mDowner;
      /**
       * 监听
       */
      protected OnLoadFinishedListener mOnLoadFinishedListener;

      public BitmapLoader ( int maxMemorySize, File cacheDir ) {

            mMemory = new MemoryBitmap<>( maxMemorySize );
            mDowner = new RetrofitDowner( cacheDir );
            mBitmapConverter = new BitmapConverter();
      }

      public BitmapLoader ( int maxMemorySize, File cacheDir, int maxFileSize ) throws IOException {

            mMemory = new MemoryBitmap<>( maxMemorySize );
            mDowner = new RetrofitDowner( cacheDir, maxFileSize );
            mBitmapConverter = new BitmapConverter();
      }

      /**
       * 配置bitmap加载配置
       *
       * @param width 需求宽度
       * @param height 需求高度
       */
      public void configBitmap ( int width, int height ) {

            configBitmap( width, height, MATCH_SIZE, Config.RGB_565 );
      }

      /**
       * 配置bitmap加载配置
       *
       * @param width 需求宽度
       * @param height 需求高度
       * @param scaleMode 缩放方式
       */
      public void configBitmap ( int width, int height, @ScaleMode int scaleMode ) {

            configBitmap( width, height, scaleMode, Config.RGB_565 );
      }

      /**
       * 配置bitmap加载配置
       *
       * @param width 需求宽度
       * @param height 需求高度
       * @param scaleMode 缩放方式
       */
      public void configBitmap ( int width, int height, @ScaleMode int scaleMode, Config config ) {

            mBitmapConverter.setWidth( width );
            mBitmapConverter.setHeight( height );
            mBitmapConverter.setMode( scaleMode );
            mBitmapConverter.setBitmapConfig( config );
      }

      /**
       * 转发给监听消息
       */
      protected void notifyListener ( String url, Bitmap bitmap ) {

            if( mOnLoadFinishedListener != null ) {

                  mOnLoadFinishedListener.onFinished( url, bitmap );
            }
      }

      /**
       * 异步加载该url对应的图片,需要实现{@link #asyncLoad(Runnable)}完成异步任务执行,使用{@link OnLoadFinishedListener}监听结果
       *
       * @param url 图片url
       */
      public void load ( String url ) {

            Bitmap fromMemory = loadFromMemory( url );
            if( fromMemory == null ) {

                  asyncLoad( createRunnable( url ) );
            } else {

                  notifyListener( url, fromMemory );
            }
      }

      /**
       * 子类实现该方法,决定如何在后台线程加载图片,
       * 该框架并不提供异步功能,由用户自己提供,
       * 这样可以防止引入多个库的情况下,线程驻留太多,耗费资源
       *
       * @param runnable runnable 框架封装的后台加载任务
       */
      protected void asyncLoad ( Runnable runnable ) { }

      /**
       * 创建后台加载图片任务
       *
       * @param url 图片 url
       *
       * @return 任务
       */
      protected AsyncLoadRunnable createRunnable ( String url ) {

            return new AsyncLoadRunnable( url );
      }

      /**
       * 用于设置异步加载{@link #asyncLoad(Runnable)}设置结果
       */
      protected void setResult ( String url, Bitmap bitmap ) {

            if( bitmap == null ) {

                  Messengers.send( LOAD_FAILED, url, this );
            } else {
                  Messengers.send( LOAD_SUCCESS, url, this );
            }
      }

      @Override
      public void onReceive ( int what, Object extra ) {

            String url = (String) extra;

            if( what == LOAD_SUCCESS ) {

                  Bitmap bitmap = loadFromMemory( url );
                  notifyListener( url, bitmap );
            } else {

                  notifyListener( url, null );
            }
      }

      /**
       * 仅从内存读取
       *
       * @param url mUrl
       *
       * @return bitmap or null
       */
      public Bitmap loadFromMemory ( String url ) {

            return mMemory.load( url );
      }

      /**
       * @return 当前使用内存大小
       */
      public int memorySize ( ) {

            return mMemory.size();
      }

      /**
       * 清空内存
       */
      public void clearMemory ( ) {

            mMemory.clear();
      }

      public void removeMemory ( String url ) {

            mMemory.remove( url );
      }

      /**
       * 仅从本地文件读取
       *
       * @param url mUrl
       *
       * @return bitmap or null
       */
      public Bitmap loadFromFile ( String url ) {

            File file = mDowner.getFile( url );
            if( file != null ) {

                  Bitmap bitmap = mBitmapConverter.read( file );

                  if( bitmap != null ) {
                        mMemory.save( url, bitmap );
                        return bitmap;
                  }
            }
            return null;
      }

      public void removeFile ( String url ) {

            mDowner.removeFile( url );
      }

      /**
       * get mUrl file
       *
       * @param url mUrl
       *
       * @return file or null
       */
      public File getFile ( String url ) {

            return mDowner.getFile( url );
      }

      /**
       * file dir
       *
       * @return dir
       */
      public File getDir ( ) {

            return mDowner.getDir();
      }

      /**
       * 仅从网络读取
       *
       * @param url mUrl
       *
       * @return bitmap or null
       */
      public Bitmap loadFromNet ( String url ) {

            File file = mDowner.load( url );
            if( file != null ) {

                  Bitmap bitmap = mBitmapConverter.read( file );
                  if( bitmap != null ) {
                        mMemory.save( url, bitmap );
                        return bitmap;
                  }
            }
            return null;
      }

      /**
       * 获取设置的下载进度监听
       *
       * @return 监听
       */
      public OnProgressUpdateListener getOnProgressUpdateListener ( ) {

            return mDowner.getOnProgressUpdateListener();
      }

      /**
       * 设置下载进度监听
       *
       * @param onProgressUpdateListener 监听
       */
      public void setOnProgressUpdateListener (
          OnProgressUpdateListener onProgressUpdateListener ) {

            mDowner.setOnProgressUpdateListener( onProgressUpdateListener );
      }

      /**
       * 获取设置的加载完成监听
       *
       * @return 监听
       */
      public OnLoadFinishedListener getOnLoadFinishedListener ( ) {

            return mOnLoadFinishedListener;
      }

      /**
       * 设置加载完成监听
       *
       * @param onLoadFinishedListener 加载完成监听
       */
      public void setOnLoadFinishedListener (
          OnLoadFinishedListener onLoadFinishedListener ) {

            mOnLoadFinishedListener = onLoadFinishedListener;
      }

      /**
       * 辅助类,辅助异步加载bitmap {@link #load(String)}
       */
      protected class AsyncLoadRunnable implements Runnable {

            private String mUrl;

            public AsyncLoadRunnable ( String url ) {

                  mUrl = url;
            }

            @Override
            public void run ( ) {

                  Bitmap fromFile = loadFromFile( mUrl );
                  if( fromFile == null ) {

                        Bitmap fromNet = loadFromNet( mUrl );
                        if( fromNet == null ) {
                              setResult( mUrl, null );
                        } else {
                              setResult( mUrl, fromNet );
                        }
                  } else {

                        setResult( mUrl, fromFile );
                  }
            }
      }

      /**
       * 用于{@link #load(String)}加载任务完成后回调
       */
      public interface OnLoadFinishedListener {

            /**
             * 当{@link #load(String)}完成时回调该方法
             *
             * @param url bitmap url
             * @param bitmap bitmap
             */
            void onFinished ( String url, Bitmap bitmap );
      }
}
