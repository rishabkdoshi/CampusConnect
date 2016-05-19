package com.campusconnect.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.campusconnect.R;
import com.campusconnect.activity.InEventActivity;
import com.campusconnect.bean.CampusFeedBean;
import com.campusconnect.constant.AppConstants;
import com.campusconnect.database.DatabaseHandler;
import com.campusconnect.utility.CircularImageView;
import com.campusconnect.utility.DateUtility;
import com.campusconnect.utility.SharedpreferenceUtility;
import com.squareup.picasso.Picasso;

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

/**
 * Created by RK on 06/11/2015.
 */
public class NewsPostsByGroupAdapterActivity extends
        RecyclerView.Adapter<NewsPostsByGroupAdapterActivity.NewsPostsByGroupViewHolder> {

    private List<CampusFeedBean> NewsPostsByGroupList;
    ArrayList<Boolean> flag_attending_clicked, flag_share_clicked;
    int posi = 0;
    int going_click_count = 0;
    int share_click_count = 0;
    Context context;
    private DatabaseHandler dataBase;

    public NewsPostsByGroupAdapterActivity(List<CampusFeedBean> NewsPostsByGroupList, Context context) {
        this.NewsPostsByGroupList = NewsPostsByGroupList;
        flag_attending_clicked = new ArrayList<Boolean>();
        flag_share_clicked = new ArrayList<Boolean>();
        this.context = context;
        dataBase = new DatabaseHandler(context);
    }

    @Override
    public int getItemCount() {
        return NewsPostsByGroupList.size();
    }


    @Override
    public void onBindViewHolder(NewsPostsByGroupViewHolder news_posts_by_groupViewHolder, int i) {
        CampusFeedBean ci = NewsPostsByGroupList.get(i);
        flag_attending_clicked.add(i, false);
        flag_share_clicked.add(i, false);


        news_posts_by_groupViewHolder.timestamp.setText("Posted " + DateUtility.getRelativeDate(ci.getTimeStamp()));


//        news_posts_by_groupViewHolder.timestamp.setText(timeAgo(ci.getTimeStamp()));
        news_posts_by_groupViewHolder.event_title.setText(ci.getTitle());
        news_posts_by_groupViewHolder.group_name.setText(ci.getClubAbbreviation());

        if (!SharedpreferenceUtility.getInstance(context).getBoolean(AppConstants.IS_LITE)) {
            try {
                Picasso.with(context).load(ci.getPhoto()).placeholder(R.drawable.default_card_image).into(news_posts_by_groupViewHolder.event_photo);

            } catch (Exception e) {
                Picasso.with(context).load(R.drawable.default_card_image).into(news_posts_by_groupViewHolder.event_photo);
            }
            try {
                Picasso.with(context).load(ci.getClubphoto()).into(news_posts_by_groupViewHolder.group_icon);
            } catch (Exception e) {
                Picasso.with(context).load(R.mipmap.default_image).into(news_posts_by_groupViewHolder.group_icon);
            }
        }

        if (dataBase.getFeedIsLike(ci.getPid())) {
            flag_attending_clicked.set(i, true);
            news_posts_by_groupViewHolder.like.setImageResource(R.mipmap.heart_selected);
        } else {
            flag_attending_clicked.set(i, false);
            news_posts_by_groupViewHolder.like.setImageResource(R.mipmap.heart);
        }

    }

    public String timeAgo(String createTimeStr) {
        try {
            SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            simpleDateFormat.setTimeZone(TimeZone.getDefault());
            Date d = simpleDateFormat.parse(createTimeStr);

//        java.util.Date d = f.parse(createTimeStr);
            String currentDateandTime = f.format(new Date());
            java.util.Date d1 = f.parse(currentDateandTime);
            long milliseconds = d.getTime();
            long millisecondsCurrent = d1.getTime();
            long diff_Milli = millisecondsCurrent - milliseconds;
            long minutes = Math.abs((millisecondsCurrent - milliseconds) / 60000);
            long seconds = Math.abs((diff_Milli) / 1000);
            long hours = Math.abs((minutes) / 60);
            long days = Math.abs((hours) / 24);
            long weeks = Math.abs((days) / 7);
            if (days > 7) {
                createTimeStr = String.valueOf(weeks);
                try {
                    if (Integer.parseInt(createTimeStr) > 1)
                        createTimeStr = "Posted " + createTimeStr + " weeks ago ";
                    else
                        createTimeStr = "Posted " + createTimeStr + " week ago ";
                } catch (NumberFormatException nfe) {
                    System.out.println("Could not parse " + nfe);
                }
            } else if (hours > 24) {
                createTimeStr = String.valueOf(days);
                try {
                    if (Integer.parseInt(createTimeStr) > 1)
                        createTimeStr = "Posted " + createTimeStr + " days ago ";
                    else
                        createTimeStr = "Posted " + createTimeStr + " day ago ";
                } catch (NumberFormatException nfe) {
                    System.out.println("Could not parse " + nfe);
                }
            } else if (minutes > 60) {
                createTimeStr = String.valueOf(hours);
                createTimeStr = String.valueOf(days);
                try {
                    if (Integer.parseInt(createTimeStr) > 1)
                        createTimeStr = "Posted " + createTimeStr + " hours ago ";
                    else
                        createTimeStr = "Posted " + createTimeStr + " hour ago ";
                } catch (NumberFormatException nfe) {
                    System.out.println("Could not parse " + nfe);
                }
            } else {
                createTimeStr = String.valueOf(minutes);
                createTimeStr = String.valueOf(days);
                try {
                    if (Integer.parseInt(createTimeStr) > 1)
                        createTimeStr = "Posted " + createTimeStr + " minutes ago ";
                    else
                        createTimeStr = "Posted " + createTimeStr + " minute ago ";
                } catch (NumberFormatException nfe) {
                    System.out.println("Could not parse " + nfe);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return createTimeStr;
    }

    @Override
    public NewsPostsByGroupViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView;
        if (!SharedpreferenceUtility.getInstance(context).getBoolean(AppConstants.IS_LITE)) {
            itemView = LayoutInflater.from(viewGroup.getContext()).inflate(
                    R.layout.activity_card_layout_college_feed, viewGroup, false);
        } else {
            itemView = LayoutInflater.from(viewGroup.getContext()).inflate(
                    R.layout.activity_card_layout_college_feed_lite, viewGroup, false);
        }
        return new NewsPostsByGroupViewHolder(itemView);
    }

    public class NewsPostsByGroupViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        CardView news_feed;
        TextView event_title, group_name, timestamp;
        ImageView event_photo, news_icon, like, share;
        CircularImageView group_icon;

        public NewsPostsByGroupViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);

            news_feed = (CardView) itemView.findViewById(R.id.college_feed_card);
            event_title = (TextView) itemView.findViewById(R.id.tv_event);
            group_name = (TextView) itemView.findViewById(R.id.tv_group);
            timestamp = (TextView) itemView.findViewById(R.id.tv_timestamp);
            event_photo = (ImageView) itemView.findViewById(R.id.iv_event_photo);
            like = (ImageView) itemView.findViewById(R.id.iv_going);
            share = (ImageView) itemView.findViewById(R.id.iv_share);
            news_icon = (ImageView) itemView.findViewById(R.id.iv_news_icon);
            group_icon = (CircularImageView) itemView.findViewById(R.id.group_image);

            Typeface r_med = Typeface.createFromAsset(v.getContext().getAssets(), "font/Roboto_Medium.ttf");
            Typeface r_reg = Typeface.createFromAsset(v.getContext().getAssets(), "font/Roboto_Regular.ttf");

            event_title.setTypeface(r_med);
            group_name.setTypeface(r_reg);
            timestamp.setTypeface(r_reg);

            news_feed.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();
                    Intent intent_temp = new Intent(v.getContext(), InEventActivity.class);
                    intent_temp.putExtra("CallerActivity", "beanUsage");
                    posi = getAdapterPosition();
                    Bundle bundle = new Bundle();
                    CampusFeedBean bean = NewsPostsByGroupList.get(pos);
                    bundle.putSerializable("BEAN", bean);
                    bundle.putBoolean("FLAG_NEWS", true);
                    bundle.putBoolean("FLAG_SELECTED_SHARE", flag_share_clicked.get(pos));
                    bundle.putBoolean("FLAG_SELECTED_ATTEND/LIKE", flag_attending_clicked.get(pos));
                    intent_temp.putExtras(bundle);
                    v.getContext().startActivity(intent_temp);
                }
            });
            like.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos_for_going = getAdapterPosition();
                    if (flag_attending_clicked.get(pos_for_going)) {
                        like.setImageResource(R.mipmap.heart);

                        dataBase.saveFeedInfo(NewsPostsByGroupList.get(pos_for_going).getPid(), "0");
                        flag_attending_clicked.set(pos_for_going, false);
                        //Toast.makeText(context, "coming soon", Toast.LENGTH_SHORT).show();
                    } else {
                        like.setImageResource(R.mipmap.heart_selected);
                        dataBase.saveFeedInfo(NewsPostsByGroupList.get(pos_for_going).getPid(), "1");
                        flag_attending_clicked.set(pos_for_going, true);
                        //Toast.makeText(context, "coming soon", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            share.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();
                    Intent i = new Intent(android.content.Intent.ACTION_SEND);
                    i.setType("text/plain");
                    String shareBody = "Title : " + NewsPostsByGroupList.get(pos).getTitle() + "\n" + "Description : " + NewsPostsByGroupList.get(posi).getDescription() + " Download the app now from visit bit.ly/campusconnectandroid";
                    i.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                    v.getContext().startActivity(Intent.createChooser(i, "Share via"));
                    int pos_for_share = getAdapterPosition();
                    if (flag_share_clicked.get(pos_for_share)) {
                        share.setAlpha((float) 0.5);
                        flag_share_clicked.set(pos_for_share, false);
                    } else {
                        share.setAlpha((float) 1);
                        flag_share_clicked.set(pos_for_share, true);
                    }
                }
            });
        }

        @Override
        public void onClick(View view) {

        }
    }
}


