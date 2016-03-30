package com.wings.zilizili.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.transition.Fade;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.wings.zilizili.R;
import com.wings.zilizili.domain.VideoDetailInfo;
import com.wings.zilizili.ui.fragment.DramaFragment;
import com.wings.zilizili.GlobalConstant;
import com.wings.zilizili.utils.MySingleton;
import com.wings.zilizili.utils.ToastUtils;


import derson.com.multipletheme.colorUi.widget.ColorImageView;
import derson.com.multipletheme.colorUi.widget.ColorToolbar;

public class VideoDetailActivity extends BaseActivity implements View.OnClickListener {

    private ImageLoader mImageLoader;

    private ColorToolbar mColorToolbar;
    private String av;
    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private ColorImageView mDownload;
    private ColorImageView mAddFav;
    private ColorImageView mComment;
    private ColorImageView mShare;
    private LinearLayout mActionModeBar;

    private NetworkImageView mCover;
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
        String uri = getIntent().getStringExtra(DramaFragment.IMAGE);

        mCover.setImageUrl(uri, mImageLoader);
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
    }

    private void setTransitionAnim() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // inside your activity (if you did not enable transitions in your theme)
            getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
            // set an exit transition
            getWindow().setExitTransition(new Fade());
            getWindow().setEnterTransition(new Fade());
            System.out.println("setExitTransition");
        }
    }

    private void init() {
        initData();
        initView();
        setListener();
        getDataWithVolley();
    }

    private void setListener() {
        mPlayButton.setOnClickListener(this);
    }

    /**
     * 根据URL从服务器获取数据,并调用解析数据和刷新界面的方法
     */
    protected void getDataWithVolley() {
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);
        String uri = TextUtils.equals(av, "123456") ? "video_1.json" : "video_2.json";
        StringRequest jsonRequest = new StringRequest(
                Request.Method.GET,
                GlobalConstant.TX_URL + uri,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(final String result) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                decodeResult(result);
                                System.out.println("over with volley");
                                VideoDetailActivity.this.runOnUiThread(new Runnable() {
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
        mImageLoader = MySingleton.getInstance(this).getImageLoader();
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
        }
        return super.onOptionsItemSelected(item);
    }

    private void back() {
        this.finish();
    }

    private <T extends View> T $(int resId) {
        return (T) super.findViewById(resId);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.play:
                Intent intent = new Intent(this, SystemVideoActivity.class);
                intent.setData(Uri.parse(mVideoInfo.video_uri));
                StartActivityWithTransitionAnim(intent);
                break;
        }
    }
}
