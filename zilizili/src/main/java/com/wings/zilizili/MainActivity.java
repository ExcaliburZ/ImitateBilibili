package com.wings.zilizili;

import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.wings.zilizili.fragment.LeftMenuFragment;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements LeftMenuFragment.OnFragmentInteractionListener {

    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private Toolbar mToolbar;
    private FloatingActionButton mFab;
    private TabLayout mTabLayout;
    private ViewPager mMainContent;
    private ArrayList<String> mTabLists;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    private void findView() {
        mToolbar = $(R.id.toolbar);
        mDrawerLayout = $(R.id.dl_main);
        mFab = $(R.id.fab);
        mTabLayout = $(R.id.tab_layout);
        mMainContent = $(R.id.vp_main);
        mTabLists = new ArrayList<String>() {{
            add("番剧");
            add("推荐");
            add("分区");
            add("关注");
            add("发现");
        }};
    }

    private void init() {
        findView();
        setToggleLeftDrawer();
        mToolbar.setTitle(R.string.home_top_default_nickname);
        initViewPager();
        setListener();
    }

    private void initViewPager() {
        for (String tab : mTabLists) {
            mTabLayout.addTab(mTabLayout.newTab().setText(tab));
        }
    }

    private void setListener() {
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    private void setToggleLeftDrawer() {
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar, R.string.drawer_open,
                R.string.drawer_close);
        mDrawerToggle.syncState();
        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }


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
