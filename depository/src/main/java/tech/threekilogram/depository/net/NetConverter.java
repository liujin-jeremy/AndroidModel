package tech.threekilogram.depository.net;

import okhttp3.ResponseBody;

/**
 * 使用 {@link String} 类型向网络发送请求,收到 {@link P} 类型响应数据,转为 {@link V} 类型结果
 *
 * @param <V> value 类型
 * @param <P> 网络响应类型
 *
 * @author liujin
 */
public interface NetConverter<V, P> {

      /**
       * 当网络响应成功之后的回调,需要完成从响应到value的变换
       * <p>
       * get a success response then convert it to value
       *
       * @param key key
       * @param response response
       *
       * @return value
       *
       * @throws Exception when convert  {@link ResponseBody} to {@link V} may occur a exception
       *                   {@see #onConvertException(Object, String, Exception)}
       */
      V onExecuteSuccess ( String key, P response ) throws Exception;
}
