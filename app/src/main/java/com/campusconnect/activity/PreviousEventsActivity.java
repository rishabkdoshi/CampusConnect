package com.campusconnect.activity;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.campusconnect.R;
import com.campusconnect.adapter.PreviousEventsAdapterActivity;
import com.campusconnect.supportClasses.UpcomingEvents_infoActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by RK on 06/11/2015.
 */
public class PreviousEventsActivity extends ActionBarActivity {

    @Bind(R.id.rv_previous_events) RecyclerView previous_events;
    @Bind(R.id.cross_button) LinearLayout close;
    @Bind(R.id.tv_previous_events) TextView previous_events_text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_previous_events);
        ButterKnife.bind(this);

        Typeface r_med = Typeface.createFromAsset(getAssets(), "font/Roboto_Medium.ttf");
        previous_events_text.setTypeface(r_med);

        previous_events.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        previous_events.setLayoutManager(llm);
        previous_events.setItemAnimator(new DefaultItemAnimator());

        PreviousEventsAdapterActivity previousEventsAdapterActivity = new PreviousEventsAdapterActivity(
                createList_previousEventsAdapterActivity(3));
        previous_events.setAdapter(previousEventsAdapterActivity);

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();

            }
        });

    }

    private List<UpcomingEvents_infoActivity> createList_previousEventsAdapterActivity(int size) {
        List<UpcomingEvents_infoActivity> result = new ArrayList<UpcomingEvents_infoActivity>();
        for (int i = 1; i <= size; i++) {
            UpcomingEvents_infoActivity ci = new UpcomingEvents_infoActivity();
            result.add(ci);
        }

        return result;
    }


}


