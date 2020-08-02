package com.example.appfoodsavior;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.appfoodsavior.fragments.CamLibraryFragment;
import com.example.appfoodsavior.fragments.CamPhotoFragment;

public class PageAdapter extends FragmentPagerAdapter {

    int numOfTabs;

    public PageAdapter(FragmentManager fm, int numOfTabs){
        super(fm);
        this.numOfTabs = numOfTabs;
    }
    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return new CamLibraryFragment();
            case 1:
                return new CamPhotoFragment();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return numOfTabs;
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        return POSITION_NONE;
    }
}
