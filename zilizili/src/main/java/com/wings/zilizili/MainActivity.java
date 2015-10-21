package com.wings.zilizili;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.wings.zilizili.fragment.HomeFragment;
import com.wings.zilizili.fragment.LeftMenuFragment;
import com.wings.zilizili.utils.ToastUtils;

public class MainActivity extends AppCompatActivity implements LeftMenuFragment.OnFragmentInteractionListener {

    //    private ActionBarDrawerToggle mDrawerToggle;
//    private DrawerLayout mDrawerLayout;
//    private Toolbar mToolbar;
//    //    private FloatingActionButton mFab;
//    private TabLayout mTabLayout;
//    private ViewPager mMainContent;
//    private ArrayList<String> mTabLists;
    private FragmentManager mFragmentManager;
//    private ArrayList<Fragment> mFragmentLists;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

//    private void findView() {
//        mToolbar = $(R.id.toolbar);
//        mDrawerLayout = $(R.id.dl_main);
////        mFab = $(R.id.fab);
//        mTabLayout = $(R.id.tab_layout);
//        mMainContent = $(R.id.vp_main);
//        mTabLists = new ArrayList<String>() {{
//            add("番剧");
//            add("推荐");
//            add("分区");
//            add("关注");
//            add("发现");
//        }};

//        mFragmentLists = new ArrayList<Fragment>() {{
//            add(new DramaFragment());
//            add(new DramaFragment());
//            add(new DramaFragment());
//            add(new DramaFragment());
//            add(new DramaFragment());
//        }};
//    }

    private void init() {
        mFragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = mFragmentManager.beginTransaction();
        transaction.replace(R.id.content_layout, new HomeFragment());
        transaction.commit();
//        findView();
//        setToggleLeftDrawer();
//        mToolbar.setTitle(R.string.home_top_default_nickname);
//        initViewPager();
//        setListener();
    }


//    private void initViewPager() {
//        HomeAdapter homeAdapter = new HomeAdapter(mFragmentManager);
//        mMainContent.setAdapter(homeAdapter);
////        mTabLayout.setTabMode(TabLayout.MODE_FIXED);//设置tab模式，当前为系统默认模式
//        mTabLayout.setupWithViewPager(mMainContent);//将TabLayout和ViewPager关联起来。
//        mTabLayout.setTabsFromPagerAdapter(homeAdapter);//给Tabs设置适配器
//    }

//    private void setListener() {
//        mFab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
//    }

//    private void setToggleLeftDrawer() {
//        setSupportActionBar(mToolbar);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar, R.string.drawer_open,
//                R.string.drawer_close);
//        mDrawerToggle.syncState();
//        mDrawerLayout.setDrawerListener(mDrawerToggle);
//    }

//    class HomeAdapter extends FragmentPagerAdapter {
//
//        public HomeAdapter(FragmentManager fm) {
//            super(fm);
//        }
//
//        @Override
//        public Fragment getItem(int position) {
//            return mFragmentLists.get(position);
//        }
//
//        @Override
//        public int getCount() {
//            return mFragmentLists.size();
//        }
//
//        @Override
//        public CharSequence getPageTitle(int position) {
//            return mTabLists.get(position);
//        }
//    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_game) {
            ToastUtils.showToast(this, "game");
            System.out.println("Activity");
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    private <T extends View> T $(int resId) {
        return (T) super.findViewById(resId);
    }
}
