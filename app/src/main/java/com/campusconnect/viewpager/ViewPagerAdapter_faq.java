package com.campusconnect.viewpager;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.campusconnect.fragment.FAQFragments.FragmentFAQAdmin;
import com.campusconnect.fragment.FAQFragments.FragmentFAQUser;

public class ViewPagerAdapter_faq extends FragmentPagerAdapter {


    CharSequence Titles[]; // This will Store the Titles of the Tabs which are Going to be passed when ViewPagerAdapter_faq is created
    int NumbOfTabs; // Store the number of tabs, this will also be passed when the ViewPagerAdapter_faq is created

    private Context mContext;

    public ViewPagerAdapter_faq(FragmentManager fm, CharSequence mTitles[], int mNumbOfTabsumb, Context context) {
        super(fm);
        this.Titles = mTitles;
        this.NumbOfTabs = mNumbOfTabsumb;
        this.mContext = context;
    }


    //This method return the fragment for the every position in the View Pager
    @Override
    public Fragment getItem(int position) {

        Fragment fragment = null;
        if (position == 0) {
            fragment = new FragmentFAQUser();
        } else if (position == 1) {
            fragment = new FragmentFAQAdmin();
        }
        return fragment;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return Titles[position];
    }


    @Override
    public int getCount() {
        return NumbOfTabs;
    }
}