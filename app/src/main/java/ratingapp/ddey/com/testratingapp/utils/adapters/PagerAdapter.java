package ratingapp.ddey.com.testratingapp.utils.adapters;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import ratingapp.ddey.com.testratingapp.ui.fragments.RatingFragment;
import ratingapp.ddey.com.testratingapp.ui.fragments.ReviewFragment;


public class PagerAdapter extends FragmentStatePagerAdapter {
    private int mTabsNumber;
    private Bundle mBundle;

    public PagerAdapter(FragmentManager fm, int tabsNumber, Bundle bundle) {
        super(fm);
        this.mTabsNumber = tabsNumber;
        mBundle = bundle;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                ReviewFragment reviewFragment = new ReviewFragment();
                reviewFragment.setArguments(mBundle);
                return reviewFragment;
            case 1:
                RatingFragment ratingFragment = new RatingFragment();
                ratingFragment.setArguments(mBundle);
                return ratingFragment;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mTabsNumber;
    }
}
