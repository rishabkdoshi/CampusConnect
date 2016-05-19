package com.campusconnect.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.campusconnect.R;
import com.campusconnect.communicator.WebRequestTask;
import com.campusconnect.communicator.WebServiceDetails;
import com.campusconnect.constant.AppConstants;
import com.campusconnect.utility.SharedpreferenceUtility;
import com.flurry.android.FlurryAgent;

import org.apache.http.NameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rkd on 3/12/15.
 */
public class FlashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flashpage);
        Thread thread = new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    sleep(2000);
                    SharedpreferenceUtility.getInstance(FlashActivity.this).putInt("AdminStatus", 0);
                    SharedpreferenceUtility.getInstance(FlashActivity.this).putInt("GroupsLoad",0);
                    SharedpreferenceUtility.getInstance(FlashActivity.this).putInt("MyFeedLoad", 0);
                    SharedpreferenceUtility.getInstance(FlashActivity.this).putInt("CollegeFeedLoad", 0);
                    SharedpreferenceUtility.getInstance(FlashActivity.this).putInt("LiveLoad", 0);
                    SharedpreferenceUtility.getInstance(FlashActivity.this).putInt("HashTagsLoad", 0);


                    Boolean loggedIn = SharedpreferenceUtility.getInstance(FlashActivity.this).getBoolean(AppConstants.LOG_IN_STATUS);

                    if (loggedIn) {
                        Intent next = new Intent(FlashActivity.this, MainActivity.class);
                        startActivity(next);
                        finish();
                    } else {
                        Intent next = new Intent(FlashActivity.this, SelectCollegeActivity.class);
                        startActivity(next);
                        finish();
                    }

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        };
        thread.start();

    }
}
