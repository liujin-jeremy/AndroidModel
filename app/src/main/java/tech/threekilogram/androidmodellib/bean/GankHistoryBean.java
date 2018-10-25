package tech.threekilogram.androidmodellib.bean;

import java.util.List;

/**
 * @author: Liujin
 * @version: V1.0
 * @date: 2018-09-01
 * @time: 12:52
 */
public class GankHistoryBean {

      private boolean      error;
      private List<String> results;

      public boolean isError ( ) {

            return error;
      }

      public void setError ( boolean error ) {

            this.error = error;
      }

      public List<String> getResults ( ) {

            return results;
      }

      public void setResults ( List<String> results ) {

            this.results = results;
      }
}
