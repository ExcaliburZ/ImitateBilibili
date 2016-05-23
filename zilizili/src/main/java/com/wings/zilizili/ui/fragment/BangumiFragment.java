package com.wings.zilizili.ui.fragment;


import android.graphics.Rect;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.google.gson.Gson;
import com.wings.zilizili.R;
import com.wings.zilizili.adapter.DramaAdapter;
import com.wings.zilizili.adapter.RecommendAdapter;
import com.wings.zilizili.adapter.TopNewsAdapter;
import com.wings.zilizili.domain.Data;
import com.wings.zilizili.domain.DataInfo;
import com.wings.zilizili.domain.RecommendItem;
import com.wings.zilizili.ui.widget.DramaRecyclerView;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

/**
 * BangumiFragment,HomeFragment中的番剧标签页Fragment
 * 主要利用为一个RecyclerView添加头布局完成主体设计
 * 头布局中又自带一个TopNews滚动条的ViewPager和其他一些数据
 */
public class BangumiFragment extends BaseFragment {

    public static final int START_AUTO_PLAY = 0;
    public static final int PAUSE_AUTO_PLAY = 1;
    private static String TAG = "BangumiFragment";
    private DramaRecyclerView mRecyclerView;
    private RecyclerView mGridView;
    private ViewPager mTopNewsViewPager;
    private View mHeadView;
    private ArrayList<RecommendItem> mRecommendList;
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
                    ResumePlay();
                    break;
                case PAUSE_AUTO_PLAY:
                    pausePlay();
                    break;
                default:
                    throw new RuntimeException("can not handler null message");
            }
        }
    };

    public BangumiFragment() {
        // Required empty public constructor
    }

    private void pausePlay() {
        mHandler.removeCallbacksAndMessages(null);
    }

    private void ResumePlay() {
        int currentItem = mTopNewsViewPager.getCurrentItem();
        mTopNewsViewPager.setCurrentItem(++currentItem);
        startPlay();
    }

    @Override
    public void onResume() {
        super.onResume();
        startPlay();
    }

    @Override
    public void onPause() {
        super.onPause();
        pausePlay();
        EventBus.getDefault().unregister(this);
    }

    protected void initView() {
        //调用父类的方法,初始化一些通用的控件
        super.initView();
        //初始化头布局及其控件
        mHeadView = LayoutInflater.from(mActivity).inflate(R.layout.header_drama, null);
        mTopNewsViewPager = findViewInHeadView(R.id.vp_top);
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

        mRecyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view,
                                       RecyclerView parent, RecyclerView.State state) {

                if (parent.getChildLayoutPosition(view) == 0) {
                    return;
                }
                int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.spacing);
                outRect.left = spacingInPixels;
                outRect.right = spacingInPixels;
                outRect.bottom = spacingInPixels;
                outRect.top = spacingInPixels;
            }
        });
        //填充需要的数据
        itemSpacing = getResources().getDimensionPixelSize(R.dimen.spacing_item);
        itemSize = getResources().getDimensionPixelSize(R.dimen.point_item_size);

        mRecommendList = new ArrayList<>();
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
        mTopNewsAdapter = new TopNewsAdapter(getContext(), null);
        mTopNewsViewPager.setAdapter(mTopNewsAdapter);

        mRecyclerView.setVisibility(View.INVISIBLE);
        mDramaAdapter = new DramaAdapter(getContext(), mHeadView, null);
        mRecyclerView.setAdapter(mDramaAdapter);

        mRecommendAdapter = new RecommendAdapter(getContext(), null);
        mGridView.setAdapter(mRecommendAdapter);
        mTopNewsViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
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
                position = position % mTopNewsAdapter.getTopNewsList().size();
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
        mTopNewsViewPager.setCurrentItem(0);

        //开启自动播放
        startPlay();
    }

    private void startPlay() {
        mHandler.sendEmptyMessageDelayed(START_AUTO_PLAY, 3000);
    }

    private void initRelativePointSet() {
        List topNewsList = mTopNewsAdapter.getTopNewsList();
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
            mTopNewsAdapter.setTopNewsList(data.topnews);
            mRecommendAdapter.setRecommendList(data.recomment);
            mDramaAdapter.setDramaList(data.drama);
        }
    }

    //从HeadView中获取控件
    private <T extends View> T findViewInHeadView(int resId) {
        return (T) mHeadView.findViewById(resId);
    }

}
