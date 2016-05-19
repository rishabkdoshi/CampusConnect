package com.campusconnect.fragment;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.campusconnect.R;
import com.campusconnect.adapter.NotificationAdapterActivity;
import com.campusconnect.bean.NotificationBean;
import com.campusconnect.communicator.CCWebService;
import com.campusconnect.communicator.ServiceGenerator;
import com.campusconnect.communicator.WebRequestTask;
import com.campusconnect.communicator.WebServiceDetails;
import com.campusconnect.communicator.models.ModelsCollegeFeed;
import com.campusconnect.communicator.models.ModelsFeed;
import com.campusconnect.communicator.models.ModelsNotificationList;
import com.campusconnect.communicator.models.ModelsNotificationMiniForm;
import com.campusconnect.communicator.models.ModelsNotificationResponseForm;
import com.campusconnect.constant.AppConstants;
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


public class NotificationFragment extends BaseFragment {

    @Bind(R.id.rv_notification)
    RecyclerView notificationsRecyclerView;


    @Bind(R.id.loading_iv)
    ProgressBar mLoadingImageView;

    @Bind(R.id.error_ll)
    LinearLayout mErrorLinearLayout;

    @Bind(R.id.error_tv)
    TextView mErrorTextView;

    View mRootView;

    @Bind(R.id.tv_notification)
    TextView notification_title;

    @Bind(R.id.tv_show_blank)
    TextView mEmptyView;

    NotificationAdapterActivity mNotificationAdapter;

    CCWebService mCCService;

    Context context;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (mRootView != null) {
            ViewGroup parent = (ViewGroup) mRootView.getParent();
            if (parent != null)
                parent.removeView(mRootView);
        }
        try {
            mRootView = inflater.inflate(R.layout.activity_notification, container, false);
            ButterKnife.bind(this, mRootView);


//            notification_title = (TextView) mRootView.findViewById(R.id.tv_notification);
            Typeface r_med = Typeface.createFromAsset(mRootView.getContext().getAssets(), "font/Roboto_Medium.ttf");
            notification_title.setTypeface(r_med);
            context=mRootView.getContext();

            notificationsRecyclerView.setHasFixedSize(true);
            LinearLayoutManager llm = new LinearLayoutManager(getActivity());
            llm.setOrientation(LinearLayoutManager.VERTICAL);
            notificationsRecyclerView.setLayoutManager(llm);
            notificationsRecyclerView.setItemAnimator(new DefaultItemAnimator());


            mCCService = ServiceGenerator.createService(
                    CCWebService.class,
                    CCWebService.BASE_URL);

            mNotificationAdapter = new NotificationAdapterActivity(mRootView.getContext());
            String pid=SharedpreferenceUtility.getInstance(getContext()).getString(AppConstants.PERSON_PID);
            notificationsRecyclerView.setAdapter(mNotificationAdapter);
            ModelsNotificationMiniForm modelsNotificationMiniForm=new ModelsNotificationMiniForm();
            modelsNotificationMiniForm.setPid(pid);
            Call findMyNotifications = mCCService.getMyNotifications(modelsNotificationMiniForm);
            findMyNotifications.enqueue(findNotificationCallback);
           /* nl = new NotificationAdapterActivity(
                    createList_nl(4));*/
            //   notification_list.setAdapter(nl);


        } catch (InflateException e) {
            e.printStackTrace();
        }
        return mRootView;
    }

    // region Callbacks
    private Callback<ModelsNotificationList> findNotificationCallback = new Callback<ModelsNotificationList>() {
        @Override
        public void onResponse(Response<ModelsNotificationList> response, Retrofit retrofit) {
            mLoadingImageView.setVisibility(View.GONE);
//            Log.d("findVid", response.body().toString());
            if (response != null) {
                if (response.isSuccess()) {
                    ModelsNotificationList modelsNotificationList = response.body();
                    if (modelsNotificationList != null) {
                        try {
                            List<ModelsNotificationResponseForm> notificationItems = modelsNotificationList.getList();
                            if (notificationItems != null || notificationItems.size()>0) {
                                mNotificationAdapter.addAll(notificationItems);

//                            if (notificationItems.size() >= PAGE_SIZE) {
//                                mFeedAdapter.addLoading();
//                            } else {
//                                mIsLastPage = true;
//                            }
                            }else{
                            mEmptyView.setVisibility(View.VISIBLE);
                                Toast.makeText(context,"No new notifications",Toast.LENGTH_LONG);
                            }
                        }catch (NullPointerException e){
//                            Log.d("exce",e.getMessage());
                            mEmptyView.setVisibility(View.VISIBLE);

                            Toast.makeText(context, "No new notifications", Toast.LENGTH_LONG);
                        }

                    }
                } else {
                    com.squareup.okhttp.Response rawResponse = response.raw();
                    if (rawResponse != null) {
                        LogUtility.logFailedResponse(rawResponse);

                        int code = rawResponse.code();
                        switch (code) {
                            case 500:
                                mErrorTextView.setText("Can't load data.\nCheck your network connection.");
                                mErrorLinearLayout.setVisibility(View.VISIBLE);
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
//
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
//
//    public void WebApiNotification() {
//        try {
//            String pid = SharedpreferenceUtility.getInstance(getActivity()).getString(AppConstants.PERSON_PID);
//            JSONObject jsonObject = new JSONObject();
//            jsonObject.put("pid", pid);
//            List<NameValuePair> param = new ArrayList<NameValuePair>();
//            String url = WebServiceDetails.DEFAULT_BASE_URL + "myNotifications";
//            new WebRequestTask(getActivity(), param, _handler, WebRequestTask.POST, jsonObject, WebServiceDetails.PID_NOTIFICATION,
//                    true, url).execute();
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
//    }


//
//    private final Handler _handler = new Handler() {
//        public void handleMessage(Message msg) {
//            int response_code = msg.what;
//            if (response_code != 0) {
//                String strResponse = (String) msg.obj;
//                Log.v("Response", strResponse);
//                if (strResponse != null && strResponse.length() > 0) {
//                    switch (response_code) {
//                        case WebServiceDetails.PID_NOTIFICATION: {
//                            try {
//                                mNotifiList.clear();
//                                JSONObject jsonObject = new JSONObject(strResponse);
//                                if (jsonObject.has("list")) {
//
//                                    JSONArray array = jsonObject.getJSONArray("list");
//                                    if (array.length() > 0) {
//
//                                        for (int i = 0; i < array.length(); i++) {
//                                            JSONObject obj = array.getJSONObject(i);
//                                            String gropname = obj.optString("clubName");
//                                            String type = obj.optString("type");
//                                            String groupId = obj.optString("clubId");
//                                            String timestamp = obj.optString("timestamp");
//                                            String eventName = obj.optString("eventName");
//                                            String eventid = obj.optString("eventId");
//                                            String photoUrl = obj.optString("clubphotoUrl");
//                                            String postName= obj.optString("postName");
//
//                                            NotificationBean bean = new NotificationBean();
//                                            bean.setGroup_name(gropname);
//                                            bean.setGroupId(groupId);
//                                            bean.setType(type);
//                                            bean.setEventName(eventName);
//                                            bean.setTimestamp(timestamp);
//                                            bean.setPhotoUrl(photoUrl);
//                                            bean.setPostName(postName);
//
//                                            if(type.equalsIgnoreCase("post")||type.equalsIgnoreCase("Event")||type.equalsIgnoreCase("Reminder")||type.equalsIgnoreCase("approved join reques"))
//                                            {
//                                                mNotifiList.add(bean);
//                                            }
//                                        }
//                                        nl = new NotificationAdapterActivity(mNotifiList, getActivity());
//                                        notification_list.setAdapter(nl);
//                                    }
//                                }
//                            } catch (Exception e) {
//                                e.printStackTrace();
//                            }
//                        }
//                        break;
//                        default:
//                            break;
//                    }
//                } else {
//                    Toast.makeText(getActivity(), "SERVER_ERROR", Toast.LENGTH_LONG).show();
//                }
//            } else {
//                Toast.makeText(getActivity(), "SERVER_ERROR", Toast.LENGTH_LONG).show();
//            }
//        }
//    };


}
