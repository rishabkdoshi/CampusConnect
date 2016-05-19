package com.campusconnect.adapter;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.campusconnect.R;
import com.campusconnect.activity.CreatePostActivity;
import com.campusconnect.activity.EditPostActivity;
import com.campusconnect.activity.InEventActivity;
import com.campusconnect.communicator.CCWebService;
import com.campusconnect.communicator.ServiceGenerator;
import com.campusconnect.communicator.models.ModelsFeed;
import com.campusconnect.constant.AppConstants;
import com.campusconnect.utility.CircularImageView;
import com.campusconnect.utility.DateUtility;
import com.campusconnect.utility.SharedpreferenceUtility;
import com.squareup.picasso.Picasso;

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
public class PostsForEditAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    // region Constants
    public static final int HEADER = 0;
    public static final int ITEM = 1;
    public static final int LOADING = 2;
    // endregion

    CCWebService mCCService;

    // region Member Variables
    private List<ModelsFeed> mPosts;
    private boolean mIsLoadingFooterAdded = false;
    // endregion

    // region Listeners
    // endregion

    Context context;

    // region Constructors
    public PostsForEditAdapter(Context context) {
        mPosts = new ArrayList<>();
        this.context = context;
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
        void onItemClick(int position, View view)                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                               ;
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

    }

    private void setUpYellowCard(CollegeCampusFeedViewHolder college_feedViewHolder, ModelsFeed post, int i) {
        //news
        if (post.getFeedType().equals("Post")) {
            setUpNewsCard(college_feedViewHolder, post, i);
        } else {
            setUpEventCard(college_feedViewHolder, post, i);
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

    public static Typeface r_med, r_reg;

    int posi = 0;


    public PostsForEditAdapter(List<ModelsFeed> mPosts, Context context) {
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
        if (position >= 0) {
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
                    R.layout.activity_card_layout_college_feed_edit, viewGroup, false);
        } else {
            itemView = LayoutInflater.from(viewGroup.getContext()).inflate(
                    R.layout.activity_card_layout_college_feed_lite_edit, viewGroup, false);
        }
        return new CollegeCampusFeedViewHolder(itemView);
    }

    public class CollegeCampusFeedViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener {

        //new
        ImageView blur_rect_card;

        CardView college_feed;
        RelativeLayout c_tag, r_layout;
        TextView event_title, group_name, timestamp, day, date_month, time;
        ImageView event_photo, news_icon, delete, edit;
        CircularImageView group_icon;

        Context context;


        public CollegeCampusFeedViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);

            context = v.getContext();

            mCCService = ServiceGenerator.createService(
                    CCWebService.class,
                    CCWebService.BASE_URL);

            r_med = Typeface.createFromAsset(v.getContext().getAssets(), "font/Roboto_Medium.ttf");
            r_reg = Typeface.createFromAsset(v.getContext().getAssets(), "font/Roboto_Regular.ttf");

            college_feed = (CardView) itemView.findViewById(R.id.college_feed_card);
            r_layout = (RelativeLayout) itemView.findViewById(R.id.rel_layout);
            blur_rect_card = (ImageView) itemView.findViewById(R.id.blur_card);
            event_title = (TextView) itemView.findViewById(R.id.tv_event);
            group_name = (TextView) itemView.findViewById(R.id.tv_group);
            timestamp = (TextView) itemView.findViewById(R.id.tv_timestamp);
            event_photo = (ImageView) itemView.findViewById(R.id.iv_event_photo);
            delete = (ImageView) itemView.findViewById(R.id.iv_remove);
            edit = (ImageView) itemView.findViewById(R.id.iv_edit);
            news_icon = (ImageView) itemView.findViewById(R.id.iv_news_icon);
            day = (TextView) itemView.findViewById(R.id.tv_day);
            date_month = (TextView) itemView.findViewById(R.id.tv_date_month);
            time = (TextView) itemView.findViewById(R.id.tv_time);
            group_icon = (CircularImageView) itemView.findViewById(R.id.group_image);
            c_tag = (RelativeLayout) itemView.findViewById(R.id.card_tag);

            event_title.setTypeface(r_med);
            group_name.setTypeface(r_reg);
            timestamp.setTypeface(r_reg);

            college_feed.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    posi = getAdapterPosition();
                    Intent intent_temp = new Intent(v.getContext(), InEventActivity.class);
                    Bundle bundle = new Bundle();
                    ModelsFeed feed = mPosts.get(posi);
                    bundle.putParcelable("feed", feed);
                    bundle.putString("CallerActivity", "PostsForEdit");
                    intent_temp.putExtras(bundle);
                    context.startActivity(intent_temp);
                }
            });

/*
            college_feed.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    blur_rect_card.setVisibility(View.VISIBLE);
                    college_feed.setClickable(false);
                    return true;
                }
            });
*/
            edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    posi = getAdapterPosition();
                    Intent intent_temp = new Intent(v.getContext(), EditPostActivity.class);
                    Bundle bundle = new Bundle();
                    ModelsFeed feed = mPosts.get(posi);
                    bundle.putParcelable("feed", feed);
                    bundle.putString("ACTIVITY", "PostsForEdit");
                    intent_temp.putExtras(bundle);
                    context.startActivity(intent_temp);
                }
            });

            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    DeletePostConfirmationDialog confirmDialog = new DeletePostConfirmationDialog((Activity) v.getContext(), getAdapterPosition());
                    Window window = confirmDialog.getWindow();
                    window.setLayout(450, ViewGroup.LayoutParams.WRAP_CONTENT);
                    confirmDialog.show();
                }
            });

        }

        @Override
        public boolean onLongClick(View view) {

            return false;
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

    //Dialog start region
    public class DeletePostConfirmationDialog extends Dialog implements
            View.OnClickListener {

        int adapter_position;

        public Activity c;
        public Dialog d;
        public TextView yes;
        public TextView no;
        public TextView dialog_info;
        Context context;


        public DeletePostConfirmationDialog(Activity a, int adapter_pos) {
            super(a);
            // TODO Auto-generated constructor stub
            this.adapter_position = adapter_pos;
            this.c = a;
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            setContentView(R.layout.member_confirmation_dialog);

            dialog_info = (TextView) findViewById(R.id.tv_dialog_info);
            dialog_info.setText("Are you sure you want to delete the post?");

            yes = (TextView) findViewById(R.id.btn_yes);
            no = (TextView) findViewById(R.id.btn_no);

            yes.setOnClickListener(this);
            no.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_yes: {
                    ModelsFeed modelsFeed=mPosts.get(adapter_position);
                    String pid=SharedpreferenceUtility.getInstance(this.getContext()).getString(AppConstants.PERSON_PID);
                    if(modelsFeed.getFeedType().equals("Post")){
                        Call deletePost=mCCService.deletePost(modelsFeed.getFeedId(),pid);
                        deletePost.enqueue(callback);
                    }else{
                        Call deleteEvent=mCCService.deleteEvent(modelsFeed.getFeedId(),pid);
                        deleteEvent.enqueue(callback);
                    }
                    mPosts.remove(adapter_position);
                    notifyItemRemoved(adapter_position);
                    dismiss();
                    break;
                }
                case R.id.btn_no:
                    dismiss();
                    break;
                default:
                    break;
            }
        }

    }

    private Callback<Void> callback = new Callback<Void>() {
        @Override
        public void onResponse(Response<Void> response, Retrofit retrofit) {

        }

        @Override
        public void onFailure(Throwable t) {
            //Timber.d("onFailure() : mQuery - " + mQuery);

        }
    };
    //Dialog end region
}
