package tech.threekilogram.model.net.responsebody;

import java.io.InputStream;
import okhttp3.ResponseBody;

/**
 * 将网络响应转换为stream对象
 *
 * @author liujin
 */
public class BodyStreamConverter implements ResponseBodyConverter<InputStream> {

      @Override
      public InputStream onExecuteSuccess (
          String url, ResponseBody response )
          throws Exception {

            return response.byteStream();
      }
}
