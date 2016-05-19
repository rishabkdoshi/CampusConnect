package com.campusconnect.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.TextView;

import com.campusconnect.R;
import com.campusconnect.communicator.models.ModelsClubMiniForm;
import com.campusconnect.constant.AppConstants;
import com.campusconnect.slidingtab.SlidingTabLayout_InAdmin;
import com.campusconnect.utility.SharedpreferenceUtility;
import com.campusconnect.viewpager.ViewPagerAdapter_InAdmin;

import butterknife.ButterKnife;

/**
 * Created by RK on 05/11/2015.
 */
public class InAdminGroupPageActivity extends ActionBarActivity {

    TextView group_name,invite;
    ViewPager pager;
    ViewPagerAdapter_InAdmin adapter;
    public static SlidingTabLayout_InAdmin tabs_InAdmin;
    CharSequence Titles[] = {"Members", "Edit Posts", "Requests"};
    int Numboftabs = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inadmin_group);
        ButterKnife.bind(this);
        final ModelsClubMiniForm club = getIntent().getParcelableExtra("club");

        group_name = (TextView) findViewById(R.id.tv_group_name_title);
        pager = (ViewPager) findViewById(R.id.pager);
        invite = (TextView)findViewById(R.id.tv_invite);
        tabs_InAdmin = (SlidingTabLayout_InAdmin) findViewById(R.id.tabs);
        adapter = new ViewPagerAdapter_InAdmin(getSupportFragmentManager(), Titles, Numboftabs, this, club);
        pager.setAdapter(adapter);
        pager.setCurrentItem(0);
        tabs_InAdmin.setDistributeEvenly(true);
        tabs_InAdmin.setViewPager(pager);

        group_name.setText(club.getName());

        invite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(android.content.Intent.ACTION_SEND);
                i.setType("text/plain");
                String shareBody= "Hey, I have created Group : "+club.getName()+" on CampusConnect. Download the app and join as an exclusive group member. Link: bit.ly/campusconnectandroid ";

//                String shareBody = feed.getTitle() + "\n" + feed.getDescription();
                i.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                v.getContext().startActivity(Intent.createChooser(i, "Share via"));
//
            }
        });


    }


}