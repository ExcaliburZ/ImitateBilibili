package com.wings.zilizili.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.wings.zilizili.R;
import com.wings.zilizili.activity.MainActivity;
import com.wings.zilizili.customView.LowPrioritySwipeRefreshLayout;
import com.wings.zilizili.GlobalConstant;
import com.wings.zilizili.utils.MySingleton;

/**
 * Created by wing on 2015/10/28.
 * HomeFragment中显示大量信息的Fragment的基类
 * 抽取了自动刷新的SwipeRefreshLayout,请求数据地址URL
 */
public abstract class BaseFragment extends Fragment {

    //TODO
    //refresh may cause ANR ,caused by input event,need fixed.
    protected LowPrioritySwipeRefreshLayout mContentView;
    protected View mRootView;
    protected MainActivity mActivity;
    protected String URL;
    protected boolean isRefreshing;
    protected ImageLoader mImageLoader;


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
//                getDataFromServer();
                getDataWithVolley();
            }
        });
    }

    /**
     * 初始化需要的控件
     */
    protected void initView() {
        URL = initURL();
        mContentView = (LowPrioritySwipeRefreshLayout) mRootView.findViewById(R.id.sl_refresh);
        mContentView.setColorSchemeColors(Color.BLUE);
        mRootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mContentView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                mContentView.setRefreshing(true);
                isRefreshing = true;
                getDataWithVolley();
            }
        });
        mImageLoader = MySingleton.getInstance(mActivity).getImageLoader();

    }

    /**
     * 初始化获取数据的地址
     *
     * @return 地址的URL
     */
    protected abstract String initURL();

    /**
     * 根据URL从服务器获取数据,并调用解析数据和刷新界面的方法
     */
    protected void getDataWithVolley() {
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(mActivity);
        StringRequest jsonRequest = new StringRequest(
                Request.Method.GET,
                GlobalConstant.TX_URL + URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(final String result) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                decodeResult(result);
                                System.out.println("over with volley");
                                mActivity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        notifyDataRefresh();
                                    }
                                });
                            }
                        }).start();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        System.out.println("onErrorResponse");
                    }
                }
        );
        // Add the request to the RequestQueue.
        queue.add(jsonRequest);
    }

    /**
     * 获取到的数据解析完毕,可以刷新界面了
     */
    protected void notifyDataRefresh() {

    }

    /**
     * 解析获取到的JSON数据
     */
    protected void decodeResult(String result) {

    }

    protected <T extends View> T $(int resId) {
        return (T) mRootView.findViewById(resId);
    }

}
