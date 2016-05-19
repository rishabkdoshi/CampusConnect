package com.campusconnect.activity;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.campusconnect.R;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by RK on 05/11/2015.
 */
public class AboutGroupActivity extends BaseActivity {

    @Bind(R.id.cross_button)
    LinearLayout close;
    @Bind(R.id.tv_about)
    TextView about_text;
    @Bind(R.id.tv_about_group_info)
    TextView group_info;
    Typeface r_reg, r_med;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_about_group);
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);

        String aboutStr = getIntent().getStringExtra("About");

        r_reg = Typeface.createFromAsset(getAssets(), "font/Roboto_Regular.ttf");
        r_med = Typeface.createFromAsset(getAssets(), "font/Roboto_Medium.ttf");

        about_text.setTypeface(r_med);
        group_info.setTypeface(r_reg);
        group_info.setText(aboutStr);

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();

            }
        });

        String frag_location = MainActivity.frag_loc;
        UpdateUi(frag_location);
    }


}
