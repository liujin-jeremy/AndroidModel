package tech.threekilogram.depository.net.retrofit.stream;

import tech.threekilogram.depository.net.retrofit.converter.RetrofitStringConverter;

/**
 * 从网络读取string对象
 *
 * @author: Liujin
 * @version: V1.0
 * @date: 2018-08-13
 * @time: 18:19
 */
public class RetrofitStringLoader extends RetrofitStreamLoader<String> {

      public RetrofitStringLoader ( ) {

            super( new RetrofitStringConverter() );
      }
}
