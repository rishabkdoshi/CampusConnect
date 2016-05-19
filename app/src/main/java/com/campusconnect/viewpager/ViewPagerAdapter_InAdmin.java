package com.campusconnect.viewpager;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.campusconnect.communicator.models.ModelsClubMiniForm;
import com.campusconnect.fragment.InAdminFragments.FragmentMembersList_InAdmin;
import com.campusconnect.fragment.InAdminFragments.FragmentPostsList_InAdmin;
import com.campusconnect.fragment.InAdminFragments.FragmentRequestsList_InAdmin;

/**
 * Created by hp1 on 21-01-2015.
 */
public class ViewPagerAdapter_InAdmin extends FragmentPagerAdapter {

    CharSequence Titles[]; // This will Store the Titles of the Tabs which are Going to be passed when ViewPagerAdapter_home is created
    int NumbOfTabs; // Store the number of tabs, this will also be passed when the ViewPagerAdapter_home is created

    private Context mContext;
    ModelsClubMiniForm club;
    // Build a Constructor and assign the passed Values to appropriate values in the class
    public ViewPagerAdapter_InAdmin(FragmentManager fm, CharSequence mTitles[], int mNumbOfTabsumb, Context context, ModelsClubMiniForm club) {
        super(fm);
        this.Titles = mTitles;
        this.NumbOfTabs = mNumbOfTabsumb;
        this.mContext = context;
        this.club=club;
    }

    //This method return the fragment for the every position in the View Pager
    @Override
    public Fragment getItem(int position) {
        Bundle bundle = new Bundle();
        bundle.putParcelable("club", this.club);
        if(position == 0)
        {
            FragmentMembersList_InAdmin frag_members_list_inAdmin = new FragmentMembersList_InAdmin();
            frag_members_list_inAdmin.setArguments(bundle);
            return frag_members_list_inAdmin;
        }
        else if(position == 1)
        {
            FragmentPostsList_InAdmin frag_posts_list_inAdmin = new FragmentPostsList_InAdmin();
            frag_posts_list_inAdmin.setArguments(bundle);
            return frag_posts_list_inAdmin;
        }
        else{
            FragmentRequestsList_InAdmin frag_requests_list_inAdmin = new FragmentRequestsList_InAdmin();
            frag_requests_list_inAdmin.setArguments(bundle);
            return frag_requests_list_inAdmin;
        }

    }



    @Override
    public CharSequence getPageTitle(int position) {
        return Titles[position];
    }


    // This method return the Number of tabs for the tabs Strip

    @Override
    public int getCount() {
        return NumbOfTabs;
    }
}