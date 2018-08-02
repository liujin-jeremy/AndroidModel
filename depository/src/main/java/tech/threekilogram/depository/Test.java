package tech.threekilogram.depository;

import java.io.File;

/**
 * test this lib
 */
class Test {

     private static final File TEMP = new File("Temp");

      static {
            if(!TEMP.exists()) {
                  boolean mkdirs = TEMP.mkdirs();
            } else {
                  if(TEMP.isFile()) {
                        boolean delete = TEMP.delete();
                        boolean mkdirs = TEMP.mkdirs();
                  }
            }
      }

      public static void main (String[] args) {



      }
}
