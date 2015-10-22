package com.wings.zilizili.fragment;


import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.wings.zilizili.GlobalConstant;
import com.wings.zilizili.R;
import com.wings.zilizili.activity.MainActivity;

/**
 * A simple {@link Fragment} subclass.
 */
public class LeftMenuFragment extends Fragment {
    private NavigationView mNavigationView;
    private View mContentView;
    private MainActivity mActivity;


    public LeftMenuFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mContentView = inflater.inflate(R.layout.navigation_layout, container, false);
        init();
        return mContentView;
    }

    private void init() {
        mNavigationView = $(R.id.nv_main);
        mActivity = (MainActivity) getActivity();
        mNavigationView.setNavigationItemSelectedListener(new NavigationView.
                OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                int itemId = menuItem.getItemId();
                switch (itemId) {
                    case R.id.nav_home:
                        mActivity.changeFragment(GlobalConstant.HOMEFRAGMENT);
                        break;
                    case R.id.nav_histories:
                        mActivity.changeFragment(GlobalConstant.HISTORYFRAGMENT);
                        break;
                    //android:checkable="false"
                    //可以响应点击事件,但是不会被选中
                    case R.id.nav_offline_manager:
                        mActivity.changeFragment(GlobalConstant.HOMEFRAGMENT);
                        break;

                }
                mActivity.closeLeftMenu();
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

}
