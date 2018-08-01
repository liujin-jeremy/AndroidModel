package tech.threekilogram.depository.net.retrofit.download;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import tech.threekilogram.depository.net.NetLoadSupport;
import tech.threekilogram.depository.function.NameFunction;
import tech.threekilogram.depository.net.NetMapper;
import tech.threekilogram.depository.global.RetrofitClient;

/**
 * 使用 retrofit 发送一个 get 请求,然后读取响应为 byte[]
 *
 * @param <K> key 类型
 * @param <V> 结果类型
 *
 * @author liujin
 */
public class RetrofitDownLoader<K, V> implements NetLoadSupport<K, V> {

      private File                  mSavedDir;
      private NetMapper<K, File, V> mMapper;

      public RetrofitDownLoader (File dir, NetMapper<K, File, V> mapper) {

            mSavedDir = dir;
            mMapper = mapper;
      }

      @Override
      public V loadFromNet (K key) throws IOException {

            String url = mMapper.keyToUrl(key);
            String name = NameFunction.nameFromMd5(url);
            File toSave = new File(mSavedDir, name);

            StreamService streamService = RetrofitClient.INSTANCE.create(StreamService.class);
            Call<ResponseBody> bodyCall = streamService.toGet(url);
            Response<ResponseBody> execute = bodyCall.execute();

            if(execute.isSuccessful()) {
                  ResponseBody body = execute.body();
                  if(body != null) {

                        InputStream inputStream = null;
                        FileOutputStream outputStream = null;

                        try {
                              inputStream = body.byteStream();
                              outputStream = new FileOutputStream(toSave);

                              byte[] bytes = new byte[512];
                              int len = 0;

                              while((len = inputStream.read(bytes)) != -1) {
                                    outputStream.write(bytes, 0, len);
                              }

                              outputStream.flush();

                              return mMapper.responseToValue(toSave);
                        } finally {

                              if(inputStream != null) {
                                    inputStream.close();
                              }
                              if(outputStream != null) {
                                    outputStream.close();
                              }
                        }
                  }
            } else {

                  int code = execute.code();
                  mMapper.errorCode(code);
            }

            return null;
      }
}
