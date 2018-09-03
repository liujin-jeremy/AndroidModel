package tech.threekilogram.depository.bitmap;

import static tech.threekilogram.depository.bitmap.BitmapConverter.MATCH_SIZE;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import java.io.File;
import java.io.IOException;
import tech.threekilogram.depository.CacheLoader;
import tech.threekilogram.depository.bitmap.BitmapConverter.ScaleMode;
import tech.threekilogram.depository.memory.lru.MemoryBitmap;
import tech.threekilogram.depository.net.retrofit.loader.RetrofitDowner;
import tech.threekilogram.depository.net.retrofit.loader.RetrofitDowner.OnProgressUpdateListener;

/**
 * 缓存bitmap对象
 *
 * @author: Liujin
 * @version: V1.0
 * @date: 2018-08-16
 * @time: 21:44
 */
@SuppressWarnings("WeakerAccess")
public class BitmapLoader implements CacheLoader<Bitmap> {

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

            mBitmapConverter.configBitmap( width, height, scaleMode, config );
      }

      /**
       * 配置图片保存方式
       *
       * @param compressFormat 保存格式
       * @param compressQuality 保存质量
       */
      public void configCompress ( CompressFormat compressFormat, int compressQuality ) {

            mBitmapConverter.configCompress( compressFormat, compressQuality );
      }

      /**
       * 仅从网络读取
       *
       * @param url mUrl
       *
       * @return bitmap or null
       */
      @Override
      public Bitmap loadFromNet ( String url ) {

            File file = mDowner.load( url );
            if( file != null && file.exists() ) {

                  Bitmap bitmap = mBitmapConverter.read( file );
                  if( bitmap != null ) {
                        saveToMemory( url, bitmap );
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

            File file = getFile( url );
            if( file.exists() ) {
                  return;
            }
            mDowner.load( url );
      }

      /**
       * 仅从内存读取
       *
       * @param url mUrl
       *
       * @return bitmap or null
       */
      @Override
      public Bitmap loadFromMemory ( String url ) {

            return mMemory.load( url );
      }

      /**
       * 删除内存中对应bitmap
       *
       * @param url url
       */
      @Override
      public Bitmap removeFromMemory ( String url ) {

            return mMemory.remove( url );
      }

      @Override
      public void saveToMemory ( String key, Bitmap bitmap ) {

            mMemory.save( key, bitmap );
      }

      @Override
      public boolean containsOfMemory ( String key ) {

            return mMemory.containsOf( key );
      }

      /**
       * @return 当前使用内存大小
       */
      @Override
      public int memorySize ( ) {

            return mMemory.size();
      }

      /**
       * 清空内存
       */
      @Override
      public void clearMemory ( ) {

            mMemory.clear();
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

      @Override
      public boolean containsOfFile ( String key ) {

            return getFile( key ).exists();
      }

      @Override
      public void saveToFile ( String key, Bitmap bitmap ) {

            File file = getFile( key );
            mBitmapConverter.write( file, bitmap );
      }

      @Override
      public void removeFromFile ( String key ) {

            boolean delete = getFile( key ).delete();
      }

      /**
       * 从本地文件读取
       *
       * @param url mUrl
       *
       * @return bitmap or null
       */
      @Override
      public Bitmap loadFromFile ( String url ) {

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

      @Override
      public void clearFile ( ) {

            mDowner.clearFile();
      }

      @Override
      public boolean containsOf ( String key ) {

            return containsOfMemory( key ) || containsOfFile( key );
      }

      @Override
      public void save ( String key, Bitmap bitmap ) {

            saveToMemory( key, bitmap );
            saveToFile( key, bitmap );
      }

      @Override
      public Bitmap load ( String key ) {

            Bitmap bitmap = loadFromMemory( key );
            if( bitmap != null ) {
                  return bitmap;
            }

            return loadFromFile( key );
      }

      public int getConfigWidth ( ) {

            return mBitmapConverter.getWidth();
      }

      public int getConfigHeight ( ) {

            return mBitmapConverter.getHeight();
      }

      public int getConfigMode ( ) {

            return mBitmapConverter.getMode();
      }

      public Config getBitmapConfig ( ) {

            return mBitmapConverter.getBitmapConfig();
      }

      /**
       * 获取设置的图片保存质量
       */
      public int getCompressQuality ( ) {

            return mBitmapConverter.getCompressQuality();
      }

      /**
       * 获取设置的图片保存格式
       */
      public CompressFormat getCompressFormat ( ) {

            return mBitmapConverter.getCompressFormat();
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
