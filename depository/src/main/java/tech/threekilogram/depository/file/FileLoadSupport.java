package tech.threekilogram.depository.file;

import android.support.annotation.IntDef;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import tech.threekilogram.depository.ContainerLoader;

/**
 * 使用一个key从文件系统加载数据
 *
 * @param <K> key type
 * @param <V> value type
 *
 * @author liujin
 */
public interface FileLoadSupport<K, V> extends ContainerLoader<K, V> {

      int SAVE_STRATEGY_COVER      = 0;
      int SAVE_STRATEGY_RETURN_OLD = 1;

      @IntDef(value = {FileLoadSupport.SAVE_STRATEGY_COVER,
                       FileLoadSupport.SAVE_STRATEGY_RETURN_OLD})
      @interface SaveStrategyValue {}

      /**
       * handle exception when Object run
       */
      interface ExceptionHandler<K, V> {

            /**
             * a exception occur at {@link ValueFileConverter#toValue(Object, InputStream)} will
             * call this
             *
             * @param e exception
             * @param key which key occur
             */
            void onConvertToValue (Exception e, K key);

            /**
             * a exception occur at {@link ValueFileConverter#saveValue(Object, OutputStream,
             * Object)} will call
             * this
             *
             * @param e exception
             * @param key key
             * @param value to save
             */
            void onSaveValueToFile (IOException e, K key, V value);
      }
}
