package com.campusconnect.fragment.InAdminFragments;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.campusconnect.R;
import com.campusconnect.adapter.JoinRequestsAdapterActivity;
import com.campusconnect.bean.JoinRequestBean;
import com.campusconnect.communicator.CCWebService;
import com.campusconnect.communicator.ServiceGenerator;
import com.campusconnect.communicator.WebRequestTask;
import com.campusconnect.communicator.WebServiceDetails;
import com.campusconnect.communicator.models.ModelsAdminFeed;
import com.campusconnect.communicator.models.ModelsClubJoinResponse;
import com.campusconnect.communicator.models.ModelsClubMiniForm;
import com.campusconnect.communicator.models.ModelsCollegeFeed;
import com.campusconnect.communicator.models.ModelsFeed;
import com.campusconnect.constant.AppConstants;
import com.campusconnect.fragment.BaseFragment;
import com.campusconnect.utility.DividerItemDecoration;
import com.campusconnect.utility.LogUtility;
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
 * Created by RK on 04/02/2016.
 */
public class FragmentRequestsList_InAdmin extends BaseFragment {

    @Bind(R.id.tv_caught_up)
    TextView caught_up;
    @Bind(R.id.rv_join_requests)
    RecyclerView join_requests;
    Context context;
    CCWebService mCCService;

    ArrayList<ModelsClubJoinResponse> joinRequestBeans = new ArrayList<ModelsClubJoinResponse>();

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_requests_in_admin, container, false);
        ButterKnife.bind(this, v);
        context = v.getContext();

        //Bundle bundle = v.getIntent().getExtras();
        //String club_id=bundle.getString("club_id");

        Bundle bundle = getArguments();

        final ModelsClubMiniForm club = bundle.getParcelable("club");

        LinearLayoutManager llm = new LinearLayoutManager(v.getContext());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        join_requests.setLayoutManager(llm);
        join_requests.setHasFixedSize(false);
        join_requests.setItemAnimator(new DefaultItemAnimator());
        join_requests.addItemDecoration(new DividerItemDecoration(context, LinearLayoutManager.VERTICAL));


        mCCService = ServiceGenerator.createService(
                CCWebService.class,
                CCWebService.BASE_URL);

        String pid = SharedpreferenceUtility.getInstance(getContext()).getString(AppConstants.PERSON_PID);
//
//        Call findFeedCall = mCCService.getAdminFeed(club.getClubId(), pid);
//
//        mCalls.add(findFeedCall);
//        findFeedCall.enqueue(requestListCallback);
        webApiGetRequests(club.getClubId());

        return v;
    }

    public void webApiGetRequests(String club_id) {
        try {
            String pid = SharedpreferenceUtility.getInstance(this.getContext()).getString(AppConstants.PERSON_PID);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("clubId", club_id);
            jsonObject.put("pid",pid);
            List<NameValuePair> param = new ArrayList<NameValuePair>();
            String url = WebServiceDetails.DEFAULT_BASE_URL + "adminFeed";
            Log.d("Requests",url);
            new WebRequestTask(this.getContext(), param, _handler, WebRequestTask.POST, jsonObject, WebServiceDetails.PID_GET_ADMIN_REQUESTS,
                    true, url).execute();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

        private final Handler _handler = new Handler() {
        public void handleMessage(Message msg) {
            int response_code = msg.what;
            if (response_code != 0 && response_code != 204) {
                String strResponse = (String) msg.obj;
                Log.v("Response", strResponse);
                if (strResponse != null && strResponse.length() > 0) {
                    switch (response_code) {
                        case WebServiceDetails.PID_GET_ADMIN_REQUESTS: {
                            try {

                                JSONObject jsonObject = new JSONObject(strResponse);
                                if (jsonObject.has("joinReq")) {

                                    JSONArray array = jsonObject.getJSONArray("joinReq");
                                    if (array.length() > 0) {
                                        for (int i = 0; i < array.length(); i++) {
                                            JSONObject innerObject = array.getJSONObject(i);
                                            ModelsClubJoinResponse bean = new ModelsClubJoinResponse();
                                            bean.setFromPhotoUrl(innerObject.optString("from_photoUrl"));
                                            bean.setRequestId(innerObject.optString("requestId"));
                                            bean.setTimestamp(innerObject.optString("timestamp"));
                                            bean.setClubName(innerObject.optString("club_name"));
                                            bean.setFromPid(innerObject.optString("fromPid"));
                                            bean.setFromName(innerObject.optString("from_name"));
                                            bean.setFromBatch(innerObject.optString("from_batch"));
                                            bean.setFromBranch(innerObject.optString("from_branch"));
                                            joinRequestBeans.add(bean);
                                        }
                                        if(joinRequestBeans.size()>0){
                                            JoinRequestsAdapterActivity gm = new JoinRequestsAdapterActivity(joinRequestBeans
                                                    , getContext() );
                                            join_requests.setAdapter(gm);
                                        }

                                    }
                                }
                                else{
                                    caught_up.setVisibility(View.VISIBLE);
                                    join_requests.setVisibility(View.GONE);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }
                        break;

                        default:
                            break;
                    }
                } else {
                    Toast.makeText(getContext(), "SERVER_ERROR", Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(getContext(), "SERVER_ERROR", Toast.LENGTH_LONG).show();
            }
        }
    };



    private Callback<ModelsAdminFeed> requestListCallback = new Callback<ModelsAdminFeed>() {
        @Override
        public void onResponse(Response<ModelsAdminFeed> response, Retrofit retrofit) {


            if (response != null) {
                if (response.isSuccess()) {
                    ModelsAdminFeed modelsAdminFeed = response.body();
                    if (modelsAdminFeed != null) {
                        List<ModelsClubJoinResponse> reqItems = modelsAdminFeed.getJoinReq();
                        if (reqItems != null || reqItems.size() > 0) {
                            JoinRequestsAdapterActivity gm = new JoinRequestsAdapterActivity(reqItems
                                    , context);
                            join_requests.setAdapter(gm);

                        }
                    }
                } else {
                    com.squareup.okhttp.Response rawResponse = response.raw();
                    if (rawResponse != null) {
                        LogUtility.logFailedResponse(rawResponse);

                        int code = rawResponse.code();
                        switch (code) {
                            case 500:
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
//                    mIsLoading = false;
//                    mLoadingImageView.setVisibility(View.GONE);

//                    mErrorTextView.setText("Can't load data.\nCheck your network connection.");
//                    mErrorLinearLayout.setVisibility(View.VISIBLE);
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