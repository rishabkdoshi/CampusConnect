package com.campusconnect.adapter;

/**
 * Created by sylumani on 2/23/2016.
 */

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.campusconnect.R;
import com.campusconnect.adt.TagViewHolder;
import com.campusconnect.adt.TrendingTags;
import com.campusconnect.utility.DateUtility;

import org.w3c.dom.Text;

import java.util.List;

/**
 * Custom adapter for displaying an array of Tag objects.
 */
public class TagArrayAdapter extends ArrayAdapter<TrendingTags> {

    private LayoutInflater inflater;

    //Typeface declaration start region
    Typeface r_reg, r_lig;

    public TagArrayAdapter(Context context, List<TrendingTags> tagList) {
        super(context, R.layout.tag_list, R.id.tag_name, tagList);
        // Cache the LayoutInflate to avoid asking for a new one each time.
        inflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Tag to display
        TrendingTags tag = (TrendingTags) this.getItem(position);

        // The child views in each row.
        CheckBox checkBox;
        TextView textView;
        TextView starts_in;

        // Create a new row view
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.tag_list, null);

            // Find the child views.
            textView = (TextView) convertView.findViewById(R.id.tag_name);
            checkBox = (CheckBox) convertView.findViewById(R.id.tag_check);
            starts_in = (TextView) convertView.findViewById(R.id.tv_time_to_start);

            r_lig = Typeface.createFromAsset(convertView.getContext().getAssets(), "font/Roboto_Light.ttf");
            r_reg = Typeface.createFromAsset(convertView.getContext().getAssets(), "font/Roboto_Regular.ttf");

            textView.setTypeface(r_reg);
            starts_in.setTypeface(r_lig);

            // Optimization: Tag the row with it's child views, so we don't have to
            // call findViewById() later when we reuse the row.
            convertView.setTag(new TagViewHolder(textView, checkBox,starts_in));

            // If CheckBox is toggled, update the tag it is tagged with.
            checkBox.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    CheckBox cb = (CheckBox) v;
                    TrendingTags tag = (TrendingTags) cb.getTag();
                    tag.setChecked(cb.isChecked());
                }
            });
        }
        // Reuse existing row view
        else {
            // Because we use a ViewHolder, we avoid having to call findViewById().
            TagViewHolder viewHolder = (TagViewHolder) convertView.getTag();
            checkBox = viewHolder.getCheckBox();
            textView = viewHolder.getTextView();
            starts_in = viewHolder.getStarts_in();
        }

        // Tag the CheckBox with the tag it is displaying, so that we can
        // access the planet in onClick() when the CheckBox is toggled.
        checkBox.setTag(tag);

        // Display planet data
        checkBox.setChecked(tag.isChecked());
        textView.setText("#"+tag.getName());
        starts_in.setText(DateUtility.getRelativeDate(tag.getTimeStamp()));

        return convertView;
    }

}