package com.campusconnect.photoFilter;

/**
 * Created by Black Ops on 22-02-2016.
 */
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.campusconnect.R;

public class Gallery extends AppCompatActivity {

    // Declare Variables
    ViewPager viewPager;
    PagerAdapter adapter;
    public static Bitmap bitmap;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Get the view from viewpager_main.xml
        setContentView(R.layout.viewpager_main);
        Log.d("here", "here");
        // Locate the ViewPager in viewpager_main.xml
        viewPager = (ViewPager) findViewById(R.id.pager);
        // Pass results to ViewPagerAdapter Class
        adapter = new ViewPagerAdapter(Gallery.this, PhotoActivty.filters,this.bitmap);
        // Binds the Adapter to the ViewPager
        viewPager.setAdapter(adapter);

    }


}