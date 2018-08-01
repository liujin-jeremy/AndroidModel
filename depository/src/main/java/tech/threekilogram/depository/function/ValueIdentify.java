package tech.threekilogram.depository.function;

/**
 * to get a identification of a value
 *
 * @param <V> value to get a ValueIdentify
 * @param <I> identification of a value
 *
 * @author liujin
 */
public interface ValueIdentify<V, I> {

      /**
       * get a value of a identification if has defined a Identify
       *
       * @param v which one to get identification
       *
       * @return identification or null this value has not defined a identification
       */
      I identifyOf (V v);

      /**
       * generate a identification to value
       *
       * @param v a value to generate a identification
       *
       * @return identification
       */
      I generate (V v);
}
