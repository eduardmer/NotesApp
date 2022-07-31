package com.notes.binding;

import android.view.View;

import androidx.databinding.BindingAdapter;

public class ViewBinding {

    @BindingAdapter("app:visible")
    public static void setVisibility(View view, boolean visible) {
        view.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

}
