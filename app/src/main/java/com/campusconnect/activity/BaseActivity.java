package com.campusconnect.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.campusconnect.R;

import butterknife.Bind;
import butterknife.ButterKnife;


/**
 * Created by rkd on 13/2/16.
 */
public class BaseActivity extends Activity {

    @Bind(R.id.ib_search)
    ImageButton search;
    @Bind(R.id.ib_calendar)
    ImageButton calender;
    @Bind(R.id.ib_profile)
    ImageButton profile;
    @Bind(R.id.ib_home)
    ImageButton home;
    @Bind(R.id.ib_notification)
    ImageButton notification;
    @Bind(R.id.lnr_search_line)
    LinearLayout searchLine;
    @Bind(R.id.lnr_cal_line)
    LinearLayout calLine;
    @Bind(R.id.lnr_notification_line)
    LinearLayout notificationLine;
    @Bind(R.id.lnr_profile_line)
    LinearLayout profileLine;
    @Bind(R.id.lnr_home_line)
    LinearLayout homeLine;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent_temp = new Intent(v.getContext(), MainActivity.class);
                Bundle bundle = new Bundle();
                bundle.putInt("FRAGMENT", 1);
                intent_temp.putExtras(bundle);
                intent_temp.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent_temp);
                finish();
            }
        });
        calender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent_temp = new Intent(v.getContext(), MainActivity.class);
                Bundle bundle = new Bundle();
                bundle.putInt("FRAGMENT", 2);
                intent_temp.putExtras(bundle);
                intent_temp.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent_temp);
                finish();
            }
        });
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent_temp = new Intent(v.getContext(), MainActivity.class);
                Bundle bundle = new Bundle();
                bundle.putInt("FRAGMENT", 3);
                intent_temp.putExtras(bundle);
                intent_temp.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent_temp);
                finish();
            }
        });
        notification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent_temp = new Intent(v.getContext(), MainActivity.class);
                Bundle bundle = new Bundle();
                bundle.putInt("FRAGMENT", 4);
                intent_temp.putExtras(bundle);
                intent_temp.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent_temp);
                finish();
            }
        });
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent_temp = new Intent(v.getContext(), MainActivity.class);
                Bundle bundle = new Bundle();
                bundle.putInt("FRAGMENT", 5);
                intent_temp.putExtras(bundle);
                intent_temp.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent_temp);
                finish();
            }
        });
    }


    public void UpdateUi(String str) {


        if (str.equals("home")) {
            search.setImageResource(R.mipmap.search);
            calender.setImageResource(R.mipmap.calendar);
            home.setImageResource(R.mipmap.home_selected);
            notification.setImageResource(R.mipmap.notification);
            profile.setImageResource(R.mipmap.profile);

            searchLine.setBackgroundColor(getResources().getColor(R.color.carbonColor));
            calLine.setBackgroundColor(getResources().getColor(R.color.carbonColor));
            notificationLine.setBackgroundColor(getResources().getColor(R.color.carbonColor));
            profileLine.setBackgroundColor(getResources().getColor(R.color.carbonColor));
            homeLine.setBackgroundColor(getResources().getColor(R.color.yello));

        } else if (str.equals("profile")) {

            search.setImageResource(R.mipmap.search);
            calender.setImageResource(R.mipmap.calendar);
            home.setImageResource(R.mipmap.home);
            notification.setImageResource(R.mipmap.notification);
            profile.setImageResource(R.mipmap.profile_selected);
            searchLine.setBackgroundColor(getResources().getColor(R.color.carbonColor));
            calLine.setBackgroundColor(getResources().getColor(R.color.carbonColor));
            notificationLine.setBackgroundColor(getResources().getColor(R.color.carbonColor));
            profileLine.setBackgroundColor(getResources().getColor(R.color.yello));
            homeLine.setBackgroundColor(getResources().getColor(R.color.carbonColor));

        } else if (str.equals("search")) {

            search.setImageResource(R.mipmap.search_selected);
            calender.setImageResource(R.mipmap.calendar);
            home.setImageResource(R.mipmap.home);
            notification.setImageResource(R.mipmap.notification);
            profile.setImageResource(R.mipmap.profile);

            searchLine.setBackgroundColor(getResources().getColor(R.color.yello));
            calLine.setBackgroundColor(getResources().getColor(R.color.carbonColor));
            notificationLine.setBackgroundColor(getResources().getColor(R.color.carbonColor));
            profileLine.setBackgroundColor(getResources().getColor(R.color.carbonColor));
            homeLine.setBackgroundColor(getResources().getColor(R.color.carbonColor));

        } else if (str.equals("calender")) {


            search.setImageResource(R.mipmap.search);
            calender.setImageResource(R.mipmap.calendar_selected);
            home.setImageResource(R.mipmap.home);
            notification.setImageResource(R.mipmap.notification);
            profile.setImageResource(R.mipmap.profile);

            searchLine.setBackgroundColor(getResources().getColor(R.color.carbonColor));
            calLine.setBackgroundColor(getResources().getColor(R.color.yello));
            notificationLine.setBackgroundColor(getResources().getColor(R.color.carbonColor));
            profileLine.setBackgroundColor(getResources().getColor(R.color.carbonColor));
            homeLine.setBackgroundColor(getResources().getColor(R.color.carbonColor));

        } else if (str.equals("notification")) {

            search.setImageResource(R.mipmap.search);
            calender.setImageResource(R.mipmap.calendar);
            home.setImageResource(R.mipmap.home);
            notification.setImageResource(R.mipmap.notification_selected);
            profile.setImageResource(R.mipmap.profile);

            searchLine.setBackgroundColor(getResources().getColor(R.color.carbonColor));
            calLine.setBackgroundColor(getResources().getColor(R.color.carbonColor));
            notificationLine.setBackgroundColor(getResources().getColor(R.color.yello));
            profileLine.setBackgroundColor(getResources().getColor(R.color.carbonColor));
            homeLine.setBackgroundColor(getResources().getColor(R.color.carbonColor));
        }


    }
}
