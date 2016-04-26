package com.wings.zilizili.domain.holder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.wings.zilizili.R;

/**
 * Created by wing on 2016/4/26.
 */
public class DramaViewHolder extends RecyclerView.ViewHolder {
    public TextView title;
    public ImageView image;
    public View view;

    public DramaViewHolder(View itemView) {
        super(itemView);
        view = itemView;
        title = (TextView) itemView.findViewById(R.id.tv_title);
        image = (ImageView) itemView.findViewById(R.id.iv_item);
    }
}
