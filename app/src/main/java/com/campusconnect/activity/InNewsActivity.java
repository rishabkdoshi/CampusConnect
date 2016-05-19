package com.campusconnect.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.TypefaceSpan;
import android.text.style.UnderlineSpan;
import android.text.util.Linkify;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.campusconnect.R;
import com.campusconnect.adapter.GroupMembersByGroupAdapterActivity;
import com.campusconnect.bean.CampusFeedBean;
import com.campusconnect.bean.GroupMemberBean;
import com.campusconnect.communicator.CCWebService;
import com.campusconnect.communicator.ServiceGenerator;
import com.campusconnect.communicator.WebRequestTask;
import com.campusconnect.communicator.WebServiceDetails;
import com.campusconnect.communicator.models.ModelsFeed;
import com.campusconnect.communicator.models.ModelsLikePost;
import com.campusconnect.communicator.models.ModelsModifyEvent;
import com.campusconnect.communicator.models.ModelsPersonalInfoResponse;
import com.campusconnect.communicator.models.ModelsPersonalResponse;
import com.campusconnect.constant.AppConstants;
import com.campusconnect.database.DatabaseHandler;
import com.campusconnect.utility.CircularImageView;
import com.campusconnect.utility.CustomTypefaceSpan;
import com.campusconnect.utility.DividerItemDecoration;
import com.campusconnect.utility.SharedpreferenceUtility;
import com.squareup.picasso.Picasso;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * Created by RK on 22-09-2015.
 */
public class InNewsActivity extends AppCompatActivity {

    //to be deleted
    CampusFeedBean bean;
    Boolean flag_selected_share;
    Boolean flag_share_clicked = false;
//    private DatabaseHandler dataBase;
    //to be deleted ends

    @Bind(R.id.iv_event) ImageView event_photo;
    @Bind(R.id.iv_location) ImageView location_icon;
    @Bind(R.id.heart_going) ImageView going;
    @Bind(R.id.iv_share) ImageView share;
    @Bind(R.id.tv_event_name) TextView e_name;
    @Bind(R.id.tv_time) TextView e_time;
    @Bind(R.id.tv_date) TextView e_date;
    @Bind(R.id.tv_group_name) TextView g_name;
    @Bind(R.id.tv_venue) TextView v_name;
    @Bind(R.id.tv_event_description) TextView e_description;
    @Bind(R.id.tv_attendees_count) TextView attendees_count;
    @Bind(R.id.tv_header) TextView tv_header;
    @Bind(R.id.group_icon) CircularImageView g_icon;
    @Bind(R.id.tv_comments_link) TextView tv_comments_text;
    @Bind(R.id.b_comment) Button comment_button;
    RecyclerView attendees_list;

    Typeface r_lig, r_reg, r_med;


    int no_of_comments=0;
    SpannableStringBuilder comments_text;

    String activity_title = "event post";

    CCWebService mCCService;

    TypefaceSpan robotoRegularSpan_for_attendees;
    SpannableStringBuilder attendees_text;

    int no_of_attendees;
    Boolean flag_news, flag_selected_attend_like;
    Boolean flag_attended_clicked = false;
    static int attending = 1;

    String caller="";

    ArrayList<ModelsPersonalResponse> attendeesList = new ArrayList<ModelsPersonalResponse>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_in_event);
        ButterKnife.bind(this);

        if(getIntent().getStringExtra("CallerActivity")!=null)
            caller = getIntent().getStringExtra("CallerActivity");
        else
            caller="";

        r_lig = Typeface.createFromAsset(getAssets(), "font/Roboto_Light.ttf");
        r_reg = Typeface.createFromAsset(getAssets(), "font/Roboto_Regular.ttf");
        r_med = Typeface.createFromAsset(getAssets(), "font/Roboto_Medium.ttf");

        tv_comments_text.setVisibility(View.GONE);
        comment_button.setVisibility(View.GONE);
        attendees_count.setVisibility(View.INVISIBLE);

        if(!(caller.equals("beanUsage"))) {
            attendees_count.setVisibility(View.VISIBLE);
            tv_comments_text.setVisibility(View.VISIBLE);
            comment_button.setVisibility(View.VISIBLE);
            final ModelsFeed feed = getIntent().getParcelableExtra("feed");
            if (feed.getCommentCount() != null)
                no_of_comments = new Integer(feed.getCommentCount()); //Attach the value gotten from the server
            if (no_of_comments == 1)
                comments_text = new SpannableStringBuilder("This " + activity_title + " has " + no_of_comments + " comment.");
            else
                comments_text = new SpannableStringBuilder("This " + activity_title + " has " + no_of_comments + " comments.");
            comments_text.setSpan(new UnderlineSpan(), 5 + activity_title.length() + 5, 5 + activity_title.length() + 5 + Integer.toString(no_of_comments).length(), 0);
            tv_comments_text.setText(comments_text);

            robotoRegularSpan_for_attendees = new CustomTypefaceSpan("", r_reg);
            no_of_attendees = 255; //Value has to be taken from the server
            attendees_text = new SpannableStringBuilder("+" + no_of_attendees + attending);
            attendees_text.setSpan(robotoRegularSpan_for_attendees, 0, Integer.toString(no_of_attendees).length() + 1, 0);
            attendees_count.setText(attendees_text);

            e_name.setTypeface(r_med);
            e_time.setTypeface(r_reg);
            e_date.setTypeface(r_reg);
            e_description.setTypeface(r_reg);
            g_name.setTypeface(r_reg);
            v_name.setTypeface(r_reg);
            attendees_count.setTypeface(r_lig);

            flag_news = feed.getAttendees() == null;

            if (feed.getHasLiked() != null) {
                flag_selected_attend_like = feed.getHasLiked().equals("Y");
            } else if (feed.getIsAttending() != null) {
                flag_selected_attend_like = feed.getIsAttending().equals("Y");
            }
            if (flag_news) {
                location_icon.setVisibility(View.GONE);
                going.setImageResource(R.drawable.selector_heart);
            } else {
                location_icon.setVisibility(View.VISIBLE);
                going.setImageResource(R.mipmap.going);
            }

            if (feed != null) {
                g_name.setText("" + feed.getClubabbreviation());
                e_name.setText("" + feed.getTitle());
                e_description.setText("" + feed.getDescription());
                Linkify.addLinks(e_description, Linkify.ALL);

                if (feed.getStartDate() == null || feed.getStartDate().isEmpty()) {
                    attendees_count.setVisibility(View.INVISIBLE);
                    e_time.setVisibility(View.INVISIBLE);
                    tv_header.setText("News");
                    v_name.setVisibility(View.INVISIBLE);
                } else {

                    v_name.setText("" + feed.getVenue());
                    int attendies = new Integer(feed.getAttendees());
                    SpannableString underlining_attendees = new SpannableString("+" + attendies + " attending");
                    underlining_attendees.setSpan(new UnderlineSpan(), 0, underlining_attendees.length(), 0);
                    attendees_count.setText(underlining_attendees);
                    try {
                        SimpleDateFormat _24HourSDF = new SimpleDateFormat("HH:mm:ss");
                        SimpleDateFormat _12HourSDF = new SimpleDateFormat("hh:mm a");
                        Date _24HourDt = null;
                        _24HourDt = _24HourSDF.parse(feed.getStartTime());
                        String time12 = _12HourSDF.format(_24HourDt);
                        e_time.setText("" + time12);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
                try {
                    Picasso.with(InNewsActivity.this).load(feed.getPhotoUrl()).placeholder(R.drawable.default_card_image).into(event_photo);
                } catch (Exception e) {
                    e.printStackTrace();
                    Picasso.with(InNewsActivity.this).load(R.drawable.default_card_image).into(event_photo);
                }
                try {
                    Picasso.with(InNewsActivity.this).load(feed.getClubphotoUrl()).into(g_icon);
                } catch (Exception e) {
                    e.printStackTrace();
                    Picasso.with(InNewsActivity.this).load(R.mipmap.default_image).into(event_photo);
                }
                SimpleDateFormat inFormat = new SimpleDateFormat("yyyy-MM-dd");
                Date date = null;
                try {
                    if (flag_news) {
                        date = inFormat.parse(feed.getTimestamp());
                    } else {
                        date = inFormat.parse(feed.getStartDate());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
                calendar.setTime(date);


                SimpleDateFormat outFormat = new SimpleDateFormat("EEEE");
                String goal = outFormat.format(date);
                Log.e("day", goal);
                //  Log.e("entry", cf.getStartDate());
                SimpleDateFormat monthFormat = new SimpleDateFormat("MMMMMMMM");
                String month = monthFormat.format(date);
                Log.e("month", month);

                SimpleDateFormat dateformate = new SimpleDateFormat("dd");
                String dayFormate = monthFormat.format(date);
                Log.e("day", "" + calendar.get(Calendar.DAY_OF_MONTH));
                String day = "" + calendar.get(Calendar.DAY_OF_MONTH);
                e_date.setText("" + goal + " " + day + " " + month);

            }
//Extract the data…
            if (flag_selected_attend_like) {
                if (flag_news)
                    going.setImageResource(R.mipmap.heart_selected);
                else
                    going.setImageResource(R.mipmap.going_selected);
                flag_attended_clicked = true;
            } else {
                if (flag_news)
                    going.setImageResource(R.mipmap.heart);
                else
                    going.setImageResource(R.mipmap.going);
                flag_attended_clicked = false;
            }

            going.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    String pid = SharedpreferenceUtility.getInstance(v.getContext()).getString(AppConstants.PERSON_PID);

                    mCCService = ServiceGenerator.createService(
                            CCWebService.class,
                            CCWebService.BASE_URL);

                    if (feed.getStartDate() == null || feed.getStartDate().isEmpty()){
                        if (feed.getHasLiked().equals("Y")) {
                            going.setImageResource(R.mipmap.heart);
                            feed.setHasLiked("N");
                        } else {
                            feed.setHasLiked("Y");
                            going.setImageResource(R.mipmap.heart_selected);
                        }
                    }else{
                        if (feed.getIsAttending() != null) {
                            if (feed.getIsAttending().equals("Y")) {
                                going.setImageResource(R.mipmap.going);
                                feed.setIsAttending("N");
                                ModelsLikePost modelsLikePost = new ModelsLikePost();
                                modelsLikePost.setPostId(feed.getFeedId());
                                modelsLikePost.setFromPid(pid);
                                Call unattendEvent = mCCService.unattendEvent(modelsLikePost);
                                unattendEvent.enqueue(callBack);
                                Toast.makeText(InNewsActivity.this, "NOT ATTENDING",
                                        Toast.LENGTH_SHORT).show();
                            } else {
                                ModelsModifyEvent modelsModifyEvent = new ModelsModifyEvent();
                                modelsModifyEvent.setFromPid(pid);
                                modelsModifyEvent.setEventId(feed.getFeedId());
                                feed.setIsAttending("Y");
                                Call attendEvent = mCCService.attendEvent(modelsModifyEvent);
                                attendEvent.enqueue(callBack);
                                going.setImageResource(R.mipmap.going_selected);
                                Toast.makeText(InNewsActivity.this, "ATTENDING",
                                        Toast.LENGTH_SHORT).show();
                            }

                        }
                    }
                }
            });

//
//        going.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                if (flag_attended_clicked) {
//                    if (flag_news) {
//                        going.setImageResource(R.mipmap.heart);
////                        dataBase.saveFeedInfo(bean.getPid(), "0");
//                        //Toast.makeText(InEventActivity.this, "coming soon", Toast.LENGTH_SHORT).show();
//                    } else {
//                        going.setImageResource(R.mipmap.going);
////                        dataBase.saveFeedInfo(bean.getPid(), "0");
//
//                        try {
////                            String persoPid = SharedpreferenceUtility.getInstance(InEventActivity.this).getString(AppConstants.PERSON_PID);
////                            String pid = feed.getId();
////                            JSONObject jsonObject = new JSONObject();
////                            jsonObject.put("eventId", pid);
////                            jsonObject.put("fromPid", persoPid);
////                            WebApiAttending(jsonObject);
//
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                    }
//                    flag_attended_clicked = false;
//                } else {
//                    if (flag_news) {
////                        dataBase.saveFeedInfo(bean.getPid(), "1");
//                        going.setImageResource(R.mipmap.heart_selected);
//                        //Toast.makeText(InEventActivity.this, "coming soon", Toast.LENGTH_SHORT).show();
//                    } else {
////                        dataBase.saveFeedInfo(bean.getPid(), "1");
//                        going.setImageResource(R.mipmap.going_selected);
//                        try {
////                            String persoPid = SharedpreferenceUtility.getInstance(InEventActivity.this).getString(AppConstants.PERSON_PID);
////                            String pid = bean.getPid();
////                            JSONObject jsonObject = new JSONObject();
////                            jsonObject.put("eventId", pid);
////                            jsonObject.put("from_pid", persoPid);
////                            WebApiAttending(jsonObject);
//
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                    }
//                    flag_attended_clicked = true;
//                }
//            }
//        });

            share.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(android.content.Intent.ACTION_SEND);
                    i.setType("text/plain");
                    StringBuilder stringBuilder = new StringBuilder();
                    String collegeId = SharedpreferenceUtility.getInstance(v.getContext()).getString(AppConstants.COLLEGE_ID);
                    String clubId = feed.getClubId();
                    String postId = feed.getFeedId();
                    String URL= "http://campusconnect.cc/college#/"+collegeId+"/groups/"+clubId+"/"+postId+"/1";
                    stringBuilder.append(" Hey check out "+feed.getTitle()+" at " + URL);
                    String shareBody = stringBuilder.toString();
                    i.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                    v.getContext().startActivity(Intent.createChooser(i, "Share via"));
//
                }
            });

            attendees_count.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String id = feed.getFeedId();
                    AttendeesDialog attendeesDialog = new AttendeesDialog((Activity) v.getContext(),id);
                    attendeesDialog.show();
                    Window window = attendeesDialog.getWindow();
                    window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                }

            });

            event_photo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent_temp = new Intent(v.getContext(), EventPhotoFullScreenActivity.class);
                    intent_temp.putExtra("PICTURE", feed.getPhotoUrl());
                    startActivity(intent_temp);
                }
            });


            tv_comments_text.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent_comments = new Intent(v.getContext(), CommentsActivity.class);
                    intent_comments.putExtra("FLAG_POST_COMMENT", false);
                    intent_comments.putExtra("POST_ID",feed.getFeedId());
                    startActivity(intent_comments);
                }

            });

            comment_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent_comments = new Intent(v.getContext(), CommentsActivity.class);
                    intent_comments.putExtra("FLAG_POST_COMMENT", true);
                    intent_comments.putExtra("POST_ID", feed.getFeedId());
                    startActivity(intent_comments);
                }

            });
        }


    }


    // region Callbacks
    private Callback<ModelsPersonalInfoResponse> getAttendeescallBack = new Callback<ModelsPersonalInfoResponse>() {
        @Override
        public void onResponse(Response<ModelsPersonalInfoResponse> response, Retrofit retrofit) {
            attendeesList.clear();
            ModelsPersonalInfoResponse modelsPersonalInfoResponse=response.body();
            List<ModelsPersonalResponse> modelsPersonalResponses = modelsPersonalInfoResponse.getItems();
            attendeesList.addAll(modelsPersonalResponses);
//            for(int i=0;i<modelsPersonalResponses.size();i++){
//                ModelsPersonalResponse bean = new ModelsPersonalResponse();
//                bean.setPhotoUrl(modelsPersonalResponses.get(i).getPhotoUrl());
//                bean.setName(modelsPersonalResponses.get(i).getName());
//                bean.setBatch(modelsPersonalResponses.get(i).getBatch());
//                bean.setBranch(modelsPersonalResponses.get(i).getBranch());
////                bean.setKind(modelsPersonalResponses.get(i).ge);
//                attendeesList.add(bean);
//            }
            GroupMembersByGroupAdapterActivity gm = new GroupMembersByGroupAdapterActivity(attendeesList
                    , InNewsActivity.this);
            attendees_list.setAdapter(gm);
        }

        @Override
        public void onFailure(Throwable t) {
            //Timber.d("onFailure() : mQuery - " + mQuery);

        }
    };
    // region Callbacks
    private Callback<Void> callBack = new Callback<Void>() {
        @Override
        public void onResponse(Response<Void> response, Retrofit retrofit) {

        }

        @Override
        public void onFailure(Throwable t) {
            //Timber.d("onFailure() : mQuery - " + mQuery);

        }
    };

    public class AttendeesDialog extends Dialog {

        public Activity c;
        public Dialog d;
        public String id;
        Context context;

        @Bind(R.id.cross_button)
        LinearLayout close;
        @Bind(R.id.tv_title)
        TextView attendees_text;
        Typeface r_med;


        public AttendeesDialog(Activity a,String id) {
            super(a);
            // TODO Auto-generated constructor stub
            this.c = a;
            this.context = context;
            this.id=id;
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            setContentView(R.layout.attending_dialog_box);
            ButterKnife.bind(this);

            attendees_list = (RecyclerView) findViewById(R.id.rv_attendees_list);


            mCCService = ServiceGenerator.createService(
                    CCWebService.class,
                    CCWebService.BASE_URL);

            if(this.id!=null&&this.id.length()!=0){
                Call getAttendees=mCCService.getAttendeeList(id);
                getAttendees.enqueue(getAttendeescallBack);
            }
//            String clubId = getIntent().getStringExtra("clubId");
//            WebApiGetMembers("5696758453633024");   //I was just testing. I've no cclue how to add attendees list. --- RK
//
            r_med = Typeface.createFromAsset(getAssets(), "font/Roboto_Medium.ttf");
            attendees_text.setTypeface(r_med);

            attendees_list.setHasFixedSize(true);
            LinearLayoutManager llm = new LinearLayoutManager(c);
            llm.setOrientation(LinearLayoutManager.VERTICAL);
            attendees_list.setLayoutManager(llm);
            attendees_list.setItemAnimator(new DefaultItemAnimator());

            close.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                }

            });
        }

    }

}
