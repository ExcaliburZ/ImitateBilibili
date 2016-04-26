package com.wings.zilizili.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.wings.zilizili.R;
import com.wings.zilizili.domain.RecommendItem;
import com.wings.zilizili.domain.holder.RecommendViewHolder;
import com.wings.zilizili.utils.PicassoImageLoader;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wing on 2016/4/26.
 */
public class RecommendAdapter extends RecyclerView.Adapter<RecommendViewHolder> {
    private Context mContext;
    private List<RecommendItem> mRecommendList;
    private OnItemClickListener onItemClickListener;

    public RecommendAdapter(Context mContext, List<RecommendItem> mRecommendList, OnItemClickListener onItemClickListener) {
        this.mContext = mContext;
        this.onItemClickListener = onItemClickListener;
        if (mRecommendList == null) {
            this.mRecommendList = new ArrayList<>();
        } else {
            this.mRecommendList = mRecommendList;
        }
    }

    public void setRecommendList(List<RecommendItem> mRecommendList) {
        this.mRecommendList = mRecommendList;
    }

    @Override
    public RecommendViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.item_recomment, parent, false);
        return new RecommendViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecommendViewHolder holder, final int position) {
        final RecommendItem recommendItem = mRecommendList.get(position);
        holder.title.setText("\u3000" + recommendItem.name);
        final ImageView imageView = holder.image;
        PicassoImageLoader.getInstance().
                displayRecommendImage(mContext, recommendItem.topimage, imageView);
        holder.online.setText(recommendItem.playCount);

        holder.rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListener.onItemClick(v, imageView, recommendItem.topimage);
            }
        });
    }


    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @Override
    public int getItemCount() {
        return mRecommendList.size();
    }
}
