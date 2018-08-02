package tech.threekilogram.depository.net.retrofit;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

/**
 * @author: Liujin
 * @version: V1.0
 * @date: 2018-08-02
 * @time: 14:14
 */
public interface StreamService {

      /**
       * to get
       *
       * @param path url path
       *
       * @return response
       */
      @GET
      @Streaming
      Call<ResponseBody> toGet (@Url String path);
}
