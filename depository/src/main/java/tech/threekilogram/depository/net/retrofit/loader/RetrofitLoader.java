package tech.threekilogram.depository.net.retrofit.loader;

import android.support.annotation.NonNull;
import java.io.IOException;
import java.io.InputStream;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import tech.threekilogram.depository.ContainerLoader;
import tech.threekilogram.depository.Loader;
import tech.threekilogram.depository.function.CloseFunction;
import tech.threekilogram.depository.global.RetrofitClient;
import tech.threekilogram.depository.net.OnNetErrorListener;
import tech.threekilogram.depository.net.UrlFactory;
import tech.threekilogram.depository.net.retrofit.converte.RetrofitConverter;
import tech.threekilogram.depository.net.retrofit.service.StreamService;

/**
 * @author: Liujin
 * @version: V1.0
 * @date: 2018-08-01
 * @time: 19:16
 */
public class RetrofitLoader<K, V> implements Loader<K, V> {

      private UrlFactory<K>         mUrlFactory;
      private RetrofitConverter<V>  mRetrofitConverter;
      private ContainerLoader<K, V> mLoader;
      private OnNetErrorListener<K> mOnNetErrorListener;

      public RetrofitLoader (
          @NonNull UrlFactory<K> urlFactory,
          @NonNull RetrofitConverter<V> retrofitConverter) {

            this(urlFactory, retrofitConverter, null);
      }

      public RetrofitLoader (
          @NonNull UrlFactory<K> urlFactory,
          @NonNull RetrofitConverter<V> retrofitConverter,
          ContainerLoader<K, V> loader) {

            mUrlFactory = urlFactory;
            mRetrofitConverter = retrofitConverter;
            mLoader = loader;
      }

      public void setOnNetErrorListener (
          OnNetErrorListener<K> onNetErrorListener) {

            mOnNetErrorListener = onNetErrorListener;
      }

      @Override
      public V save (K key, V value) {

            /* use a container a save, we cant save a value to net */

            if(mLoader != null) {

                  return mLoader.save(key, value);
            }

            return null;
      }

      @Override
      public V remove (K key) {

            /* use container to save and remove from container */

            if(mLoader != null) {
                  return mLoader.remove(key);
            }

            return null;
      }

      @Override
      public V load (K key) {

            /* first read value from container */

            if(mLoader != null && mLoader.containsOf(key)) {
                  V load = mLoader.load(key);
                  if(load != null) {
                        return load;
                  }
            }

            /* send response */

            String url = mUrlFactory.url(key);
            StreamService streamService = RetrofitClient.INSTANCE.create(StreamService.class);
            Call<ResponseBody> responseBodyCall = streamService.toGet(url);

            InputStream inputStream = null;

            try {
                  Response<ResponseBody> execute = responseBodyCall.execute();
                  if(execute.isSuccessful()) {

                        /* successful response */

                        ResponseBody body = execute.body();

                        if(body != null) {

                              long contentLength = body.contentLength();
                              inputStream = body.byteStream();

                              V value = null;
                              try {
                                    value = mRetrofitConverter
                                        .onResponse(contentLength, inputStream);
                              } catch(Exception e) {

                                    /* maybe exception if convert failed */

                                    if(mOnNetErrorListener != null) {
                                          mOnNetErrorListener.onResponseConvertException(key, e);
                                    }
                              }

                              if(value != null) {
                                    save(key, value);
                              }
                        }
                  } else {

                        /* http code is not in 200~300 */

                        int code = execute.code();
                        mOnNetErrorListener.onErrorCode(key, code);
                  }
            } catch(IOException e) {
                  e.printStackTrace();

                  /* execute exception */

                  if(mOnNetErrorListener != null) {
                        mOnNetErrorListener.onConnectException(key, e);
                  }
            } finally {

                  CloseFunction.close(inputStream);
            }

            return null;
      }
}
