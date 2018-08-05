package tech.threekilogram.depository.net.retrofit;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import okhttp3.ResponseBody;
import retrofit2.Call;
import tech.threekilogram.depository.function.CloseFunction;
import tech.threekilogram.depository.function.Md5Function;
import tech.threekilogram.depository.net.NetConverter;
import tech.threekilogram.depository.net.retrofit.service.StreamService;

/**
 * 使用 retrofit 下载文件到文件夹
 *
 * @param <K> key
 *
 * @author liujin
 */
@SuppressWarnings("WeakerAccess")
public class RetrofitDownLoadLoadSupport<K> extends
                                            BaseRetrofitLoadSupport<K, File, StreamService> {

      public RetrofitDownLoadLoadSupport (DownLoadConverter<K> converter) {

            mNetConverter = converter;
            mServiceType = StreamService.class;
      }

      @Override
      public File loadFromNet (K key) {

            DownLoadConverter<K> netConverter = (DownLoadConverter<K>) mNetConverter;
            File file = netConverter.getFile(key);

            if(file.exists()) {
                  return file;
            }

            return super.loadFromNet(key);
      }

      @Override
      protected Call<ResponseBody> configService (
          K key, String url, StreamService service) {

            return service.toGet(url);
      }

      // ========================= DownLoadConverter =========================

      public static class DownLoadConverter<K> implements NetConverter<K, File, ResponseBody> {

            /**
             * 下载文件夹
             */
            private File mDir;

            public DownLoadConverter (File dir) {

                  mDir = dir;
            }

            public File getFile (K key) {

                  String url = urlFromKey(key);
                  String name = Md5Function.nameFromMd5(url);

                  return new File(mDir, name);
            }

            @Override
            public String urlFromKey (K key) {

                  return null;
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
            public void onExecuteFailed (K key, int httpCode, ResponseBody errorResponse) {

            }
      }
}
