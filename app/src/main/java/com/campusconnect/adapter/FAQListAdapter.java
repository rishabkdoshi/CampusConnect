package com.campusconnect.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.campusconnect.R;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by rkd on 15/01/16.
 */
public class FAQListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    //FAQ variables start region
    String[] questions_user = {
            "How do I create a new group?",
            "Who can create an event post?",
            "What kind of groups are allowed?",
            "Are class groups allowed?",
            "What is the difference between a member and a follower?",
            "How to get to know about up coming events?",
            "How does MyFeed section work?",
            "How do I become a member of a group?",
            "Is there any lighter version to conserve data?",
            "I’m not an android user, so how do I get college updates?",
            "How can I give feedback?",
            "What is the live section?",
            "What are trending events?",
            "How can I find directions to an event?",
            "What are custom filters?"
    };

    String[] answers_user = {
            "Use the create group option under the group section.However the group will be displayed only after verification by Students’ Council President or any other authorized person.We believe in less spams for a smooth experience.",
            "Only the admins & members of a particular group can make a group post.",
            "Only clubs or committees authorized by College are allowed on this platform in order to have less spam and more useful content.",
            "Not at the moment. We’re working on it.",
            "A member can create events and post news feed of that group. However, as a follower you can get to know about all the events and news feed of that group.",
            "You can see all the upcoming events in the Calendar section.",
            "In MyFeed section you get feeds according to the groups you follow.",
            "You need to send a member request to that group. Only Admin can add you as a member.",
            "Click on the option button (three dot button) to switch your feed to lite version. This allows you to conserve your data.",
            "We have a webapp at our website (campusconnect.cc) as well from where you can get regular updates.",
            "Please write to us at contact@campusconnect.cc. We take your feedback very seriously. After all it is a platform for students, made by students just like you!",
            "Live section is your live feed for your campus. Here you can talk about happenings on your campus real time. Share photos and more!",
            "Trending events are current and upcoming events. You can start a discussion and filter by any of these events to have an effective news channel.",
            "In the event page, you can find the location to the event. On clicking the event name, you will be taken to google maps where you will be shown the directions to the event from your present location.",
            "Custom filters are special image filters which you can place over the photos you take in the Live News section. These are made with love from the Campus Connect team specifically for your campus."
    };

    String[] questions_admin = {
            "How to add a member to a group?",
            "How to post a new event?",
            "Can I get an estimate of people attending my event?",
            "Can I edit/delete a post I have created?",
            "I have added a group member by mistake, I need to remove them."
    };

    String[] answers_admin = {
            "The person needs to send a member request for it. After that the admin of that group can add him/her. Admin section is the white button just next to the ‘+’ tab.",
            "Go to ‘+’ tab to create a new event. You can post poster, venue, date, time etc. in a single click.",
            "Of course! When people tap ‘Attending’ tab you’ll be notified.",
            "Yes, you can go to the admin section and go through any posts created in your group. You may then edit or delete any post.",
            "In the admin section, click on members. Next to each member you can remove a member from the group."
    };
    //end region

    String user_type;

    //Typeface declaration start region
    Typeface r_reg, r_lig;
    //end region

    Context context;

    // region Constructors
    public FAQListAdapter(Context context, String u_type) {
        this.context = context;
        user_type = u_type;
    }
    // endregion

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.activity_card_layout_faq, parent, false);

        return new FAQListViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        final FAQListViewHolder faq_viewholder = (FAQListViewHolder) viewHolder;

        if(user_type.equals("ForUser")) {
            faq_viewholder.question.setText(questions_user[position]);
            faq_viewholder.answer.setText(answers_user[position]);
        }else{
            faq_viewholder.question.setText(questions_admin[position]);
            faq_viewholder.answer.setText(answers_admin[position]);
        }

    }


    @Override
    public int getItemCount() {
        if(user_type.equals("ForUser")) {
            return questions_user.length;
        }else{
            return questions_admin.length;
        }
    }

    public class FAQListViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.tv_question)
        TextView question;
        @Bind(R.id.tv_answer)
        TextView answer;

        public FAQListViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);

            r_lig = Typeface.createFromAsset(v.getContext().getAssets(), "font/Roboto_Light.ttf");
            r_reg = Typeface.createFromAsset(v.getContext().getAssets(), "font/Roboto_Regular.ttf");

            question.setTypeface(r_reg);
            answer.setTypeface(r_lig);

            Linkify.addLinks(answer, Linkify.ALL);


        }

    }
}
