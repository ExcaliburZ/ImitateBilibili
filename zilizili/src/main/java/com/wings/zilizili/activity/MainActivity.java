package com.wings.zilizili.activity;

import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;

import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.wings.zilizili.GlobalConstant;
import com.wings.zilizili.R;
import com.wings.zilizili.ui.fragment.HistoryFragment;
import com.wings.zilizili.ui.fragment.HomeFragment;
import com.wings.zilizili.ui.fragment.LeftMenuFragment;
import com.wings.zilizili.ui.widget.NoScrollViewPager;
import com.wings.zilizili.utils.ToastUtils;

import java.util.ArrayList;

/**
 * 应用的主Activity,使用MainContent部分ViewPager和侧边栏Fragment来构成主要界面
 */
public class MainActivity extends BaseActivity
        implements LeftMenuFragment.OnLeftMenuSelectedListener {

    private ArrayList<Fragment> mFragmentLists;
    private DrawerLayout mDrawerLayout;
    private NoScrollViewPager vp_content;
    private long[] mHits;
    private int mItemId;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTransitionAnim();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    private void setTransitionAnim() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // inside your activity (if you did not enable transitions in your theme)
            getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
            // set an exit transition
//            getWindow().setExitTransition(new Fade());
//            getWindow().setEnterTransition(new AutoTransition());
        }
    }

    private void init() {
        mDrawerLayout = $(R.id.dl_main);
        vp_content = $(R.id.vp_content);
        mHits = new long[2];
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
    }

    private void changeFragment(int position) {
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

        switch (id) {
            case R.id.action_game:
                ToastUtils.showToast(this, "game activity");
                return true;
            case R.id.action_download:
                ToastUtils.showToast(this, "download");
                return true;
        }
        return super.onOptionsItemSelected(item);

    }

    private void rebuildLeftMenuFragment() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        LeftMenuFragment leftMenuFragment = new LeftMenuFragment();
        leftMenuFragment.setCurrentSelectedItemId(mItemId);
        transaction.replace(R.id.fg_left_menu, leftMenuFragment, "com.wings.leftmenu");
        transaction.commit();
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

    @Override
    public void onLeftMenuSelected(int itemId) {
        switch (itemId) {
            case R.id.nav_home:
                changeFragment(GlobalConstant.HOME_FRAGMENT);
                this.mItemId = itemId;
                break;
            case R.id.nav_histories:
                changeFragment(GlobalConstant.HISTORY_FRAGMENT);
                this.mItemId = itemId;
                break;
            case R.id.nav_favorites:
                this.mItemId = itemId;
                changeFragment(2);
                break;
            case R.id.nav_following:
                this.mItemId = itemId;
                changeFragment(3);
                break;
            case R.id.nav_pay:
                this.mItemId = itemId;
                changeFragment(4);
                break;
            case R.id.nav_theme:
                changeTheme();
                break;
            //android:checkable="false"
            //可以响应点击事件,但是不会被选中
            case R.id.nav_offline_manager:
                changeFragment(GlobalConstant.HOME_FRAGMENT);
                break;
        }
        closeLeftMenu();
    }

    @Override
    public void onItemClicked(View v) {
        switch (v.getId()) {
            case R.id.iv_night:
            case R.id.iv_edit:
                changeTheme();
                rebuildLeftMenuFragment();
                break;
        }
    }

    private <T extends View> T $(int resId) {
        return (T) super.findViewById(resId);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                //Google 多击处理逻辑
                System.arraycopy(mHits, 1, mHits, 0, mHits.length - 1);
                mHits[mHits.length - 1] = SystemClock.uptimeMillis();

                if (mHits[0] >= (SystemClock.uptimeMillis() - 2000)) {
                    //在(SystemClock.uptimeMillis()- 2000) ~ SystemClock.uptimeMillis()之间
                    //满足多击条件
                    finish();
                } else {
                    ToastUtils.showToast(this, "再按一次退出");
                }
                return true;
        }
        return super.onKeyDown(keyCode, event);
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

}
