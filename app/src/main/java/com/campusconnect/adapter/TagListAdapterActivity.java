package com.campusconnect.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.campusconnect.R;
import com.campusconnect.adt.TrendingTags;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by rkd on 15/01/16.
 */
public class TagListAdapterActivity extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    //Typeface declaration start region
    Typeface r_reg, r_lig;
    //end region

    Context context;

    List<TrendingTags> trendingTags;
    List<TrendingTags> selectedTags;

    // region Constructors
    public TagListAdapterActivity(List<TrendingTags> trendingTags,Context context) {
        this.context = context;
        this.trendingTags = trendingTags;
    }
    // endregion

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.tag_list, parent, false);

        return new TagListViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        final TagListViewHolder faq_viewholder = (TagListViewHolder) viewHolder;
        faq_viewholder.tag.setText(trendingTags.get(position).getName());



    }


    @Override
    public int getItemCount() {
        return trendingTags.size();
    }

    public List<TrendingTags> getSelectedTags() {
        return selectedTags;
    }

    public class TagListViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.tag_name)
        TextView tag;
        @Bind(R.id.tag_check)
        CheckBox tagCheck;

        public TagListViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);

            r_reg = Typeface.createFromAsset(v.getContext().getAssets(), "font/Roboto_Regular.ttf");

            tag.setTypeface(r_reg);

            if(tagCheck.isChecked()){
                   selectedTags.add(trendingTags.get(getAdapterPosition()));
                }




        }

    }
}
