package tech.threekilogram.depository.net.retrofit;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import okhttp3.ResponseBody;
import retrofit2.Call;
import tech.threekilogram.depository.function.CloseFunction;
import tech.threekilogram.depository.function.Md5Function;

/**
 * @author: Liujin
 * @version: V1.0
 * @date: 2018-08-02
 * @time: 14:57
 */
@SuppressWarnings("WeakerAccess")
public class RetrofitDownLoadConverter extends
                                       BaseRetrofitConverter<String, File, StreamService> {

      private File mDir;

      public RetrofitDownLoadConverter (File dir) {

            super(StreamService.class);
            mDir = dir;
      }

      @Override
      public File loadFromNet (String key) {

            String name = Md5Function.nameFromMd5(key);
            File file = new File(mDir, name);

            if(file.exists()) {
                  return file;
            }

            return super.loadFromNet(key);
      }

      @Override
      protected String urlFromKey (String key) {

            return key;
      }

      @Override
      protected Call<ResponseBody> configService (
          String key, String url, StreamService service) {

            return service.toGet(url);
      }

      @Override
      protected File onExecuteSuccess (String key, ResponseBody response) throws Exception {

            String name = Md5Function.nameFromMd5(key);
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
}
