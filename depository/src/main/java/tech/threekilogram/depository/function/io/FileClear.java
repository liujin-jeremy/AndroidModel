package tech.threekilogram.depository.function.io;

import android.content.Context;
import java.io.File;
import java.util.Arrays;
import java.util.Comparator;

/**
 * @author: Liujin
 * @version: V1.0
 * @date: 2018-08-16
 * @time: 8:38
 */
public class FileClear {

      public static void clearExternalCache ( Context context ) {

            context = context.getApplicationContext();
            File externalCacheDir = context.getExternalCacheDir();
            clearFile( externalCacheDir );
      }

      public static void clearFile ( File file ) {

            if( file == null || !file.exists() ) {
                  return;
            }

            if( file.isFile() ) {

                  boolean delete = file.delete();
                  return;
            }

            if( file.isDirectory() ) {

                  File[] files = file.listFiles();
                  for( File item : files ) {

                        boolean delete = item.delete();
                  }
            }
      }

      public static void clearCache ( Context context ) {

            context = context.getApplicationContext();
            File cacheDir = context.getCacheDir();
            clearFile( cacheDir );
      }

      /**
       * @param file file
       * @param size byte
       */
      public static void clearToSize ( File file, long size ) {

            if( file == null || !file.exists() ) {
                  return;
            }

            if( file.isFile() ) {

                  if( file.length() > size ) {

                        boolean delete = file.delete();
                  }
                  return;
            }

            if( file.isDirectory() ) {

                  File[] files = file.listFiles();
                  Arrays.sort( files, new FileLastModifiedComparator() );

                  int length = files.length;
                  long[] sizes = new long[ length ];
                  long fileSize = directorySize( file, sizes );

                  if( fileSize < size ) {
                        return;
                  }

                  for( int i = 0; i < files.length; i++ ) {

                        boolean delete = files[ i ].delete();
                        if( delete ) {

                              fileSize -= sizes[ i ];

                              if( fileSize < size ) {
                                    return;
                              }
                        }
                  }
            }
      }

      private static long directorySize ( File file, long[] sizes ) {

            long result = 0;

            File[] files = file.listFiles();

            for( int i = 0; i < files.length; i++ ) {
                  long size = fileSize( files[ i ] );
                  result += size;
                  sizes[ i ] = size;
            }

            return result;
      }

      private static long fileSize ( File file ) {

            if( file.isFile() ) {

                  return file.length();
            }

            if( file.isDirectory() ) {

                  long result = 0;

                  File[] files = file.listFiles();
                  for( File item : files ) {

                        result += fileSize( item );
                  }

                  return result;
            }

            return 0;
      }

      private static class FileLastModifiedComparator implements Comparator<File> {

            @Override
            public int compare ( File o1, File o2 ) {

                  long l = o1.lastModified() - o2.lastModified();

                  if( l < 0 ) {
                        return -1;
                  } else if( l > 0 ) {
                        return 1;
                  } else {

                        return 0;
                  }
            }
      }
}
