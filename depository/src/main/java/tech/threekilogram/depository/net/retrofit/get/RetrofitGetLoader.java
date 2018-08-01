package tech.threekilogram.depository.net.retrofit.get;

import java.io.IOException;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import tech.threekilogram.depository.net.NetLoadSupport;
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
public class RetrofitGetLoader<K, V> implements NetLoadSupport<K, V> {

      private NetMapper<K, byte[], V> mMapper;

      public RetrofitGetLoader (NetMapper<K, byte[], V> mapper) {

            mMapper = mapper;
      }

      @Override
      public V loadFromNet (K key) throws IOException {

            String url = mMapper.keyToUrl(key);
            GetService getService = RetrofitClient.INSTANCE.create(GetService.class);
            Call<ResponseBody> bodyCall = getService.toGet(url);
            Response<ResponseBody> execute = bodyCall.execute();

            if(execute.isSuccessful()) {
                  ResponseBody body = execute.body();
                  if(body != null) {
                        byte[] bytes = body.bytes();
                        return mMapper.responseToValue(bytes);
                  }
            } else {

                  int code = execute.code();
                  mMapper.errorCode(code);
            }

            return null;
      }
}
