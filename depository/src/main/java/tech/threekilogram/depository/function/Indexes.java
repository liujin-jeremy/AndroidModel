package tech.threekilogram.depository.function;

import java.util.ArrayList;

/**
 * @author: Liujin
 * @version: V1.0
 * @date: 2018-09-01
 * @time: 10:36
 */
public class Indexes {

      private ArrayList<Node> mNodes = new ArrayList<>();

      private class Node {

            int low;
            int high;

            Node ( int low, int high ) {

                  this.low = low;
                  this.high = high;
            }

            int count ( ) {

                  return high - low + 1;
            }
      }

      private synchronized void updateIndex ( int low, int high ) {

            if( high < low ) {
                  int temp = high;
                  high = low;
                  low = temp;
            }

            ArrayList<Node> nodes = mNodes;

            if( nodes.size() == 0 ) {
                  nodes.add( new Node( low, high ) );
                  return;
            }

            for( int i = 0; i < nodes.size(); i++ ) {

                  Node node = nodes.get( i );
                  int nodeLow = node.low;
                  int nodeHigh = node.high;

                  if( high < nodeLow ) {
                        mNodes.add( i, new Node( low, high ) );
                        return;
                  }

                  if( high <= nodeHigh ) {

                        if( low < nodeLow ) {
                              node.low = low;
                              reRange( i );
                              return;
                        } else {
                              return;
                        }
                  }

                  /* 以下high>nodeHigh恒为true */

                  if( low < nodeLow ) {
                        node.low = low;
                        if( high > nodeHigh ) {
                              node.high = high;
                        }
                        reRange( i );
                  }

                  if( low <= nodeHigh ) {
                        if( high > nodeHigh ) {
                              node.high = high;
                              reRange( i );
                              return;
                        } else {
                              return;
                        }
                  }
            }

            mNodes.add( new Node( low, high ) );
      }

      private void reRange ( int starIndex ) {

            ArrayList<Node> nodes = mNodes;

            if( starIndex == 0 ) {
                  return;
            }

            for( int i = starIndex; i < nodes.size(); i++ ) {

                  Node prev = nodes.get( i - 1 );
                  Node node = nodes.get( i );

                  if( node.low <= prev.high + 1 ) {

                        if( prev.high < node.high ) {
                              prev.high = node.high;
                        }
                        nodes.remove( i );
                        i--;
                  }

                  try {
                        node = nodes.get( i );
                        Node next = nodes.get( i + 1 );
                        if( node.high + 1 < next.low ) {
                              break;
                        }
                  } catch(Exception e) {

                        /* do nothing */
                  }
            }
      }

      private int count ( ) {

            ArrayList<Node> nodes = mNodes;
            int result = 0;
            for( Node node : nodes ) {
                  result += node.count();
            }

            return result;
      }

      private int getMinIndex ( ) {

            if( mNodes.size() == 0 ) {
                  return -1;
            }

            return mNodes.get( 0 ).low;
      }

      private int getMaxIndex ( ) {

            if( mNodes.size() == 0 ) {
                  return -1;
            }

            return mNodes.get( mNodes.size() - 1 ).high;
      }

      private synchronized void clear ( ) {

            mNodes.clear();
      }
}
