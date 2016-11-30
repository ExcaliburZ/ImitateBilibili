package com.wings.zilizili.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.wings.zilizili.R;
import com.wings.zilizili.domain.DramaItem;
import com.wings.zilizili.domain.holder.DramaViewHolder;
import com.wings.zilizili.utils.PicassoImageLoader;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wing on 2016/4/26.
 */
public class DramaAdapter extends RecyclerView.Adapter<DramaViewHolder> {
    public int HEADER = 0;
    public int ITEM = 1;
    private View rootView;
    private Context context;
    private List<DramaItem> mDramaList;

    public DramaAdapter(Context context, View mHeadView,
                        List<DramaItem> list) {
        this.context = context;
        this.rootView = mHeadView;
        if (list == null) {
            this.mDramaList = new ArrayList<>();
        } else {
            this.mDramaList = list;
        }
    }

    public void setDramaList(List<DramaItem> mDramaList) {
        this.mDramaList = mDramaList;
    }

    public boolean isHeader(int position) {
        return position == 0;
    }

    @Override
    public int getItemViewType(int position) {
        return isHeader(position) ? HEADER : ITEM;
    }

    @Override
    public DramaViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == HEADER) {
            return new DramaViewHolder(rootView);
        }
        View view = LayoutInflater.from(context).inflate(R.layout.item_drama, parent, false);
        return new DramaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final DramaViewHolder holder, int position) {
        if (isHeader(position)) {
            StaggeredGridLayoutManager.LayoutParams params =
                    new StaggeredGridLayoutManager.LayoutParams
                            (ViewGroup.LayoutParams.MATCH_PARENT,
                                    ViewGroup.LayoutParams.WRAP_CONTENT);
            params.setFullSpan(true);
            holder.view.setLayoutParams(params);
            return;
        }
        //去掉头布局占得位置
        final int index = --position;

        final DramaItem dramaItem = mDramaList.get(position);
        holder.title.setText(dramaItem.name);
        final ImageView imageView = holder.image;
        Integer height = Integer.valueOf(dramaItem.height);
        PicassoImageLoader.INSTANCE.
                displayBangumiImage(context, dramaItem.topimage, imageView);

        ViewGroup.LayoutParams params = imageView.getLayoutParams();
        params.height = height;
        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventBus.getDefault().
                        post(new ItemClickEvent(v.getId(), imageView, dramaItem.topimage));
            }
        });
    }


    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @Override
    public int getItemCount() {
        return mDramaList.size() + 1;
    }
}

