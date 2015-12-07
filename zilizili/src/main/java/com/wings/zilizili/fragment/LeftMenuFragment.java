package com.wings.zilizili.fragment;


import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.util.Log;
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
    private final static String TAG = "LeftMenuFragment";
    private final static String ITEMSELECTED = "itemSelected";
    private NavigationView mNavigationView;
    private View mContentView;
    private ImageView iv_theme;
    private ViewGroup container;
    private OnLeftMenuSelectedListener mLeftMenuSelectedListener;
    private int CurrentSelectedItemId;


    public LeftMenuFragment() {
        // Required empty public constructor
    }


    public void setCurrentSelectedItemId(int currentSelectedItemId) {
        this.CurrentSelectedItemId = currentSelectedItemId;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mLeftMenuSelectedListener = (OnLeftMenuSelectedListener) context;
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
        mNavigationView.setNavigationItemSelectedListener(new NavigationView.
                OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                int itemId = menuItem.getItemId();
                mLeftMenuSelectedListener.onLeftMenuSelected(itemId);
                return false;
            }
        });
        mNavigationView.setCheckedItem(
                CurrentSelectedItemId == 0 ? R.id.nav_home : CurrentSelectedItemId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
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
        mLeftMenuSelectedListener.onItemClicked(v);
    }

    public void invalidateLeftMenu() {
    }

    public interface OnLeftMenuSelectedListener {
        void onLeftMenuSelected(int itemId);

        void onItemClicked(View v);
    }
}
