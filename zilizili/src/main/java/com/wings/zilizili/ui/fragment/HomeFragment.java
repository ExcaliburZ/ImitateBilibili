package com.wings.zilizili.ui.fragment;


import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.wings.zilizili.R;
import com.wings.zilizili.activity.MainActivity;

import java.util.ArrayList;

/**
 * MainActivity的ViewPager中默认显示的主页Fragment
 * 含有一个FragmentList组成ViewPager来构成五个标签页
 */
public class HomeFragment extends Fragment implements View.OnClickListener {
    private View mContentView;
    private ActionBarDrawerToggle mDrawerToggle;
    private Toolbar mToolbar;
    private TabLayout mTabLayout;
    private ViewPager mMainContent;
    private ArrayList<String> mTabLists;
    private FragmentManager mFragmentManager;
    private ArrayList<Fragment> mFragmentLists;
    private MainActivity mActivity;
    private ImageView homeDrawer;
    private HomeAdapter mHomeAdapter;
    private FrameLayout mAvatar;


    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        System.out.println("Home onCreate:::::");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mContentView = inflater.inflate(R.layout.fragment_home, container, false);
        mActivity = (MainActivity) getActivity();
        init();
        return mContentView;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
//        System.out.println("mFragmentLists :: " + mFragmentLists.size());
    }

    private void init() {
        findView();
        setToggleLeftDrawer();
        initViewPager();
        setListener();
    }

    @Override
    public void onStop() {
        super.onStop();
        mMainContent = null;
    }

    private void findView() {
        mToolbar = $(R.id.toolbar);
        mTabLayout = $(R.id.tab_layout);
        mMainContent = $(R.id.vp_main);
        homeDrawer = $(R.id.iv_drawer);
        mAvatar = $(R.id.fl_avatar);
        mTabLists = new ArrayList<String>() {{
            add("番剧");
            add("推荐");
            add("分区");
            add("关注");
            add("发现");
        }};

        mFragmentManager = mActivity.getSupportFragmentManager();
        mFragmentLists = new ArrayList<Fragment>() {{
            add(new BangumiFragment());
            add(new BangumiFragment());
            add(new BangumiFragment());
            add(new BangumiFragment());
            add(new BangumiFragment());
        }};

    }

    private void initViewPager() {
        mHomeAdapter = new HomeAdapter(mFragmentManager);
        mMainContent.setAdapter(mHomeAdapter);
        mMainContent.setOffscreenPageLimit(1);
//        mTabLayout.setTabMode(TabLayout.MODE_FIXED);//设置tab模式，当前为系统默认模式
        mTabLayout.setupWithViewPager(mMainContent);//将TabLayout和ViewPager关联起来。
        mTabLayout.setTabsFromPagerAdapter(mHomeAdapter);//给Tabs设置适配器
    }

    private void setListener() {
        homeDrawer.setOnClickListener(this);
        mAvatar.setOnClickListener(this);
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
            case R.id.fl_avatar:
                mActivity.changeLeftMenuState();
                break;
        }
    }

    private <T extends View> T $(int resId) {
        return (T) mContentView.findViewById(resId);
    }

    class HomeAdapter extends FragmentStatePagerAdapter {

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


        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
//             super.destroyItem(container, position, object);
        }
    }


}
