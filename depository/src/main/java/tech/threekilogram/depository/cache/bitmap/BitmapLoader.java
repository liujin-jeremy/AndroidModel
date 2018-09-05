package tech.threekilogram.depository.cache.bitmap;

import static tech.threekilogram.depository.cache.bitmap.BitmapConverter.MATCH_SIZE;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import java.io.File;
import java.io.IOException;
import tech.threekilogram.depository.CacheLoader;
import tech.threekilogram.depository.cache.bitmap.BitmapConverter.ScaleMode;
import tech.threekilogram.depository.memory.lru.MemoryBitmap;
import tech.threekilogram.depository.net.retrofit.down.RetrofitDowner;

/**
 * 该类提供bitmap三级缓存
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
       * 下载bitmap
       */
      protected RetrofitDowner       mDowner;
      /**
       * bitmap 转换
       */
      protected BitmapConverter      mBitmapConverter;

      /**
       * 创建一个bitmap加载器
       *
       * @param maxMemorySize 内存最大缓存数量
       * @param cacheDir 缓存文件夹,网络图片必须线下再到本地之后在解析成bitmap
       */
      public BitmapLoader ( int maxMemorySize, File cacheDir ) {

            mMemory = new MemoryBitmap<>( maxMemorySize );
            mDowner = new RetrofitDowner( cacheDir );
            mBitmapConverter = new BitmapConverter();
      }

      /**
       * 创建一个bitmap加载器,文件缓存使用{@link com.jakewharton.disklrucache.DiskLruCache}
       *
       * @param maxMemorySize 内存最大可作为缓存大小
       * @param cacheDir 缓存文件夹
       * @param maxFileSize 缓存文件夹大小
       */
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
       * 从网络读取该url对应的图片,并缓存到内存中
       *
       * @param url 图片url
       *
       * @return bitmap or null
       */
      @Override
      public Bitmap loadFromNet ( String url ) {

            /* 先下载到本地 */
            File file = mDowner.load( url );
            /* 解析成bitmap */
            if( file != null && file.exists() ) {

                  Bitmap bitmap = mBitmapConverter.from( file );
                  if( bitmap != null ) {
                        saveToMemory( url, bitmap );
                        return bitmap;
                  }
            }
            return null;
      }

      /**
       * 仅从网络下载图片文件,之后可以使用{@link #getFile(String)}获取改文件
       *
       * @param url 图片url
       */
      @Override
      public void download ( String url ) {

            File file = getFile( url );
            if( file != null && file.exists() ) {
                  return;
            }
            mDowner.load( url );
      }

      @Override
      public Bitmap loadFromDownload ( String url ) {

            download( url );
            File file = getFile( url );
            if( file.exists() ) {

                  return loadFromFile( url );
            }
            return null;
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

      @Override
      public int memorySize ( ) {

            return mMemory.size();
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
      public void saveToMemory ( String url, Bitmap bitmap ) {

            mMemory.save( url, bitmap );
      }

      @Override
      public boolean containsOfMemory ( String url ) {

            return mMemory.containsOf( url );
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
      @Override
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
      public boolean containsOfFile ( String url ) {

            return getFile( url ).exists();
      }

      @Override
      public void saveToFile ( String url, Bitmap bitmap ) {

            File file = getFile( url );
            mBitmapConverter.to( file, bitmap );
      }

      @Override
      public void removeFromFile ( String url ) {

            boolean delete = getFile( url ).delete();
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

                  Bitmap bitmap = mBitmapConverter.from( file );
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
      public boolean containsOf ( String url ) {

            return containsOfMemory( url ) || containsOfFile( url );
      }

      @Override
      public void save ( String url, Bitmap bitmap ) {

            saveToMemory( url, bitmap );
            saveToFile( url, bitmap );
      }

      @Override
      public Bitmap load ( String url ) {

            Bitmap bitmap = loadFromMemory( url );
            if( bitmap != null ) {
                  return bitmap;
            }

            return loadFromFile( url );
      }

      /**
       * 获取设置的宽度
       */
      public int getConfigWidth ( ) {

            return mBitmapConverter.getWidth();
      }

      /**
       * 获取设置的高度
       */
      public int getConfigHeight ( ) {

            return mBitmapConverter.getHeight();
      }

      /**
       * 获取设置的加载模式
       */
      public int getConfigMode ( ) {

            return mBitmapConverter.getMode();
      }

      /**
       * 获取设置的bitmap像素格式
       */
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
}
