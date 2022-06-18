package com.notes.ui.add_notes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.datepicker.MaterialDatePicker;
import com.notes.R;
import com.notes.databinding.ActivityAddNoteBinding;

import java.util.Objects;

public class AddNoteActivity extends AppCompatActivity {

    ActivityAddNoteBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_note);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        AddNoteViewModel viewModel = new ViewModelProvider(this).get(AddNoteViewModel.class);
        binding.setViewModel(viewModel);

        binding.reminderSwitch.setOnCheckedChangeListener(((compoundButton, b) -> viewModel.setReminder(b)));
        binding.dateContainer.setOnClickListener(v -> MaterialDatePicker.Builder.datePicker().setTitleText("Select date").build().show(getSupportFragmentManager(), "tag"));
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}