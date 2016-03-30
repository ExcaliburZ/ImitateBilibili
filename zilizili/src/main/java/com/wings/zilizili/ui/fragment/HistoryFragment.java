package com.wings.zilizili.ui.fragment;


import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.wings.zilizili.R;
import com.wings.zilizili.activity.MainActivity;

/**
 * MainActivity的ViewPager中可以选择显示的历史Fragment
 *
 */
public class HistoryFragment extends Fragment implements View.OnClickListener {


    private MainActivity mActivity;
    private CoordinatorLayout mContentView;
    private Toolbar mToolbar;
    private ImageButton ib_menu;


    public HistoryFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mContentView = (CoordinatorLayout) inflater.inflate(R.layout.fragment_history, container, false);
        mActivity = (MainActivity) getActivity();
        init();
        return mContentView;
    }

    private void init() {
        initView();
        setToggleLeftDrawer();
        setListener();
    }

    private void setListener() {
        ib_menu.setOnClickListener(this);
    }

    private void setToggleLeftDrawer() {
        //这句使Toolbar成为Actionbar,可以管理Activity的Menu
        //mActivity.setSupportActionBar(mToolbar);
    }

    private void initView() {
        mToolbar = $(R.id.toolbar);
        ib_menu = $(R.id.ib_menu);
    }

    private <T extends View> T $(int resId) {
        return (T) mContentView.findViewById(resId);
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.ib_menu:
                mActivity.changeLeftMenuState();
                break;
        }
    }
}
