package com.wings.zilizili.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.wings.zilizili.R;
import com.wings.zilizili.activity.MainActivity;
import com.wings.zilizili.customView.MySwipeRefreshLayout;
import com.wings.zilizili.global.GlobalConstant;

/**
 * Created by wing on 2015/10/28.
 * HomeFragment中显示大量信息的Fragment的基类
 * 抽取了自动刷新的SwipeRefreshLayout,请求数据地址URL
 */
public abstract class BaseFragment extends Fragment {

    protected MySwipeRefreshLayout mContentView;
    protected View mRootView;
    protected MainActivity mActivity;
    protected String URL;
    protected boolean isRefreshing;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mRootView = initRootView(inflater, container);
        mActivity = (MainActivity) getActivity();
        init();
        return mContentView;
    }

    protected abstract View initRootView(LayoutInflater inflater, ViewGroup container);

    protected void init() {
        initView();
        setListener();
    }

    protected void setListener() {
        //设置监听器
        mContentView.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
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

    protected void initView() {
        URL = initURL();
        mContentView = (MySwipeRefreshLayout) mRootView.findViewById(R.id.sl_refresh);
        mContentView.setColorSchemeColors(Color.BLUE);
        mRootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mContentView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                mContentView.setRefreshing(true);
                isRefreshing = true;
                getDataFromServer();
            }
        });
    }

    protected abstract String initURL();




    protected void getDataFromServer() {
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
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                decodeResult(result);
                                System.out.println("over");
                                mActivity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        notifyDataRefresh();
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

    protected void notifyDataRefresh() {

    }

    protected void decodeResult(String result) {

    }
}
