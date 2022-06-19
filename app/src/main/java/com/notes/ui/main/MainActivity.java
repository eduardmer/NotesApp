package com.notes.ui.main;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import com.notes.R;
import com.notes.databinding.ActivityMainBinding;
import com.notes.ui.adapter.NotesAdapter;
import com.notes.ui.add_notes.AddNoteActivity;

import java.util.ArrayList;

import dagger.hilt.android.AndroidEntryPoint;
import io.reactivex.rxjava3.disposables.CompositeDisposable;

@AndroidEntryPoint
public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    final CompositeDisposable compositeDisposable = new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        MainViewModel viewModel =  new ViewModelProvider(this).get(MainViewModel.class);

        NotesAdapter adapter = new NotesAdapter(new ArrayList<>());

        binding.addNote.setOnClickListener(v -> startActivity(new Intent(this, AddNoteActivity.class)));
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        binding.recyclerView.setAdapter(adapter);
        compositeDisposable.add(viewModel.getNotes().subscribe(
                adapter::update,
                error -> Log.i("pergjigja", error.toString())));
    }

    @Override
    protected void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }
}