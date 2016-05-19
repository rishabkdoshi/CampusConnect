package com.campusconnect.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.campusconnect.R;
import com.campusconnect.constant.AppConstants;
import com.campusconnect.fragment.HomeFragment;
import com.campusconnect.utility.SharedpreferenceUtility;

public class MenuListAdapter extends ArrayAdapter<String> {

    private final Activity activity;
    private final String[] itemname;
    public CheckBox chk_menu_item;

    public MenuListAdapter(Activity activity, String[] itemname) {
        super(activity, R.layout.menu_item, itemname);
        // TODO Auto-generated constructor stub

        this.activity=activity;
        this.itemname=itemname;
    }

    public boolean is_chk_box_checked(){
        if(chk_menu_item.isChecked())
            return true;
        else
            return false;
    }

    public View getView(int position,View view, final ViewGroup parent) {
        LayoutInflater inflater= activity.getLayoutInflater();
        View rowView=inflater.inflate(R.layout.menu_item, null, true);

        TextView menu_item_name = (TextView) rowView.findViewById(R.id.tv_menu_item);
        chk_menu_item = (CheckBox) rowView.findViewById(R.id.cb_menu_item);

        menu_item_name.setText(itemname[position]);

        if(position==0) {
            chk_menu_item.setVisibility(View.VISIBLE);

            chk_menu_item.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (((CheckBox) v).isChecked()) {
                        SharedpreferenceUtility.getInstance(v.getContext()).putBoolean(AppConstants.IS_LITE_CHECKED, Boolean.TRUE);
                        SharedpreferenceUtility.getInstance(v.getContext()).putBoolean(AppConstants.IS_LITE, Boolean.TRUE);
                        ((ListView) parent).performItemClick(v,0,0);
                    }
                    else{
                        SharedpreferenceUtility.getInstance(v.getContext()).putBoolean(AppConstants.IS_LITE_CHECKED, Boolean.FALSE);
                        SharedpreferenceUtility.getInstance(v.getContext()).putBoolean(AppConstants.IS_LITE, Boolean.FALSE);
                        ((ListView) parent).performItemClick(v, 0, 0);
                    }
                }
            });

            if(SharedpreferenceUtility.getInstance(rowView.getContext()).getBoolean(AppConstants.IS_LITE_CHECKED))
                chk_menu_item.setChecked(true);
            else
                chk_menu_item.setChecked(false);


        }
        else
            chk_menu_item.setVisibility(View.GONE);

        return rowView;

    };
}