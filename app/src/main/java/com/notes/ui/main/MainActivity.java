package com.notes.ui.main;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.notes.R;
import com.notes.ViewModelFactory;
import com.notes.data.Notes;
import com.notes.databinding.ActivityMainBinding;
import com.notes.ui.add_notes.AddNoteActivity;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    @Inject ViewModelFactory viewModelFactory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        MainViewModel viewModel =  new ViewModelProvider(this, viewModelFactory).get(MainViewModel.class);

        binding.addNote.setOnClickListener(v -> startActivity(new Intent(this, AddNoteActivity.class)));
        viewModel.getNotes().subscribe(notes -> {
            for (Notes note : notes)
                Log.i("pergjigja", note.getTitle());
        });
    }
}