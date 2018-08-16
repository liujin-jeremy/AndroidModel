package tech.threekilogram.depository.function;

import java.io.Closeable;
import java.io.IOException;

/**
 * close io stream
 *
 * @author: Liujin
 * @version: V1.0
 * @date: 2018-08-01
 * @time: 11:18
 */
public class Close {

      public static void close (Closeable closeable) {

            if(closeable != null) {

                  try {
                        closeable.close();
                  } catch(IOException e) {

                        /* do nothing */
                        e.printStackTrace();
                  }
            }
      }
}
