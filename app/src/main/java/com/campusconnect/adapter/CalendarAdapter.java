package com.campusconnect.adapter;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableStringBuilder;
import android.text.style.TypefaceSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

//import com.activeandroid.query.Select;
import com.campusconnect.R;
import com.campusconnect.activity.CommentsActivity;
import com.campusconnect.activity.GroupPageActivity;
import com.campusconnect.activity.InEventActivity;
import com.campusconnect.bean.CampusFeedBean;
import com.campusconnect.communicator.CCWebService;
import com.campusconnect.communicator.ServiceGenerator;
import com.campusconnect.communicator.WebRequestTask;
import com.campusconnect.communicator.WebServiceDetails;
import com.campusconnect.communicator.models.ModelsClubMiniForm;
import com.campusconnect.communicator.models.ModelsEventForm;
import com.campusconnect.communicator.models.ModelsEvents;
import com.campusconnect.communicator.models.ModelsFeed;
import com.campusconnect.communicator.models.ModelsLikePost;
import com.campusconnect.communicator.models.ModelsModifyEvent;
import com.campusconnect.communicator.models.ModelsPersonalInfoResponse;
import com.campusconnect.communicator.models.ModelsPersonalResponse;
import com.campusconnect.constant.AppConstants;
import com.campusconnect.database.DBHandler;
import com.campusconnect.database.DatabaseHandler;
import com.campusconnect.utility.CircularImageView;
import com.campusconnect.utility.CustomTypefaceSpan;
import com.campusconnect.utility.DateUtility;
import com.campusconnect.utility.SharedpreferenceUtility;
import com.campusconnect.utility.StickyRecyclerHeadersAdapter;
import com.squareup.picasso.Picasso;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
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

public class CalendarAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements StickyRecyclerHeadersAdapter<RecyclerView.ViewHolder> {

    // region Constants
    public static final int HEADER = 0;
    public static final int ITEM = 1;
    public static final int LOADING = 2;
    // endregion

    // region Member Variables
    private List<ModelsFeed> mPosts;
    private boolean mIsLoadingFooterAdded = false;
    // endregion

    public static Typeface r_med, r_reg;
    public static Typeface r_lig;
    TypefaceSpan robotoRegularSpan_for_comments_attendee_number;
    SpannableStringBuilder comments_text, attendees_text;
    RecyclerView attendees_list;
    ArrayList<ModelsPersonalResponse> attendeesList = new ArrayList<ModelsPersonalResponse>();

//    ArrayList<CampusFeedBean> calList;

        private DBHandler db;
    public int attending = 1;
    int posi = 0;
    Context context;
    //Get the day from the server for each card and feed it to the getHeaderId(int position) function below.
    //int dates[] = {1, 18, 27, 27, 29, 18, 29};

    CCWebService mCCService;


    // region Constructors
    public CalendarAdapter(Context context) {
        this.mPosts = new ArrayList<>();
        this.context = context;
        db = new DBHandler(context);
    }
    // endregion

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;

        mCCService = ServiceGenerator.createService(
                CCWebService.class,
                CCWebService.BASE_URL);

        switch (viewType) {
            case HEADER:
                viewHolder = onCreateHeaderViewHolder(parent);
                break;
            case ITEM:
                viewHolder = createFeedViewHolder(parent);
                break;
            case LOADING:
                viewHolder = createLoadingViewHolder(parent);
                break;
            default:
                //Timber.e("[ERR] type is not supported!!! type is %d", viewType);
                break;
        }

        return viewHolder;
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        switch (getItemViewType(position)) {
            case HEADER:
                onBindHeaderViewHolder(viewHolder,
                        position);
                break;
            case ITEM:
                bindCalendarViewHolder(viewHolder, position);
                break;
            case LOADING:
                bindLoadingViewHolder(viewHolder);
            default:
                break;
        }
    }

    private void bindLoadingViewHolder(RecyclerView.ViewHolder viewHolder) {
        MoreViewHolder holder = (MoreViewHolder) viewHolder;
    }


    private RecyclerView.ViewHolder createLoadingViewHolder(ViewGroup parent) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.load_more, parent, false);

        return new MoreViewHolder(v);
    }

    public static class MoreViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.loading_iv)
        ProgressBar mLoadingImageView;

        public MoreViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            mLoadingImageView.getIndeterminateDrawable().setColorFilter(Color.rgb(250, 209, 86), PorterDuff.Mode.MULTIPLY);
        }
    }


    public CalendarViewHolder createFeedViewHolder(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_card_layout_college_feed, parent, false);
        return new CalendarViewHolder(view) {
        };
    }

    @Override
    public int getItemViewType(int position) {
        return (position == mPosts.size() - 1 && mIsLoadingFooterAdded) ? LOADING : ITEM;
    }

    // region Helper Methods
    private void add(ModelsFeed item) {
        mPosts.add(item);
        notifyItemInserted(mPosts.size() - 1);
    }

    public void addAll(List<ModelsFeed> posts) {
        for (ModelsFeed post : posts) {
            add(post);
        }
    }

    public void remove(ModelsFeed item) {
        int position = mPosts.indexOf(item);
        if (position > -1) {
            mPosts.remove(position);
            notifyItemRemoved(position);
        }
    }

    public void clear() {
        mIsLoadingFooterAdded = false;
        while (getItemCount() > 0) {
            remove(getItem(0));
        }
    }

    public void addLoading() {
        mIsLoadingFooterAdded = true;
        add(new ModelsFeed());
    }

    public ModelsFeed getItem(int position) {
        return mPosts.get(position);
    }

    public void removeLoading() {
        mIsLoadingFooterAdded = false;
        ModelsFeed item = null;
        int position = mPosts.size() - 1;
        if (position >= 0) {
            item = getItem(position);
        }
        if (item != null) {
            mPosts.remove(position);
            notifyItemRemoved(position);
        }
    }


    public void bindCalendarViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
        CalendarViewHolder holder = (CalendarViewHolder) viewHolder;
        ModelsFeed cf = mPosts.get(i);
        holder.event_title.setText("" + cf.getTitle());

        holder.timestamp.setText("Posted " + DateUtility.getRelativeDate(cf.getTimestamp()));


        holder.group_name.setText("" + cf.getClubName());
        try {
            Picasso.with(context).load(cf.getPhotoUrl()).placeholder(R.drawable.default_card_image).into(holder.event_photo);
        } catch (Exception e) {
            Picasso.with(context).load(R.drawable.default_card_image).into(holder.event_photo);
        }
        try {
            Picasso.with(context).load(cf.getClubphotoUrl()).into(holder.group_icon);
        } catch (Exception e) {
            Picasso.with(context).load(R.mipmap.default_image).into(holder.group_icon);
        }
        String title = cf.getTitle();
        //news
        {
            holder.day.setVisibility(View.VISIBLE);
            holder.date_month.setVisibility(View.VISIBLE);
            holder.time.setVisibility(View.VISIBLE);

            SimpleDateFormat inFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date date = null;
            try {
                date = inFormat.parse(cf.getStartDate());
                Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
                calendar.setTime(date);

                SimpleDateFormat outFormat = new SimpleDateFormat("EEEE");
                String goal = outFormat.format(date);
                Log.e("day", goal);
                //  Log.e("entry", cf.getStartDate());
                SimpleDateFormat monthFormat = new SimpleDateFormat("MMM");
                String month = monthFormat.format(date);
                Log.e("month", month);
                //    SimpleDateFormat dateformate = new SimpleDateFormat("dd");
                //   String dayFormate = monthFormat.format(date);
                // Log.e("day", "" + calendar.get(Calendar.DAY_OF_MONTH));
                if (goal.length() > 3) {
                    goal = goal.substring(0, 3);
                } else {
                }
                holder.day.setText("" + goal.toUpperCase());
                String day = "" + calendar.get(Calendar.DAY_OF_MONTH);
                if (month.length() > 0) {
                    month = month.substring(0, 3);
                } else {
                }
                Log.e(cf.getTitle(), day + "" + month);
                holder.date_month.setText("" + day + "" + month);

                SimpleDateFormat _24HourSDF = new SimpleDateFormat("HH:mm:ss");
                SimpleDateFormat _12HourSDF = new SimpleDateFormat("hh:mm a");
                Date _24HourDt = _24HourSDF.parse(cf.getStartTime());
                String time12 = _12HourSDF.format(_24HourDt);
                holder.time.setText("" + time12);

                setUpCount_attending(holder, cf);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            holder.news_icon.setVisibility(View.GONE);
            if (cf.getIsAttending().equals("Y")) {
                holder.going.setImageResource(R.mipmap.going_selected);
            } else {
                holder.going.setImageResource(R.mipmap.going);
            }
        }
    }

    private void setUpCount_attending(CalendarViewHolder college_feedViewHolder, ModelsFeed post) {

        robotoRegularSpan_for_comments_attendee_number = new CustomTypefaceSpan("", r_reg);

        attendees_text = new SpannableStringBuilder(post.getAttendees() + " ATTENDING");
        attendees_text.setSpan(robotoRegularSpan_for_comments_attendee_number, 0, post.getAttendees().length() + 1, 0);
        college_feedViewHolder.attendee_count.setText(attendees_text);

        comments_text = new SpannableStringBuilder(post.getCommentCount() + " COMMENTS");
        comments_text.setSpan(robotoRegularSpan_for_comments_attendee_number, 0, post.getCommentCount().length() + 1, 0);
        college_feedViewHolder.comments_count.setText(comments_text);
    }

    @Override
    public int getItemCount() {
        return mPosts.size();
    }

    @Override
    public long getHeaderId(int position) {
        char ch;
        String datestr = mPosts.get(position).getStartDate();
        SimpleDateFormat formate = new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;
        Calendar calendar = null;
        char ch1 = 0;
        try {
            date = formate.parse(datestr);
            calendar = Calendar.getInstance();
            calendar.setTime(date);
            Log.e("postion", "" + position);
            int dayStr = calendar.get(Calendar.DAY_OF_MONTH);
            ch1 = (char) ((char) dayStr + 64);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ch1;
    }

    public RecyclerView.ViewHolder onCreateHeaderViewHolder(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.view_header, parent, false);
        return new RecyclerView.ViewHolder(view) {
        };
    }

    public void onBindHeaderViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (mPosts.size() > position) {
            try {
                TextView textView = (TextView) holder.itemView;

                SimpleDateFormat formate = new SimpleDateFormat("yyyy-MM-dd");
                Date date = null;
                Calendar calendar = Calendar.getInstance();
                date = formate.parse(mPosts.get(position).getStartDate());
                calendar.setTime(date);
                SimpleDateFormat outFormat = new SimpleDateFormat("yyyy");
                String year = outFormat.format(date);
                Log.e("year", year);
                SimpleDateFormat monthFormat = new SimpleDateFormat("MMM");
                String month = monthFormat.format(date);
                Log.e("month", month);

                SimpleDateFormat dateformate = new SimpleDateFormat("dd");
                String dayFormate = dateformate.format(date);
                Log.e("day", "" + calendar.get(Calendar.DAY_OF_MONTH));

                textView.setText(dayFormate + " " + month + " " + year);
                Typeface r_med = Typeface.createFromAsset(holder.itemView.getContext().getAssets(), "font/Roboto_Light.ttf");
                textView.setTypeface(r_med);
                holder.itemView.setBackgroundColor(Color.rgb(56, 56, 56));
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    public class CalendarViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.college_feed_card)
        CardView college_feed;
        @Bind(R.id.tv_event)
        TextView event_title;
        @Bind(R.id.tv_group)
        TextView group_name;
        @Bind(R.id.tv_timestamp)
        TextView timestamp;
        @Bind(R.id.tv_day)
        TextView day;
        @Bind(R.id.tv_date_month)
        TextView date_month;
        @Bind(R.id.tv_time)
        TextView time;
        @Bind(R.id.tv_comments_count)
        TextView comments_count;
        @Bind(R.id.tv_attending_count)
        TextView attendee_count;
        @Bind(R.id.iv_event_photo)
        ImageView event_photo;
        @Bind(R.id.iv_news_icon)
        ImageView news_icon;
        @Bind(R.id.iv_going)
        ImageView going;
        @Bind(R.id.iv_share)
        ImageView share;
        @Bind(R.id.group_image)
        CircularImageView group_icon;

        public CalendarViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);

            r_med = Typeface.createFromAsset(v.getContext().getAssets(), "font/Roboto_Medium.ttf");
            r_reg = Typeface.createFromAsset(v.getContext().getAssets(), "font/Roboto_Regular.ttf");
            r_lig = Typeface.createFromAsset(v.getContext().getAssets(), "font/Roboto_Light.ttf");

            event_title.setTypeface(r_med);
            group_name.setTypeface(r_reg);
            timestamp.setTypeface(r_reg);
            if (!SharedpreferenceUtility.getInstance(context).getBoolean(AppConstants.IS_LITE)) {
                comments_count.setTypeface(r_lig);
                attendee_count.setTypeface(r_lig);
            }

            college_feed.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    posi = getAdapterPosition();
                    Intent intent_temp = new Intent(v.getContext(), InEventActivity.class);
//                    intent_temp.putExtra("CallerActivity", "beanUsage");
                    Bundle bundle = new Bundle();
                    ModelsFeed bean = mPosts.get(posi);
                    bundle.putParcelable("feed", bean);
//                    bundle.putSerializable("BEAN", bean);
                    intent_temp.putExtras(bundle);
                    context.startActivity(intent_temp);
                }
            });

            group_icon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos_for_share = getAdapterPosition();
                    ModelsFeed post = mPosts.get(pos_for_share);

                    ModelsClubMiniForm modelsClubMiniForm = db.getClubByClubId(post.getClubId());

//                    List<ModelsClubMiniForm> modelsClubMiniForms = new Select().from(ModelsClubMiniForm.class).where("clubId = ?", post.getClubId()).execute();
                    if (modelsClubMiniForm!=null) {
//                        ModelsClubMiniForm modelsClubMiniForm = modelsClubMiniForms.get(0);
                        Intent intent_temp = new Intent(v.getContext(), GroupPageActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putParcelable("club", modelsClubMiniForm);
                        intent_temp.putExtras(bundle);
                        v.getContext().startActivity(intent_temp);
                    }
                }
            });


            going.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    posi = getAdapterPosition();
                    String pid = SharedpreferenceUtility.getInstance(context).getString(AppConstants.PERSON_PID);
                    ModelsFeed post = mPosts.get(posi);
                    if (post.getIsAttending().equals("Y")) {
                        going.setImageResource(R.mipmap.going);
                        post.setIsAttending("N");
                        ModelsLikePost modelsLikePost = new ModelsLikePost();
                        modelsLikePost.setPostId(post.getFeedId());
                        modelsLikePost.setFromPid(pid);
                        Call unattendEvent = mCCService.unattendEvent(modelsLikePost);
                        unattendEvent.enqueue(callBack);
                        Toast.makeText(context, "NOT ATTENDING",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        ModelsModifyEvent modelsModifyEvent = new ModelsModifyEvent();
                        modelsModifyEvent.setFromPid(pid);
                        modelsModifyEvent.setEventId(post.getFeedId());
                        Call attendEvent = mCCService.attendEvent(modelsModifyEvent);
                        attendEvent.enqueue(callBack);
                        going.setImageResource(R.mipmap.going_selected);
                        post.setIsAttending("Y");
                        Toast.makeText(context, "ATTENDING",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            });

            share.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos_for_share = getAdapterPosition();
                    Intent i = new Intent(android.content.Intent.ACTION_SEND);
                    i.setType("text/plain");
                    StringBuilder stringBuilder = new StringBuilder();
                    String collegeId = SharedpreferenceUtility.getInstance(v.getContext()).getString(AppConstants.COLLEGE_ID);
                    String clubId = mPosts.get(getAdapterPosition()).getClubId();
                    String postId = mPosts.get(getAdapterPosition()).getFeedId();
                    String URL = "http://campusconnect.cc/college#/" + collegeId + "/groups/" + clubId + "/" + postId + "/1";
                    stringBuilder.append(" Hey check out "+mPosts.get(getAdapterPosition()).getTitle()+" at " + URL);
                    String shareBody = stringBuilder.toString();
                    i.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                    v.getContext().startActivity(Intent.createChooser(i, "Share via"));
                }
            });

            if (!SharedpreferenceUtility.getInstance(context).getBoolean(AppConstants.IS_LITE)) {
                attendee_count.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!(mPosts.get(getAdapterPosition()).getStartDate() == null || mPosts.get(getAdapterPosition()).getStartDate().isEmpty())) {
                            String id = mPosts.get(getAdapterPosition()).getFeedId();
                            AttendeesDialog attendeesDialog = new AttendeesDialog((Activity) v.getContext(), id);
                            attendeesDialog.show();
                            Window window = attendeesDialog.getWindow();
                            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                        }
                    }
                });

                comments_count.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        posi = getAdapterPosition();
                        Intent intent_comments = new Intent(v.getContext(), CommentsActivity.class);
                        intent_comments.putExtra("FLAG_POST_COMMENT", false);
                        intent_comments.putExtra("POST_ID", mPosts.get(posi).getFeedId());
                        context.startActivity(intent_comments);
                    }
                });
            }
        }
    }


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
        Context context_dialog;

        @Bind(R.id.cross_button)
        LinearLayout close;
        @Bind(R.id.tv_title)
        TextView attendees_text;
        Typeface r_med;


        public AttendeesDialog(Activity a, String id) {
            super(a);
            // TODO Auto-generated constructor stub
            this.c = a;
            this.context_dialog = context;
            this.id = id;
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

            if (this.id != null && this.id.length() != 0) {
                Call getAttendees = mCCService.getAttendeeList(id);
                getAttendees.enqueue(getAttendeescallBack);
            }
//            String clubId = getIntent().getStringExtra("clubId");
//            WebApiGetMembers("5696758453633024");   //I was just testing. I've no cclue how to add attendees list. --- RK
//
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

        // region Callbacks
        private Callback<ModelsPersonalInfoResponse> getAttendeescallBack = new Callback<ModelsPersonalInfoResponse>() {
            @Override
            public void onResponse(Response<ModelsPersonalInfoResponse> response, Retrofit retrofit) {
                attendeesList.clear();
                ModelsPersonalInfoResponse modelsPersonalInfoResponse = response.body();
                List<ModelsPersonalResponse> modelsPersonalResponses = modelsPersonalInfoResponse.getItems();
                attendeesList.addAll(modelsPersonalResponses);
//                for(int i=0;i<modelsPersonalResponses.size();i++){
//                    ModelsPersonalResponse bean = new ModelsPersonalResponse();
//                    bean.setPhotoUrl(modelsPersonalResponses.get(i).getPhotoUrl());
//                    bean.setName(modelsPersonalResponses.get(i).getName());
//                    bean.setBatch(modelsPersonalResponses.get(i).getBatch());
//                    bean.setBranch(modelsPersonalResponses.get(i).getBranch());
////                bean.setKind(modelsPersonalResponses.get(i).ge);
//                    attendeesList.add(bean);
//                }
                GroupMembersByGroupAdapterActivity gm = new GroupMembersByGroupAdapterActivity(attendeesList
                        , context);
                attendees_list.setAdapter(gm);
            }

            @Override
            public void onFailure(Throwable t) {
                //Timber.d("onFailure() : mQuery - " + mQuery);

            }
        };

        public void WebApiGetMembers(String clubId) {
            try {
                String pid = SharedpreferenceUtility.getInstance(context).getString(AppConstants.PERSON_PID);
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("club_id", clubId);
                List<NameValuePair> param = new ArrayList<NameValuePair>();
                String url = WebServiceDetails.DEFAULT_BASE_URL + "getClubMembers";
                new WebRequestTask(context, param, _handler_attendees_list, WebRequestTask.POST, jsonObject, WebServiceDetails.PID_GET_CLUB_MEMBER,
                        true, url).execute();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

    }

    private final Handler _handler_attendees_list = new Handler() {
        public void handleMessage(Message msg) {
            int response_code = msg.what;
            if (response_code != 0 && response_code != 204) {
                String strResponse = (String) msg.obj;
                Log.v("Response", strResponse);
                if (strResponse != null && strResponse.length() > 0) {
                    switch (response_code) {
                        case WebServiceDetails.PID_GET_CLUB_MEMBER: {
                            try {
                                attendeesList.clear();
                                JSONObject jsonObject = new JSONObject(strResponse);
                                if (jsonObject.has("items")) {

                                    JSONArray array = jsonObject.getJSONArray("items");
                                    if (array.length() > 0) {
                                        for (int i = 0; i < array.length(); i++) {
                                            JSONObject innerObject = array.getJSONObject(i);
                                            ModelsPersonalResponse bean = new ModelsPersonalResponse();
                                            bean.setPhotoUrl(innerObject.optString("photoUrl"));
                                            bean.setName(innerObject.optString("name"));
                                            bean.setBatch(innerObject.optString("batch"));
                                            bean.setBranch(innerObject.optString("branch"));
//                                            bean.setKind(innerObject.optString("kind"));
                                            attendeesList.add(bean);
                                        }
                                        GroupMembersByGroupAdapterActivity gm = new GroupMembersByGroupAdapterActivity(attendeesList
                                                , context);
                                        attendees_list.setAdapter(gm);
                                    }
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
                    Toast.makeText(context, "Network Not Available", Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(context, "Network Not Available", Toast.LENGTH_LONG).show();
            }
        }
    };
    //Attendee dialog box end region
}