package com.wings.zilizili.fragment;


import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.wings.zilizili.R;

/**
 * 在MainActivity中加载的侧边栏Fragment
 */
public class LeftMenuFragment extends Fragment implements View.OnClickListener {
    private NavigationView mNavigationView;
    private View mContentView;
    private ImageView iv_theme;
    private ViewGroup container;
    private onLeftMenuSelectedListener mLeftMenuSelectedListner;


    public LeftMenuFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mLeftMenuSelectedListner = (onLeftMenuSelectedListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnHeadlineSelectedListener");
        }
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
        mNavigationView.setCheckedItem(R.id.nav_home);
        mNavigationView.setNavigationItemSelectedListener(new NavigationView.
                OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                int itemId = menuItem.getItemId();
                mLeftMenuSelectedListner.onLeftMenuSelected(itemId);
                return false;
            }

        });
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
        mLeftMenuSelectedListner.onItemClicked(v);
    }

    public void invalidateLeftMenu() {
    }

    public interface onLeftMenuSelectedListener {
        void onLeftMenuSelected(int itemId);

        void onItemClicked(View v);
    }
}
