package com.campusconnect.adt;

import android.widget.CheckBox;
import android.widget.TextView;

/**
 * Created by sylumani on 2/23/2016.
 */
public class TagViewHolder {
    private CheckBox checkBox;
    private TextView textView;
    private TextView starts_in;

    public TagViewHolder() {
    }

    public TagViewHolder(TextView textView, CheckBox checkBox,TextView starts_in) {
        this.checkBox = checkBox;
        this.textView = textView;
        this.starts_in = starts_in;
    }

    public CheckBox getCheckBox() {
        return checkBox;
    }

    public void setCheckBox(CheckBox checkBox) {
        this.checkBox = checkBox;
    }

    public TextView getTextView() {
        return textView;
    }

    public void setTextView(TextView textView) {
        this.textView = textView;
    }

    public TextView getStarts_in() {
        return starts_in;
    }

    public void setStarts_in(TextView starts_in) {
        this.starts_in = starts_in;
    }
}
