package tech.threekilogram.depository.net.retrofit.download;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

/**
 * @author: Liujin
 * @version: V1.0
 * @date: 2018-07-31
 * @time: 9:40
 */
public interface StreamService {

      /**
       * use this url to get a response from net
       *
       * @param url use this url to get a response
       *
       * @return response
       */
      @GET
      @Streaming
      Call<ResponseBody> toGet (@Url String url);
}
