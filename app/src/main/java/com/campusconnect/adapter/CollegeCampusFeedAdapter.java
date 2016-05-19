
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
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.TypefaceSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

//import com.activeandroid.query.Select;
import com.campusconnect.R;
import com.campusconnect.activity.CommentsActivity;
import com.campusconnect.activity.GroupPageActivity;
import com.campusconnect.activity.InEventActivity;
import com.campusconnect.communicator.CCWebService;
import com.campusconnect.communicator.ServiceGenerator;
import com.campusconnect.communicator.WebRequestTask;
import com.campusconnect.communicator.WebServiceDetails;
import com.campusconnect.communicator.models.ModelsClubMiniForm;
import com.campusconnect.communicator.models.ModelsCollegeFeed;
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
import com.campusconnect.utility.LogUtility;
import com.campusconnect.utility.SharedpreferenceUtility;
import com.squareup.picasso.Picasso;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
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

/**
 * Created by rkd on 15/01/16.
 */
public class CollegeCampusFeedAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    // region Constants
    public static final int HEADER = 0;
    public static final int ITEM = 1;
    public static final int LOADING = 2;
    // endregion

    // region Member Variables
    private List<ModelsFeed> mPosts;
    private boolean mIsLoadingFooterAdded = false;
    // endregion

    // region Listeners
    // endregion

    Context context;
    RecyclerView attendees_list;
    private DBHandler db;

//    private DatabaseHandler dataBase;
CCWebService mCCService;
    ArrayList<ModelsPersonalResponse> attendeesList = new ArrayList<ModelsPersonalResponse>();
    // region Constructors
    public CollegeCampusFeedAdapter(Context context) {
        mPosts = new ArrayList<>();
        this.context = context;
        db = new DBHandler(context);

    }
    // endregion

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;

        switch (viewType) {
            case HEADER:
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
                break;
            case ITEM:
                bindCollegeViewHolder(viewHolder, position);
                break;
            case LOADING:
                bindLoadingViewHolder(viewHolder);
            default:
                break;
        }
    }

    // region Interfaces
    public interface OnItemClickListener {
        void onItemClick(int position, View view);
    }
    // endregion
//
//    private void setUpType(CollegeCampusFeedViewHolder college_feedViewHolder,ModelsFeed post){
//        if(post.getHasLiked()!=null){
//            post.setType("news");
//            college_feedViewHolder.going.setVisibility(View.VISIBLE);
////            college_feedViewHolder.going.setVisibility(View.GONE);
//        }else{
//            post.setType("event");
//            college_feedViewHolder.going.setVisibility(View.VISIBLE);
////            college_feedViewHolder.news_icon.setVisibility(View.GONE);
//        }
//    }

    private void setClickListeners(final CollegeCampusFeedViewHolder collegeCampusFeedViewHolder, final ModelsFeed post){
        final CCWebService mCCService ;
        mCCService = ServiceGenerator.createService(
                CCWebService.class,
                CCWebService.BASE_URL);

        if(post.getHasLiked()!=null){
            collegeCampusFeedViewHolder.going.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (post.getHasLiked().equals("Y")) {
                        collegeCampusFeedViewHolder.going.setImageResource(R.mipmap.heart);
                        post.setHasLiked("N");
                    } else {
                        collegeCampusFeedViewHolder.going.setImageResource(R.mipmap.heart_selected);
                        post.setHasLiked("Y");
                    }
                }
            });
        }
        else{

            collegeCampusFeedViewHolder.going.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    String pid=SharedpreferenceUtility.getInstance(context).getString(AppConstants.PERSON_PID);
                    Log.d("pid here",pid);
                    if(post.getIsAttending().equals("Y")){
                        collegeCampusFeedViewHolder.going.setImageResource(R.mipmap.going);
                        post.setIsAttending("N");
                        ModelsLikePost modelsLikePost=new ModelsLikePost();
                        modelsLikePost.setPostId(post.getFeedId());
                        modelsLikePost.setFromPid(pid);
                        Call unattendEvent = mCCService.unattendEvent(modelsLikePost);
                        unattendEvent.enqueue(callBack);
                        Toast.makeText(context, "NOT ATTENDING",
                                Toast.LENGTH_SHORT).show();
                    }else{
                        ModelsModifyEvent modelsModifyEvent=new ModelsModifyEvent();
                        modelsModifyEvent.setFromPid(pid);
                        modelsModifyEvent.setEventId(post.getFeedId());
                        Call attendEvent = mCCService.attendEvent(modelsModifyEvent);
                        attendEvent.enqueue(callBack);
                        collegeCampusFeedViewHolder.going.setImageResource(R.mipmap.going_selected);
                        post.setIsAttending("Y");
                        Toast.makeText(context, "ATTENDING",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
    private void bindCollegeViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
        final CollegeCampusFeedViewHolder college_feedViewHolder = (CollegeCampusFeedViewHolder) viewHolder;

        final ModelsFeed post = mPosts.get(i);
        if (post != null) {
//            setUpType(college_feedViewHolder,post);
            setUpTitle(college_feedViewHolder.event_title, post);
            setUpDuration(college_feedViewHolder.timestamp, post);
            setUpGroupName(college_feedViewHolder.group_name, post);
            setUpFeedImage(college_feedViewHolder.event_photo, post);
            setUpGroupImage(college_feedViewHolder.group_icon, post);
            setUpYellowCard(college_feedViewHolder, post, i);
            setClickListeners(college_feedViewHolder,post);
        }
    }


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

    private void setUpNewsCard(CollegeCampusFeedViewHolder college_feedViewHolder, ModelsFeed post, int i) {
        college_feedViewHolder.day.setVisibility(View.GONE);
        college_feedViewHolder.date_month.setVisibility(View.GONE);
        college_feedViewHolder.time.setVisibility(View.GONE);
        college_feedViewHolder.c_tag.setBackgroundResource(R.drawable.post_tag);
        if(post.getHasLiked().equals("Y")){
            college_feedViewHolder.going.setImageResource(R.mipmap.heart_selected);
        }else{
            college_feedViewHolder.going.setImageResource(R.mipmap.heart);
        }
    }

    private void setUpEventCard(CollegeCampusFeedViewHolder college_feedViewHolder, ModelsFeed post, int i) {
        college_feedViewHolder.day.setVisibility(View.VISIBLE);
        college_feedViewHolder.date_month.setVisibility(View.VISIBLE);
        college_feedViewHolder.time.setVisibility(View.VISIBLE);
        college_feedViewHolder.c_tag.setBackgroundResource(R.drawable.tag);
        college_feedViewHolder.news_icon.setVisibility(View.GONE);
        SimpleDateFormat inFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;
        try {
            date = inFormat.parse(post.getStartDate());
            Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
            calendar.setTime(date);

            SimpleDateFormat outFormat = new SimpleDateFormat("EEEE");
            String goal = outFormat.format(date);
            SimpleDateFormat monthFormat = new SimpleDateFormat("MMM");
            String month = monthFormat.format(date);
            if (goal.length() > 3) {
                goal = goal.substring(0, 3);
            } else {
            }
            college_feedViewHolder.day.setText("" + goal.toUpperCase());
            String day = "" + calendar.get(Calendar.DAY_OF_MONTH);
            if (month.length() > 0) {
                month = month.substring(0, 3);
            } else {
            }
            Log.e(post.getTitle(), day + "" + month);
            college_feedViewHolder.date_month.setText("" + day + "" + month);


            SimpleDateFormat _24HourSDF = new SimpleDateFormat("HH:mm:ss");
            SimpleDateFormat _12HourSDF = new SimpleDateFormat("hh:mm a");
            Date _24HourDt = _24HourSDF.parse(post.getStartTime());
            String time12 = _12HourSDF.format(_24HourDt);
            college_feedViewHolder.time.setText("" + time12);


        } catch (ParseException e) {
            e.printStackTrace();
        }
        if(post.getIsAttending().equals("Y")){
            college_feedViewHolder.going.setImageResource(R.mipmap.going_selected);
        }else{
            college_feedViewHolder.going.setImageResource(R.mipmap.going);
        }
    }

    private void setUpCount_attending(CollegeCampusFeedViewHolder college_feedViewHolder, ModelsFeed post) {

        robotoRegularSpan_for_comments_attendee_number = new CustomTypefaceSpan("", r_reg);

        attendees_text = new SpannableStringBuilder(post.getAttendees() + " ATTENDING");
        attendees_text.setSpan(robotoRegularSpan_for_comments_attendee_number, 0, post.getAttendees().length() + 1, 0);
        college_feedViewHolder.attendee_count.setText(attendees_text);

        comments_text = new SpannableStringBuilder(post.getCommentCount() + " COMMENTS");
        comments_text.setSpan(robotoRegularSpan_for_comments_attendee_number, 0, post.getCommentCount().length() + 1, 0);
        college_feedViewHolder.comments_count.setText(comments_text);
    }

    private void setUpCount_likes(CollegeCampusFeedViewHolder college_feedViewHolder, ModelsFeed post) {

        robotoRegularSpan_for_comments_attendee_number = new CustomTypefaceSpan("", r_reg);

        attendees_text = new SpannableStringBuilder(post.getLikes() + " LIKES");
        attendees_text.setSpan(robotoRegularSpan_for_comments_attendee_number, 0, post.getLikes().length() + 1, 0);
        college_feedViewHolder.attendee_count.setText(attendees_text);

        comments_text = new SpannableStringBuilder(post.getCommentCount() + " COMMENTS");
        comments_text.setSpan(robotoRegularSpan_for_comments_attendee_number, 0, post.getCommentCount().length() + 1, 0);
        college_feedViewHolder.comments_count.setText(comments_text);
    }


    private void setUpYellowCard(CollegeCampusFeedViewHolder college_feedViewHolder, ModelsFeed post, int i) {
        //news
        if (post.getFeedType().equals("Post")) {
            setUpNewsCard(college_feedViewHolder, post, i);
            if (!SharedpreferenceUtility.getInstance(context).getBoolean(AppConstants.IS_LITE)) {
                setUpCount_likes(college_feedViewHolder, post);
            }
        } else {
            setUpEventCard(college_feedViewHolder, post, i);
            if (!SharedpreferenceUtility.getInstance(context).getBoolean(AppConstants.IS_LITE)) {
                setUpCount_attending(college_feedViewHolder, post);
            }
        }
    }

    private void bindLoadingViewHolder(RecyclerView.ViewHolder viewHolder) {
        MoreViewHolder holder = (MoreViewHolder) viewHolder;
    }


    private void setUpTitle(TextView tv, ModelsFeed feed) {
        String name = feed.getTitle();
        if (!TextUtils.isEmpty(name)) {
            tv.setText(name);
        }
    }


    private void setUpGroupName(TextView tv, ModelsFeed feed) {
        String name = feed.getClubName();
        if (!TextUtils.isEmpty(name)) {
            tv.setText(name);
        }
    }

    public static Typeface r_lig;
    TypefaceSpan robotoRegularSpan_for_comments_attendee_number;
    SpannableStringBuilder comments_text, attendees_text;

    public static Typeface r_med, r_reg;

    int posi = 0;


    public CollegeCampusFeedAdapter(List<ModelsFeed> mPosts, Context context) {
        this.mPosts = mPosts;
        this.context = context;
    }

    @Override
    public int getItemCount() {
        return mPosts.size();
    }

    public ModelsFeed getItem(int position) {
        return mPosts.get(position);
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

    public void removeLoading() {
        mIsLoadingFooterAdded = false;
        ModelsFeed item = null;
        int position = mPosts.size() - 1;
        if(position>=0) {
            item = getItem(position);
        }
        if (item != null) {
            mPosts.remove(position);
            notifyItemRemoved(position);
        }
    }

    public CollegeCampusFeedViewHolder createFeedViewHolder(ViewGroup viewGroup) {
        View itemView;
        if (!SharedpreferenceUtility.getInstance(context).getBoolean(AppConstants.IS_LITE)) {
            itemView = LayoutInflater.from(viewGroup.getContext()).inflate(
                    R.layout.activity_card_layout_college_feed, viewGroup, false);
        } else {
            itemView = LayoutInflater.from(viewGroup.getContext()).inflate(
                    R.layout.activity_card_layout_college_feed_lite, viewGroup, false);
        }
        return new CollegeCampusFeedViewHolder(itemView);
    }

    public class CollegeCampusFeedViewHolder extends RecyclerView.ViewHolder {

        CardView college_feed;
        RelativeLayout c_tag;
        TextView event_title, group_name, timestamp, day, date_month, time,comments_count, attendee_count;;
        ImageView event_photo, news_icon;
        ImageButton going, share,comment;
        CircularImageView group_icon;


        public CollegeCampusFeedViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);

            r_med = Typeface.createFromAsset(v.getContext().getAssets(), "font/Roboto_Medium.ttf");
            r_reg = Typeface.createFromAsset(v.getContext().getAssets(), "font/Roboto_Regular.ttf");
            r_lig = Typeface.createFromAsset(v.getContext().getAssets(), "font/Roboto_Light.ttf");
            college_feed = (CardView) itemView.findViewById(R.id.college_feed_card);
            event_title = (TextView) itemView.findViewById(R.id.tv_event);
            group_name = (TextView) itemView.findViewById(R.id.tv_group);
            timestamp = (TextView) itemView.findViewById(R.id.tv_timestamp);
            comments_count = (TextView) itemView.findViewById(R.id.tv_comments_count);
            attendee_count = (TextView) itemView.findViewById(R.id.tv_attending_count);
            event_photo = (ImageView) itemView.findViewById(R.id.iv_event_photo);
            going = (ImageButton) itemView.findViewById(R.id.iv_going);
            share = (ImageButton) itemView.findViewById(R.id.iv_share);
            comment = (ImageButton) itemView.findViewById(R.id.iv_comment);
            news_icon = (ImageView) itemView.findViewById(R.id.iv_news_icon);
            day = (TextView) itemView.findViewById(R.id.tv_day);
            date_month = (TextView) itemView.findViewById(R.id.tv_date_month);
            time = (TextView) itemView.findViewById(R.id.tv_time);
            group_icon = (CircularImageView) itemView.findViewById(R.id.group_image);
            c_tag = (RelativeLayout) itemView.findViewById(R.id.card_tag);

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
                    Bundle bundle = new Bundle();
                    ModelsFeed feed = mPosts.get(posi);
                    bundle.putParcelable("feed", feed);
                    intent_temp.putExtras(bundle);
                    context.startActivity(intent_temp);
                }
            });

            if (!SharedpreferenceUtility.getInstance(context).getBoolean(AppConstants.IS_LITE)) {
                group_icon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int pos_for_share = getAdapterPosition();
                        ModelsFeed post = mPosts.get(pos_for_share);


//                        List<ModelsClubMiniForm> modelsClubMiniForms = new Select().from(ModelsClubMiniForm.class).where("clubId = ?", post.getClubId()).execute();
                        ModelsClubMiniForm modelsClubMiniForm = db.getClubByClubId(post.getClubId());

                        if (modelsClubMiniForm!=null) {
//                            ModelsClubMiniForm modelsClubMiniForm = modelsClubMiniForms.get(0);
                            Intent intent_temp = new Intent(v.getContext(), GroupPageActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putParcelable("club", modelsClubMiniForm);
                            intent_temp.putExtras(bundle);
                            v.getContext().startActivity(intent_temp);
                        }
                    }
                });
            }

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
                    String URL= "http://campusconnect.cc/college#/"+collegeId+"/groups/"+clubId+"/"+postId+"/1";
                    stringBuilder.append(" Hey check out " + mPosts.get(getAdapterPosition()).getTitle()+" at " + URL);
                    String shareBody = stringBuilder.toString();
                    i.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                    v.getContext().startActivity(Intent.createChooser(i, "Share via"));
                }
            });

            comment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    posi = getAdapterPosition();
                    Intent intent_comments = new Intent(v.getContext(), CommentsActivity.class);
                    intent_comments.putExtra("FLAG_POST_COMMENT", true);
                    intent_comments.putExtra("POST_ID", mPosts.get(posi).getFeedId());
                    context.startActivity(intent_comments);
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

    private RecyclerView.ViewHolder createLoadingViewHolder(ViewGroup parent) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.load_more, parent, false);

        return new MoreViewHolder(v);
    }

    public void setUpFeedImage(ImageView iv, ModelsFeed feed) {
        if (!SharedpreferenceUtility.getInstance(context).getBoolean(AppConstants.IS_LITE)) {
            try {
                Picasso.with(context).load(feed.getPhotoUrl()).placeholder(R.drawable.default_card_image).into(iv);
            } catch (Exception e) {
                Picasso.with(context).load(R.drawable.default_card_image).into(iv);
            }
        }
    }

    public void setUpGroupImage(ImageView iv, ModelsFeed feed) {
        if (!SharedpreferenceUtility.getInstance(context).getBoolean(AppConstants.IS_LITE)) {
            try {
                Picasso.with(context).load(feed.getClubphotoUrl()).into(iv);
            } catch (Exception e) {
                Picasso.with(context).load(R.mipmap.default_image).into(iv);
            }
        }
    }

    public void setUpDuration(TextView tv, ModelsFeed feed) {
//        String createTimeStr = feed.getTimestamp();cal.setTime(df.parse(feed.getTimestamp()));
            tv.setText("Posted " + DateUtility.getRelativeDate(feed.getTimestamp()));

//        tv.setText(createTimeStr);
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
    // endregion


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


        public AttendeesDialog(Activity a,String id) {
            super(a);
            // TODO Auto-generated constructor stub
            this.c = a;
            this.context_dialog = context;
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
                ModelsPersonalInfoResponse modelsPersonalInfoResponse=response.body();
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

