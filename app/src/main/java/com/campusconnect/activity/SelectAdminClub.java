package com.campusconnect.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.campusconnect.R;
import com.campusconnect.adapter.ClubListAdapterActivity;
import com.campusconnect.adapter.CollegeListAdapterActivity;
import com.campusconnect.adapter.GroupMembersByGroupAdapter_InAdminAdapterActivity;
import com.campusconnect.bean.CollegeListInfoBean;
import com.campusconnect.bean.GroupBean;
import com.campusconnect.communicator.CCWebService;
import com.campusconnect.communicator.ServiceGenerator;
import com.campusconnect.communicator.WebRequestTask;
import com.campusconnect.communicator.WebServiceDetails;
import com.campusconnect.communicator.models.ModelsClubListResponse;
import com.campusconnect.communicator.models.ModelsClubMiniForm;
import com.campusconnect.communicator.models.ModelsPersonalInfoResponse;
import com.campusconnect.constant.AppConstants;
import com.campusconnect.utility.NetworkAvailablity;
import com.campusconnect.utility.SharedpreferenceUtility;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * Created by rkd on 28/12/15.
 */
public class SelectAdminClub  extends AppCompatActivity {

    private static final String LOG_TAG = "SelectAdminClub";

    //  private String mEmailAccount = "";
    static Context context;
    RecyclerView club_list;
    LinearLayout close;
    TextView admin_title_text;
    TextView caught_up_text;


    public ArrayList<GroupBean> groupList = new ArrayList<GroupBean>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_admin_club);

        club_list = (RecyclerView) findViewById(R.id.rv_admin_club_list);
        close = (LinearLayout) findViewById(R.id.cross_button);
        admin_title_text = (TextView) findViewById(R.id.tv_admin_text);
        caught_up_text = (TextView) findViewById(R.id.tv_caught_up);

        Typeface r_med = Typeface.createFromAsset(getAssets(), "font/Roboto_Medium.ttf");
        Typeface r_reg = Typeface.createFromAsset(getAssets(), "font/Roboto_Regular.ttf");

        admin_title_text.setTypeface(r_med);
        caught_up_text.setTypeface(r_reg);

        club_list.setHasFixedSize(false);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        club_list.setLayoutManager(llm);
        club_list.setItemAnimator(new DefaultItemAnimator());

        //TODO network check
        CCWebService mCCService = ServiceGenerator.createService(
                CCWebService.class,
                CCWebService.BASE_URL);


        if (NetworkAvailablity.hasInternetConnection(SelectAdminClub.this)) {
            String pid=SharedpreferenceUtility.getInstance(this).getString(AppConstants.PERSON_PID);
            Call getAdminClubs = mCCService.getAdminClubs(pid);
            getAdminClubs.enqueue(getAdminClubsCallback);

        } else {
            Toast.makeText(SelectAdminClub.this, "Network is not available.", Toast.LENGTH_SHORT).show();
        }

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();

            }
        });
    }


    private Callback<ModelsClubListResponse> getAdminClubsCallback = new Callback<ModelsClubListResponse>() {
        @Override
        public void onResponse(Response<ModelsClubListResponse> response, Retrofit retrofit) {

//            Log.d("findVid", response.body().toString());
            if (response != null) {
                if (response.isSuccess()) {
                    ModelsClubListResponse modelsClubListResponse = response.body();

                    ClubListAdapterActivity cl = new ClubListAdapterActivity(modelsClubListResponse.getList(),SelectAdminClub.this);
                    club_list.setAdapter(cl);
                } else {
                    com.squareup.okhttp.Response rawResponse = response.raw();
                    if (rawResponse != null) {
//                            LogUtility.logFailedResponse(rawResponse);

                        int code = rawResponse.code();
                        switch (code) {
                            case 500:
//                                    mErrorTextView.setText("Can't load data.\nCheck your network connection.");
//                                    mErrorLinearLayout.setVisibility(View.VISIBLE);
                                break;
                            default:
                                break;
                        }
                    }
                }
            }
        }

        @Override
        public void onFailure(Throwable t) {
            //Timber.d("onFailure() : mQuery - " + mQuery);
            if (t != null) {
                String message = t.getMessage();
                //LogUtility.logFailure(t);

                if (t instanceof SocketTimeoutException
                        || t instanceof UnknownHostException
                        || t instanceof SocketException) {
                    //Timber.e("Timeout occurred");

//                        mErrorTextView.setText("Can't load data.\nCheck your network connection.");
//                        mErrorLinearLayout.setVisibility(View.VISIBLE);
                } else if (t instanceof IOException) {
                    if (message.equals("Canceled")) {
                        //Timber.e("onFailure() : Canceled");
                    } else {
//                        mIsLoading = false;
//                        mLoadingImageView.setVisibility(View.GONE);
                    }
                }
            }
        }
    };

}