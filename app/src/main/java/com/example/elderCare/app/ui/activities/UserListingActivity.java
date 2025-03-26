package com.example.elderCare.app.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.example.elderCare.app.R;
import com.example.elderCare.app.ui.adapters.UserListingPagerAdapter;
import com.google.android.material.tabs.TabLayout;

public class UserListingActivity extends AppCompatActivity {
    private Toolbar mToolbar;
    private TabLayout mTabLayoutUserListing;
    private ViewPager mViewPagerUserListing;


    public static void startActivity(Context context) {
        Intent intent = new Intent(context, UserListingActivity.class);
        context.startActivity(intent);
    }

    public static void startActivity(Context context, int flags) {
        Intent intent = new Intent(context, UserListingActivity.class);
        intent.setFlags(flags);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_listing);
        bindViews();
        init();
    }

    private void bindViews() {
        mToolbar = findViewById(R.id.toolbar);
        mTabLayoutUserListing = findViewById(R.id.tab_layout_user_listing);
        mViewPagerUserListing = findViewById(R.id.view_pager_user_listing);
    }

    private void init() {
        // set the toolbar
        setSupportActionBar(mToolbar);

        // set the view pager adapter
        UserListingPagerAdapter userListingPagerAdapter = new UserListingPagerAdapter(getSupportFragmentManager());
        mViewPagerUserListing.setAdapter(userListingPagerAdapter);

        // attach tab layout with view pager
        mTabLayoutUserListing.setupWithViewPager(mViewPagerUserListing);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_user_listing, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.action_logout) {

        }
        return super.onOptionsItemSelected(item);
    }


}
