package com.wings.zilizili.fragment;


import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.viewpagerindicator.CirclePageIndicator;
import com.wings.zilizili.GlobalConstant;
import com.wings.zilizili.R;
import com.wings.zilizili.activity.MainActivity;
import com.wings.zilizili.customView.DramaRecyclerView;
import com.wings.zilizili.customView.MySwipeRefreshLayout;
import com.wings.zilizili.domain.Data;
import com.wings.zilizili.domain.DataInfo;
import com.wings.zilizili.domain.RecommendItem;
import com.wings.zilizili.domain.TopNewsItem;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class DramaFragment extends Fragment {
    private MySwipeRefreshLayout mRefreshLayout;
    private DramaRecyclerView mRecyclerView;
    private ViewPager topNews;
    private CirclePageIndicator indicator;
    private Boolean isRefreshing;
    private String URL = "list_1.json";
    private View mContentView;
    private Data data;
    private long startTime;
    private ArrayList<TopNewsItem> topNewsList;
    private ArrayList<RecommendItem> mRecommentList;
    private ArrayList<RecommendItem> mDramaList;
    private MainActivity mActivity;
    private GridLayoutManager mGridManager;
    private StaggeredGridLayoutManager mStaggeredGridLayoutManager;


    public DramaFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mContentView = inflater.inflate(R.layout.fragment_drama, container, false);
        mActivity = (MainActivity) getActivity();
        init();
        return mContentView;
    }

    private void init() {
        initView();
        setListener();
    }

    private void initView() {
        mRefreshLayout = $(R.id.sl_refresh);
        topNews = $(R.id.vp_top);
        indicator = $(R.id.indicator);
        mRecyclerView = $(R.id.rv_drama);
        mStaggeredGridLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        mGridManager = new GridLayoutManager(mActivity, 2);
//        mRecyclerView.setLayoutManager(mGridManager);
        mRecyclerView.setLayoutManager(mStaggeredGridLayoutManager);
        //设置箭头的颜色
        mRecyclerView.setHasFixedSize(false);
        mRefreshLayout.setColorSchemeColors(Color.BLUE);

        mContentView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mContentView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                mRefreshLayout.setRefreshing(true);
                isRefreshing = true;
                getDataFromServer();
//                loadData(true);
            }
        });
    }

    private void setListener() {
        //设置监听器
        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override //刷新时回调方法
            public void onRefresh() {
                System.out.println("onRefresh");
                if (isRefreshing) {
                    return;
                }
                getDataFromServer();
            }
        });
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    private void getDataFromServer() {
        startTime = SystemClock.uptimeMillis();
        HttpUtils http = new HttpUtils();
        http.send(HttpRequest.HttpMethod.GET,
                GlobalConstant.SERVER_URL + URL,
                new RequestCallBack<String>() {

                    @Override
                    public void onLoading(long total, long current, boolean isUploading) {
                    }

                    @Override
                    public void onSuccess(ResponseInfo<String> responseInfo) {
                        final String result = responseInfo.result;
//                        System.out.println("result ::" + result);
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                decodeResult(result);
                                //                        sleepMoment();
                                System.out.println("over");
                                mActivity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        setAdapter();
                                    }
                                });
                            }
                        }).start();
                    }

                    @Override
                    public void onFailure(HttpException error, String msg) {
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

    private void setAdapter() {
        System.out.println("setAdapter");
        topNews.setAdapter(new TopNewsAdapter(mActivity));
        indicator.setViewPager(topNews);
        isRefreshing = false;
        mRefreshLayout.setRefreshing(false);
        mRecyclerView.setAdapter(new DramaAdapter());
    }

    private void decodeResult(String result) {
        Gson gson = new Gson();
        DataInfo dataInfo = gson.fromJson(result, DataInfo.class);
        if (dataInfo.retcode / 100 == 2) {
            data = dataInfo.data;
            topNewsList = data.topnews;
            mRecommentList = data.recomment;
            mDramaList = data.drama;
        }
    }

    private <T extends View> T $(int resId) {
        return (T) mContentView.findViewById(resId);
    }

    class TopNewsAdapter extends PagerAdapter {

        private final BitmapUtils utils;
        private final Context context;

        TopNewsAdapter(Context context) {
            this.context = context;
            utils = new BitmapUtils(context);
            utils.configDefaultLoadingImage(R.drawable.topnews_item_default);
        }

        @Override
        public int getCount() {
            return topNewsList.size();
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View view = View.inflate(context, R.layout.item_top_news, null);
            View iv = view.findViewById(R.id.iv_item);
            utils.display(iv, topNewsList.get(position).topimage);
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

    class DramaAdapter extends RecyclerView.Adapter<ViewHolder> {
        private BitmapUtils bitmapUtils;

        DramaAdapter() {
            bitmapUtils = new BitmapUtils(mActivity);
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View view = LayoutInflater.from(mActivity).inflate(R.layout.item_drama, parent, false);
            ViewHolder holder = new ViewHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.title.setText(mDramaList.get(position).name);
            bitmapUtils.display(holder.image, mDramaList.get(position).topimage);
        }

        @Override
        public int getItemCount() {
            return mDramaList.size();
        }
    }

    private class ViewHolder extends RecyclerView.ViewHolder {
        public TextView title;
        public ImageView image;

        public ViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.tv_title);
            image = (ImageView) itemView.findViewById(R.id.iv_item);
        }
    }
}
