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

/**
 * 缓存bitmap对象
 *
 * @author: Liujin
 * @version: V1.0
 * @date: 2018-08-16
 * @time: 21:44
 */
@SuppressWarnings("WeakerAccess")
public class BitmapLoader {

      /**
       * 内存缓存
       */
      protected MemoryBitmap<String> mMemory;
      /**
       * bitmap 转换
       */
      protected BitmapConverter      mBitmapConverter;
      /**
       * 下载
       */
      protected RetrofitDowner       mDowner;

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
       * 从文件网络加载该url对应的图片
       *
       * @param url 图片url
       */
      public Bitmap loadFileNet ( String url ) {

            Bitmap fromFile = loadFile( url );
            if( fromFile == null ) {

                  Bitmap fromNet = loadNet( url );
                  if( fromNet == null ) {
                        return null;
                  } else {
                        return fromNet;
                  }
            } else {

                  return fromFile;
            }
      }

      /**
       * 仅从内存读取
       *
       * @param url mUrl
       *
       * @return bitmap or null
       */
      public Bitmap loadMemory ( String url ) {

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

      /**
       * 删除内存中对应bitmap
       *
       * @param url url
       */
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
      public Bitmap loadFile ( String url ) {

            File file = mDowner.getFile( url );
            if( file != null && file.exists() ) {

                  Bitmap bitmap = mBitmapConverter.read( file );

                  if( bitmap != null ) {
                        mMemory.save( url, bitmap );
                        return bitmap;
                  }
            }
            return null;
      }

      /**
       * 删除对应bitmap文件
       *
       * @param url url
       */
      public void removeFile ( String url ) {

            mDowner.removeFile( url );
      }

      /**
       * get url file
       *
       * @param url mUrl
       *
       * @return file may not exist
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
      public Bitmap loadNet ( String url ) {

            File file = mDowner.load( url );
            if( file != null && file.exists() ) {

                  Bitmap bitmap = mBitmapConverter.read( file );
                  if( bitmap != null ) {
                        mMemory.save( url, bitmap );
                        return bitmap;
                  }
            }
            return null;
      }

      /**
       * 仅从网络下载文件,之后可以使用{@link #getFile(String)}获取改文件
       *
       * @param url 图片url
       */
      public void downLoad ( String url ) {

            mDowner.load( url );
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
}
