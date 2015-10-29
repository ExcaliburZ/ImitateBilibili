package com.wings.zilizili.activity;

import android.animation.Animator;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;

import com.readystatesoftware.systembartint.SystemBarTintManager;
import com.wings.zilizili.R;

import derson.com.multipletheme.colorUi.util.ColorUiUtil;
import derson.com.multipletheme.colorUi.util.SharedPreferencesMgr;

/**
 * Created by wing on 2015/10/27.
 */
public class BaseActivity extends AppCompatActivity {
    public final int BLUE_THEME = 0;
    public final int PINK_THEME = 1;
    protected SystemBarTintManager tintManager;

    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // create our manager instance after the content view is set
        tintManager = new SystemBarTintManager(this);
        // enable status bar tint
        tintManager.setStatusBarTintEnabled(true);
        // enable navigation bar tint
        tintManager.setNavigationBarTintEnabled(true);

        if (SharedPreferencesMgr.getInt("theme", 0) == BLUE_THEME) {
            changeTheme(BLUE_THEME);
        } else {
            changeTheme(PINK_THEME);
        }

    }

    public void changeTheme() {
        if (SharedPreferencesMgr.getInt("theme", 0) == BLUE_THEME) {
            changeTheme(PINK_THEME);
            SharedPreferencesMgr.setInt("theme", PINK_THEME);
        } else {
            changeTheme(BLUE_THEME);
            SharedPreferencesMgr.setInt("theme", BLUE_THEME);
        }
        changeColor();
    }

    public void changeTheme(int type) {
        if (type == PINK_THEME) {
            setTheme(R.style.AppTheme_NoActionBar_Pink);
            // set a custom tint color for all system bars
            tintManager.setTintColor(getResources().getColor(R.color.pink_dark));
        } else {
            setTheme(R.style.AppTheme_NoActionBar_Blue);
            // set a custom tint color for all system bars
            tintManager.setTintColor(getResources().getColor(R.color.colorPrimaryDark));
        }
    }

    protected void changeColor() {
        final View rootView = getWindow().getDecorView();
        if (Build.VERSION.SDK_INT >= 14) {
            rootView.setDrawingCacheEnabled(true);
            rootView.buildDrawingCache(true);
            final Bitmap localBitmap = Bitmap.createBitmap(rootView.getDrawingCache());
            rootView.setDrawingCacheEnabled(false);
            if (null != localBitmap && rootView instanceof ViewGroup) {
                final View localView2 = new View(getApplicationContext());
                localView2.setBackgroundDrawable(new BitmapDrawable(getResources(), localBitmap));
                ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                ((ViewGroup) rootView).addView(localView2, params);
                localView2.animate().alpha(0).setDuration(400).setListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        ColorUiUtil.changeTheme(rootView, getTheme());
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        ((ViewGroup) rootView).removeView(localView2);
                        localBitmap.recycle();
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                }).start();
            }
        } else {
            ColorUiUtil.changeTheme(rootView, getTheme());
        }
    }
}
