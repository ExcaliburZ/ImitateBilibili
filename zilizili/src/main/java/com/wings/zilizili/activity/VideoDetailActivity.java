package com.wings.zilizili.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.wings.zilizili.GlobalConstant;
import com.wings.zilizili.R;
import com.wings.zilizili.domain.VideoDetailInfo;
import com.wings.zilizili.utils.OkHttpClientManager;
import com.wings.zilizili.utils.PicassoImageLoader;
import com.wings.zilizili.utils.ToastUtils;

import java.io.IOException;

import derson.com.multipletheme.colorUi.widget.ColorImageView;
import derson.com.multipletheme.colorUi.widget.ColorToolbar;
import okhttp3.Call;
import okhttp3.Callback;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Video详情页
 */
public class VideoDetailActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = "VideoDetailActivity";
    private static final String VIDEO_CODE = "123456";
    private ColorToolbar mColorToolbar;
    private String av;
    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private ColorImageView mDownload;
    private ColorImageView mAddFav;
    private ColorImageView mComment;
    private ColorImageView mShare;
    private LinearLayout mActionModeBar;
    private ImageView mCover;
    private TextView mNick;
    private TextView mTitle;
    private TextView mInfoViews;
    private TextView mInfoDanmakus;
    private TextView mInfoDate;
    private Button mPlayButton;
    private VideoDetailInfo mVideoInfo;

    private void initView() {
        mColorToolbar = $(R.id.nav_top_bar);
        mColorToolbar.setTitle("av" + av);
        setSupportActionBar(mColorToolbar);
        //设置取消默认标题
        //getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mCover = $(R.id.cover);
        String uri = getIntent().getStringExtra(GlobalConstant.IMAGE_URL);

        PicassoImageLoader.getInstance()
                .displayTopNewsImage(this, uri, mCover);
        mNick = $(R.id.nick);
        mInfoViews = $(R.id.info_views);
        mInfoDate = $(R.id.info_date);
        mPlayButton = $(R.id.play);
        mInfoDanmakus = $(R.id.info_danmakus);
        mTitle = $(R.id.text);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTransitionAnim();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_detail);
        init();
        Log.i(TAG, "onCreate: video");
    }

    private void setTransitionAnim() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // inside your activity (if you did not enable transitions in your theme)
            getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
            // set an exit transition
            //共享元素
//            getWindow().setExitTransition(new Slide(Gravity.LEFT));
            //将原先的跳转改成如下方式，注意这里面的第三个参数决定了ActivityTwo 布局中的android:transitionName的值，它们要保持一致
        }
    }

    private void init() {
        initData();
        initView();
        setListener();
        getDataWithOkHttp();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Log.i(TAG, "onBackPressed: video");
        this.finishAfterTransition();
    }

    private void setListener() {
        mPlayButton.setOnClickListener(this);
    }

    protected void getDataWithOkHttp() {
        String videoUrl = TextUtils.equals(av, VIDEO_CODE) ?
                GlobalConstant.VIDEO_MPG :
                GlobalConstant.VIDEO_MPG_BIG;
        String url = GlobalConstant.TX_URL + videoUrl;
        OkHttpClientManager.getInstance().getAsync(url, new Callback() {
            @Override
            public void onResponse(Call call, final okhttp3.Response response) throws IOException {
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

            @Override
            public void onFailure(Call call, IOException e) {
                Log.i(TAG, "onFailure: failed");
            }
        });
    }

    private void notifyDataRefresh() {
        setData();
    }

    private void setData() {
        mTitle.setText(mVideoInfo.title);
        mNick.setText(String.format(getString(R.string.video_upuser_fmt), mVideoInfo.up));
        mInfoDate.setText(String.format(getString(R.string.pubdate_fmt), mVideoInfo.date));
        mInfoDanmakus.setText(String.format(getString(R.string.video_danmakus_fmt), mVideoInfo.danmu));
        mInfoViews.setText(String.format(getString(R.string.video_plays_fmt), mVideoInfo.play));
        mPlayButton.setClickable(true);
    }

    private void decodeResult(String result) {
        Gson gson = new Gson();
        mVideoInfo = gson.fromJson(result, VideoDetailInfo.class);
    }


    private void initData() {
        //读取av号
        av = getIntent().getStringExtra("av");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_video_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
//        System.out.println(" item.getItemId() :: " + item.getItemId());
        switch (item.getItemId()) {
            case R.id.action_search:
                break;
            case R.id.action_other:
                ToastUtils.showToast(this, "action_other");
                break;
            case android.R.id.home:
                finishAfterTransition();
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    private void back() {
        this.finishAfterTransition();
    }

    private <T extends View> T $(int resId) {
        return (T) super.findViewById(resId);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.play:
                playVideo();
                break;
        }
    }

    private void playVideo() {
        //使用系统MediaPlayer的VideoView
        Intent intent = new Intent(this, SystemVideoActivity.class);
        //使用ijkMediaPlayer的VideoView
//        Intent intent = new Intent(this, IjkPlayerVideoActivity.class);
        //使用MediaPlayer和SurfaceView
//        Intent intent = new Intent(this, NewVideoActivity.class);
        //使用IjkMediaPlayer和SurfaceView
//        Intent intent = new Intent(this, NewIjkVideoActivity.class);
        intent.setData(Uri.parse(mVideoInfo.video_uri));
        StartActivityWithTransitionAnim(intent);
    }

}
