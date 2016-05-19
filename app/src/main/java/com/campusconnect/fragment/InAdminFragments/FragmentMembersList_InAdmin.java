package com.campusconnect.fragment.InAdminFragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.campusconnect.R;
import com.campusconnect.adapter.GroupMembersByGroupAdapter_InAdminAdapterActivity;
import com.campusconnect.bean.GroupMemberBean;
import com.campusconnect.bean.JoinRequestBean;
import com.campusconnect.communicator.CCWebService;
import com.campusconnect.communicator.ServiceGenerator;
import com.campusconnect.communicator.models.ModelsClubMiniForm;
import com.campusconnect.communicator.models.ModelsPersonalInfoResponse;
import com.campusconnect.utility.DividerItemDecoration;

import java.io.IOException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * Created by RK on 04/02/2016.
 */
public class FragmentMembersList_InAdmin extends Fragment {


    @Bind(R.id.rv_members_list)
    RecyclerView members_list;


    ModelsClubMiniForm club;
    ArrayList<GroupMemberBean> memberList = new ArrayList<GroupMemberBean>();
    CCWebService mCCService;

    Context context;

    ArrayList<JoinRequestBean> joinRequestBeans = new ArrayList<JoinRequestBean>();

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_group_members_in_admin, container, false);
        ButterKnife.bind(this, v);
        context = v.getContext();
        Bundle bundle = getArguments();
        club = bundle.getParcelable("club");

        CCWebService mCCService = ServiceGenerator.createService(
                CCWebService.class,
                CCWebService.BASE_URL);

        Call getClubMembers = mCCService.getClubMembers(club.getClubId());
        getClubMembers.enqueue(getClubMembersCallback);



        members_list.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(context);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        members_list.setLayoutManager(llm);
        members_list.setItemAnimator(new DefaultItemAnimator());
        members_list.addItemDecoration(new DividerItemDecoration(context, LinearLayoutManager.VERTICAL));

        return  v;
    }

    private Callback<ModelsPersonalInfoResponse> getClubMembersCallback = new Callback<ModelsPersonalInfoResponse>() {
        @Override
        public void onResponse(Response<ModelsPersonalInfoResponse> response, Retrofit retrofit) {

//            Log.d("findVid", response.body().toString());
            if (response != null) {
                if (response.isSuccess()) {
                    ModelsPersonalInfoResponse modelsPersonalInfoResponse = response.body();
                    GroupMembersByGroupAdapter_InAdminAdapterActivity gm_InAdmin = new GroupMembersByGroupAdapter_InAdminAdapterActivity(modelsPersonalInfoResponse.getItems()
                            , context, club.getClubId());
                    members_list.setAdapter(gm_InAdmin);
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