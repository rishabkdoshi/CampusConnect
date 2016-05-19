package com.campusconnect.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableStringBuilder;
import android.text.style.TypefaceSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.campusconnect.R;
import com.campusconnect.activity.GetFeedActivity;
import com.campusconnect.activity.InEventActivity;
import com.campusconnect.bean.NotificationBean;
import com.campusconnect.communicator.models.ModelsFeed;
import com.campusconnect.communicator.models.ModelsNotificationList;
import com.campusconnect.communicator.models.ModelsNotificationResponseForm;
import com.campusconnect.utility.CircularImageView;
import com.campusconnect.utility.CustomTypefaceSpan;
import com.campusconnect.utility.DateUtility;
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
 * Created by RK on 22-09-2015.
 */
public class NotificationAdapterActivity extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    // region Constants
    public static final int HEADER = 0;
    public static final int ITEM = 1;
    public static final int LOADING = 2;
    // endregion

    private List<ModelsNotificationResponseForm> mNotifications;
    private Context context;
    private boolean mIsLoadingFooterAdded = false;

    public static SpannableStringBuilder g_name;
    public static Typeface r_lig, r_reg;
    public static TypefaceSpan robotoRegularSpan_for_group_name, robotoRegularSpan_for_subject;
    public static String group_name = " ", subject = " ", end = "";

    public NotificationAdapterActivity(List<ModelsNotificationResponseForm> mNotifications, Context context) {
        this.mNotifications = mNotifications;
        this.context = context;
    }

    public NotificationAdapterActivity(Context context) {
        this.context = context;
        this.mNotifications = new ArrayList<>();

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;

        switch (viewType) {
            case HEADER:
                break;
            case ITEM:
                viewHolder = createNotificationViewHolder(parent);
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
    public int getItemCount() {
        return mNotifications.size();
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        switch (getItemViewType(position)) {
            case HEADER:
                break;
            case ITEM:
                bindNotificationViewHolder(viewHolder, position);
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

    private void add(ModelsNotificationResponseForm item) {
        mNotifications.add(item);
        notifyItemInserted(mNotifications.size() - 1);
    }

    public void addAll(List<ModelsNotificationResponseForm> modelsNotificationResponseForms) {
        for (ModelsNotificationResponseForm modelsNotificationResponseForm : modelsNotificationResponseForms) {
            add(modelsNotificationResponseForm);
        }
    }

    public void remove(ModelsNotificationResponseForm item) {
        int position = mNotifications.indexOf(item);
        if (position > -1) {
            mNotifications.remove(position);
            notifyItemRemoved(position);
        }
    }

    public ModelsNotificationResponseForm getItem(int position) {
        return mNotifications.get(position);
    }

    public void clear() {
        mIsLoadingFooterAdded = false;
        while (getItemCount() > 0) {
            remove(getItem(0));
        }
    }

    public void addLoading() {
        mIsLoadingFooterAdded = true;
        add(new ModelsNotificationResponseForm());
    }

    public void removeLoading() {
        mIsLoadingFooterAdded = false;
        ModelsNotificationResponseForm item = null;
        int position = mNotifications.size() - 1;
        if(position>=0) {
            item = getItem(position);
        }
        if (item != null) {
            mNotifications.remove(position);
            notifyItemRemoved(position);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return (position == mNotifications.size() - 1 && mIsLoadingFooterAdded) ? LOADING : ITEM;
    }


    public void bindNotificationViewHolder(RecyclerView.ViewHolder viewHolder , int i) {
        final ModelsNotificationResponseForm ci = mNotifications.get(i);
        final NotificationViewHolder notification_listViewHolder=(NotificationViewHolder)viewHolder;

        String intro = "";
        String middle = "";

        if (ci.getType() != null) {

            if (ci.getType().equalsIgnoreCase("post")) {

                SettingRestData(notification_listViewHolder, ci);
                intro = "This just in! ";
                middle = " has posted a news post ";
                group_name = "" + ci.getClubName();            //To be gotten from server
                subject = ci.getPostName();      ////To be gotten from server

                g_name = new SpannableStringBuilder(intro + group_name + middle + subject + ".");
                g_name.setSpan(robotoRegularSpan_for_group_name, intro.length(), intro.length() + group_name.length(), 0);
                g_name.setSpan(robotoRegularSpan_for_subject, middle.length() + intro.length() + group_name.length(), middle.length() + intro.length() + group_name.length() + subject.length() + 1, 0);

                notification_listViewHolder.notification.setText(g_name);

            } else if (ci.getType().equalsIgnoreCase("Event")) {
                SettingRestData(notification_listViewHolder, ci);

                intro = "Check it out! ";
                middle = " has posted an event post ";
                group_name = "" + ci.getClubName();            //To be gotten from server
                subject = ci.getEventName();       ////To be gotten from server

                g_name = new SpannableStringBuilder(intro + group_name + middle + subject + ".");
                g_name.setSpan(robotoRegularSpan_for_group_name, intro.length(), intro.length() + group_name.length(), 0);
                g_name.setSpan(robotoRegularSpan_for_subject, middle.length() + intro.length() + group_name.length(), middle.length() + intro.length() + group_name.length() + subject.length() + 1, 0);

                notification_listViewHolder.notification.setText(g_name);
            } else if (ci.getType().equalsIgnoreCase("Reminder")) {

                SettingRestData(notification_listViewHolder, ci);

                intro = "Get Ready! ";
                middle = " posted by ";
                end = " will begin in half hour ";
                group_name = "" + ci.getClubName();           //To be gotten from server
                subject = ci.getEventName();       ////To be gotten from server

                g_name = new SpannableStringBuilder(intro + subject + middle + group_name + end + ".");
                g_name.setSpan(robotoRegularSpan_for_group_name, intro.length(), intro.length() + subject.length(), 0);
                g_name.setSpan(robotoRegularSpan_for_subject, middle.length() + intro.length() + subject.length(), middle.length() + intro.length() + group_name.length() + subject.length() + 1, 0);

                notification_listViewHolder.notification.setText(g_name);
            } else if (ci.getType().trim().equalsIgnoreCase("approved join request")) {

                SettingRestData(notification_listViewHolder, ci);


                intro = "Congratulations! Your request to join ";
                end = " has been accepted";
                group_name = "" + ci.getClubName();          //To be gotten from server
                g_name = new SpannableStringBuilder(intro + group_name + end + ".");
                g_name.setSpan(robotoRegularSpan_for_group_name, intro.length(), intro.length() + group_name.length(), 0);
                notification_listViewHolder.notification.setText(g_name);
            }





        }
    }

    public void SettingRestData(NotificationViewHolder holder, ModelsNotificationResponseForm ci) {


        holder.notification_ts.setText("Posted " + DateUtility.getRelativeDate(ci.getTimestamp()));

//        holder.notification_ts.setText(timeAgo(ci.getTimestamp()));
        try {
            Picasso.with(context).load(ci.getClubphotoUrl()).into(holder.group_icon_notification);
        } catch (Exception e) {

            Picasso.with(context).load(R.mipmap.default_image).into(holder.group_icon_notification);
        }

    }

    public NotificationViewHolder createNotificationViewHolder(ViewGroup viewGroup) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(
                R.layout.activity_card_layout_notification, viewGroup, false);

        return new NotificationViewHolder(itemView);
    }

    private RecyclerView.ViewHolder createLoadingViewHolder(ViewGroup parent) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.load_more, parent, false);

        return new MoreViewHolder(v);
    }


    public class NotificationViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.tv_notification)
        TextView notification;
        @Bind(R.id.tv_notification_timestamp)
        TextView notification_ts;
        @Bind(R.id.group_icon_notification)
        CircularImageView group_icon_notification;

        public NotificationViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);

            r_lig = Typeface.createFromAsset(v.getContext().getAssets(), "font/Roboto_Light.ttf");
            r_reg = Typeface.createFromAsset(v.getContext().getAssets(), "font/Roboto_Regular.ttf");

            robotoRegularSpan_for_group_name = new CustomTypefaceSpan("", r_reg);
            robotoRegularSpan_for_subject = new CustomTypefaceSpan("", r_reg);

            notification.setTypeface(r_lig);
            notification_ts.setTypeface(r_lig);

            notification.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos=getAdapterPosition();
                    ModelsNotificationResponseForm ci = mNotifications.get(pos);
                    Bundle bundle = new Bundle();
                    ModelsNotificationResponseForm notificationResponseForm = new ModelsNotificationResponseForm();
                    if (ci.getType().equals("Post")) {
                        notificationResponseForm.setType("Post");

                        notificationResponseForm.setPostId(ci.getPostId());
                    } else if (ci.getType().equals("Event")) {
                        notificationResponseForm.setType("Event");

                        notificationResponseForm.setEventId(ci.getEventId());
                    }

                    bundle.putParcelable("notification", notificationResponseForm);
                    Intent intent_temp = new Intent(v.getContext(), GetFeedActivity.class);
                    intent_temp.putExtras(bundle);
                    context.startActivity(intent_temp);
                }
            });

            group_icon_notification.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    int pos=getAdapterPosition();
                    ModelsNotificationResponseForm ci=mNotifications.get(pos);
                    ModelsNotificationResponseForm notificationResponseForm = new ModelsNotificationResponseForm();
                    if (ci.getType().equals("Post")) {
                        notificationResponseForm.setType("Post");

                        notificationResponseForm.setPostId(ci.getPostId());
                    } else if (ci.getType().equals("Event")) {
                        notificationResponseForm.setType("Event");

                        notificationResponseForm.setEventId(ci.getEventId());
                    }

                    bundle.putParcelable("notification", notificationResponseForm);
                    Intent intent_temp = new Intent(v.getContext(), GetFeedActivity.class);
                    intent_temp.putExtras(bundle);
                    context.startActivity(intent_temp);
                }
            });


        }

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

}


