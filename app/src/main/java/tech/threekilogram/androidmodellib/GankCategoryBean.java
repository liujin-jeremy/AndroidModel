package tech.threekilogram.androidmodellib;

import java.util.List;

/**
 * @author: Liujin
 * @version: V1.0
 * @date: 2018-09-01
 * @time: 13:47
 */
public class GankCategoryBean {

      private boolean           error;
      private List<ResultsBean> results;

      public boolean isError ( ) { return error;}

      public void setError ( boolean error ) { this.error = error;}

      public List<ResultsBean> getResults ( ) { return results;}

      public void setResults ( List<ResultsBean> results ) { this.results = results;}
}
