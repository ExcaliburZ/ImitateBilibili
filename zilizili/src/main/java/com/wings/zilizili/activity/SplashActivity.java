package com.wings.zilizili.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.wings.zilizili.R;

import java.util.Timer;
import java.util.TimerTask;
/**
 * SplashActivity,进入的Splash页
* */
public class SplashActivity extends AppCompatActivity {

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
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                SplashActivity.this.startActivity(
                        new Intent(SplashActivity.this, MainActivity.class));
                SplashActivity.this.finish();
            }
        }, 1500);
    }

    private void doInBackground() {
    }

    class WebTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            return null;
        }
    }
}
