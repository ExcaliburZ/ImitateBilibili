package com.wings.zilizili.activity;

import android.animation.Animator;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.readystatesoftware.systembartint.SystemBarTintManager;
import com.wings.zilizili.R;
import com.wings.zilizili.customView.NoScrollViewPager;
import com.wings.zilizili.fragment.HistoryFragment;
import com.wings.zilizili.fragment.HomeFragment;
import com.wings.zilizili.utils.ToastUtils;

import java.util.ArrayList;

import derson.com.multipletheme.colorUi.util.ColorUiUtil;
import derson.com.multipletheme.colorUi.util.SharedPreferencesMgr;

public class MainActivity extends BaseActivity {

    private FragmentManager mFragmentManager;
    private ArrayList<Fragment> mFragmentLists;
    private DrawerLayout mDrawerLayout;
    private FrameLayout mContent;
    private NoScrollViewPager vp_content;
    private SystemBarTintManager tintManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // create our manager instance after the content view is set
        tintManager = new SystemBarTintManager(this);
        // enable status bar tint
        tintManager.setStatusBarTintEnabled(true);
        // enable navigation bar tint
        tintManager.setNavigationBarTintEnabled(true);
        init();
    }

    private void init() {
        System.out.println("init");
        mDrawerLayout = $(R.id.dl_main);
//        mContent = $(R.id.content_layout);
        vp_content = $(R.id.vp_content);
        mFragmentLists = new ArrayList<Fragment>() {
            {
                add(new HomeFragment());
                add(new HistoryFragment());
                add(new HistoryFragment());
                add(new HistoryFragment());
                add(new HistoryFragment());
                add(new HistoryFragment());
            }
        };
        vp_content.setAdapter(new MainAdapter(getSupportFragmentManager()));
        vp_content.setOffscreenPageLimit(10);
//        mContent.addView(mViewPager);
//        changeFragment(0);
    }

    public void changeFragment(int position) {
//        mFragmentManager = getSupportFragmentManager();
//        FragmentTransaction transaction = mFragmentManager.beginTransaction();
//        transaction.replace(R.id.content_layout, mFragmentLists.get(position));
//        transaction.commit();
        vp_content.setCurrentItem(position, false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id) {
            case R.id.action_game:
                ToastUtils.showToast(this, "game");
//                Utils.changeToTheme(this, Utils.THEME_DEFAULT);
                return true;
            case R.id.action_download:
//                Utils.changeToTheme(this, Utils.THEME_WHITE);
                return true;
        }
        return super.onOptionsItemSelected(item);

    }

    private void changeColor() {
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

    public void closeLeftMenu() {
        mDrawerLayout.closeDrawer(GravityCompat.START);
    }

    public void openLeftMenu() {
        mDrawerLayout.openDrawer(GravityCompat.START);
    }

    public boolean getLeftMenuIsOpened() {
        return mDrawerLayout.isDrawerOpen(GravityCompat.START);
    }

    public void changeLeftMenuState() {
        boolean state = getLeftMenuIsOpened();
        if (state) {
            closeLeftMenu();
        } else {
            openLeftMenu();
        }
    }

    public void changeTheme() {
        if (SharedPreferencesMgr.getInt("theme", 0) == BLUE_THEME) {
            setTheme(R.style.AppTheme_NoActionBar_Pink);
            // set a custom tint color for all system bars
            tintManager.setTintColor(getResources().getColor(R.color.pink_dark));
            SharedPreferencesMgr.setInt("theme", PINK_THEME);
        } else {
            setTheme(R.style.AppTheme_NoActionBar_Blue);
            // set a custom tint color for all system bars
            tintManager.setTintColor(getResources().getColor(R.color.colorPrimaryDark));
            SharedPreferencesMgr.setInt("theme", BLUE_THEME);
        }
        changeColor();
    }

    class MainAdapter extends FragmentStatePagerAdapter {

        public MainAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentLists.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentLists.size();
        }
    }

    private <T extends View> T $(int resId) {
        return (T) super.findViewById(resId);
    }
}
