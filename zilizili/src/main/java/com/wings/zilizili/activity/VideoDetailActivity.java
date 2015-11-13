package com.wings.zilizili.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.wings.zilizili.R;
import com.wings.zilizili.domain.VideoDetailInfo;
import com.wings.zilizili.fragment.DramaFragment;
import com.wings.zilizili.global.GlobalConstant;
import com.wings.zilizili.utils.SingleBitmapUtils;
import com.wings.zilizili.utils.ToastUtils;

import derson.com.multipletheme.colorUi.widget.ColorImageView;
import derson.com.multipletheme.colorUi.widget.ColorToolbar;

public class VideoDetailActivity extends BaseActivity implements View.OnClickListener {

    private BitmapUtils mBitmapUtils;

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
        mColorToolbar.setNavigationIcon(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        mColorToolbar.setTitle("av" + av);
        setSupportActionBar(mColorToolbar);
        //设置取消默认标题
        //getSupportActionBar().setDisplayShowTitleEnabled(false);
        mCover = $(R.id.cover);
        String uri = getIntent().getStringExtra(DramaFragment.IMAGE);
        mBitmapUtils.display(mCover, uri);

        mNick = $(R.id.nick);
        mInfoViews = $(R.id.info_views);
        mInfoDate = $(R.id.info_date);
        mPlayButton = $(R.id.play);
        mInfoDanmakus = $(R.id.info_danmakus);
        mTitle = $(R.id.text);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_detail);
        init();
    }

    private void init() {
        initData();
        initView();
        setListener();
        getDataFromServer();
    }

    private void setListener() {
        mPlayButton.setOnClickListener(this);
    }

    protected void getDataFromServer() {
        HttpUtils http = new HttpUtils();
        String uri = TextUtils.equals(av, "123456") ? "video_1.json" : "video_2.json";
        http.send(HttpRequest.HttpMethod.GET,
                GlobalConstant.TX_URL + uri,
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
                                VideoDetailActivity.this.runOnUiThread(new Runnable() {
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
        mBitmapUtils = SingleBitmapUtils.getInstance().getBitmapUtils();

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
                back();
                break;
            default:
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
//                Intent intent = new Intent(this, VideoActivity.class);
                Intent intent = new Intent(this, SystemVideoActivity.class);
                intent.setData(Uri.parse(mVideoInfo.video_uri));
                startActivity(intent);
                break;
        }
    }
}
