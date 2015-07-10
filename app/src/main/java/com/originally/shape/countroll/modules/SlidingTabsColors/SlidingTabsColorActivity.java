package com.originally.shape.countroll.modules.SlidingTabsColors;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

import com.originally.shape.R;
import com.originally.shape.controllers.base.MDLBaseActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.InjectView;

public class SlidingTabsColorActivity extends MDLBaseActivity {

    private List<SamplePagerItem> mTabs = new ArrayList<SamplePagerItem>();

    @InjectView(R.id.sliding_tabs)
    SlidingTabLayout mSlidingTabLayout;
    @InjectView(R.id.viewpager)
    ViewPager mViewPager;

    public static Intent getInstance(Context context) {
        Intent intent = new Intent(context, SlidingTabsColorActivity.class);
        return intent;
    }

    @Override
    protected void setLayoutResource() {
        setContentView(R.layout.activity_sliding_tabs_color);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initialiseData();
        initialiseUI();
    }

    //----------------------------------------------------------------------------------------------
    // Setup
    //----------------------------------------------------------------------------------------------

    protected void initialiseData() {
        mTabs.add(new SamplePagerItem(
                "Tab 1", // Title
                Color.BLUE, // Indicator color
                Color.GRAY // Divider color
        ));

        mTabs.add(new SamplePagerItem(
                "Tab 2", // Title
                Color.RED, // Indicator color
                Color.GRAY // Divider color
        ));

        mTabs.add(new SamplePagerItem(
                "Tab 3", // Title
                Color.YELLOW, // Indicator color
                Color.GRAY // Divider color
        ));

        mTabs.add(new SamplePagerItem(
                "Tab 4", // Title
                Color.GREEN, // Indicator color
                Color.GRAY // Divider color
        ));
    }

    protected void initialiseUI() {
        mViewPager.setAdapter(new SampleFragmentPagerAdapter(getSupportFragmentManager()));
        mSlidingTabLayout.setViewPager(mViewPager);

        mSlidingTabLayout.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {

            @Override
            public int getIndicatorColor(int position) {
                return mTabs.get(position).getIndicatorColor();
            }

            @Override
            public int getDividerColor(int position) {
                return mTabs.get(position).getDividerColor();
            }
        });
    }


    //----------------------------------------------------------------------------------------------
    // Sling Tabs Pager
    //----------------------------------------------------------------------------------------------

    static class SamplePagerItem {
        private final CharSequence mTitle;
        private final int mIndicatorColor;
        private final int mDividerColor;

        SamplePagerItem(CharSequence title, int indicatorColor, int dividerColor) {
            mTitle = title;
            mIndicatorColor = indicatorColor;
            mDividerColor = dividerColor;
        }

        Fragment createFragment() {
            return ContentFragment.newInstance(mTitle, mIndicatorColor, mDividerColor);
        }

        CharSequence getTitle() {
            return mTitle;
        }

        int getIndicatorColor() {
            return mIndicatorColor;
        }

        int getDividerColor() {
            return mDividerColor;
        }
    }

    //----------------------------------------------------------------------------------------------
    // View pager adapter
    //----------------------------------------------------------------------------------------------

    class SampleFragmentPagerAdapter extends FragmentPagerAdapter {

        SampleFragmentPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {
            return mTabs.get(i).createFragment();
        }

        @Override
        public int getCount() {
            return mTabs.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mTabs.get(position).getTitle();
        }
    }
}
