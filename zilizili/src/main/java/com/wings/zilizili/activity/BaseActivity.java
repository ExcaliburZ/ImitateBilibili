package com.wings.zilizili.activity;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;

import com.wings.zilizili.R;

import derson.com.multipletheme.colorUi.util.SharedPreferencesMgr;

/**
 * Created by wing on 2015/10/27.
 */
public class BaseActivity extends AppCompatActivity {
    public final int BLUE_THEME = 0;
    public final int PINK_THEME = 1;

    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (SharedPreferencesMgr.getInt("theme", 0) == BLUE_THEME) {
            setTheme(R.style.AppTheme_NoActionBar_Blue);
        } else {
            setTheme(R.style.AppTheme_NoActionBar_Pink);
        }
    }
}
