package com.campusconnect.activity;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.campusconnect.R;
import com.campusconnect.slidingtab.SlidingTabLayout_faq;
import com.campusconnect.viewpager.ViewPagerAdapter_faq;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by RK on 05/11/2015.
 */
public class FAQActivity extends ActionBarActivity {


    @Bind(R.id.cross_button)
    LinearLayout close;
    @Bind(R.id.tv_faq)
    TextView faq_page_title;

    Typeface r_reg, r_med;

    ViewPagerAdapter_faq adapter;
    ViewPager pager;
    public static SlidingTabLayout_faq tabs;
    CharSequence Titles[] = {"User", "Admin"};
    int Numboftabs = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faq);
        ButterKnife.bind(this);

        r_med = Typeface.createFromAsset(getAssets(), "font/Roboto_Medium.ttf");
        faq_page_title.setTypeface(r_med);

        pager = (ViewPager) findViewById(R.id.pager);
        tabs = (SlidingTabLayout_faq) findViewById(R.id.tabs);
        adapter = new ViewPagerAdapter_faq(getSupportFragmentManager(), Titles, Numboftabs, this);
        pager.setAdapter(adapter);
        tabs.setDistributeEvenly(true);
        tabs.setViewPager(pager);

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();

            }
        });


    }

}