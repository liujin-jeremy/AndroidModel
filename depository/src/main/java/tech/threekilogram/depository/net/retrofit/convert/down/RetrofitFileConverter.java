package tech.threekilogram.depository.net.retrofit.convert.down;

/**
 * @author: Liujin
 * @version: V1.0
 * @date: 2018-08-05
 * @time: 23:12
 */

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import okhttp3.ResponseBody;
import tech.threekilogram.depository.function.CloseFunction;
import tech.threekilogram.depository.function.Md5Function;
import tech.threekilogram.depository.net.UrlConverter;

/**
 * 下载文件到一个文件夹下
 *
 * @param <K> key
 *
 * @author liujin
 */
public class RetrofitFileConverter<K> extends
                                      AbstractRetrofitDownLoadConverter<K> {

      /**
       * 提供Url
       */
      @SuppressWarnings("WeakerAccess")
      protected UrlConverter<K> mUrlConverter;

      public RetrofitFileConverter (File dir, UrlConverter<K> urlConverter) {

            super(dir);
            mUrlConverter = urlConverter;
      }

      @Override
      public File onExecuteSuccess (K key, ResponseBody response) throws Exception {

            File file = getFile(key);

            FileOutputStream outputStream = new FileOutputStream(file);

            InputStream inputStream = response.byteStream();
            int length = (int) response.contentLength();

            final int limit = 512;

            if(length <= limit) {

                  byte[] bytes = new byte[length];
                  int read = inputStream.read(bytes);
                  outputStream.write(bytes, 0, read);
            } else {

                  byte[] bytes = new byte[256];
                  int len = 0;

                  while((len = inputStream.read(bytes)) != -1) {

                        outputStream.write(bytes, 0, len);
                  }
            }

            CloseFunction.close(inputStream);
            CloseFunction.close(outputStream);

            return file;
      }

      @Override
      public File getFile (K key) {

            String url = urlFromKey(key);
            String name = Md5Function.nameFromMd5(url);
            return new File(mDir, name);
      }

      @Override
      public String urlFromKey (K key) {

            return mUrlConverter.urlFromKey(key);
      }

      @Override
      public void onExecuteFailed (K key, int httpCode, ResponseBody errorResponse) {

      }
}
