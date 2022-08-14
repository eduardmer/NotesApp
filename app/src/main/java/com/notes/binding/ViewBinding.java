package com.notes.binding;

import android.view.View;
import android.widget.TextView;

import androidx.databinding.BindingAdapter;

import com.google.android.material.textfield.TextInputEditText;
import com.notes.utils.Constants;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class ViewBinding {

    @BindingAdapter("app:visible")
    public static void setVisibility(View view, boolean visible) {
        view.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    @BindingAdapter("app:date_24h")
    public static void setDate(TextView view, long time) {
        view.setText(new SimpleDateFormat(Constants.DATE_FORMAT_24H, Locale.getDefault()).format(new Date(time)));
    }

    @BindingAdapter("app:date")
    public static void setDate(TextInputEditText view, Calendar date) {
        view.setText(new SimpleDateFormat(Constants.DATE_TIME_FORMAT, Locale.getDefault()).format(date.getTimeInMillis()));
    }

}
