package tech.threekilogram.model.cache.bitmap;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.support.annotation.DrawableRes;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.IOException;
import java.io.InputStream;

/**
 * 按照配置读取bitmap
 *
 * @author wuxio
 * @date 2017-05-30
 */
public class BitmapReader {

      /**
       * 读取原图,使用RGB_565格式
       *
       * @param context context
       * @param id 资源ID
       *
       * @return 资源对应bitmap
       */
      public static Bitmap readRgb ( Context context, @DrawableRes int id ) {

            final Options options = new Options();
            options.inPreferredConfig = Config.RGB_565;

            return BitmapFactory.decodeResource( context.getResources(), id, options );
      }

      /**
       * 读取原图,使用RGB_565格式
       *
       * @param file bitmap file
       *
       * @return 资源对应bitmap
       */
      public static Bitmap readRgb ( File file ) {

            final Options options = new Options();
            options.inPreferredConfig = Config.RGB_565;

            return BitmapFactory.decodeFile( file.getAbsolutePath(), options );
      }

      /**
       * 读取原图,使用RGB_565格式
       *
       * @param stream stream
       *
       * @return 资源对应bitmap
       */
      public static Bitmap readRgb ( InputStream stream ) {

            final Options options = new Options();
            options.inPreferredConfig = Config.RGB_565;

            return BitmapFactory.decodeStream( stream, null, options );
      }

      /**
       * 读取原图,使用RGB_565格式
       *
       * @param fileDescriptor :fileDescriptor
       *
       * @return 资源对应bitmap
       */
      public static Bitmap readRgb ( FileDescriptor fileDescriptor ) {

            final Options options = new Options();
            options.inPreferredConfig = Config.RGB_565;

            return BitmapFactory.decodeFileDescriptor( fileDescriptor, null, options );
      }

      /**
       * 读取原图,使用ARGB_8888格式
       *
       * @param context context
       * @param id 资源ID
       *
       * @return 资源对应bitmap
       */
      public static Bitmap readArgb ( Context context, @DrawableRes int id ) {

            return BitmapFactory.decodeResource( context.getResources(), id );
      }

      /**
       * 读取原图,使用ARGB_8888格式
       *
       * @param file bitmap file
       *
       * @return 资源对应bitmap
       */
      public static Bitmap readArgb ( File file ) {

            return BitmapFactory.decodeFile( file.getAbsolutePath() );
      }

      /**
       * 读取原图,使用ARGB_8888格式
       *
       * @param stream stream
       *
       * @return 资源对应bitmap
       */
      public static Bitmap readArgb ( InputStream stream ) {

            return BitmapFactory.decodeStream( stream, null, null );
      }

      /**
       * 读取原图,使用ARGB_8888格式
       *
       * @param fileDescriptor fileDescriptor
       *
       * @return 资源对应bitmap
       */
      public static Bitmap readArgb ( FileDescriptor fileDescriptor ) {

            return BitmapFactory.decodeFileDescriptor( fileDescriptor, null, null );
      }

      /**
       * 计算图片缩放比例
       */
      private static int calculateInSampleSize (
          Options options, int reqWidth, int reqHeight ) {

            if( reqWidth == 0 || reqHeight == 0 ) {
                  return 1;
            }
            final int height = options.outHeight;
            final int width = options.outWidth;
            int inSampleSize = 1;

            //如果图片宽或者高 大于 view的宽高
            if( height > reqHeight || width > reqWidth ) {

                  //计算图片的宽高的一半
                  final int halfHeight = height / 2;
                  final int halfWidth = width / 2;

                  //如果原始图片宽高是 view宽高的 2,4,8,16 ... 倍以上,会压缩图片至宽高任意一条边略大于 view 的 宽高为止,图片质量不会太差
                  while( ( halfHeight / inSampleSize ) >= reqHeight
                      && ( halfWidth / inSampleSize ) >= reqWidth ) {
                        inSampleSize *= 2;
                  }
            }
            return inSampleSize;
      }

      /**
       * 计算图片极限缩放比例,使用宽高采样率中最大的
       */
      private static int calculateMaxInSampleSize (
          Options options, int reqWidth, int reqHeight ) {

            if( reqWidth == 0 || reqHeight == 0 ) {
                  return 1;
            }

            final int height = options.outHeight;
            final int width = options.outWidth;
            int inSampleSize = 1;

            //如果图片宽或者高 大于 view的宽高
            if( height > reqHeight || width > reqWidth ) {

                  //分别计算宽高采样率,使用其中最大的值
                  while( ( height / inSampleSize ) >= reqHeight ) {
                        inSampleSize *= 2;
                  }

                  int heightSampleSize = inSampleSize;

                  while( ( width / inSampleSize ) >= reqWidth ) {
                        inSampleSize *= 2;
                  }

                  inSampleSize = heightSampleSize > inSampleSize ? heightSampleSize : inSampleSize;
            }
            return inSampleSize;
      }

      /**
       * 压缩图片至 宽度/高度 任一小于要求的 宽度/高度
       *
       * @param context context
       * @param resId 资源id
       * @param reqWidth 期望宽度
       * @param reqHeight 期望高度
       *
       * @return 压缩后图片
       */
      public static Bitmap sampledBitmap (
          Context context,
          @DrawableRes int resId,
          int reqWidth,
          int reqHeight ) {

            return sampledBitmap( context, resId, reqWidth, reqHeight, Config.RGB_565 );
      }

      /**
       * 压缩图片至 宽度/高度 任一小于要求的 宽度/高度
       *
       * @param context context
       * @param resId 资源id
       * @param reqWidth 期望宽度
       * @param reqHeight 期望高度
       * @param config 图片像素配置
       *
       * @return 压缩后图片
       */
      public static Bitmap sampledBitmap (
          Context context,
          @DrawableRes int resId,
          int reqWidth,
          int reqHeight,
          Config config ) {

            // 第一次解析将inJustDecodeBounds设置为true，来获取图片大小
            final Options options = new Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeResource( context.getResources(), resId, options );

            // 调用下面定义的方法计算inSampleSize值
            options.inSampleSize = calculateInSampleSize( options, reqWidth, reqHeight );
            // 使用获取到的inSampleSize值再次解析图片
            options.inJustDecodeBounds = false;

            options.inPreferredConfig = config;

            return BitmapFactory.decodeResource( context.getResources(), resId, options );
      }

      /**
       * 压缩图片至 宽度/高度 任一小于要求的 宽度/高度
       *
       * @param file 本地文件
       * @param reqWidth 期望宽度
       * @param reqHeight 期望高度
       *
       * @return 压缩后图片
       */
      public static Bitmap sampledBitmap ( File file, int reqWidth, int reqHeight ) {

            return sampledBitmap( file, reqWidth, reqHeight, Config.RGB_565 );
      }

      /**
       * 压缩图片至 宽度/高度 任一小于要求的 宽度/高度
       *
       * @param file 本地文件
       * @param reqWidth 期望宽度
       * @param reqHeight 期望高度
       * @param config 图片像素配置
       *
       * @return 压缩后图片
       */
      public static Bitmap sampledBitmap (
          File file,
          int reqWidth,
          int reqHeight,
          Config config ) {

            String path = file.getPath();

            // 第一次解析将inJustDecodeBounds设置为true，来获取图片大小
            final Options options = new Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile( path, options );

            // 调用下面定义的方法计算inSampleSize值
            options.inSampleSize = calculateInSampleSize( options, reqWidth, reqHeight );

            // 使用获取到的inSampleSize值再次解析图片
            options.inJustDecodeBounds = false;

            // 配置格式
            options.inPreferredConfig = config;

            return BitmapFactory.decodeFile( path, options );
      }

      /**
       * 压缩图片至 宽度/高度 任一小于要求的 宽度/高度
       *
       * @param stream stream
       * @param reqWidth 期望宽度
       * @param reqHeight 期望高度
       *
       * @return 压缩后图片
       */
      public static Bitmap sampledBitmap ( InputStream stream, int reqWidth, int reqHeight ) {

            return sampledBitmap( stream, reqWidth, reqHeight, Config.RGB_565 );
      }

      /**
       * 压缩图片至 宽度/高度 任一小于要求的 宽度/高度
       *
       * @param stream stream
       * @param reqWidth 期望宽度
       * @param reqHeight 期望高度
       * @param config 图片像素配置
       *
       * @return 压缩后图片
       */
      public static Bitmap sampledBitmap (
          InputStream stream,
          int reqWidth,
          int reqHeight,
          Config config ) {

            BufferedInputStream inputStream = new BufferedInputStream( stream, 64 );
            inputStream.mark( 1024 );

            // 第一次解析将inJustDecodeBounds设置为true，来获取图片大小
            final Options options = new Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeStream( inputStream, null, options );

            // 调用下面定义的方法计算inSampleSize值
            options.inSampleSize = calculateInSampleSize( options, reqWidth, reqHeight );

            // 使用获取到的inSampleSize值再次解析图片
            options.inJustDecodeBounds = false;
            // 配置格式
            options.inPreferredConfig = config;

            try {
                  inputStream.reset();
                  return BitmapFactory.decodeStream( inputStream, null, options );
            } catch(IOException e) {

                  try {
                        inputStream.close();
                  } catch(IOException e1) {
                        e1.printStackTrace();
                  }
            }

            return null;
      }

      /**
       * 压缩图片至 宽度/高度 任一小于要求的 宽度/高度
       *
       * @param fileDescriptor 本地文件
       * @param reqWidth 期望宽度
       * @param reqHeight 期望高度
       *
       * @return 压缩后图片
       */
      public static Bitmap sampledBitmap (
          FileDescriptor fileDescriptor, int reqWidth, int reqHeight ) {

            return sampledBitmap( fileDescriptor, reqWidth, reqHeight, Config.RGB_565 );
      }

      /**
       * 压缩图片至 宽度/高度 任一小于要求的 宽度/高度
       *
       * @param fileDescriptor 本地文件
       * @param reqWidth 期望宽度
       * @param reqHeight 期望高度
       * @param config 图片像素配置
       *
       * @return 压缩后图片
       */
      public static Bitmap sampledBitmap (
          FileDescriptor fileDescriptor,
          int reqWidth,
          int reqHeight,
          Config config ) {

            // 第一次解析将inJustDecodeBounds设置为true，来获取图片大小
            final Options options = new Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFileDescriptor( fileDescriptor, null, options );

            // 调用下面定义的方法计算inSampleSize值
            options.inSampleSize = calculateInSampleSize( options, reqWidth, reqHeight );

            // 使用获取到的inSampleSize值再次解析图片
            options.inJustDecodeBounds = false;

            // 配置格式
            options.inPreferredConfig = config;

            return BitmapFactory.decodeFileDescriptor( fileDescriptor, null, options );
      }

      /**
       * 压缩图片至 宽度/高度 全部小于要求的 宽度/高度
       *
       * @param context context
       * @param resId 资源id
       * @param reqWidth 期望宽度
       * @param reqHeight 期望高度
       *
       * @return 压缩后图片
       */
      public static Bitmap maxSampledBitmap (
          Context context,
          @DrawableRes int resId,
          int reqWidth,
          int reqHeight ) {

            return maxSampledBitmap( context, resId, reqWidth, reqHeight, Config.RGB_565 );
      }

      /**
       * 压缩图片至 宽度/高度 全部小于要求的 宽度/高度
       *
       * @param context context
       * @param resId 资源id
       * @param reqWidth 期望宽度
       * @param reqHeight 期望高度
       * @param config 图片像素配置
       *
       * @return 压缩后图片
       */
      public static Bitmap maxSampledBitmap (
          Context context,
          @DrawableRes int resId,
          int reqWidth,
          int reqHeight,
          Config config ) {

            // 第一次解析将inJustDecodeBounds设置为true，来获取图片大小
            final Options options = new Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeResource( context.getResources(), resId, options );

            // 调用下面定义的方法计算inSampleSize值
            options.inSampleSize = calculateMaxInSampleSize( options, reqWidth, reqHeight );
            // 使用获取到的inSampleSize值再次解析图片
            options.inJustDecodeBounds = false;
            options.inPreferredConfig = config;

            return BitmapFactory.decodeResource( context.getResources(), resId, options );
      }

      /**
       * 压缩图片至 宽度/高度 全部小于要求的 宽度/高度
       *
       * @param file 本地文件
       * @param reqWidth 期望宽度
       * @param reqHeight 期望高度
       *
       * @return 压缩后图片
       */
      public static Bitmap maxSampledBitmap ( File file, int reqWidth, int reqHeight ) {

            return maxSampledBitmap( file, reqWidth, reqHeight, Config.RGB_565 );
      }

      /**
       * 压缩图片至 宽度/高度 全部小于要求的 宽度/高度
       *
       * @param file 本地文件
       * @param reqWidth 期望宽度
       * @param reqHeight 期望高度
       * @param config 图片像素配置
       *
       * @return 压缩后图片
       */
      public static Bitmap maxSampledBitmap (
          File file,
          int reqWidth,
          int reqHeight,
          Config config ) {

            String path = file.getPath();

            // 第一次解析将inJustDecodeBounds设置为true，来获取图片大小
            final Options options = new Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile( path, options );

            // 调用下面定义的方法计算inSampleSize值
            options.inSampleSize = calculateMaxInSampleSize( options, reqWidth, reqHeight );

            // 使用获取到的inSampleSize值再次解析图片
            options.inJustDecodeBounds = false;

            // 配置格式
            options.inPreferredConfig = config;

            return BitmapFactory.decodeFile( path, options );
      }

      /**
       * 压缩图片至 宽度/高度 全部小于要求的 宽度/高度
       *
       * @param fileDescriptor 本地文件
       * @param reqWidth 期望宽度
       * @param reqHeight 期望高度
       *
       * @return 压缩后图片
       */
      public static Bitmap maxSampledBitmap (
          FileDescriptor fileDescriptor, int reqWidth, int reqHeight ) {

            return maxSampledBitmap( fileDescriptor, reqWidth, reqHeight, Config.RGB_565 );
      }

      /**
       * 压缩图片至 宽度/高度 全部小于要求的 宽度/高度
       *
       * @param fileDescriptor 本地文件
       * @param reqWidth 期望宽度
       * @param reqHeight 期望高度
       * @param config 图片像素配置
       *
       * @return 压缩后图片
       */
      public static Bitmap maxSampledBitmap (
          FileDescriptor fileDescriptor,
          int reqWidth,
          int reqHeight,
          Config config ) {

            // 第一次解析将inJustDecodeBounds设置为true，来获取图片大小
            final Options options = new Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFileDescriptor( fileDescriptor, null, options );

            // 调用下面定义的方法计算inSampleSize值
            options.inSampleSize = calculateMaxInSampleSize( options, reqWidth, reqHeight );

            // 使用获取到的inSampleSize值再次解析图片
            options.inJustDecodeBounds = false;

            // 配置格式
            options.inPreferredConfig = config;

            return BitmapFactory.decodeFileDescriptor( fileDescriptor, null, options );
      }

      /**
       * 压缩图片至 宽度/高度 全部小于要求的 宽度/高度
       *
       * @param stream stream
       * @param reqWidth 期望宽度
       * @param reqHeight 期望高度
       *
       * @return 压缩后图片
       */
      public static Bitmap maxSampledBitmap ( InputStream stream, int reqWidth, int reqHeight ) {

            return maxSampledBitmap( stream, reqWidth, reqHeight, Config.RGB_565 );
      }

      /**
       * 压缩图片至 宽度/高度 全部小于要求的 宽度/高度
       *
       * @param stream stream
       * @param reqWidth 期望宽度
       * @param reqHeight 期望高度
       * @param config 图片像素配置
       *
       * @return 压缩后图片
       */
      public static Bitmap maxSampledBitmap (
          InputStream stream,
          int reqWidth,
          int reqHeight,
          Config config ) {

            BufferedInputStream inputStream = new BufferedInputStream( stream, 64 );
            inputStream.mark( 1024 );

            // 第一次解析将inJustDecodeBounds设置为true，来获取图片大小
            final Options options = new Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeStream( inputStream, null, options );

            // 调用下面定义的方法计算inSampleSize值
            options.inSampleSize = calculateMaxInSampleSize( options, reqWidth, reqHeight );

            // 使用获取到的inSampleSize值再次解析图片
            options.inJustDecodeBounds = false;
            // 配置格式
            options.inPreferredConfig = config;

            try {
                  inputStream.reset();
                  return BitmapFactory.decodeStream( inputStream, null, options );
            } catch(IOException e) {

                  try {
                        inputStream.close();
                  } catch(IOException e1) {
                        e1.printStackTrace();
                  }
            }

            return null;
      }

      /**
       * 解析一个图片资源到匹配设定的尺寸,即:宽度或者高度全部满足设定的要求
       *
       * @param context 提供resource
       * @param id 资源id
       * @param widthSize 要求的宽度
       * @param heightSize 要求的高度
       *
       * @return bitmap
       */
      public static Bitmap matchSize (
          Context context,
          @DrawableRes int id,
          int widthSize,
          int heightSize ) {

            return matchSize( context, id, widthSize, heightSize, Config.RGB_565 );
      }

      /**
       * 解析一个图片资源到匹配设定的尺寸,即:宽度或者高度全部满足设定的要求
       *
       * @param context 提供resource
       * @param id 资源id
       * @param widthSize 要求的宽度
       * @param heightSize 要求的高度
       * @param config 图片像素配置
       *
       * @return bitmap
       */
      public static Bitmap matchSize (
          Context context,
          @DrawableRes int id,
          int widthSize,
          int heightSize,
          Config config ) {

            /* 只读取尺寸 */

            Options options = new Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeResource( context.getResources(), id, options );

            int outWidth = options.outWidth;
            int outHeight = options.outHeight;

            float fateWidth = outWidth * 1f / widthSize;
            float fateHeight = outHeight * 1f / heightSize;

            /* 根据比率选择缩放边 */

            if( fateHeight > fateWidth ) {

                  options.inDensity = outHeight;
                  options.inTargetDensity = heightSize;
            } else {

                  options.inDensity = outWidth;
                  options.inTargetDensity = widthSize;
            }

            /* 完成解析 */

            options.inScaled = true;
            options.inJustDecodeBounds = false;

            /* config */

            options.inPreferredConfig = config;

            return BitmapFactory.decodeResource( context.getResources(), id, options );
      }

      /**
       * 解析一个图片资源到匹配设定的尺寸,即:宽度或者高度全部满足设定的要求
       *
       * @param widthSize 要求的宽度
       * @param heightSize 要求的高度
       *
       * @return bitmap
       */
      public static Bitmap matchSize (
          File file,
          int widthSize,
          int heightSize ) {

            return matchSize( file, widthSize, heightSize, Config.RGB_565 );
      }

      /**
       * 解析一个图片资源到匹配设定的尺寸,即:宽度或者高度全部满足设定的要求
       *
       * @param file bitmap file
       * @param widthSize 要求的宽度
       * @param heightSize 要求的高度
       * @param config 图片像素配置
       *
       * @return bitmap
       */
      public static Bitmap matchSize (
          File file,
          int widthSize,
          int heightSize,
          Config config ) {

            Options options = new Options();
            options.inJustDecodeBounds = true;
            String absolutePath = file.getAbsolutePath();
            BitmapFactory.decodeFile( absolutePath, options );

            int outWidth = options.outWidth;
            int outHeight = options.outHeight;

            float fateWidth = outWidth * 1f / widthSize;
            float fateHeight = outHeight * 1f / heightSize;

            if( fateHeight > fateWidth ) {

                  options.inDensity = outHeight;
                  options.inTargetDensity = heightSize;
            } else {

                  options.inDensity = outWidth;
                  options.inTargetDensity = widthSize;
            }

            options.inScaled = true;
            options.inJustDecodeBounds = false;

            options.inPreferredConfig = config;

            return BitmapFactory.decodeFile( absolutePath, options );
      }

      /**
       * 解析一个图片资源到匹配设定的尺寸,即:宽度或者高度全部满足设定的要求
       *
       * @param widthSize 要求的宽度
       * @param heightSize 要求的高度
       *
       * @return bitmap
       */
      public static Bitmap matchSize (
          FileDescriptor fileDescriptor,
          int widthSize,
          int heightSize ) {

            return matchSize( fileDescriptor, widthSize, heightSize, Config.RGB_565 );
      }

      /**
       * 解析一个图片资源到匹配设定的尺寸,即:宽度或者高度全部满足设定的要求
       *
       * @param fileDescriptor bitmap fileDescriptor
       * @param widthSize 要求的宽度
       * @param heightSize 要求的高度
       * @param config 图片像素配置
       *
       * @return bitmap
       */
      public static Bitmap matchSize (
          FileDescriptor fileDescriptor,
          int widthSize,
          int heightSize,
          Config config ) {

            Options options = new Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFileDescriptor( fileDescriptor, null, options );

            int outWidth = options.outWidth;
            int outHeight = options.outHeight;

            float fateWidth = outWidth * 1f / widthSize;
            float fateHeight = outHeight * 1f / heightSize;

            if( fateHeight > fateWidth ) {

                  options.inDensity = outHeight;
                  options.inTargetDensity = heightSize;
            } else {

                  options.inDensity = outWidth;
                  options.inTargetDensity = widthSize;
            }

            options.inScaled = true;
            options.inJustDecodeBounds = false;

            options.inPreferredConfig = config;

            return BitmapFactory.decodeFileDescriptor( fileDescriptor, null, options );
      }

      /**
       * 解析一个图片资源到匹配设定的尺寸,即:宽度或者高度全部满足设定的要求
       *
       * @param stream stream
       * @param widthSize 要求的宽度
       * @param heightSize 要求的高度
       *
       * @return bitmap
       */
      public static Bitmap matchSize (
          InputStream stream,
          int widthSize,
          int heightSize ) {

            return matchSize( stream, widthSize, heightSize, Config.RGB_565 );
      }

      /**
       * 解析一个图片资源到匹配设定的尺寸,即:宽度或者高度全部满足设定的要求
       *
       * @param stream stream
       * @param widthSize 要求的宽度
       * @param heightSize 要求的高度
       * @param config 图片像素配置
       *
       * @return bitmap
       */
      public static Bitmap matchSize (
          InputStream stream,
          int widthSize,
          int heightSize,
          Config config ) {

            BufferedInputStream inputStream = new BufferedInputStream( stream, 64 );
            inputStream.mark( 1024 );

            // 第一次解析将inJustDecodeBounds设置为true，来获取图片大小
            final Options options = new Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeStream( inputStream, null, options );

            int outWidth = options.outWidth;
            int outHeight = options.outHeight;

            float fateWidth = outWidth * 1f / widthSize;
            float fateHeight = outHeight * 1f / heightSize;

            if( fateHeight > fateWidth ) {

                  options.inDensity = outHeight;
                  options.inTargetDensity = heightSize;
            } else {

                  options.inDensity = outWidth;
                  options.inTargetDensity = widthSize;
            }

            options.inScaled = true;
            options.inJustDecodeBounds = false;
            options.inPreferredConfig = config;

            try {
                  inputStream.reset();
                  return BitmapFactory.decodeStream( inputStream, null, options );
            } catch(IOException e) {

                  try {
                        inputStream.close();
                  } catch(IOException e1) {
                        e1.printStackTrace();
                  }
            }

            return null;
      }

      /**
       * 解析一个图片资源到匹配设定的尺寸,即:宽度或者高度任一满足设定的要求
       *
       * @param context 提供resource
       * @param id 资源id
       * @param widthSize 要求的宽度
       * @param heightSize 要求的高度
       *
       * @return bitmap
       */
      public static Bitmap matchSizeMost (
          Context context,
          @DrawableRes int id,
          int widthSize,
          int heightSize ) {

            return matchSizeMost( context, id, widthSize, heightSize, Config.RGB_565 );
      }

      /**
       * 解析一个图片资源到匹配设定的尺寸,即:宽度或者高度任一满足设定的要求
       *
       * @param context 提供resource
       * @param id 资源id
       * @param widthSize 要求的宽度
       * @param heightSize 要求的高度
       * @param config 图片像素配置
       *
       * @return bitmap
       */
      public static Bitmap matchSizeMost (
          Context context,
          @DrawableRes int id,
          int widthSize,
          int heightSize,
          Config config ) {

            /* 只读取尺寸 */

            Options options = new Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeResource( context.getResources(), id, options );

            int outWidth = options.outWidth;
            int outHeight = options.outHeight;

            float fateWidth = outWidth * 1f / widthSize;
            float fateHeight = outHeight * 1f / heightSize;

            /* 根据比率选择缩放边 */

            if( fateHeight < fateWidth ) {

                  options.inDensity = outHeight;
                  options.inTargetDensity = heightSize;
            } else {

                  options.inDensity = outWidth;
                  options.inTargetDensity = widthSize;
            }

            /* 完成解析 */

            options.inScaled = true;
            options.inJustDecodeBounds = false;

            /* config */

            options.inPreferredConfig = config;

            return BitmapFactory.decodeResource( context.getResources(), id, options );
      }

      /**
       * 解析一个图片资源到匹配设定的尺寸,即:宽度或者高度任一满足设定的要求
       *
       * @param widthSize 要求的宽度
       * @param heightSize 要求的高度
       *
       * @return bitmap
       */
      public static Bitmap matchSizeMost (
          File file,
          int widthSize,
          int heightSize ) {

            return matchSizeMost( file, widthSize, heightSize, Config.RGB_565 );
      }

      /**
       * 解析一个图片资源到匹配设定的尺寸,即:宽度或者高度任一满足设定的要求
       *
       * @param file bitmap file
       * @param widthSize 要求的宽度
       * @param heightSize 要求的高度
       * @param config 图片像素配置
       *
       * @return bitmap
       */
      public static Bitmap matchSizeMost (
          File file,
          int widthSize,
          int heightSize,
          Config config ) {

            Options options = new Options();
            options.inJustDecodeBounds = true;
            String absolutePath = file.getAbsolutePath();
            BitmapFactory.decodeFile( absolutePath, options );

            int outWidth = options.outWidth;
            int outHeight = options.outHeight;

            float fateWidth = outWidth * 1f / widthSize;
            float fateHeight = outHeight * 1f / heightSize;

            if( fateHeight < fateWidth ) {

                  options.inDensity = outHeight;
                  options.inTargetDensity = heightSize;
            } else {

                  options.inDensity = outWidth;
                  options.inTargetDensity = widthSize;
            }

            options.inScaled = true;
            options.inJustDecodeBounds = false;

            options.inPreferredConfig = config;

            return BitmapFactory.decodeFile( absolutePath, options );
      }

      /**
       * 解析一个图片资源到匹配设定的尺寸,即:宽度或者高度任一满足设定的要求
       *
       * @param widthSize 要求的宽度
       * @param heightSize 要求的高度
       *
       * @return bitmap
       */
      public static Bitmap matchSizeMost (
          FileDescriptor fileDescriptor,
          int widthSize,
          int heightSize ) {

            return matchSizeMost( fileDescriptor, widthSize, heightSize, Config.RGB_565 );
      }

      /**
       * 解析一个图片资源到匹配设定的尺寸,即:宽度或者高度任一满足设定的要求
       *
       * @param fileDescriptor bitmap fileDescriptor
       * @param widthSize 要求的宽度
       * @param heightSize 要求的高度
       * @param config 图片像素配置
       *
       * @return bitmap
       */
      public static Bitmap matchSizeMost (
          FileDescriptor fileDescriptor,
          int widthSize,
          int heightSize,
          Config config ) {

            Options options = new Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFileDescriptor( fileDescriptor, null, options );

            int outWidth = options.outWidth;
            int outHeight = options.outHeight;

            float fateWidth = outWidth * 1f / widthSize;
            float fateHeight = outHeight * 1f / heightSize;

            if( fateHeight < fateWidth ) {

                  options.inDensity = outHeight;
                  options.inTargetDensity = heightSize;
            } else {

                  options.inDensity = outWidth;
                  options.inTargetDensity = widthSize;
            }

            options.inScaled = true;
            options.inJustDecodeBounds = false;

            options.inPreferredConfig = config;

            return BitmapFactory.decodeFileDescriptor( fileDescriptor, null, options );
      }

      /**
       * 解析一个图片资源到匹配设定的尺寸,即:宽度或者高度任一满足设定的要求
       *
       * @param stream stream
       * @param widthSize 要求的宽度
       * @param heightSize 要求的高度
       *
       * @return bitmap
       */
      public static Bitmap matchSizeMost (
          InputStream stream,
          int widthSize,
          int heightSize ) {

            return matchSizeMost( stream, widthSize, heightSize, Config.RGB_565 );
      }

      /**
       * 解析一个图片资源到匹配设定的尺寸,即:宽度或者高度任一满足设定的要求
       *
       * @param stream stream
       * @param widthSize 要求的宽度
       * @param heightSize 要求的高度
       * @param config 图片像素配置
       *
       * @return bitmap
       */
      public static Bitmap matchSizeMost (
          InputStream stream,
          int widthSize,
          int heightSize,
          Config config ) {

            BufferedInputStream inputStream = new BufferedInputStream( stream, 64 );
            inputStream.mark( 1024 );

            // 第一次解析将inJustDecodeBounds设置为true，来获取图片大小
            final Options options = new Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeStream( inputStream, null, options );

            int outWidth = options.outWidth;
            int outHeight = options.outHeight;

            float fateWidth = outWidth * 1f / widthSize;
            float fateHeight = outHeight * 1f / heightSize;

            if( fateHeight < fateWidth ) {

                  options.inDensity = outHeight;
                  options.inTargetDensity = heightSize;
            } else {

                  options.inDensity = outWidth;
                  options.inTargetDensity = widthSize;
            }

            options.inScaled = true;
            options.inJustDecodeBounds = false;
            options.inPreferredConfig = config;

            try {
                  inputStream.reset();
                  return BitmapFactory.decodeStream( inputStream, null, options );
            } catch(IOException e) {

                  try {
                        inputStream.close();
                  } catch(IOException e1) {
                        e1.printStackTrace();
                  }
            }

            return null;
      }

      /**
       * 解析一个图片资源到匹配设定的尺寸,即满足宽度要求
       *
       * @param context 提供resource
       * @param id 资源id
       * @param widthSize 要求的宽度
       *
       * @return bitmap
       */
      public static Bitmap matchWidth (
          Context context,
          @DrawableRes int id,
          int widthSize ) {

            return matchWidth( context, id, widthSize, Config.RGB_565 );
      }

      /**
       * 解析一个图片资源到匹配设定的尺寸,即满足宽度要求
       *
       * @param context 提供resource
       * @param id 资源id
       * @param widthSize 要求的宽度
       * @param config 图片像素配置
       *
       * @return bitmap
       */
      public static Bitmap matchWidth (
          Context context,
          @DrawableRes int id,
          int widthSize,
          Config config ) {

            Options options = new Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeResource( context.getResources(), id, options );

            options.inDensity = options.outWidth;
            options.inTargetDensity = widthSize;

            options.inScaled = true;
            options.inJustDecodeBounds = false;

            options.inPreferredConfig = config;

            return BitmapFactory.decodeResource( context.getResources(), id, options );
      }

      /**
       * 解析一个图片资源到匹配设定的尺寸,即满足宽度要求
       *
       * @param widthSize 要求的宽度
       *
       * @return bitmap
       */
      public static Bitmap matchWidth (
          File file,
          int widthSize ) {

            return matchWidth( file, widthSize, Config.RGB_565 );
      }

      /**
       * 解析一个图片资源到匹配设定的尺寸,即满足宽度要求
       *
       * @param file bitmap file
       * @param widthSize 要求的宽度
       * @param config 图片像素配置
       *
       * @return bitmap
       */
      public static Bitmap matchWidth (
          File file,
          int widthSize,
          Config config ) {

            Options options = new Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile( file.getAbsolutePath(), options );

            options.inDensity = options.outWidth;
            options.inTargetDensity = widthSize;

            options.inScaled = true;
            options.inJustDecodeBounds = false;

            options.inPreferredConfig = config;

            return BitmapFactory.decodeFile( file.getAbsolutePath(), options );
      }

      /**
       * 解析一个图片资源到匹配设定的尺寸,即满足宽度要求
       *
       * @param widthSize 要求的宽度
       *
       * @return bitmap
       */
      public static Bitmap matchWidth (
          FileDescriptor fileDescriptor,
          int widthSize ) {

            return matchWidth( fileDescriptor, widthSize, Config.RGB_565 );
      }

      /**
       * 解析一个图片资源到匹配设定的尺寸,即满足宽度要求
       *
       * @param fileDescriptor bitmap fileDescriptor
       * @param widthSize 要求的宽度
       * @param config 图片像素配置
       *
       * @return bitmap
       */
      public static Bitmap matchWidth (
          FileDescriptor fileDescriptor,
          int widthSize,
          Config config ) {

            Options options = new Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFileDescriptor( fileDescriptor, null, options );

            options.inDensity = options.outWidth;
            options.inTargetDensity = widthSize;

            options.inScaled = true;
            options.inJustDecodeBounds = false;

            options.inPreferredConfig = config;

            return BitmapFactory.decodeFileDescriptor( fileDescriptor, null, options );
      }

      /**
       * 解析一个图片资源到匹配设定的尺寸,即满足宽度要求
       *
       * @param stream stream
       * @param widthSize 要求的宽度
       *
       * @return bitmap
       */
      public static Bitmap matchWidth (
          InputStream stream,
          int widthSize ) {

            return matchWidth( stream, widthSize, Config.RGB_565 );
      }

      /**
       * 解析一个图片资源到匹配设定的尺寸,即满足宽度要求
       *
       * @param stream stream
       * @param widthSize 要求的宽度
       * @param config 图片像素配置
       *
       * @return bitmap
       */
      public static Bitmap matchWidth (
          InputStream stream,
          int widthSize,
          Config config ) {

            BufferedInputStream inputStream = new BufferedInputStream( stream, 64 );
            inputStream.mark( 1024 );

            // 第一次解析将inJustDecodeBounds设置为true，来获取图片大小
            final Options options = new Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeStream( inputStream, null, options );

            options.inDensity = options.outWidth;
            options.inTargetDensity = widthSize;
            options.inScaled = true;
            options.inJustDecodeBounds = false;
            options.inPreferredConfig = config;

            try {
                  inputStream.reset();
                  return BitmapFactory.decodeStream( inputStream, null, options );
            } catch(IOException e) {

                  try {
                        inputStream.close();
                  } catch(IOException e1) {
                        e1.printStackTrace();
                  }
            }

            return null;
      }

      /**
       * 解析一个图片资源到匹配设定的尺寸,即满足高度要求
       *
       * @param context 提供resource
       * @param id 资源id
       * @param heightSize 要求的宽度
       *
       * @return bitmap
       */
      public static Bitmap matchHeight (
          Context context,
          @DrawableRes int id,
          int heightSize ) {

            return matchHeight( context, id, heightSize, Config.RGB_565 );
      }

      /**
       * 解析一个图片资源到匹配设定的尺寸,即满足高度要求
       *
       * @param context 提供resource
       * @param id 资源id
       * @param heightSize 要求的宽度
       * @param config 图片像素配置
       *
       * @return bitmap
       */
      public static Bitmap matchHeight (
          Context context,
          @DrawableRes int id,
          int heightSize,
          Config config ) {

            Options options = new Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeResource( context.getResources(), id, options );

            options.inDensity = options.outHeight;
            options.inTargetDensity = heightSize;

            options.inScaled = true;
            options.inJustDecodeBounds = false;

            options.inPreferredConfig = config;

            return BitmapFactory.decodeResource( context.getResources(), id, options );
      }

      /**
       * 解析一个图片资源到匹配设定的尺寸,即满足高度要求
       *
       * @param heightSize 要求的高度
       *
       * @return bitmap
       */
      public static Bitmap matchHeight (
          File file,
          int heightSize ) {

            return matchHeight( file, heightSize, Config.RGB_565 );
      }

      /**
       * 解析一个图片资源到匹配设定的尺寸,即满足高度要求
       *
       * @param file bitmap file
       * @param heightSize 要求的高度
       * @param config 图片像素配置
       *
       * @return bitmap
       */
      public static Bitmap matchHeight (
          File file,
          int heightSize,
          Config config ) {

            Options options = new Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile( file.getAbsolutePath(), options );

            options.inDensity = options.outHeight;
            options.inTargetDensity = heightSize;

            options.inScaled = true;
            options.inJustDecodeBounds = false;

            options.inPreferredConfig = config;

            return BitmapFactory.decodeFile( file.getAbsolutePath(), options );
      }

      /**
       * 解析一个图片资源到匹配设定的尺寸,即满足高度要求
       *
       * @param heightSize 要求的高度
       *
       * @return bitmap
       */
      public static Bitmap matchHeight (
          FileDescriptor fileDescriptor,
          int heightSize ) {

            return matchHeight( fileDescriptor, heightSize, Config.RGB_565 );
      }

      /**
       * 解析一个图片资源到匹配设定的尺寸,即满足高度要求
       *
       * @param fileDescriptor bitmap fileDescriptor
       * @param heightSize 要求的高度
       * @param config 图片像素配置
       *
       * @return bitmap
       */
      public static Bitmap matchHeight (
          FileDescriptor fileDescriptor,
          int heightSize,
          Config config ) {

            Options options = new Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFileDescriptor( fileDescriptor, null, options );

            options.inDensity = options.outHeight;
            options.inTargetDensity = heightSize;

            options.inScaled = true;
            options.inJustDecodeBounds = false;

            options.inPreferredConfig = config;

            return BitmapFactory.decodeFileDescriptor( fileDescriptor, null, options );
      }

      /**
       * 解析一个图片资源到匹配设定的尺寸,即满足高度要求
       *
       * @param stream stream
       * @param heightSize 要求的高度
       *
       * @return bitmap
       */
      public static Bitmap matchHeight (
          InputStream stream,
          int heightSize ) {

            return matchHeight( stream, heightSize, Config.RGB_565 );
      }

      /**
       * 解析一个图片资源到匹配设定的尺寸,即满足高度要求
       *
       * @param stream stream
       * @param heightSize 要求的高度
       * @param config 图片像素配置
       *
       * @return bitmap
       */
      public static Bitmap matchHeight (
          InputStream stream,
          int heightSize,
          Config config ) {

            BufferedInputStream inputStream = new BufferedInputStream( stream, 64 );
            inputStream.mark( 1024 );

            // 第一次解析将inJustDecodeBounds设置为true，来获取图片大小
            final Options options = new Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeStream( inputStream, null, options );

            options.inDensity = options.outHeight;
            options.inTargetDensity = heightSize;
            options.inScaled = true;
            options.inJustDecodeBounds = false;
            options.inPreferredConfig = config;

            try {
                  inputStream.reset();
                  return BitmapFactory.decodeStream( inputStream, null, options );
            } catch(IOException e) {

                  try {
                        inputStream.close();
                  } catch(IOException e1) {
                        e1.printStackTrace();
                  }
            }

            return null;
      }

      public static Bitmap clip ( Bitmap bitmap, int x, int y, int width, int height ) {

            return Bitmap.createBitmap( bitmap, x, y, width, height );
      }

      public static Bitmap clipStart ( Bitmap bitmap, int height ) {

            return Bitmap.createBitmap( bitmap, 0, 0, bitmap.getWidth(), height );
      }

      public static Bitmap clipEnd ( Bitmap bitmap, int height ) {

            return Bitmap.createBitmap(
                bitmap,
                0,
                bitmap.getHeight() - height,
                bitmap.getWidth(),
                height
            );
      }

      public static Bitmap clipLeft ( Bitmap bitmap, int width ) {

            return Bitmap.createBitmap( bitmap, 0, 0, width, bitmap.getHeight() );
      }

      public static Bitmap clipRight ( Bitmap bitmap, int width ) {

            return Bitmap
                .createBitmap( bitmap, bitmap.getWidth() - width, 0, width, bitmap.getHeight() );
      }

      public static Bitmap clipCenter ( Bitmap bitmap, int width, int height ) {

            int x = ( bitmap.getWidth() - width ) >> 1;
            int y = ( bitmap.getHeight() - height ) >> 1;

            return Bitmap
                .createBitmap( bitmap, x, y, width, height );
      }
}