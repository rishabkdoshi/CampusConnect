//package com.campusconnect.activity;
//
//import android.content.Intent;
//import android.graphics.Typeface;
//import android.os.Bundle;
//import android.os.Handler;
//import android.os.Message;
//import android.support.v7.app.ActionBarActivity;
//import android.support.v7.widget.DefaultItemAnimator;
//import android.support.v7.widget.LinearLayoutManager;
//import android.support.v7.widget.RecyclerView;
//import android.util.Log;
//import android.view.View;
//import android.widget.ImageButton;
//import android.widget.LinearLayout;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import com.campusconnect.R;
//import com.campusconnect.adapter.GroupMembersByGroupAdapterActivity;
//import com.campusconnect.bean.GroupMemberBean;
//import com.campusconnect.communicator.CCWebService;
//import com.campusconnect.communicator.ServiceGenerator;
//import com.campusconnect.communicator.WebRequestTask;
//import com.campusconnect.communicator.WebServiceDetails;
//import com.campusconnect.constant.AppConstants;
//import com.campusconnect.supportClasses.GroupMembersByGroup_infoActivity;
//import com.campusconnect.utility.DividerItemDecoration;
//import com.campusconnect.utility.SharedpreferenceUtility;
//
//import org.apache.http.NameValuePair;
//import org.json.JSONArray;
//import org.json.JSONObject;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import butterknife.Bind;
//import butterknife.ButterKnife;
//import retrofit.Call;
//
///**
// * Created by RK on 05/11/2015.
// */
//public class GroupFollowersByGroupActivity extends ActionBarActivity {
//
//    @Bind(R.id.ib_search) ImageButton search;
//    @Bind(R.id.ib_calendar) ImageButton calender;
//    @Bind(R.id.ib_profile) ImageButton profile;
//    @Bind(R.id.ib_home) ImageButton home;
//    @Bind(R.id.ib_notification) ImageButton notification;
//    @Bind(R.id.lnr_search_line) LinearLayout searchLine;
//    @Bind(R.id.lnr_cal_line) LinearLayout calLine;
//    @Bind(R.id.lnr_notification_line) LinearLayout notificationLine;
//    @Bind(R.id.lnr_profile_line) LinearLayout profileLine;
//    @Bind(R.id.lnr_home_line) LinearLayout homeLine;
//
//    @Bind(R.id.rv_members_list) RecyclerView members_list;
//    @Bind(R.id.cross_button) LinearLayout close;
//    @Bind(R.id.tv_title) TextView members_text;
//
//    Typeface r_med;
//
//    ArrayList<GroupMemberBean> memberList= new ArrayList<GroupMemberBean>();
//    CCWebService mCCService;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_group_followers);
//        ButterKnife.bind(this);
//
//        String clubId = getIntent().getStringExtra("clubId");
////        mCCService = ServiceGenerator.createService(
////                CCWebService.class,
////                CCWebService.BASE_URL);
////
////        Call findGroupMembersCall = mCCService.getClubMembers(clubId);
////        findGroupMembersCall.enqueue();
//
//        WebApiGetMembers(clubId);
//
//        r_med = Typeface.createFromAsset(getAssets(), "font/Roboto_Medium.ttf");
//        members_text.setTypeface(r_med);
//
//        members_list.setHasFixedSize(true);
//        LinearLayoutManager llm = new LinearLayoutManager(this);
//        llm.setOrientation(LinearLayoutManager.VERTICAL);
//        members_list.setLayoutManager(llm);
//        members_list.setItemAnimator(new DefaultItemAnimator());
//        members_list.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
//
//        close.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                finish();
//
//            }
//        });
//
//        String frag_location = MainActivity.frag_loc;
//        UpdateUi(frag_location);
//        search.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                Intent intent_temp = new Intent(v.getContext(), MainActivity.class);
//                Bundle bundle = new Bundle();
//                bundle.putInt("FRAGMENT", 1);
//                intent_temp.putExtras(bundle);
//                intent_temp.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                startActivity(intent_temp);
//                finish();
//            }
//        });
//        calender.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                Intent intent_temp = new Intent(v.getContext(), MainActivity.class);
//                Bundle bundle = new Bundle();
//                bundle.putInt("FRAGMENT", 2);
//                intent_temp.putExtras(bundle);
//                intent_temp.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                startActivity(intent_temp);
//                finish();
//            }
//        });
//        home.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                Intent intent_temp = new Intent(v.getContext(), MainActivity.class);
//                Bundle bundle = new Bundle();
//                bundle.putInt("FRAGMENT", 3);
//                intent_temp.putExtras(bundle);
//                intent_temp.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                startActivity(intent_temp);
//                finish();
//            }
//        });
//        notification.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                Intent intent_temp = new Intent(v.getContext(), MainActivity.class);
//                Bundle bundle = new Bundle();
//                bundle.putInt("FRAGMENT", 4);
//                intent_temp.putExtras(bundle);
//                intent_temp.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                startActivity(intent_temp);
//                finish();
//            }
//        });
//        profile.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                Intent intent_temp = new Intent(v.getContext(), MainActivity.class);
//                Bundle bundle = new Bundle();
//                bundle.putInt("FRAGMENT", 5);
//                intent_temp.putExtras(bundle);
//                intent_temp.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                startActivity(intent_temp);
//                finish();
//            }
//        });
//    }
//
//    public void WebApiGetMembers(String clubId) {
//        try {
//            String pid = SharedpreferenceUtility.getInstance(GroupFollowersByGroupActivity.this).getString(AppConstants.PERSON_PID);
//            JSONObject jsonObject = new JSONObject();
//            jsonObject.put("club_id", clubId);
//            List<NameValuePair> param = new ArrayList<NameValuePair>();
//            String url = WebServiceDetails.DEFAULT_BASE_URL + "getClubMembers";
//            new WebRequestTask(GroupFollowersByGroupActivity.this, param, _handler, WebRequestTask.POST, jsonObject, WebServiceDetails.PID_GET_CLUB_MEMBER,
//                    true, url).execute();
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
//    }
//
//    private List<GroupMembersByGroup_infoActivity> createList_gm(int size) {
//        List<GroupMembersByGroup_infoActivity> result = new ArrayList<GroupMembersByGroup_infoActivity>();
//        for (int i = 1; i <= size; i++) {
//            GroupMembersByGroup_infoActivity ci = new GroupMembersByGroup_infoActivity();
//            result.add(ci);
//        }
//
//        return result;
//    }
//
//    private final Handler _handler = new Handler() {
//        public void handleMessage(Message msg) {
//            int response_code = msg.what;
//            if (response_code != 0 && response_code != 204) {
//                String strResponse = (String) msg.obj;
//                Log.v("Response", strResponse);
//                if (strResponse != null && strResponse.length() > 0) {
//                    switch (response_code) {
//                        case WebServiceDetails.PID_GET_CLUB_MEMBER: {
//                            try {
//
//                                JSONObject jsonObject = new JSONObject(strResponse);
//                                if (jsonObject.has("items")) {
//
//                                    JSONArray array = jsonObject.getJSONArray("items");
//                                    if (array.length() > 0) {
//                                        for (int i = 0; i < array.length(); i++) {
//                                            JSONObject innerObject = array.getJSONObject(i);
//                                            GroupMemberBean bean = new GroupMemberBean();
//                                            bean.setPhotoUrl(innerObject.optString("photoUrl"));
//                                            bean.setName(innerObject.optString("name"));
//                                            bean.setBatch(innerObject.optString("batch"));
//                                            bean.setBranch(innerObject.optString("branch"));
//                                            bean.setKind(innerObject.optString("kind"));
//                                            memberList.add(bean);
//                                        }
//                                        GroupMembersByGroupAdapterActivity gm = new GroupMembersByGroupAdapterActivity(memberList
//                                                , GroupFollowersByGroupActivity.this  );
//                                        members_list.setAdapter(gm);
//                                    }
//                                }
//                            } catch (Exception e) {
//                                e.printStackTrace();
//                            }
//
//                        }
//                        break;
//
//                        default:
//                            break;
//                    }
//                } else {
//                    Toast.makeText(GroupFollowersByGroupActivity.this, "SERVER_ERROR", Toast.LENGTH_LONG).show();
//                }
//            } else {
//                Toast.makeText(GroupFollowersByGroupActivity.this, "SERVER_ERROR", Toast.LENGTH_LONG).show();
//            }
//        }
//    };
//
//    public void UpdateUi(String str) {
//
//        if (str.equals("home")) {
//            search.setImageResource(R.mipmap.search);
//            calender.setImageResource(R.mipmap.calendar);
//            home.setImageResource(R.mipmap.home_selected);
//            notification.setImageResource(R.mipmap.notification);
//            profile.setImageResource(R.mipmap.profile);
//
//            searchLine.setBackgroundColor(getResources().getColor(R.color.carbonColor));
//            calLine.setBackgroundColor(getResources().getColor(R.color.carbonColor));
//            notificationLine.setBackgroundColor(getResources().getColor(R.color.carbonColor));
//            profileLine.setBackgroundColor(getResources().getColor(R.color.carbonColor));
//            homeLine.setBackgroundColor(getResources().getColor(R.color.yello));
//
//        } else if (str.equals("profile")) {
//
//            search.setImageResource(R.mipmap.search);
//            calender.setImageResource(R.mipmap.calendar);
//            home.setImageResource(R.mipmap.home);
//            notification.setImageResource(R.mipmap.notification);
//            profile.setImageResource(R.mipmap.profile_selected);
//            searchLine.setBackgroundColor(getResources().getColor(R.color.carbonColor));
//            calLine.setBackgroundColor(getResources().getColor(R.color.carbonColor));
//            notificationLine.setBackgroundColor(getResources().getColor(R.color.carbonColor));
//            profileLine.setBackgroundColor(getResources().getColor(R.color.yello));
//            homeLine.setBackgroundColor(getResources().getColor(R.color.carbonColor));
//
//        } else if (str.equals("search")) {
//
//            search.setImageResource(R.mipmap.search_selected);
//            calender.setImageResource(R.mipmap.calendar);
//            home.setImageResource(R.mipmap.home);
//            notification.setImageResource(R.mipmap.notification);
//            profile.setImageResource(R.mipmap.profile);
//
//            searchLine.setBackgroundColor(getResources().getColor(R.color.yello));
//            calLine.setBackgroundColor(getResources().getColor(R.color.carbonColor));
//            notificationLine.setBackgroundColor(getResources().getColor(R.color.carbonColor));
//            profileLine.setBackgroundColor(getResources().getColor(R.color.carbonColor));
//            homeLine.setBackgroundColor(getResources().getColor(R.color.carbonColor));
//
//        } else if (str.equals("calender")) {
//
//
//            search.setImageResource(R.mipmap.search);
//            calender.setImageResource(R.mipmap.calendar_selected);
//            home.setImageResource(R.mipmap.home);
//            notification.setImageResource(R.mipmap.notification);
//            profile.setImageResource(R.mipmap.profile);
//
//            searchLine.setBackgroundColor(getResources().getColor(R.color.carbonColor));
//            calLine.setBackgroundColor(getResources().getColor(R.color.yello));
//            notificationLine.setBackgroundColor(getResources().getColor(R.color.carbonColor));
//            profileLine.setBackgroundColor(getResources().getColor(R.color.carbonColor));
//            homeLine.setBackgroundColor(getResources().getColor(R.color.carbonColor));
//
//        } else if (str.equals("notification")) {
//
//            search.setImageResource(R.mipmap.search);
//            calender.setImageResource(R.mipmap.calendar);
//            home.setImageResource(R.mipmap.home);
//            notification.setImageResource(R.mipmap.notification_selected);
//            profile.setImageResource(R.mipmap.profile);
//
//            searchLine.setBackgroundColor(getResources().getColor(R.color.carbonColor));
//            calLine.setBackgroundColor(getResources().getColor(R.color.carbonColor));
//            notificationLine.setBackgroundColor(getResources().getColor(R.color.yello));
//            profileLine.setBackgroundColor(getResources().getColor(R.color.carbonColor));
//            homeLine.setBackgroundColor(getResources().getColor(R.color.carbonColor));
//        }
//
//
//    }
//
//}
//
//
