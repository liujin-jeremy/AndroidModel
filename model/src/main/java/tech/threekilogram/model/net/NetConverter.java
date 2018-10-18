package tech.threekilogram.model.net;

/**
 * 用于转换网络响应为需要的对象
 *
 * @param <V> value 类型
 * @param <P> 网络响应类型
 *
 * @author liujin
 */
public interface NetConverter<V, P> {

      /**
       * 当网络响应成功之后的回调,需要完成从响应到value的变换
       *
       * @param url url
       * @param response response
       *
       * @return value
       *
       * @throws Exception 转换时可能发生异常
       */
      V onExecuteSuccess (
          String url,
          P response ) throws Exception;
}
