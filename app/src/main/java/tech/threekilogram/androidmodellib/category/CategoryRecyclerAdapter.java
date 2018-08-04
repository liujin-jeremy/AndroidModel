package tech.threekilogram.androidmodellib.category;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import tech.threekilogram.androidmodellib.GankCategoryBean.ResultsBean;
import tech.threekilogram.androidmodellib.R;
import tech.threekilogram.androidmodellib.category.CategoryRecyclerAdapter.TextHolder;

/**
 * @author: Liujin
 * @version: V1.0
 * @date: 2018-07-31
 * @time: 18:16
 */
public class CategoryRecyclerAdapter extends Adapter<TextHolder> {

      private LayoutInflater mInflater;

      @NonNull
      @Override
      public TextHolder onCreateViewHolder (@NonNull ViewGroup parent, int viewType) {

            if(mInflater == null) {
                  mInflater = LayoutInflater.from(parent.getContext());
            }

            View view = mInflater
                .inflate(R.layout.recycler_gank_category_item, parent, false);

            return new TextHolder(view);
      }

      @Override
      public void onBindViewHolder (
          @NonNull TextHolder holder, int position) {

      }

      @Override
      public int getItemCount () {

            return 20;
      }

      public static class TextHolder extends ViewHolder {

            private TextView mCategory;
            private TextView mCreateTime;
            private TextView mDes;
            private TextView mUrl;
            private TextView mWho;

            public TextHolder (View itemView) {

                  super(itemView);
                  initView(itemView);
            }

            private void initView (@NonNull final View itemView) {

                  mCategory = itemView.findViewById(R.id.category);
                  mCreateTime = itemView.findViewById(R.id.createTime);
                  mDes = itemView.findViewById(R.id.des);
                  mUrl = itemView.findViewById(R.id.url);
                  mWho = itemView.findViewById(R.id.who);
            }

            void bind (int position, ResultsBean bean) {

                  String type = bean.getType();
                  mCategory.setText(type);

                  String createdAt = bean.getCreatedAt();
                  mCreateTime.setText(createdAt);

                  String desc = bean.getDesc();
                  mDes.setText(desc);

                  String url = bean.getUrl();
                  mUrl.setText(url);

                  String who = bean.getWho();
                  mWho.setText(who);
            }
      }
}
