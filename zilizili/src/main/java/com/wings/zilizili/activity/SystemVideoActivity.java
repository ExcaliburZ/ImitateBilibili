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
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
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
import com.wings.zilizili.ui.widget.CustomVideoView;
import com.wings.zilizili.domain.VideoInfo;
import com.wings.zilizili.utils.TimeUtils;
import com.wings.zilizili.utils.ToastUtils;

import java.util.ArrayList;


public class SystemVideoActivity extends Activity implements View.OnClickListener {

    private static final int UPDATE_PROCESS = 0;
    private static final int UPDATE_TIME_BATTERY = 1;
    private static final int HIDE_CONTROLLER = 2;
    private static final int HIDE_MESSAGE = 3;
    private static final int HIDE_LOCK = 4;
    private static final String TAG = "VideoActivity";
    private CustomVideoView mVideoView;
    private TextView title;
    private TextView time;
    private TextView currentTime;
    private TextView totalTime;
    private TextView message;
    private ImageButton back;
    private ImageButton lock;
    private ImageButton pre;
    private ImageButton pause;
    private ImageButton next;
    private ImageButton fullscreen;
    private ImageButton loadingBack;
    private ImageView battery;
    private ImageView loadImage;
    private ImageView iv_is_lock;
    private ImageView iv_is_lock2;
    private SeekBar process;
    private BatteryManager mBatteryManager;
    private AudioManager mAudioManager;
    private BatteryReceiver receiver;
    private RelativeLayout rl_controller;
    private RelativeLayout rl_loading;
    private boolean isDestroy;
    private int position;
    private boolean isHide = true;
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
                    rl_controller.setVisibility(View.GONE);
                    isHide = true;
                    break;
                case HIDE_MESSAGE:
                    message.setVisibility(View.INVISIBLE);
                    break;
                case HIDE_LOCK:
                    dismissLock();
                    break;

            }
        }
    };
    private GestureDetector detector;
    private boolean isFullScreen;
    private Point outSize;
    private int maxVolume;
    private int startX;
    private int startY;
    private int perY;
    private boolean isRegister;
    private Uri uri;
    private int preX;
    private MediaPlayer mMediaPlayer;
    private boolean isLock;
    private WindowManager.LayoutParams lp;
    private SharedPreferences sp;
    private ArrayList<VideoInfo> mVideoInfoList;
    private VideoInfo info;
    private AnimationDrawable rocketAnimation;
    private boolean isPrepared;
    private boolean isPause = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setNavigation();
        setContentView(R.layout.system_activity_video);
        init();
        getData();
        setData();
        registerReceiver();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);
        detector.onTouchEvent(event);
        if (isLock) {
            handler.removeMessages(HIDE_LOCK);
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    iv_is_lock.setVisibility(View.VISIBLE);
                    iv_is_lock2.setVisibility(View.VISIBLE);
                    break;
                case MotionEvent.ACTION_MOVE:
                    break;
                case MotionEvent.ACTION_UP:
                    System.out.println("ACTION_UP");
                    handler.removeMessages(HIDE_LOCK);
                    handler.sendEmptyMessageDelayed(HIDE_LOCK, 2000);
                    break;
            }
            return true;
        }
        System.out.println("unlock");
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
                /*if (Math.abs(dy) < 5 && Math.abs(dx) < 5) {
                    startX = (int) event.getRawX();
                    startY = (int) event.getRawY();
                    break;
                }*/
                if (Math.abs(dy) > Math.abs(dx)) {
                    if (Math.abs(dy) > perY) {
                        if (startX < outSize.x / 2) {
                            if (dy < 0) {
                                lp.screenBrightness += 0.1;
                                formatBrightness();
                                getWindow().setAttributes(lp);
                            } else {
                                lp.screenBrightness -= 0.1;
                                formatBrightness();
                                getWindow().setAttributes(lp);
                            }
                            updateBrightness();
                        } else {
                            mAudioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC,
                                    dy > 0 ? AudioManager.ADJUST_LOWER : AudioManager.ADJUST_RAISE, 0);
                            updateVolume();
                        }
                        message.setVisibility(View.VISIBLE);
                        startY = (int) event.getRawY();
                        startX = (int) event.getRawX();
                    }
                } else {
                    if (Math.abs(dx) > preX) {
                        mVideoView.pause();
                        rl_controller.setVisibility(View.VISIBLE);
                        handler.removeMessages(UPDATE_PROCESS);
                        if (dx > 0) {
                            long next = mVideoView.getCurrentPosition() + 1000;
                            if (next > mVideoView.getDuration()) {
                                finish();
                            }
                            Log.i(TAG, "next");
                            mVideoView.seekTo((int) next);
                            process.setProgress((int) next);
                        } else {
                            long pre = mVideoView.getCurrentPosition() - 1000;
                            if (pre < 0) {
                                finish();
                            }
                            Log.i(TAG, "preprepre");
                            mVideoView.seekTo((int) pre);
                            process.setProgress((int) pre);
                        }
                        if (isPause) {
                            pause();
                            Log.i(TAG, "isPause");
                        }
                        handler.removeMessages(HIDE_CONTROLLER);
                        sendHideMessage();
                        handler.sendEmptyMessage(UPDATE_PROCESS);
                        ToastUtils.showToast(this, TimeUtils.LongToStr((long) mVideoView.getCurrentPosition()));
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
        message.setText("亮度 : " + brightness + "%");
    }

    private void sendHideToast() {
        handler.sendEmptyMessageDelayed(HIDE_MESSAGE, 1000);
    }

    private void updateVolume() {
        int streamVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        int percent = (int) ((float) streamVolume / maxVolume * 100);
        sp.edit().putInt("streamVolume", streamVolume).apply();
        message.setText("音量 : " + percent + "%");
    }

    private void init() {
        restoreBrightnessAndVolume();
        findView();
        setListener();
    }

    private void setListener() {
        back.setOnClickListener(this);
        lock.setOnClickListener(this);
        pre.setOnClickListener(this);
        pause.setOnClickListener(this);
        loadingBack.setOnClickListener(this);
        next.setOnClickListener(this);
        fullscreen.setOnClickListener(this);
        iv_is_lock.setOnClickListener(this);
        iv_is_lock2.setOnClickListener(this);
    }

    private void findView() {
        mVideoView = F(R.id.vv_content);
        loadImage = F(R.id.iv_load);
        rl_loading = F(R.id.rl_loading);
        message = F(R.id.tv_message);
        title = F(R.id.tv_title);
        time = F(R.id.tv_time);
        loadingBack = F(R.id.ib_loading_back);
        currentTime = F(R.id.tv_current_time);
        totalTime = F(R.id.tv_total_time);
        battery = F(R.id.iv_battery);
        process = F(R.id.sb_process);
        back = F(R.id.ib_back);
        lock = F(R.id.iv_lock);
        pre = F(R.id.iv_pre);
        pause = F(R.id.iv_pause);
        next = F(R.id.iv_next);
        fullscreen = F(R.id.iv_full);
        rl_controller = F(R.id.rl_controller);
        iv_is_lock = F(R.id.iv_is_lock);
        iv_is_lock2 = F(R.id.iv_is_lock2);
    }

    private void restoreBrightnessAndVolume() {
        lp = getWindow().getAttributes();
        sp = getSharedPreferences("config", MODE_PRIVATE);
        lp.screenBrightness = sp.getFloat("screenBrightness", 0.5f);
        getWindow().setAttributes(lp);
        mBatteryManager = (BatteryManager) getSystemService(BATTERY_SERVICE);
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

    @Override
    protected void onDestroy() {
        if (isRegister) {
            unregisterReceiver(receiver);
            receiver = null;
        }
        isDestroy = true;
        super.onDestroy();
    }

    private void updateTimeAndBattery() {
        time.setText(TimeUtils.getCurrentTime());
    }

    private void updateProcess() {
        handler.removeMessages(UPDATE_PROCESS);
        process.setProgress(mVideoView.getCurrentPosition() > process.getProgress()
                ? mVideoView.getCurrentPosition() : process.getProgress());

        int bufferPercentage = mVideoView.getBufferPercentage();
        if (bufferPercentage != 0) {
            float i = (float) bufferPercentage / 100f;
            int bufferProcess = (int) (i * process.getMax());
            System.out.println("bufferProcess :: " + bufferProcess);
            process.setSecondaryProgress(bufferProcess);

        }
        handler.sendEmptyMessageDelayed(UPDATE_PROCESS, 400);
    }

    private void setData() {
        System.out.println("setData");
        loadImage.setBackgroundResource(R.drawable.loading_tv_chan);

        rocketAnimation = (AnimationDrawable) loadImage.getBackground();
        rocketAnimation.start();

        if (uri == null) {
            mVideoView.setVideoURI(Uri.parse(info.Data));
            title.setText(info.Title);
        } else {
            mVideoView.setVideoURI(uri);
            title.setText(uri.toString());
        }
        time.setText(TimeUtils.getCurrentTime());
        perY = (int) (outSize.y / maxVolume / 1.5);
        preX = (outSize.x / 100);
        updateVolume();
        mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                System.out.println("onPrepared");
                rocketAnimation.stop();
                rl_loading.setVisibility(View.INVISIBLE);
                int duration = mp.getDuration();
                totalTime.setText(TimeUtils.LongToStr((long) mp.getDuration()));
                process.setMax(duration);
                process.setProgress(0);
                isPrepared = true;
            }
        });
        mVideoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                if (mVideoInfoList == null || position == mVideoInfoList.size() - 1 || mVideoInfoList.size() == 0) {
                    finish();
                }

                playNext();
            }
        });
        mVideoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                Toast.makeText(SystemVideoActivity.this, "播放失败", Toast.LENGTH_SHORT).show();
                SystemVideoActivity.this.finish();
                return true;
            }
        });
        System.out.println("start");
        mVideoView.start();
        process.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    mVideoView.seekTo(progress);
                    if (isPause) {
                        pause();
//                        mMediaPlayer.pause();
                        Log.i(TAG, "isPause");
                    }
                }
                String s = TimeUtils.LongToStr((long) progress);
                currentTime.setText(s);
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

    private void sendHideMessage() {
        System.out.println("sendHideMessage");
        if (!isHide) {
            handler.sendEmptyMessageDelayed(HIDE_CONTROLLER, 3000);
        }
    }

    private void registerReceiver() {
        isRegister = true;
        //注册
        receiver = new BatteryReceiver();
        registerReceiver(receiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        handler.sendEmptyMessage(UPDATE_PROCESS);
        handler.sendEmptyMessage(UPDATE_TIME_BATTERY);

        detector = new GestureDetector(this,
                new GestureDetector.SimpleOnGestureListener() {
                    @Override
                    public boolean onDoubleTapEvent(MotionEvent e) {
                        if (isLock) {
//                            lock();
                            return true;
                        }
                        pause();
                        return true;
                    }

                    @Override
                    public void onLongPress(MotionEvent e) {
                        if (isLock) {
//                            lock();
                        } else {
                            changeScreenSize();
                        }
                        super.onLongPress(e);
                    }

                    @Override
                    public boolean onSingleTapConfirmed(MotionEvent e) {
                        if (isLock) {

                        } else {
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
        System.out.println("dismiss");
        iv_is_lock.setVisibility(View.INVISIBLE);
        iv_is_lock2.setVisibility(View.INVISIBLE);
    }

    private void changeControllerState() {
        handler.removeMessages(HIDE_CONTROLLER);
        if (isHide && isPrepared) {
            rl_controller.setVisibility(View.VISIBLE);
            isHide = false;
            sendHideMessage();
        } else {
            rl_controller.setVisibility(View.INVISIBLE);
            isHide = true;
        }
    }

    private void playNext() {
        if (mVideoInfoList == null || position == mVideoInfoList.size() - 1
                || mVideoInfoList.size() == 0) {
            return;
        }
        info = mVideoInfoList.get(++position);
        setData();
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
            case R.id.iv_pause:
                pause();
                break;
            case R.id.iv_next:
                playNext();
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
        System.out.println("lock");
        handler.removeMessages(HIDE_LOCK);
        iv_is_lock.setVisibility(View.VISIBLE);
        iv_is_lock2.setVisibility(View.VISIBLE);
        handler.sendEmptyMessageDelayed(HIDE_LOCK, 2000);
    }

    private void changeScreenSize() {
        isFullScreen = !isFullScreen;
//        mVideoView.isFullScreen = isFullScreen;
        if (isFullScreen) {
            mVideoView.setVideoSize(outSize.y, outSize.x, true);
        } else {
            mVideoView.setVideoSize(
                    mVideoView.getMeasuredHeight(), mVideoView.getMeasuredWidth(), true);
        }
        fullscreen.setImageResource(isFullScreen ? R.mipmap.ic_fullscreen_exit_white_36dp
                : R.mipmap.ic_fullscreen_white_36dp);
    }

    private void playPre() {
        if (mVideoInfoList == null || position == 0) {
            return;
        }
        info = mVideoInfoList.get(--position);
        setData();
    }

    private void pause() {
        isPause = !isPause;
        pause.setImageResource
                (isPause ? R.mipmap.ic_play_arrow_white_36dp : R.mipmap.ic_pause_white_36dp);
        if (isPause) {
            mVideoView.pause();
            handler.removeMessages(HIDE_CONTROLLER);
        } else {
            mVideoView.start();
            handler.sendEmptyMessageDelayed(HIDE_CONTROLLER, 3000);
        }
    }


    class BatteryReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            //获取当前电量
            int level = intent.getIntExtra("level", 0);
            if (level > 90) {
                battery.setImageResource(R.mipmap.ic_battery_std_white_48dp);
            } else if (level > 80) {
                battery.setImageResource(R.mipmap.ic_battery_90_white_48dp);
            } else if (level > 50) {
                battery.setImageResource(R.mipmap.ic_battery_80_white_48dp);
            } else if (level > 30) {
                battery.setImageResource(R.mipmap.ic_battery_50_white_48dp);
            } else if (level > 20) {
                battery.setImageResource(R.mipmap.ic_battery_30_white_48dp);
            } else {
                battery.setImageResource(R.mipmap.ic_battery_20_white_48dp);
            }
        }
    }
}
