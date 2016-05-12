package com.wings.zilizili.ui.fragment;


import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.wings.zilizili.R;

import org.greenrobot.eventbus.EventBus;

/**
 * 在MainActivity中加载的侧边栏Fragment
 */
public class LeftMenuFragment extends Fragment implements View.OnClickListener {
    private final static String TAG = "LeftMenuFragment";
    private final static String ITEMSELECTED = "itemSelected";
    private NavigationView mNavigationView;
    private View mContentView;
    private ImageView iv_theme;
    private ViewGroup container;
    private int CurrentSelectedItemId;


    public LeftMenuFragment() {
        // Required empty public constructor
    }


    public void setCurrentSelectedItemId(int currentSelectedItemId) {
        this.CurrentSelectedItemId = currentSelectedItemId;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        this.container = container;
        mContentView = inflater.inflate(R.layout.navigation_layout, this.container, false);
        init();
        return mContentView;
    }

    private void init() {
        mNavigationView = $(R.id.nv_main);

        iv_theme = $(R.id.iv_night);
        iv_theme.setOnClickListener(this);
        mNavigationView.setNavigationItemSelectedListener(new NavigationView.
                OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                int itemId = menuItem.getItemId();
                EventBus.getDefault().post(new LeftMenuClickEvent(false, itemId));
                return false;
            }
        });
        mNavigationView.setCheckedItem(
                CurrentSelectedItemId == 0 ? R.id.nav_home : CurrentSelectedItemId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    private <T extends View> T $(int resId) {
        return (T) mContentView.findViewById(resId);
    }

    @Override
    public void onClick(View v) {
        EventBus.getDefault().post(new LeftMenuClickEvent(true, v.getId()));
    }

}
