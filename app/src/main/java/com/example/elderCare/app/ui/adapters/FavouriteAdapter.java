package com.example.elderCare.app.ui.adapters;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.example.elderCare.app.FavouriteItemList;
import com.example.elderCare.app.FavouriteRouteList;

public class FavouriteAdapter extends FragmentStatePagerAdapter {

    int mNumOfTabs;

    public FavouriteAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }


    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                return new FavouriteItemList();

            case 1:
                return new FavouriteRouteList();

            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}