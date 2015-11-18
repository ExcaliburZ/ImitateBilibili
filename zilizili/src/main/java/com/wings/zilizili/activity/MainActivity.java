package com.wings.zilizili.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

import com.wings.zilizili.R;
import com.wings.zilizili.customView.NoScrollViewPager;
import com.wings.zilizili.fragment.HistoryFragment;
import com.wings.zilizili.fragment.HomeFragment;
import com.wings.zilizili.utils.ToastUtils;

import java.util.ArrayList;
/**
* 应用的主Activity,使用MainContent部分ViewPager和侧边栏Fragment来构成主要界面
* */
public class MainActivity extends BaseActivity {

    private FragmentManager mFragmentManager;
    private ArrayList<Fragment> mFragmentLists;
    private DrawerLayout mDrawerLayout;
    private FrameLayout mContent;
    private NoScrollViewPager vp_content;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
//            case R.id.action_download:
////                Utils.changeToTheme(this, Utils.THEME_WHITE);
//                return true;
        }
        return super.onOptionsItemSelected(item);

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
