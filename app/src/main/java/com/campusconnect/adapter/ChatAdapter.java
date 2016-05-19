package com.campusconnect.adapter;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.campusconnect.Constants;
import com.campusconnect.R;
import com.campusconnect.adt.ChatMessage;
import com.campusconnect.communicator.WebRequestTask;
import com.campusconnect.communicator.WebServiceDetails;
import com.campusconnect.constant.AppConstants;
import com.campusconnect.utility.SharedpreferenceUtility;

import org.apache.http.NameValuePair;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;



public class ChatAdapter extends ArrayAdapter<ChatMessage> {


    private List<ChatMessage> values;


    private final Context context;
    private LayoutInflater inflater;

    public ChatAdapter(Context context, List<ChatMessage> values) {
        super(context, R.layout.chat_row_layout_rk, android.R.id.text1, values);
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.values=values;
    }


    class ViewHolder {
        CardView chat_continer;
        TextView user;
        TextView message;
        TextView timeStamp;
        TextView tag;
        ChatMessage chatMsg;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ChatMessage chatMsg = this.values.get(position);
        final ViewHolder holder;

        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.chat_row_layout_rk, parent, false);
            holder.chat_continer = (CardView) convertView.findViewById(R.id.chat_card);
            holder.user = (TextView) convertView.findViewById(R.id.chat_user);
            holder.message = (TextView) convertView.findViewById(R.id.chat_message);
            holder.timeStamp = (TextView) convertView.findViewById(R.id.chat_time);
            holder.tag = (TextView) convertView.findViewById(R.id.tag_post);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.user.setText(chatMsg.getUsername());
        holder.message.setText(chatMsg.getMessage());
        holder.timeStamp.setText(formatTimeStamp(chatMsg.getTimeStamp()));
        holder.tag.setText(chatMsg.getTag());
        holder.chatMsg=chatMsg;
        holder.chat_continer = (CardView) convertView.findViewById(R.id.chat_card);

        holder.chat_continer.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                ReportDialog reportDialog = new ReportDialog((Activity) view.getContext());
                Window window = reportDialog.getWindow();
                window.setLayout(450, ViewGroup.LayoutParams.WRAP_CONTENT);
                reportDialog.show();
                return true;
            }
        });

        return convertView;
    }

    @Override
    public int getCount() {
        return this.values.size();
    }

    /**
     * Method to add a single message and update the listview.
     * @param chatMsg Message to be added
     */
    public void addMessage(ChatMessage chatMsg){
        this.values.add(chatMsg);
        notifyDataSetChanged();
    }

    /**
     * Method to add a list of messages and update the listview.
     * @param chatMsgs Messages to be added
     */
    public void setMessages(List<ChatMessage> chatMsgs){
        this.values.clear();
        this.values.addAll(chatMsgs);
        notifyDataSetChanged();
    }

    /**
     * Format the long System.currentTimeMillis() to a better looking timestamp. Uses a calendar
     *   object to format with the user's current time zone.
     * @param timeStamp
     * @return
     */
    public static String formatTimeStamp(long timeStamp){
        // Create a DateFormatter object for displaying date in specified format.
        SimpleDateFormat formatter = new SimpleDateFormat("hh:mm a");

        // Create a calendar object that will convert the date and time value in milliseconds to date.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timeStamp);
        return formatter.format(calendar.getTime());
    }

    /**
     * Clear all values from the values array and update the listview. Used when changing rooms.
     */
    public void clearMessages(){
        this.values.clear();
        notifyDataSetChanged();
    }

    public class ReportDialog extends Dialog implements
            View.OnClickListener {

        public Activity c;
        public Dialog d;
        public TextView report;
        public TextView dismiss;
        Context context;


        public ReportDialog(Activity a) {
            super(a);
            // TODO Auto-generated constructor stub
            this.c = a;
            this.context = context;
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            setContentView(R.layout.report_dialog);
            report=(TextView)findViewById(R.id.btn_report);
            dismiss=(TextView)findViewById(R.id.btn_dismiss);

            report.setOnClickListener(this);
            dismiss.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_report: {
                    Toast.makeText(v.getContext(),"This post has been flagged for the admin.", Toast.LENGTH_SHORT).show();
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


}
