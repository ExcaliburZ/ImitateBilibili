package com.wings.zilizili.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.graphics.drawable.AnimationDrawable;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.wings.zilizili.R;
import com.wings.zilizili.domain.VideoInfo;
import com.wings.zilizili.utils.TimeUtils;
import com.wings.zilizili.utils.ToastUtils;

import java.io.IOException;
import java.util.ArrayList;

import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;


public class NewIjkVideoActivity extends Activity implements View.OnClickListener, SurfaceHolder.Callback {

    private static final int UPDATE_PROCESS = 0;
    private static final int UPDATE_TIME_BATTERY = 1;
    private static final int HIDE_CONTROLLER = 2;
    private static final int HIDE_MESSAGE = 3;
    private static final int HIDE_LOCK = 4;
    private static final String TAG = "VideoActivity";
    private TextView mTitle;
    private TextView mTime;
    private TextView mCurrentTime;
    private TextView mTotalTime;
    private TextView mMessage;
    private ImageButton mBack;
    private ImageButton mLock;
    private ImageButton mPre;
    private ImageButton mPause;
    private ImageButton mNext;
    private ImageButton mFullscreen;
    private ImageButton mLoadingBack;
    private ImageView mBattery;
    private ImageView mLoadImage;
    private ImageView mLockLeft;
    private ImageView mLockRight;
    private SeekBar mProcess;
    private AudioManager mAudioManager;
    private BatteryReceiver mBatteryReceiver;
    private RelativeLayout mControllerLayout;
    private RelativeLayout mLoadingLayout;
    private boolean isDestroy;
    private int position;
    private boolean isHide = true;
    private GestureDetector mDetector;
    private boolean isFullScreen;
    private Point outSize;
    private int maxVolume;
    private int startX;
    private int startY;
    private int perY;
    private boolean isRegister;
    private Uri uri;
    private int preX;
    private boolean isLock;
    private WindowManager.LayoutParams lp;
    private SharedPreferences sp;
    private ArrayList<VideoInfo> mVideoInfoList;
    private VideoInfo info;
    private AnimationDrawable rocketAnimation;
    private boolean isPrepared;
    private boolean isPause = false;
    private IjkMediaPlayer mPlayer;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (isDestroy)
                return;
            switch (msg.what) {
                case UPDATE_PROCESS:
                    updateProcess();
                    break;
                case UPDATE_TIME_BATTERY:
                    updateTimeAndBattery();
                    sendEmptyMessageDelayed(UPDATE_TIME_BATTERY, 30000);
                    break;
                case HIDE_CONTROLLER:
                    if (isHide)
                        return;
                    mControllerLayout.setVisibility(View.GONE);
                    isHide = true;
                    break;
                case HIDE_MESSAGE:
                    mMessage.setVisibility(View.INVISIBLE);
                    break;
                case HIDE_LOCK:
                    dismissLock();
                    break;

            }
        }
    };
    private SurfaceView surfaceView;
    private SurfaceHolder holder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setNavigation();
        setContentView(R.layout.new_system_video);
        init();
        getData();
        setData();
        registerReceiver();
    }

    private void setData() {
        mLoadImage.setBackgroundResource(R.drawable.loading_tv_chan);

        rocketAnimation = (AnimationDrawable) mLoadImage.getBackground();
        rocketAnimation.start();
//        mPlayer = new IjkMediaPlayer();
        mPlayer = new IjkMediaPlayer();
//        mPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec", 1);
        mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            mPlayer.setDataSource(getApplicationContext(), uri);
        } catch (IOException e) {
            e.printStackTrace();
        }
        mTitle.setText(uri.toString());
        mTime.setText(TimeUtils.getCurrentTime());
        perY = (int) (outSize.y / maxVolume / 1.5);
        preX = (outSize.x / 100);
        updateVolume();
        mPlayer.setOnPreparedListener(new IMediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(IMediaPlayer mp) {
                System.out.println("onPrepared");
                rocketAnimation.stop();
                mLoadingLayout.setVisibility(View.INVISIBLE);
                long duration = mp.getDuration();
                mTotalTime.setText(TimeUtils.LongToStr(mp.getDuration()));
                mProcess.setMax((int) duration);
                mProcess.setProgress(0);
                isPrepared = true;
                mPlayer.start();
            }
        });

        mPlayer.setOnCompletionListener(
                new IMediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(IMediaPlayer mp) {
                        if (mVideoInfoList == null || isLastMovie() || mVideoInfoList.size() == 0) {
                            finish();
                        }
                        playNext();
                    }
                }

        );
        mPlayer.setOnErrorListener(new IMediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(IMediaPlayer mp, int what, int extra) {
                Toast.makeText(NewIjkVideoActivity.this, "播放失败", Toast.LENGTH_SHORT).show();
                NewIjkVideoActivity.this.finish();
                return true;
            }
        });


        mPlayer.prepareAsync();

        mProcess.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    mPlayer.seekTo(progress);
                    if (isPause) {
                        pause();
                        Log.i(TAG, "isPause");
                    }
                }
                String s = TimeUtils.LongToStr((long) progress);
                mCurrentTime.setText(s);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                handler.removeMessages(UPDATE_PROCESS);
                handler.removeMessages(HIDE_CONTROLLER);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                handler.sendEmptyMessage(UPDATE_PROCESS);
                handler.sendEmptyMessageDelayed(HIDE_CONTROLLER, 3000);
            }
        });
    }

    private void getData() {
        uri = getIntent().getData();
        if (uri == null) {
            mVideoInfoList = (ArrayList<VideoInfo>) getIntent().getSerializableExtra("list");
            position = getIntent().getIntExtra("position", 0);
            info = mVideoInfoList.get(position);
        }
        outSize = new Point();
        getWindowManager().getDefaultDisplay().getSize(outSize);
    }

    private void init() {
        restoreBrightnessAndVolume();
        findView();
        setListener();
    }

    private void findView() {
        mLoadImage = F(R.id.iv_load);
        mLoadingLayout = F(R.id.rl_loading);
        mMessage = F(R.id.tv_message);
        mTitle = F(R.id.tv_title);
        mTime = F(R.id.tv_time);
        mLoadingBack = F(R.id.ib_loading_back);
        mCurrentTime = F(R.id.tv_current_time);
        mTotalTime = F(R.id.tv_total_time);
        mBattery = F(R.id.iv_battery);
        mProcess = F(R.id.sb_process);
        mBack = F(R.id.ib_back);
        mLock = F(R.id.iv_lock);
        mPre = F(R.id.iv_pre);
        mPause = F(R.id.iv_pause);
        mNext = F(R.id.iv_next);
        mFullscreen = F(R.id.iv_full);
        mControllerLayout = F(R.id.rl_controller);
        mLockLeft = F(R.id.iv_is_lock);
        mLockRight = F(R.id.iv_is_lock2);
        surfaceView = (SurfaceView) this.findViewById(R.id.video_surface);
        //给SurfaceView添加CallBack监听
        holder = surfaceView.getHolder();
        holder.addCallback(this);
    }

    private void setListener() {
        mBack.setOnClickListener(this);
        mLock.setOnClickListener(this);
        mPre.setOnClickListener(this);
        mPause.setOnClickListener(this);
        mLoadingBack.setOnClickListener(this);
        mNext.setOnClickListener(this);
        mFullscreen.setOnClickListener(this);
        mLockLeft.setOnClickListener(this);
        mLockRight.setOnClickListener(this);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);
        mDetector.onTouchEvent(event);
        if (isLock) {
            handler.removeMessages(HIDE_LOCK);
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    mLockLeft.setVisibility(View.VISIBLE);
                    mLockRight.setVisibility(View.VISIBLE);
                    break;
                case MotionEvent.ACTION_MOVE:
                    break;
                case MotionEvent.ACTION_UP:
                    handler.removeMessages(HIDE_LOCK);
                    handler.sendEmptyMessageDelayed(HIDE_LOCK, 2000);
                    break;
            }
            return true;
        }
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startX = (int) event.getRawX();
                startY = (int) event.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                handler.removeMessages(HIDE_MESSAGE);
                if (startX == -1) {
                    startX = (int) event.getX();
                    startY = (int) event.getY();
                    break;
                }
                int x = (int) event.getRawX();
                int y = (int) event.getRawY();
                int dy = y - startY;
                int dx = x - startX;
                if (Math.abs(dy) > Math.abs(dx)) {
                    if (Math.abs(dy) > perY) {
                        if (startX < outSize.x / 2) {
                            if (dy < 0) {
                                raiseBrightness();
                            } else {
                                lowerBrightness();
                            }
                            updateBrightness();
                        } else {
                            mAudioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC,
                                    dy > 0 ? AudioManager.ADJUST_LOWER : AudioManager.ADJUST_RAISE, 0);
                            updateVolume();
                        }
                        mMessage.setVisibility(View.VISIBLE);
                        startY = (int) event.getRawY();
                        startX = (int) event.getRawX();
                    }
                } else {
                    if (Math.abs(dx) > preX) {
                        mPlayer.pause();
                        mControllerLayout.setVisibility(View.VISIBLE);
                        handler.removeMessages(UPDATE_PROCESS);
                        if (dx > 0) {
                            long next = mPlayer.getCurrentPosition() + 1000;
                            if (next > mPlayer.getDuration()) {
                                finish();
                            }
                            mPlayer.seekTo((int) next);
                            mProcess.setProgress((int) next);
                        } else {
                            long pre = mPlayer.getCurrentPosition() - 1000;
                            if (pre < 0) {
                                finish();
                            }
                            mPlayer.seekTo((int) pre);
                            mProcess.setProgress((int) pre);
                        }
                        if (isPause) {
                            pause();
                        }

                        handler.removeMessages(HIDE_CONTROLLER);
                        sendHideMessage();
                        handler.sendEmptyMessage(UPDATE_PROCESS);

                        ToastUtils.showToast(this, TimeUtils.LongToStr((long)
                                mPlayer.getCurrentPosition()));
                        startX = (int) event.getRawX();
                        startY = (int) event.getRawY();
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                startY = -1;
                startX = -1;
                sendHideToast();
                break;
        }
        return true;
    }

    private void lowerBrightness() {
        lp.screenBrightness -= 0.1;
        setWindowsBrightness();
    }

    private void raiseBrightness() {
        lp.screenBrightness += 0.1;
        setWindowsBrightness();
    }

    private void setWindowsBrightness() {
        formatBrightness();
        getWindow().setAttributes(lp);
    }

    private void sendHideToast() {
        handler.sendEmptyMessageDelayed(HIDE_MESSAGE, 1000);
    }

    private void formatBrightness() {
        if (lp.screenBrightness > 1.0f) {
            lp.screenBrightness = 1f;
        }
        if (lp.screenBrightness < 0f) {
            lp.screenBrightness = 0f;
        }
    }


    private void updateBrightness() {
        float screenBrightness = lp.screenBrightness;
        int brightness = (int) (screenBrightness * 100);
        sp.edit().putFloat("screenBrightness", screenBrightness).apply();
        mMessage.setText(R.string.brightness_show + brightness + R.string.percent_symbol);
    }

    private void updateVolume() {
        int streamVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        int percent = (int) ((float) streamVolume / maxVolume * 100);
        sp.edit().putInt("streamVolume", streamVolume).apply();
        mMessage.setText(getString(R.string.volume_show) + percent + getString(R.string.percent_symbol));
    }

    private void restoreBrightnessAndVolume() {
        lp = getWindow().getAttributes();
        sp = getSharedPreferences("config", MODE_PRIVATE);
        lp.screenBrightness = sp.getFloat("screenBrightness", 0.5f);
        getWindow().setAttributes(lp);
        mAudioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        maxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        int streamVolume = sp.getInt("streamVolume", maxVolume / 2);
        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, streamVolume, 0);
    }

    private void setNavigation() {
        Window window = getWindow();
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        WindowManager.LayoutParams params = window.getAttributes();
        params.systemUiVisibility = View.SYSTEM_UI_FLAG_LOW_PROFILE;
        window.setAttributes(params);
    }

    @Override
    protected void onDestroy() {
        if (isRegister) {
            unregisterReceiver(mBatteryReceiver);
            mBatteryReceiver = null;
        }
        isDestroy = true;
        super.onDestroy();
    }

    private void updateTimeAndBattery() {
        mTime.setText(TimeUtils.getCurrentTime());
    }

    private void updateProcess() {
        handler.removeMessages(UPDATE_PROCESS);
        mProcess.setProgress(mPlayer.getCurrentPosition() > mProcess.getProgress()
                ? (int) mPlayer.getCurrentPosition() : mProcess.getProgress());

//        long videoCachedPackets = mPlayer.getVideoCachedPackets();
//        if (bufferPercentage != 0) {
//            float i = (float) bufferPercentage / 100f;
//            int bufferProcess = (int) (i * mProcess.getMax());
//            System.out.println("bufferProcess :: " + bufferProcess);
//            mProcess.setSecondaryProgress(bufferProcess);
//
//        }
        handler.sendEmptyMessageDelayed(UPDATE_PROCESS, 400);
    }

    private void sendHideMessage() {
        System.out.println("sendHideMessage");
        if (!isHide) {
            handler.sendEmptyMessageDelayed(HIDE_CONTROLLER, 3000);
        }
    }

    private void registerReceiver() {
        isRegister = true;
        //注册
        mBatteryReceiver = new BatteryReceiver();
        registerReceiver(mBatteryReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        handler.sendEmptyMessage(UPDATE_PROCESS);
        handler.sendEmptyMessage(UPDATE_TIME_BATTERY);

        mDetector = new GestureDetector(this,
                new GestureDetector.SimpleOnGestureListener() {
                    @Override
                    public boolean onDoubleTapEvent(MotionEvent e) {
                        if (isLock) {
                            return true;
                        }
                        pause();
                        return true;
                    }

                    @Override
                    public void onLongPress(MotionEvent e) {
                        if (!isLock) {
                            changeScreenSize();
                        }
                        super.onLongPress(e);
                    }

                    @Override
                    public boolean onSingleTapConfirmed(MotionEvent e) {
                        if (!isLock) {
                            changeControllerState();
                        }
                        return super.onSingleTapConfirmed(e);
                    }


                    @Override
                    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                        return super.onScroll(e1, e2, distanceX, distanceY);
                    }

                });

    }

    private void dismissLock() {
        mLockLeft.setVisibility(View.INVISIBLE);
        mLockRight.setVisibility(View.INVISIBLE);
    }

    private void changeControllerState() {
        handler.removeMessages(HIDE_CONTROLLER);
        if (isHide && isPrepared) {
            mControllerLayout.setVisibility(View.VISIBLE);
            isHide = false;
            sendHideMessage();
        } else {
            mControllerLayout.setVisibility(View.INVISIBLE);
            isHide = true;
        }
    }

    private void playNext() {
        if (listIsEmpty() || isLastMovie()) {
            return;
        }
        info = mVideoInfoList.get(++position);
        setData();
    }

    private void playPre() {
        if (listIsEmpty() || position == 0) {
            return;
        }
        position--;
        info = mVideoInfoList.get(position);
        setData();
    }

    private boolean isLastMovie() {
        return position == mVideoInfoList.size() - 1;
    }

    private boolean listIsEmpty() {
        return mVideoInfoList == null || mVideoInfoList.size() == 0;
    }

    private <T extends View> T F(int resId) {
        return (T) super.findViewById(resId);
    }

    @Override
    public void onClick(View v) {
        handler.removeMessages(HIDE_CONTROLLER);
        switch (v.getId()) {
            case R.id.ib_back:
            case R.id.ib_loading_back:
                finish();
                break;
            case R.id.iv_lock:
                lock();
                break;
            case R.id.iv_pre:
                playPre();
                break;
            case R.id.iv_next:
                playNext();
                break;
            case R.id.iv_pause:
                pause();
                break;
            case R.id.iv_full:
                changeScreenSize();
                break;
            case R.id.iv_is_lock:
            case R.id.iv_is_lock2:
                unLock();
        }
        handler.sendEmptyMessageDelayed(HIDE_CONTROLLER, 3000);
    }

    private void unLock() {
        isLock = false;
        handler.sendEmptyMessage(HIDE_LOCK);
        changeControllerState();
    }

    private void lock() {
        if (!isLock) {
            isLock = true;
            handler.sendEmptyMessage(HIDE_CONTROLLER);
        }
        handler.removeMessages(HIDE_LOCK);
        mLockLeft.setVisibility(View.VISIBLE);
        mLockRight.setVisibility(View.VISIBLE);
        handler.sendEmptyMessageDelayed(HIDE_LOCK, 2000);
    }

    private void changeScreenSize() {
        isFullScreen = !isFullScreen;
        if (isFullScreen) {
//            mPlayer.
        } else {
        }
        mFullscreen.setImageResource(isFullScreen ? R.mipmap.ic_fullscreen_exit_white_36dp
                : R.mipmap.ic_fullscreen_white_36dp);
    }


    private void pause() {
        isPause = !isPause;
        mPause.setImageResource
                (isPause ? R.mipmap.ic_play_arrow_white_36dp : R.mipmap.ic_pause_white_36dp);
        if (isPause) {
            mPlayer.pause();
            handler.removeMessages(HIDE_CONTROLLER);
        } else {
            mPlayer.start();
            handler.sendEmptyMessageDelayed(HIDE_CONTROLLER, 3000);
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        // 当SurfaceView中的Surface被创建的时候被调用
        //在这里我们指定MediaPlayer在当前的Surface中进行播放
        mPlayer.setDisplay(holder);

        //在指定了MediaPlayer播放的容器后，我们就可以使用prepare或者prepareAsync来准备播放了
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    @Override
    protected void onPause() {
        super.onPause();
        mPlayer.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isPrepared) {
            mPlayer.start();
        }
    }


    class BatteryReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            //获取当前电量
            int level = intent.getIntExtra("level", 0);
            //图片间隔不均匀,无法使用表驱动法
            if (level > 90) {
                mBattery.setImageResource(R.mipmap.ic_battery_std_white_48dp);
            } else if (level > 80) {
                mBattery.setImageResource(R.mipmap.ic_battery_90_white_48dp);
            } else if (level > 50) {
                mBattery.setImageResource(R.mipmap.ic_battery_80_white_48dp);
            } else if (level > 30) {
                mBattery.setImageResource(R.mipmap.ic_battery_50_white_48dp);
            } else if (level > 20) {
                mBattery.setImageResource(R.mipmap.ic_battery_30_white_48dp);
            } else {
                mBattery.setImageResource(R.mipmap.ic_battery_20_white_48dp);
            }
        }
    }
}
