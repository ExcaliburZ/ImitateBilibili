package com.wings.zilizili.fragment;


import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
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
import com.wings.zilizili.domain.DramaItem;
import com.wings.zilizili.domain.RecommendItem;
import com.wings.zilizili.domain.TopNewsItem;
import com.wings.zilizili.utils.MyPauseOnScrollListener;

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
    private View mHeadView;
    private ArrayList<TopNewsItem> topNewsList;
    private ArrayList<RecommendItem> mRecommentList;
    private ArrayList<DramaItem> mDramaList;
    private MainActivity mActivity;
    private GridLayoutManager mGridManager;
    private StaggeredGridLayoutManager mStaggeredGridLayoutManager;
    private boolean isFirst;
    private TopNewsAdapter mTopNewsAdapter;
    private DramaAdapter mDramaAdapter;


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
        mHeadView = LayoutInflater.from(mActivity).inflate(R.layout.header_drama, null);
        topNews = F(R.id.vp_top);
        indicator = F(R.id.indicator);
        mRecyclerView = $(R.id.rv_drama);
        mStaggeredGridLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        mGridManager = new GridLayoutManager(mActivity, 2);
//        mStaggeredGridLayoutManager.
//        mRecyclerView.setLayoutManager(mGridManager)
        mRecyclerView.setLayoutManager(mStaggeredGridLayoutManager);
        //设置箭头的颜色
        mRecyclerView.setHasFixedSize(false);
        int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.spacing);
        mRecyclerView.addItemDecoration(new SpacesItemDecoration(spacingInPixels));

        mRefreshLayout.setColorSchemeColors(Color.BLUE);

        mContentView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mContentView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                mRefreshLayout.setRefreshing(true);
                isRefreshing = true;
                isFirst = true;
                getDataFromServer();
//                loadData(true);
            }
        });
        topNewsList = new ArrayList<>();
        mRecommentList = new ArrayList<>();
        mDramaList = new ArrayList<>();
    }

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

        mTopNewsAdapter = new TopNewsAdapter(mActivity);
        topNews.setAdapter(mTopNewsAdapter);
        indicator.setViewPager(topNews);
        mRefreshLayout.setRefreshing(false);
        mDramaAdapter = new DramaAdapter(mHeadView);
        mRecyclerView.setAdapter(mDramaAdapter);

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
//        if (!isFirst) {
//            mTopNewsAdapter.notifyDataSetChanged();
//            mDramaAdapter.notifyDataSetChanged();
//        }
        System.out.println("First");
        mTopNewsAdapter.notifyDataSetChanged();
        mDramaAdapter.notifyDataSetChanged();
//        mTopNewsAdapter = new TopNewsAdapter(mActivity);
//        topNews.setAdapter(mTopNewsAdapter);
//        indicator.setViewPager(topNews);
//        mDramaAdapter = new DramaAdapter(mHeadView);
//        mRecyclerView.setAdapter(mDramaAdapter);
        mRefreshLayout.setRefreshing(false);
//
        isRefreshing = false;
        isFirst = false;

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

    private <T extends View> T F(int resId) {
        return (T) mHeadView.findViewById(resId);
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
        private View headView;
        private BitmapUtils bitmapUtils;
        public int HEADER = 0;
        public int ITEM = 1;

        public DramaAdapter(View mHeadView) {
            bitmapUtils = new BitmapUtils(mActivity);
//            bitmapUtils.configDefaultLoadingImage(R.drawable)
            this.headView = mHeadView;
            mRecyclerView.addOnScrollListener(new MyPauseOnScrollListener(bitmapUtils, false, true));
        }

        public boolean isHeader(int position) {
            return position == 0;
        }

        @Override
        public int getItemViewType(int position) {
            return isHeader(position) ? HEADER : ITEM;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (viewType == HEADER) {
                return new ViewHolder(headView);
            }
            View view = LayoutInflater.from(mActivity).inflate(R.layout.item_drama, parent, false);
            ViewHolder holder = new ViewHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            if (isHeader(position)) {
                StaggeredGridLayoutManager.LayoutParams params = new StaggeredGridLayoutManager.LayoutParams
                        (ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params.setFullSpan(true);
                holder.view.setLayoutParams(params);
                return;
            }
            //去掉头布局占得位置
            position--;
            holder.title.setText(mDramaList.get(position).name);
            ImageView imageView = holder.image;
            bitmapUtils.display(imageView, mDramaList.get(position).topimage);
            Integer height = Integer.valueOf(mDramaList.get(position).height);

            ViewGroup.LayoutParams params = imageView.getLayoutParams();
            params.height = height;
//            imageView.setLayoutParams(params);

//             = new StaggeredGridLayoutManager.LayoutParams
//            System.out.println("position :: " + position);
//            StaggeredGridLayoutManager.LayoutParams layoutParams =
//                    (StaggeredGridLayoutManager.LayoutParams) holder.view.getLayoutParams();
//            if (isLeftItem(position)) {
//                layoutParams.setMargins(20, 10, 5, 10);
//            } else {
//                layoutParams.setMargins(5, 10, 20, 10);
//            }
//            holder.view.setLayoutParams(layoutParams);
        }


        private boolean isLeftItem(int position) {
            return position % 2 == 0;
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

    private class ViewHolder extends RecyclerView.ViewHolder {
        public TextView title;
        public ImageView image;
        public View view;

        public ViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            title = (TextView) itemView.findViewById(R.id.tv_title);
            image = (ImageView) itemView.findViewById(R.id.iv_item);
        }
    }
}
