
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
import android.text.Html;
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
import com.campusconnect.activity.EventPhotoFullScreenActivity;
import com.campusconnect.activity.GroupPageActivity;
import com.campusconnect.activity.InEventActivity;
import com.campusconnect.adt.ChatMessage;
import com.campusconnect.communicator.CCWebService;
import com.campusconnect.communicator.ServiceGenerator;
import com.campusconnect.communicator.WebRequestTask;
import com.campusconnect.communicator.WebServiceDetails;
import com.campusconnect.communicator.models.ModelsClubMiniForm;
import com.campusconnect.communicator.models.ModelsCollegeFeed;
import com.campusconnect.communicator.models.ModelsComments;
import com.campusconnect.communicator.models.ModelsFeed;
import com.campusconnect.communicator.models.ModelsLikePost;
import com.campusconnect.communicator.models.ModelsLiveFeed;
import com.campusconnect.communicator.models.ModelsModifyEvent;
import com.campusconnect.communicator.models.ModelsPersonalInfoResponse;
import com.campusconnect.communicator.models.ModelsPersonalResponse;
import com.campusconnect.communicator.models.ModelsReportContent;
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
public class LiveAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {



    String DEFAULT_ROOT_URL = "https://campus-connect-live.appspot.com/_ah/api/";
    String DEFAULT_SERVICE_PATH = "campusConnectApis/v1/";
    String BASE_URL = DEFAULT_ROOT_URL + DEFAULT_SERVICE_PATH;

    // region Constants
    public static final int HEADER = 0;
    public static final int ITEM = 1;
    public static final int LOADING = 2;
    // endregion

    // region Member Variables
    private List<ModelsLiveFeed> mPosts;
    private boolean mIsLoadingFooterAdded = false;
    // endregion

    public List<ModelsLiveFeed> getPosts(){
        return this.mPosts;
    }


    // region Listeners
    // endregion

    Context context;

    // region Constructors
    public LiveAdapter(Context context) {
        mPosts = new ArrayList<>();
        this.context = context;
    }
    // endregion

    public void addToTop(ModelsLiveFeed item) {
        mPosts.add(0, item);
        notifyItemInserted(0);
    }

    public void removeLastItem() {
        if (mPosts.size() >= 100) {
            int position = mPosts.size() - 1;
            if (position > -1) {
                mPosts.remove(mPosts.size() - 1);
                notifyItemRemoved(position);
            }
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;

        switch (viewType) {
            case HEADER:
                break;
            case ITEM:
                viewHolder = createLiveViewHolder(parent);
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
                bindLiveViewHolder(viewHolder, position);
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

    private String getBoldTags(String d){
        String boldened="";
        boolean check = false;
        for(int i=0;i<d.length();i++)
        {
            if(d.charAt(i)=='#' && check == false)
            {
                boldened = boldened+ "<b>"+d.charAt(i);
                check = true;
            }

            else if(d.charAt(i)==' ' && check == true)
            {
                boldened = boldened + "</b>";
                check = false;
            }


            else
            {
                boldened = boldened + d.charAt(i);
            }


        }
        if(check == true)
        {
            boldened += "</b>";
        }
        return boldened;
    }

    private void bindLiveViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
        final LiveViewHolder liveViewHolder = (LiveViewHolder) viewHolder;

        final ModelsLiveFeed msg = mPosts.get(i);


        if (msg != null) {
            liveViewHolder.user.setText(msg.getName());



            liveViewHolder.message.setText(Html.fromHtml(getBoldTags(msg.getDescription())));

            liveViewHolder.timeStamp.setText(DateUtility.getRelativeDate(msg.getTimestamp()));
//            liveViewHolder.tag.setText(msg.getTags());

            try {
                Picasso.with(context).load(msg.getPersonPhotoUrl()).into(liveViewHolder.iv_chat_user_image);
            } catch (Exception e) {
                Picasso.with(context).load(R.mipmap.default_image).into(liveViewHolder.iv_chat_user_image);
            }

            if(msg.getImageUrl()!=null && msg.getImageUrl().length()>0){
                liveViewHolder.iv_comment_image.setVisibility(View.VISIBLE);
                try {
                    Picasso.with(context).load(msg.getImageUrl()).placeholder(R.drawable.default_image_portrait).into(liveViewHolder.iv_comment_image);
                } catch (Exception e) {
//                    Picasso.with(context).load(R.mipmap.default_image).into(liveViewHolder.iv_comment_image);
                }
            }else{
                liveViewHolder.iv_comment_image.setVisibility(View.GONE);
            }

        }
    }





    private void bindLoadingViewHolder(RecyclerView.ViewHolder viewHolder) {
        MoreViewHolder holder = (MoreViewHolder) viewHolder;
    }


    public static Typeface r_lig;
    TypefaceSpan robotoRegularSpan_for_comments_attendee_number;
    SpannableStringBuilder comments_text, attendees_text;

    public static Typeface r_med, r_reg;

    int posi = 0;


    public LiveAdapter(List<ModelsLiveFeed> mPosts, Context context) {
        this.mPosts = mPosts;
        this.context = context;
    }

    @Override
    public int getItemCount() {
        return mPosts.size();
    }

    public ModelsLiveFeed getItem(int position) {
        return mPosts.get(position);
    }


    @Override
    public int getItemViewType(int position) {
        return (position == mPosts.size() - 1 && mIsLoadingFooterAdded) ? LOADING : ITEM;
    }

    // region Helper Methods
    public void add(ModelsLiveFeed item) {
        mPosts.add(item);
        notifyItemInserted(mPosts.size() - 1);
    }

    public void addAll(List<ModelsLiveFeed> posts) {
        for (ModelsLiveFeed post : posts) {
            add(post);
        }
    }

    public void remove(ModelsLiveFeed item) {
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
        add(new ModelsLiveFeed());
    }

    public void removeLoading() {
        mIsLoadingFooterAdded = false;
        ModelsLiveFeed item = null;
        int position = mPosts.size() - 1;
        if (position >= 0) {
            item = getItem(position);
        }
        if (item != null) {
            mPosts.remove(position);
            notifyItemRemoved(position);
        }
    }

    public LiveViewHolder createLiveViewHolder(ViewGroup viewGroup) {
        View itemView;
        itemView = LayoutInflater.from(viewGroup.getContext()).inflate(
                R.layout.chat_row_layout_rk, viewGroup, false);


        return new LiveViewHolder(itemView);
    }

    public class LiveViewHolder extends RecyclerView.ViewHolder {


        CardView chat_continer;
        TextView user;
        TextView message;
        TextView timeStamp;
        TextView tag;
        ImageView iv_chat_user_image;
        ImageView iv_comment_image;


        public LiveViewHolder(View convertView) {
            super(convertView);

            ButterKnife.bind(this, convertView);

            chat_continer = (CardView) convertView.findViewById(R.id.chat_card);
            user = (TextView) convertView.findViewById(R.id.chat_user);
            message = (TextView) convertView.findViewById(R.id.chat_message);
            timeStamp = (TextView) convertView.findViewById(R.id.chat_time);
            tag = (TextView) convertView.findViewById(R.id.tag_post);
            iv_chat_user_image = (ImageView) convertView.findViewById(R.id.iv_chat_user_image);
            iv_comment_image = (ImageView) convertView.findViewById(R.id.iv_comment_image);

            chat_continer.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    posi = getAdapterPosition();

                    ReportDialog reportDialog = new ReportDialog((Activity) view.getContext(),mPosts.get(posi).getMessageId());
                    Window window = reportDialog.getWindow();
                    window.setLayout(450, ViewGroup.LayoutParams.WRAP_CONTENT);
                    reportDialog.show();
                    return true;
                }
            });

            iv_comment_image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    posi = getAdapterPosition();
                    Intent intent_temp = new Intent(v.getContext(), EventPhotoFullScreenActivity.class);
                    ModelsLiveFeed msg = mPosts.get(posi);
                    intent_temp.putExtra("PICTURE", msg.getImageUrl());
                    context.startActivity(intent_temp);
                }
            });


        }

    }


    private RecyclerView.ViewHolder createLoadingViewHolder(ViewGroup parent) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.load_more, parent, false);

        return new MoreViewHolder(v);
    }

    public class ReportDialog extends Dialog implements
            View.OnClickListener {

        public Activity c;
        public Dialog d;
        public TextView report;
        public TextView dismiss;
        Context context;
        String messageId;

        public ReportDialog(Activity a, String messageId) {
            super(a);
            // TODO Auto-generated constructor stub
            this.c = a;
            this.context = context;
            this.messageId = messageId;
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            setContentView(R.layout.report_dialog);
            report = (TextView) findViewById(R.id.btn_report);
            dismiss = (TextView) findViewById(R.id.btn_dismiss);

            report.setOnClickListener(this);
            dismiss.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_report: {
                    CCWebService mCCService = ServiceGenerator.createService(CCWebService.class,BASE_URL);
                    ModelsReportContent modelsReportContent=new ModelsReportContent();
                    modelsReportContent.setMessageId(this.messageId);
                    Call report=mCCService.reportPost(modelsReportContent);
                    report.enqueue(callBack);
                    dismiss();

                    break;
                }
                case R.id.btn_dismiss:
                    dismiss();
                    break;
                default:
                    break;
            }
        }

    }
    private Callback<Void> callBack = new Callback<Void>() {
        @Override
        public void onResponse(Response<Void> response, Retrofit retrofit) {
            Toast.makeText(context, "This post has been flagged for the admin.", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onFailure(Throwable t) {
            //Timber.d("onFailure() : mQuery - " + mQuery);

        }
    };


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

}

