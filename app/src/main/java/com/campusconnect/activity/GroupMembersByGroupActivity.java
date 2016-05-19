package com.campusconnect.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.campusconnect.R;
import com.campusconnect.adapter.GroupMembersByGroupAdapterActivity;
import com.campusconnect.bean.GroupMemberBean;
import com.campusconnect.communicator.CCWebService;
import com.campusconnect.communicator.ServiceGenerator;
import com.campusconnect.communicator.WebRequestTask;
import com.campusconnect.communicator.WebServiceDetails;
import com.campusconnect.communicator.models.ModelsPersonalInfoResponse;
import com.campusconnect.communicator.models.ModelsPersonalResponse;
import com.campusconnect.communicator.models.ModelsUpdateStatus;
import com.campusconnect.constant.AppConstants;
import com.campusconnect.supportClasses.GroupMembersByGroup_infoActivity;
import com.campusconnect.utility.DividerItemDecoration;
import com.campusconnect.utility.SharedpreferenceUtility;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * Created by RK on 05/11/2015.
 */
public class GroupMembersByGroupActivity extends BaseActivity {

    @Bind(R.id.rv_members_list)
    RecyclerView members_list;
    @Bind(R.id.cross_button)
    LinearLayout close;
    @Bind(R.id.tv_title)
    TextView members_text;

    Typeface r_med;

    CCWebService mCCService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_group_members);

        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);

        String clubId = getIntent().getStringExtra("clubId");

        CCWebService mCCService = ServiceGenerator.createService(
                CCWebService.class,
                CCWebService.BASE_URL);

        Call getClubMembers = mCCService.getClubMembers(clubId);
        getClubMembers.enqueue(getClubMembersCallback);


        r_med = Typeface.createFromAsset(getAssets(), "font/Roboto_Medium.ttf");
        members_text.setTypeface(r_med);

        members_list.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        members_list.setLayoutManager(llm);
        members_list.setItemAnimator(new DefaultItemAnimator());
        members_list.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();

            }
        });

        String frag_location = MainActivity.frag_loc;
        UpdateUi(frag_location);

    }

    private Callback<ModelsPersonalInfoResponse> getClubMembersCallback = new Callback<ModelsPersonalInfoResponse>() {
        @Override
        public void onResponse(Response<ModelsPersonalInfoResponse> response, Retrofit retrofit) {

            if (response != null) {
                if (response.isSuccess()) {
                    ModelsPersonalInfoResponse modelsPersonalInfoResponse = response.body();
                    GroupMembersByGroupAdapterActivity gm = new GroupMembersByGroupAdapterActivity(modelsPersonalInfoResponse.getItems()
                            , GroupMembersByGroupActivity.this);
                    members_list.setAdapter(gm);
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


