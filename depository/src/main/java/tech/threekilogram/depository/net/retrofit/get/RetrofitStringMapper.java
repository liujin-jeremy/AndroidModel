package tech.threekilogram.depository.net.retrofit.get;

import tech.threekilogram.depository.net.NetMapper;

/**
 * @author: Liujin
 * @version: V1.0
 * @date: 2018-07-31
 * @time: 10:05
 */
public class RetrofitStringMapper implements NetMapper<String, byte[], String> {

      @Override
      public String keyToUrl (String key) {

            return key;
      }

      @Override
      public void errorCode (int errorCode) {

            System.out.println("error code : " + errorCode);
      }

      @Override
      public String responseToValue (byte[] response) {

            return new String(response);
      }
}
