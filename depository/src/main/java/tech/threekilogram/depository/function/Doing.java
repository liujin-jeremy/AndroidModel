package tech.threekilogram.depository.function;

import android.support.v4.util.ArraySet;

/**
 * 线程安全的记录正在执行的任务,防止重复执行该任务
 *
 * @author: Liujin
 * @version: V1.0
 * @date: 2018-08-27
 * @time: 11:28
 */
public class Doing {

      /**
       * 正在执行的任务集合
       */
      private final ArraySet<String> mRunning = new ArraySet<>();

      /**
       * 测试是否正在执行该任务,如果没有执行那么添加该任务到执行列表,当下次调用时返回true,
       * 当执行完成后记得删除{@link #remove(String)}
       *
       * @param url 需要测试的任务
       *
       * @return true:正在执行
       */
      public boolean isRunning ( String url ) {

            synchronized(mRunning) {

                  if( mRunning.contains( url ) ) {
                        return true;
                  } else {
                        mRunning.add( url );
                        return false;
                  }
            }
      }

      /**
       * 测试是否该任务包含在任务列表中,并不会添加到任务列表中
       *
       * @param url 需要测试任务
       *
       * @return true:该任务正在执行
       */
      public boolean containsOf ( String url ) {

            synchronized(mRunning) {

                  return mRunning.contains( url );
            }
      }

      /**
       * 删除已经完成的任务
       */
      public void remove ( String url ) {

            synchronized(mRunning) {
                  mRunning.remove( url );
            }
      }
}
