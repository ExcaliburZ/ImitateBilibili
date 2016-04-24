package com.wings.zilizili.ui.fragment;


import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.wings.zilizili.R;
import com.wings.zilizili.activity.VideoDetailActivity;
import com.wings.zilizili.domain.Data;
import com.wings.zilizili.domain.DataInfo;
import com.wings.zilizili.domain.DramaItem;
import com.wings.zilizili.domain.RecommendItem;
import com.wings.zilizili.domain.TopNewsItem;
import com.wings.zilizili.ui.widget.DramaRecyclerView;
import com.wings.zilizili.utils.PicassoImageLoader;

import java.util.ArrayList;

/**
 * BangumiFragment,HomeFragment中的番剧标签页Fragment
 * 主要利用为一个RecyclerView添加头布局完成主体设计
 * 头布局中又自带一个TopNews滚动条的ViewPager和其他一些数据
 */
public class BangumiFragment extends BaseFragment {

    public static final String IMAGE = "image";
    private static final int START_AUTO_PLAY = 0;
    private static final String MATERIAL_VIDEO_CODE = "123456";
    private static final String ANIM_VIDEO_CODE = "654321";
    private static String TAG = "BangumiFragment";
    private DramaRecyclerView mRecyclerView;
    private RecyclerView mGridView;
    private ViewPager topNews;
    private long startTime;
    private View mHeadView;
    private ArrayList<TopNewsItem> topNewsList;
    private ArrayList<RecommendItem> mRecommendList;
    private ArrayList<DramaItem> mDramaList;
    private GridLayoutManager mGridManager;
    private TopNewsAdapter mTopNewsAdapter;
    private DramaAdapter mDramaAdapter;
    private RecommendAdapter mRecommendAdapter;
    private RelativeLayout rlPointSet;
    private View redPoint;
    //TopNews点指示器间距
    private int itemSpacing;
    private int itemSize;
    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case START_AUTO_PLAY:
                    int currentItem = topNews.getCurrentItem();
                    topNews.setCurrentItem(++currentItem);
                    this.sendEmptyMessageDelayed(START_AUTO_PLAY, 3000);
                    break;
                default:
                    throw new RuntimeException("can not handler null message");
            }
        }
    };


    public BangumiFragment() {
        // Required empty public constructor
    }

    protected void initView() {
        //调用父类的方法,初始化一些通用的控件
        super.initView();
        //初始化头布局及其控件
        mHeadView = LayoutInflater.from(mActivity).inflate(R.layout.header_drama, null);
        topNews = findViewInHeadView(R.id.vp_top);
        rlPointSet = findViewInHeadView(R.id.rl_point_set);

        mGridView = findViewInHeadView(R.id.rv_grid);
        mGridManager = new GridLayoutManager(mActivity, 2);
        mGridView.setLayoutManager(mGridManager);

        //初始化RecyclerView及其LayoutManager
        mRecyclerView = $(R.id.rv_drama);
        StaggeredGridLayoutManager mStaggeredGridLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(mStaggeredGridLayoutManager);
        //设置箭头的颜色
        mRecyclerView.setHasFixedSize(false);

        int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.spacing);
        mRecyclerView.addItemDecoration(new SpacesItemDecoration(spacingInPixels));
        //填充需要的数据
        itemSpacing = getResources().getDimensionPixelSize(R.dimen.spacing_item);
        itemSize = getResources().getDimensionPixelSize(R.dimen.point_item_size);

        //初始化需要存放数据的List,防止空指针
        topNewsList = new ArrayList<>();
        mRecommendList = new ArrayList<>();
        mDramaList = new ArrayList<>();
    }

    @Override
    protected View initRootView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.fragment_drama, container, false);
    }

    @Override
    protected String initURL() {
        return "json/JsonServlet";
    }

    protected void setListener() {
        super.setListener();

        mTopNewsAdapter = new TopNewsAdapter(mActivity);
        topNews.setAdapter(mTopNewsAdapter);

        mRecyclerView.setVisibility(View.INVISIBLE);
        mDramaAdapter = new DramaAdapter(mHeadView);
        mRecyclerView.setAdapter(mDramaAdapter);

        mRecommendAdapter = new RecommendAdapter();
        mGridView.setAdapter(mRecommendAdapter);
        //点击后取消自动播放
        topNews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        topNews.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                //小红点跟随移动代码
//                RelativeLayout.LayoutParams params =
//                        (RelativeLayout.LayoutParams) redPoint.getLayoutParams();
//                params.leftMargin = (int) ((position * mLen) + mLen * positionOffset);
//                redPoint.setLayoutParams(params);
            }

            @Override
            public void onPageSelected(int position) {
                if (redPoint == null)
                    return;
                position = position % topNewsList.size();
                RelativeLayout.LayoutParams params =
                        (RelativeLayout.LayoutParams) redPoint.getLayoutParams();
                params.leftMargin = (position * itemSpacing);
                redPoint.setLayoutParams(params);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void sleepMoment() {
        long endTime = SystemClock.uptimeMillis();
        if (endTime - startTime < 1500) {
            long delayTime = 1500 - (endTime - startTime);
            SystemClock.sleep(delayTime);
        }
    }

    @Override
    protected void notifyDataRefresh() {
        //已经获取到了数据通知变化
        mTopNewsAdapter.notifyDataSetChanged();
        mDramaAdapter.notifyDataSetChanged();
        mRecommendAdapter.notifyDataSetChanged();

        mRecyclerView.setVisibility(View.VISIBLE);
        mContentView.setRefreshing(false);
        isRefreshing = false;

        //初始化TopNews的指示器并选中第一个
        initRelativePointSet();
        topNews.setCurrentItem(topNewsList.size() * 50);

        //开启自动播放
        mHandler.sendEmptyMessageDelayed(START_AUTO_PLAY, 3000);
    }

    private void initRelativePointSet() {
        for (int i = 0; i < topNewsList.size(); i++) {
            View point = new View(mActivity);
            point.setBackgroundResource(R.drawable.shape_point_gray);
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(itemSize, itemSize);
            params.leftMargin = itemSpacing * i;
            point.setLayoutParams(params);
            rlPointSet.addView(point);
        }
        //有数据添加小红点
        if (topNewsList.size() > 0) {
            redPoint = new View(mActivity);
            redPoint.setBackgroundResource(R.drawable.shape_point_red);
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(itemSize, itemSize);
            redPoint.setLayoutParams(params);
            rlPointSet.addView(redPoint);
        }
    }

    @Override
    //解析获取到的数据
    protected void decodeResult(String result) {
        Gson gson = new Gson();
        DataInfo dataInfo = gson.fromJson(result, DataInfo.class);
        if (dataInfo.retcode / 100 == 2) {
            Data data = dataInfo.data;
            topNewsList = data.topnews;
            mRecommendList = data.recomment;
            mDramaList = data.drama;
        }
    }

    //从HeadView中获取控件
    private <T extends View> T findViewInHeadView(int resId) {
        return (T) mHeadView.findViewById(resId);
    }

    private void enterVideoDetailActivity(String animVideoCode, String image, String topimage, ImageView imageView) {
        Intent intent = new Intent(getContext(), VideoDetailActivity.class);
        intent.putExtra("av", animVideoCode);
        intent.putExtra(image, topimage);

        //共享元素
        intent.putExtra("transition", "share");

        //将原先的跳转改成如下方式，注意这里面的第三个参数决定了ActivityTwo 布局中的android:transitionName的值，它们要保持一致
        Bundle shareTransition = ActivityOptions.makeSceneTransitionAnimation(
                mActivity, imageView, "shareTransition").toBundle();
//        mActivity.StartActivityWithTransitionAnim(intent);
        mActivity.startActivity(intent,
                shareTransition);
    }

    //设置各个Item之间的间距
    public class SpacesItemDecoration extends RecyclerView.ItemDecoration {

        private int space;

        public SpacesItemDecoration(int space) {
            this.space = space;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view,
                                   RecyclerView parent, RecyclerView.State state) {

            if (parent.getChildLayoutPosition(view) == 0) {
                return;
            }
            outRect.left = space;
            outRect.right = space;
            outRect.bottom = space;
            outRect.top = space;
            // Add top margin only for the first item to avoid double space between items
//            if (parent.getChildAdapterPosition(view) < 4) {
//                if (parent.getChildAdapterPosition(view) % 2 == 1) {
//                    outRect.left = 2 * space;
//                } else {
//                    outRect.right = 2 * space;
//                }
//            }else {
//                if (parent.getChildAdapterPosition(view) % 2 == 0) {
//                    outRect.left = 2 * space;
//                } else {
//                    outRect.right = 2 * space;
//                }
//            }

        }
    }

    class TopNewsAdapter extends PagerAdapter {

        private final Context context;

        TopNewsAdapter(Context context) {
            this.context = context;
        }

        @Override
        public int getCount() {
            return Integer.MAX_VALUE;
        }

        @Override
        public int getItemPosition(Object object) {
            return super.getItemPosition(object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            if (topNewsList.size() == 0) {
                return null;
            }
            position = position % topNewsList.size();

            View view = View.inflate(context, R.layout.item_top_news, null);
            final ImageView imageView = (ImageView) view.findViewById(R.id.iv_item);
            final TopNewsItem topNewsItem = topNewsList.get(position);
            PicassoImageLoader.getInstance().
                    displayTopNewsImage(getContext(), topNewsItem.topimage, imageView);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    enterVideoDetailActivity(ANIM_VIDEO_CODE, IMAGE, topNewsItem.topimage, imageView);
                }
            });

            container.addView(view);
            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }
    }

    class DramaAdapter extends RecyclerView.Adapter<DramaViewHolder> {
        public int HEADER = 0;
        public int ITEM = 1;
        private View rootView;

        public DramaAdapter(View mHeadView) {
            this.rootView = mHeadView;
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
            View view = LayoutInflater.from(mActivity).inflate(R.layout.item_drama, parent, false);
            return new DramaViewHolder(view);
        }

        @Override
        public void onBindViewHolder(DramaViewHolder holder, int position) {
            if (isHeader(position)) {
                StaggeredGridLayoutManager.LayoutParams params = new StaggeredGridLayoutManager.LayoutParams
                        (ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params.setFullSpan(true);
                holder.view.setLayoutParams(params);
                return;
            }
            //去掉头布局占得位置
            final int index = --position;

            final DramaItem dramaItem = mDramaList.get(position);
            holder.title.setText(dramaItem.name);
            ImageView imageView = holder.image;
            Integer height = Integer.valueOf(dramaItem.height);
            PicassoImageLoader.getInstance().
                    displayBangumiImage(getContext(), dramaItem.topimage, imageView);

            ViewGroup.LayoutParams params = imageView.getLayoutParams();
            params.height = height;
            holder.view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getContext(), VideoDetailActivity.class);
                    intent.putExtra("image", dramaItem.topimage);
                    mActivity.StartActivityWithTransitionAnim(intent);
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

    private class DramaViewHolder extends RecyclerView.ViewHolder {
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

    class RecommendAdapter extends RecyclerView.Adapter<RecommendViewHolder> {

        @Override
        public RecommendViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View view = LayoutInflater.from(mActivity).inflate(R.layout.item_recomment, parent, false);
            return new RecommendViewHolder(view);
        }

        @Override
        public void onBindViewHolder(RecommendViewHolder holder, final int position) {
            final RecommendItem recommendItem = mRecommendList.get(position);
            holder.title.setText("\u3000" + recommendItem.name);
            final ImageView imageView = holder.image;
            PicassoImageLoader.getInstance().
                    displayRecommendImage(getContext(), recommendItem.topimage, imageView);
            holder.online.setText(recommendItem.playCount);

            holder.rootView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    enterVideoDetailActivity(MATERIAL_VIDEO_CODE,
                            "image", recommendItem.topimage, imageView);
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

    private class RecommendViewHolder extends RecyclerView.ViewHolder {
        public TextView title;
        public ImageView image;
        public TextView online;
        public View rootView;

        public RecommendViewHolder(View itemView) {
            super(itemView);
            this.rootView = itemView;
            title = (TextView) itemView.findViewById(R.id.tv_title);
            image = (ImageView) itemView.findViewById(R.id.iv_item);
            online = (TextView) itemView.findViewById(R.id.tv_online);
        }
    }
}