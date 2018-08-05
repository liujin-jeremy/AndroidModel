package tech.threekilogram.depository.net.retrofit.convert.down;

/**
 * @author: Liujin
 * @version: V1.0
 * @date: 2018-08-05
 * @time: 23:13
 */

import com.jakewharton.disklrucache.DiskLruCache;
import com.jakewharton.disklrucache.DiskLruCache.Editor;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import okhttp3.ResponseBody;
import tech.threekilogram.depository.function.CloseFunction;
import tech.threekilogram.depository.function.Md5Function;
import tech.threekilogram.depository.net.UrlConverter;

/**
 * 使用{@link com.jakewharton.disklrucache.DiskLruCache} 保存下载的文件
 *
 * @author liujin
 */
public class RetrofitDiskLruConverter<K> extends AbstractRetrofitDownLoadConverter<K> {

      /**
       * 提供Url
       */
      @SuppressWarnings("WeakerAccess")
      protected UrlConverter<K> mUrlConverter;
      @SuppressWarnings("WeakerAccess")
      protected DiskLruCache    mDiskLruCache;

      public RetrofitDiskLruConverter (File dir, UrlConverter<K> urlConverter, int maxSize) {

            super(dir);
            mUrlConverter = urlConverter;

            try {
                  mDiskLruCache = DiskLruCache.open(dir, 1, 1, maxSize);
            } catch(IOException e) {
                  e.printStackTrace();
            }
      }

      @Override
      public File onExecuteSuccess (K key, ResponseBody response) throws Exception {

            String url = urlFromKey(key);
            String name = Md5Function.nameFromMd5(url);

            mDiskLruCache.remove(name);

            Editor editor = mDiskLruCache.edit(name);
            if(editor == null) {
                  return null;
            }

            OutputStream outputStream = editor.newOutputStream(0);
            if(outputStream == null) {
                  return null;
            }

            InputStream inputStream = null;
            try {
                  inputStream = response.byteStream();

                  byte[] bytes = new byte[128];
                  int len = 0;

                  while((len = inputStream.read(bytes)) != -1) {

                        outputStream.write(bytes, 0, len);
                  }

                  CloseFunction.close(outputStream);
                  editor.commit();
                  return new File(mDir, name + ".0");
            } catch(IOException e) {
                  e.printStackTrace();
                  abortEditor(editor);
            } finally {

                  CloseFunction.close(inputStream);
                  CloseFunction.close(outputStream);
            }

            try {
                  mDiskLruCache.flush();
            } catch(IOException e) {
                  e.printStackTrace();
            }

            return null;
      }

      @Override
      public String urlFromKey (K key) {

            return mUrlConverter.urlFromKey(key);
      }

      private void abortEditor (Editor edit) {

            try {
                  if(edit != null) {
                        edit.abort();
                  }
            } catch(IOException e) {

                  e.printStackTrace();
            }
      }

      @Override
      public void onExecuteFailed (K key, int httpCode, ResponseBody errorResponse) {

      }

      @Override
      public File getFile (K key) {

            String url = urlFromKey(key);
            String name = Md5Function.nameFromMd5(url);

            return new File(mDir, name + ".0");
      }
}
