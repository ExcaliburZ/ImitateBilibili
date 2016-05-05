package com.wings.zilizili.activity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.wings.zilizili.R;

import java.util.Timer;
import java.util.TimerTask;

/**
 * SplashActivity,进入的Splash页
 */
public class SplashActivity extends AppCompatActivity {

    private static final String DEBUG_TAG = "SplashActivity";
    private static final int DELAY_TIME = 1500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        init();
    }

    private void init() {
        //开启子线程执行耗时操作,访问网络,缓存数据
        doInBackground();
        Timer timer = new Timer();
        //延迟特定的时间进入主界面
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                SplashActivity.this.startActivity(
                        new Intent(SplashActivity.this, MainActivity.class));
                SplashActivity.this.finish();
            }
        }, DELAY_TIME);
    }

    private void doInBackground() {
        isNetWorkConnected();
    }

    //检查WIFI和数据流量是否连接
    private void isNetWorkConnected() {
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        boolean isWifiConn = networkInfo.isConnected();
        networkInfo = connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        boolean isMobileConn = networkInfo.isConnected();
        Log.d(DEBUG_TAG, "Wifi connected: " + isWifiConn);
        Log.d(DEBUG_TAG, "Mobile connected: " + isMobileConn);
    }

    //检查是否有网络
    public boolean isOnline() {
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

}
