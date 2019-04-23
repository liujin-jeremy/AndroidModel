package tech.liujin.model.cache.bitmap;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import java.io.File;
import tech.threekilogram.model.cache.CacheLoader;
import tech.threekilogram.model.converter.BitmapConverter;
import tech.threekilogram.model.memory.lru.MemoryBitmap;
import tech.threekilogram.model.net.DownLoader;

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
      protected DownLoader           mDowner;
      /**
       * bitmap 转换
       */
      protected BitmapConverter      mBitmapConverter;

      /**
       * 创建一个bitmap加载器
       *
       * @param maxMemorySize 内存最大缓存数量
       * @param cacheDir 缓存文件夹
       */
      public BitmapLoader ( int maxMemorySize, File cacheDir ) {

            mMemory = new MemoryBitmap<>( maxMemorySize );
            mDowner = new DownLoader( cacheDir );
            mBitmapConverter = new BitmapConverter();
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
            File file = mDowner.down( url );
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
       * 从网络读取该url对应的图片,并缓存到内存中,不同于{@link #loadFromNet(String)}使用{@link Config#ARGB_8888}格式
       *
       * @param url 图片url
       *
       * @return bitmap or null
       */
      public Bitmap loadSrcFromNet ( String url ) {

            /* 先下载到本地 */
            File file = mDowner.down( url );
            /* 解析成bitmap */
            if( file != null && file.exists() ) {

                  Bitmap bitmap = mBitmapConverter.from( file, Config.ARGB_8888 );
                  if( bitmap != null ) {
                        saveToMemory( url, bitmap );
                        return bitmap;
                  }
            }
            return null;
      }

      /**
       * 从网络读取该url对应的图片,并缓存到内存中,缩放至指定尺寸
       *
       * @param url 图片url
       *
       * @return bitmap or null
       */
      public Bitmap loadFromNet ( String url, int width, int height ) {

            return loadFromNet( url, width, height, Config.RGB_565 );
      }

      /**
       * 从网络读取该url对应的图片,并缓存到内存中,缩放至指定尺寸,指定格式
       *
       * @param url 图片url
       *
       * @return bitmap or null
       */
      public Bitmap loadFromNet ( String url, int width, int height, Config config ) {

            /* 先下载到本地 */
            File file = mDowner.down( url );
            /* 解析成bitmap */
            if( file != null && file.exists() ) {

                  Bitmap bitmap = mBitmapConverter.from( file, width, height, config );
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
      public File download ( String url ) {

            File file = getFile( url );
            if( file != null && file.exists() ) {
                  return file;
            }
            return mDowner.down( url );
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
       * get file
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

            File file = getFile( url );
            if( file != null && file.exists() ) {

                  Bitmap bitmap = mBitmapConverter.from( file );
                  if( bitmap != null ) {
                        mMemory.save( url, bitmap );
                        return bitmap;
                  }
            }

            return null;
      }

      /**
       * 从本地文件读取,不同于{@link #loadFromFile(String)}使用{@link Config#ARGB_8888}格式
       *
       * @param url mUrl
       *
       * @return bitmap or null
       */
      public Bitmap loadSrcFromFile ( String url ) {

            File file = getFile( url );
            if( file != null && file.exists() ) {

                  Bitmap bitmap = mBitmapConverter.from( file, Config.ARGB_8888 );
                  if( bitmap != null ) {
                        mMemory.save( url, bitmap );
                        return bitmap;
                  }
            }

            return null;
      }

      /**
       * 从本地文件读取,并且缩放至匹配尺寸,使用的是{@link Bitmap.Config#RGB_565}格式
       *
       * @param url mUrl
       *
       * @return bitmap or null
       */
      public Bitmap loadFromFile ( String url, int width, int height ) {

            return loadFromFile( url, width, height, Config.RGB_565 );
      }

      /**
       * 从本地文件读取,并且缩放至匹配尺寸
       *
       * @param url mUrl
       *
       * @return bitmap or null
       */
      public Bitmap loadFromFile ( String url, int width, int height, Config config ) {

            File file = getFile( url );
            if( file != null && file.exists() ) {

                  Bitmap bitmap = mBitmapConverter.from( file, width, height, config );
                  if( bitmap != null ) {
                        mMemory.save( url, bitmap );
                        return bitmap;
                  }
            }

            return null;
      }

      @Override
      public void clearFile ( ) {

            mDowner.clear();
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

      /**
       * 加载url对应的原图
       *
       * @param url url
       */
      @Override
      public Bitmap load ( String url ) {

            Bitmap bitmap = loadFromMemory( url );
            if( bitmap != null ) {
                  return bitmap;
            }

            return loadFromFile( url );
      }

      /**
       * 加载url对应的原图,不同于{@link #load(String)}使用{@link Config#ARGB_8888}格式
       *
       * @param url url
       */
      public Bitmap loadSrc ( String url ) {

            Bitmap bitmap = loadFromMemory( url );
            if( bitmap != null ) {
                  return bitmap;
            }

            return loadSrcFromFile( url );
      }

      /**
       * 加载URL对应的图片并且缩放至指定尺寸
       */
      public Bitmap load ( String url, int width, int height ) {

            return load( url, width, height, Config.RGB_565 );
      }

      /**
       * 加载URL对应的图片并且缩放至指定尺寸,指定格式
       */
      public Bitmap load ( String url, int width, int height, Config config ) {

            Bitmap bitmap = loadFromMemory( url );
            if( bitmap != null ) {

                  if( bitmap.getWidth() < width && bitmap.getHeight() < height ) {
                        return loadFromFile( url, width, height, config );
                  }
                  if( bitmap.getConfig() != config ) {
                        return loadFromFile( url, width, height, config );
                  }
                  return bitmap;
            }

            return loadFromFile( url, width, height, config );
      }
}
