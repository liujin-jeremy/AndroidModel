package tech.threekilogram.depository.net.retrofit;

import com.jakewharton.disklrucache.DiskLruCache;
import com.jakewharton.disklrucache.DiskLruCache.Editor;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import okhttp3.ResponseBody;
import retrofit2.Call;
import tech.threekilogram.depository.function.CloseFunction;
import tech.threekilogram.depository.function.Md5Function;
import tech.threekilogram.depository.net.NetConverter;
import tech.threekilogram.depository.net.UrlConverter;
import tech.threekilogram.depository.net.retrofit.service.StreamService;

/**
 * 使用 retrofit 下载文件到文件夹
 *
 * @param <K> key
 *
 * @author liujin
 */
@SuppressWarnings("WeakerAccess")
public class RetrofitDownLoader<K> extends
                                   BaseRetrofitLoader<K, File, StreamService> {

      public RetrofitDownLoader (UrlConverter<K> urlConverter, File dir) {

            mNetConverter = new DownLoadConverter<>(dir, urlConverter);
            mServiceType = StreamService.class;
      }

      @Override
      public File loadFromNet (K key) {

//            DownLoadConverter<K> netConverter = (DownLoadConverter<K>) mNetConverter;
//            File file = netConverter.getFile(key);
//
//            if(file.exists()) {
//                  return file;
//            }

            return super.loadFromNet(key);
      }

      @Override
      protected Call<ResponseBody> configService (
          K key, String url, StreamService service) {

            return service.toGet(url);
      }

      // ========================= DownLoadConverter =========================

      /**
       * base 接口
       */
      public static abstract class AbstractNetDownLoadConverter<K> implements
                                                                   NetConverter<K, File, ResponseBody> {

            /**
             * 下载文件夹
             */
            protected File mDir;

            public AbstractNetDownLoadConverter (File dir) {

                  mDir = dir;
            }

            public File getDir () {

                  return mDir;
            }
      }

      /**
       * 下载文件到一个文件夹下
       *
       * @param <K> key
       */
      public static class DownLoadConverter<K> extends AbstractNetDownLoadConverter<K> {

            /**
             * 提供Url
             */
            private UrlConverter<K> mUrlConverter;

            public DownLoadConverter (File dir, UrlConverter<K> urlConverter) {

                  super(dir);
                  mUrlConverter = urlConverter;
            }

            @Override
            public File onExecuteSuccess (K key, ResponseBody response) throws Exception {

                  String url = urlFromKey(key);
                  String name = Md5Function.nameFromMd5(url);
                  File file = new File(mDir, name);

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
            public String urlFromKey (K key) {

                  return mUrlConverter.urlFromKey(key);
            }

            @Override
            public void onExecuteFailed (K key, int httpCode, ResponseBody errorResponse) {

            }
      }

      /**
       * 使用{@link com.jakewharton.disklrucache.DiskLruCache} 保存下载的文件
       */
      public static class DiskLruDownLoadConverter<K> extends AbstractNetDownLoadConverter<K> {

            /**
             * 提供Url
             */
            private UrlConverter<K> mUrlConverter;
            private DiskLruCache    mDiskLruCache;

            public DiskLruDownLoadConverter (File dir, UrlConverter<K> urlConverter, int maxSize) {

                  super(dir);
                  mUrlConverter = urlConverter;

                  try {
                        mDiskLruCache = DiskLruCache.open(dir, 1, 1, maxSize);
                  } catch(IOException e) {
                        e.printStackTrace();
                  }
            }

            @Override
            public String urlFromKey (K key) {

                  return mUrlConverter.urlFromKey(key);
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
      }
}
