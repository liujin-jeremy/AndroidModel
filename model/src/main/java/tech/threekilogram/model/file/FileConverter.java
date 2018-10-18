package tech.threekilogram.model.file;

import tech.threekilogram.model.StreamConverter;

/**
 * 该接口用于辅助{@link BaseFileLoader}正常工作:
 * 将一个key对应的数据转为{@link V}类型的value
 *
 * @param <V> 需要获取的数据类型
 *
 * @author liujin
 */
public interface FileConverter<V> extends StreamConverter<V> {

      /**
       * 根据key返回文件名字,文件名字不能包含一些特殊字符,最好只包含数字字母
       * to get a file from key
       *
       * @param key key to map a file
       *
       * @return file encodeToName defined by this key, not a path
       */
      String fileName ( String key );
}
