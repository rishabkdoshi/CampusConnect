package com.campusconnect.adapter;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.campusconnect.R;
import com.campusconnect.activity.InEventActivity;
import com.campusconnect.communicator.models.ModelsComments;
import com.campusconnect.communicator.models.ModelsFeed;
import com.campusconnect.database.DatabaseHandler;
import com.campusconnect.supportClasses.UpcomingEvents_infoActivity;
import com.campusconnect.utility.CircularImageView;
import com.campusconnect.utility.DateUtility;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by RK on 05/11/2015.
 */
public class CommentsAdapterActivity extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    // region Constants
    public static final int HEADER = 0;
    public static final int ITEM = 1;
    public static final int LOADING = 2;
    // endregion

    // region Member Variables
    private List<ModelsComments> mComments;
    private boolean mIsLoadingFooterAdded = false;

    Context context;

    public CommentsAdapterActivity(List<ModelsComments> mComments, Context context) {
        this.mComments = mComments;
        this.context = context;
    }

    public CommentsAdapterActivity(Context context) {
        this.mComments = new ArrayList<>();
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;

        switch (viewType) {
            case HEADER:
                break;
            case ITEM:
                viewHolder = createCommentViewHolder(parent);
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

    private void bindLoadingViewHolder(RecyclerView.ViewHolder viewHolder) {
        MoreViewHolder holder = (MoreViewHolder) viewHolder;
    }

    public static class MoreViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.loading_iv)
        ProgressBar mLoadingImageView;

        public MoreViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    private RecyclerView.ViewHolder createLoadingViewHolder(ViewGroup parent) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.load_more, parent, false);

        return new MoreViewHolder(v);
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        switch (getItemViewType(position)) {
            case HEADER:
                break;
            case ITEM:
                bindCommentViewHolder(viewHolder, position);
                break;
            case LOADING:
                bindLoadingViewHolder(viewHolder);
            default:
                break;
        }
    }


    private void bindCommentViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
        final CommentsViewHolder commentsViewHolder = (CommentsViewHolder) viewHolder;


        commentsViewHolder.comment.setText(mComments.get(i).getCommentBody());
        commentsViewHolder.commentator.setText(mComments.get(i).getCommentor());


        if (mComments.get(i).getTimestamp().equals("Just now")) {
            commentsViewHolder.timestamp.setText("Just now");
        } else {
            commentsViewHolder.timestamp.setText("" + (DateUtility.getRelativeDate(mComments.get(i).getTimestamp())));
        }

        try {
            Picasso.with(context).load(mComments.get(i).getPhotoUrl()).into(commentsViewHolder.profileImg);
        } catch (Exception e) {
            Picasso.with(context).load(R.drawable.default_card_image).into(commentsViewHolder.profileImg);
        }

    }


    @Override
    public int getItemCount() {
        return mComments.size();
    }

    public ModelsComments getItem(int position) {
        return mComments.get(position);
    }


    @Override
    public int getItemViewType(int position) {
        return (position == mComments.size() - 1 && mIsLoadingFooterAdded) ? LOADING : ITEM;
    }

    public void addToTop(ModelsComments item) {
        mComments.add(0, item);
        notifyItemInserted(0);
    }

    // region Helper Methods
    private void add(ModelsComments item) {
        mComments.add(item);
        notifyItemInserted(mComments.size() - 1);
    }

    public void addAll(List<ModelsComments> posts) {
        for (ModelsComments post : posts) {
            add(post);
        }
    }

    public void removeLastItem() {
        if (mComments.size() >= 25) {
            int position = mComments.size() - 1;
            if (position > -1) {
                mComments.remove(mComments.size() - 1);
                notifyItemRemoved(position);
            }
        }
    }

    public void remove(ModelsComments item) {
        int position = mComments.indexOf(item);
        if (position > -1) {
            mComments.remove(position);
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
        add(new ModelsComments());
    }

    public void removeLoading() {
        mIsLoadingFooterAdded = false;

        int position = mComments.size() - 1;
        ModelsComments item = getItem(position);

        if (item != null) {
            mComments.remove(position);
            notifyItemRemoved(position);
        }
    }


    public CommentsViewHolder createCommentViewHolder(ViewGroup viewGroup) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(
                R.layout.activity_card_layout_comments, viewGroup, false);
        return new CommentsViewHolder(itemView);
    }

    public class CommentsViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.cv_comment)
        CardView comment_card;
        @Bind(R.id.tv_commentator_name)
        TextView commentator;
        @Bind(R.id.tv_comment)
        TextView comment;
        @Bind(R.id.tv_timestamp)
        TextView timestamp;
        @Bind(R.id.profile_image)
        CircularImageView profileImg;

        public CommentsViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);

            Typeface r_reg = Typeface.createFromAsset(v.getContext().getAssets(), "font/Roboto_Regular.ttf");
            Typeface r_lig = Typeface.createFromAsset(v.getContext().getAssets(), "font/Roboto_Light.ttf");

            commentator.setTypeface(r_reg);
            comment.setTypeface(r_reg);
            timestamp.setTypeface(r_lig);


        }

    }

//    public class DeleteConfirmationDialog extends Dialog implements
//            View.OnClickListener {
//
//        public Activity c;
//        public Dialog d;
//        Context context;
//        int del_position;
//
//        TextView yes;
//        TextView no;
//
//
//
//        public DeleteConfirmationDialog (Activity a) {
//            super(a);
//            // TODO Auto-generated constructor stub
//            this.c = a;
//            this.context = context;
//        }
//
//        @Override
//        protected void onCreate(Bundle savedInstanceState) {
//            super.onCreate(savedInstanceState);
//            requestWindowFeature(Window.FEATURE_NO_TITLE);
//            setContentView(R.layout.delete_comment_confirmation_dialog);
//
//            yes = (TextView)findViewById(R.id.btn_yes);
//            no = (TextView)findViewById(R.id.btn_no);
//
//            yes.setOnClickListener(this);
//            no.setOnClickListener(this);
//        }
//
//        @Override
//        public void onClick(View v) {
//            switch (v.getId()) {
//                case R.id.btn_yes: {
//                    commentList.remove(del_position);
//                    notifyItemRemoved(del_position);
//                    dismiss();
//                    break;
//                }
//                case R.id.btn_no:
//                    dismiss();
//                    break;
//                default:
//                    break;
//            }
//        }
//    }
}

