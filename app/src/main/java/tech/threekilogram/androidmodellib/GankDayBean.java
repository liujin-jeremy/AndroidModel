package tech.threekilogram.androidmodellib;

import java.util.List;

/**
 * @author: Liujin
 * @version: V1.0
 * @date: 2018-09-01
 * @time: 13:51
 */
public class GankDayBean {

      private boolean      error;
      private Results      results;
      private List<String> category;

      public boolean isError ( ) { return error;}

      public void setError ( boolean error ) { this.error = error;}

      public Results getResults ( ) { return results;}

      public void setResults ( Results results ) { this.results = results;}

      public List<String> getCategory ( ) { return category;}

      public void setCategory ( List<String> category ) { this.category = category;}

      public static class Results {

            private List<ResultsBean> Android;
            private List<ResultsBean> App;
            private List<ResultsBean> iOS;
            private List<ResultsBean> 休息视频;
            private List<ResultsBean> 前端;
            private List<ResultsBean> 拓展资源;
            private List<ResultsBean> 瞎推荐;
            private List<ResultsBean> 福利;

            public List<ResultsBean> getAndroid ( ) {

                  return Android;
            }

            public void setAndroid (
                List<ResultsBean> android ) {

                  Android = android;
            }

            public List<ResultsBean> getApp ( ) {

                  return App;
            }

            public void setApp ( List<ResultsBean> app ) {

                  App = app;
            }

            public List<ResultsBean> getiOS ( ) {

                  return iOS;
            }

            public void setiOS ( List<ResultsBean> iOS ) {

                  this.iOS = iOS;
            }

            public List<ResultsBean> get休息视频 ( ) {

                  return 休息视频;
            }

            public void set休息视频 ( List<ResultsBean> 休息视频 ) {

                  this.休息视频 = 休息视频;
            }

            public List<ResultsBean> get前端 ( ) {

                  return 前端;
            }

            public void set前端 ( List<ResultsBean> 前端 ) {

                  this.前端 = 前端;
            }

            public List<ResultsBean> get拓展资源 ( ) {

                  return 拓展资源;
            }

            public void set拓展资源 ( List<ResultsBean> 拓展资源 ) {

                  this.拓展资源 = 拓展资源;
            }

            public List<ResultsBean> get瞎推荐 ( ) {

                  return 瞎推荐;
            }

            public void set瞎推荐 ( List<ResultsBean> 瞎推荐 ) {

                  this.瞎推荐 = 瞎推荐;
            }

            public List<ResultsBean> get福利 ( ) {

                  return 福利;
            }

            public void set福利 ( List<ResultsBean> 福利 ) {

                  this.福利 = 福利;
            }
      }
}
