package com.campusconnect.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.util.Linkify;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.campusconnect.R;
import com.campusconnect.activity.InEventActivity;
import com.campusconnect.communicator.WebRequestTask;
import com.campusconnect.communicator.WebServiceDetails;
import com.campusconnect.communicator.models.ModelsEventForm;
import com.campusconnect.constant.AppConstants;
import com.campusconnect.database.DatabaseHandler;
import com.campusconnect.utility.CircularImageView;
import com.campusconnect.utility.DateUtility;
import com.campusconnect.utility.SharedpreferenceUtility;
import com.campusconnect.utility.StickyRecyclerHeadersAdapter;
import com.squareup.picasso.Picasso;

import org.apache.http.NameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;

import butterknife.Bind;
import butterknife.ButterKnife;

public class PlacesExpandableListAdapter extends BaseExpandableListAdapter {

    private Context _context;
    Typeface r_reg, r_lig;
    ImageView group_arrow;

    private List<String> _listDataHeader; // header titles
    // child data in format of header title, child title
    private HashMap<String, List<String>> _listDataChild;

    public PlacesExpandableListAdapter(Context context, List<String> listDataHeader,
                                       HashMap<String, List<String>> listChildData) {
        this._context = context;
        this._listDataHeader = listDataHeader;
        this._listDataChild = listChildData;
    }

    @Override
    public Object getChild(int groupPosition, int childPosititon) {
        return this._listDataChild.get(this._listDataHeader.get(groupPosition))
                .get(childPosititon);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {

        final String childText = (String) getChild(groupPosition, childPosition);

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.place_child_card, null);
            r_lig = Typeface.createFromAsset(convertView.getContext().getAssets(), "font/Roboto_Light.ttf");
        }

        TextView txtListChild = (TextView) convertView
                .findViewById(R.id.tv_childPlace);
        txtListChild.setTypeface(r_lig);

        txtListChild.setText(childText);
        Linkify.addLinks(txtListChild, Linkify.ALL);

        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return this._listDataChild.get(this._listDataHeader.get(groupPosition))
                .size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this._listDataHeader.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return this._listDataHeader.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        String headerTitle = (String) getGroup(groupPosition);


        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.place_card, null);
            r_reg = Typeface.createFromAsset(convertView.getContext().getAssets(), "font/Roboto_Regular.ttf");
        }


        TextView lblListHeader = (TextView) convertView
                .findViewById(R.id.tv_placeHeader);
        group_arrow = (ImageView) convertView
                .findViewById(R.id.arrow_header);
        lblListHeader.setText(headerTitle);
        lblListHeader.setTypeface(r_reg);

        if(isExpanded)
            rotateArrow();
        else
            rotatebackArrow();

        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    public void rotateArrow(){
        group_arrow.setRotation(90);
    }

    public void rotatebackArrow(){
        group_arrow.setRotation(0);
    }

}