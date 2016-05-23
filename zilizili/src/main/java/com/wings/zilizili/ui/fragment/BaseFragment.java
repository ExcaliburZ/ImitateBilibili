package com.wings.zilizili.ui.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import com.wings.zilizili.GlobalConstant;
import com.wings.zilizili.R;
import com.wings.zilizili.activity.MainActivity;
import com.wings.zilizili.ui.widget.LowPrioritySwipeRefreshLayout;
import com.wings.zilizili.utils.OkHttpClientManager;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by wing on 2015/10/28.
 * HomeFragment中显示大量信息的Fragment的基类
 * 抽取了自动刷新的SwipeRefreshLayout,请求数据地址URL
 */
public abstract class BaseFragment extends Fragment {

    private static final String TAG = "BaseFragment";
    protected LowPrioritySwipeRefreshLayout mContentView;
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
                if (isRefreshing) {
                    return;
                }
                getDataWithOkHttp();
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
                getDataWithOkHttp();
            }
        });
    }

    /**
     * 初始化获取数据的地址
     *
     * @return 地址的URL
     */
    protected abstract String initURL();


    /**
     * 根据URL从服务器获取数据,利用RxJava调用解析数据和刷新界面的方法
     */
    protected void getDataWithOkHttp() {

        String url = GlobalConstant.TX_URL + URL;
        OkHttpClientManager.getInstance().getAsync(url, new Callback() {
            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                String responseStr = response.body().string();
                notifyDataMap(responseStr);
            }

            @Override
            public void onFailure(Call call, IOException e) {
                Log.i(TAG, "onFailure: failed");
            }
        });
    }

    private void notifyData(final Response response) {
        Observable.create(new Observable.OnSubscribe<Void>() {
            @Override
            public void call(Subscriber<? super Void> subscriber) {
                try {
                    String result = response.body().string();
                    decodeResult(result);
                    Log.i(TAG, "call: get with okHttp");
                    subscriber.onCompleted();
                } catch (IOException e) {
                    e.printStackTrace();
                    subscriber.onError(e);
                }
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Void>() {
                    @Override
                    public void onNext(Void aVoid) {
                    }

                    @Override
                    public void onCompleted() {
                        notifyDataRefresh();
                    }

                    @Override
                    public void onError(Throwable e) {
                    }
                });
    }

    private void notifyDataMap(String response) {
        Observable.just(response)
                .observeOn(Schedulers.io())
                .map(new Func1<String, Void>() {
                    @Override
                    public Void call(String s) {
                        decodeResult(s);
                        Log.i(TAG, "call: MAP");
                        return null;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        notifyDataRefresh();
                    }
                });

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
