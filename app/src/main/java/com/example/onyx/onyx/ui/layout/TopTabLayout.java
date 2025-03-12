package com.example.onyx.onyx.ui.layout;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.google.android.material.tabs.TabLayout;

public class TopTabLayout extends TabLayout {
    public TopTabLayout(Context context) {
        super(context);
    }

    public TopTabLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TopTabLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void setTabsFromPagerAdapter(@NonNull PagerAdapter adapter) {

        this.removeAllTabs();


    }
}
