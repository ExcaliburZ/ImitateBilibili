package com.wings.zilizili.fragment;


import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.wings.zilizili.MainActivity;
import com.wings.zilizili.R;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment implements View.OnClickListener {
    private View mContentView;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private Toolbar mToolbar;
    private TabLayout mTabLayout;
    private ViewPager mMainContent;
    private ArrayList<String> mTabLists;
    private FragmentManager mFragmentManager;
    private ArrayList<Fragment> mFragmentLists;
    private MainActivity mActivity;
    private ImageView homeDrawer;


    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mContentView = inflater.inflate(R.layout.fragment_home, container, false);
        return mContentView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mActivity = (MainActivity) getActivity();
        init();
    }

    private void init() {
        findView();
        setToggleLeftDrawer();
        initViewPager();
        setListener();
    }

    private void findView() {
        mToolbar = $(R.id.toolbar);
        mDrawerLayout = (DrawerLayout) mActivity.findViewById(R.id.dl_main);
        mTabLayout = $(R.id.tab_layout);
        mMainContent = $(R.id.vp_main);
        homeDrawer = $(R.id.iv_drawer);
        mTabLists = new ArrayList<String>() {{
            add("番剧");
            add("推荐");
            add("分区");
            add("关注");
            add("发现");
        }};
        mFragmentManager = mActivity.getSupportFragmentManager();
        mFragmentLists = new ArrayList<Fragment>() {{
            add(new DramaFragment());
            add(new DramaFragment());
            add(new DramaFragment());
            add(new DramaFragment());
            add(new DramaFragment());
        }};
    }

    private void initViewPager() {
        HomeAdapter homeAdapter = new HomeAdapter(mFragmentManager);
        mMainContent.setAdapter(homeAdapter);
//        mTabLayout.setTabMode(TabLayout.MODE_FIXED);//设置tab模式，当前为系统默认模式
        mTabLayout.setupWithViewPager(mMainContent);//将TabLayout和ViewPager关联起来。
        mTabLayout.setTabsFromPagerAdapter(homeAdapter);//给Tabs设置适配器
    }

    private void setListener() {
        homeDrawer.setOnClickListener(this);
    }

    private void setToggleLeftDrawer() {
        mActivity.setSupportActionBar(mToolbar);
        mActivity.getSupportActionBar().setDisplayShowTitleEnabled(false);
        //默认带旋转效果的Toggle
//        mActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        mDrawerToggle = new ActionBarDrawerToggle(mActivity, mDrawerLayout, mToolbar, R.string.drawer_open,
//                R.string.drawer_close);
//        mDrawerToggle.syncState();
//        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_drawer:
                boolean isLeftMenuOpen = mDrawerLayout.isDrawerOpen(Gravity.LEFT);
                if (isLeftMenuOpen) {
                    mDrawerLayout.closeDrawer(Gravity.LEFT);
                } else {
                    mDrawerLayout.openDrawer(Gravity.LEFT);
                }
                break;
        }
    }

    class HomeAdapter extends FragmentPagerAdapter {

        public HomeAdapter(FragmentManager fm) {
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

        @Override
        public CharSequence getPageTitle(int position) {
            return mTabLists.get(position);
        }
    }

    private <T extends View> T $(int resId) {
        return (T) mContentView.findViewById(resId);
    }


}
