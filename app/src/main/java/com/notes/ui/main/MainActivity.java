package com.notes.ui.main;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.notes.R;
import com.notes.databinding.ActivityMainBinding;
import com.notes.ui.adapter.NotesAdapter;
import com.notes.ui.add_notes.AddNoteActivity;
import com.notes.utils.PDFDocumentUtils;

import java.util.ArrayList;

import dagger.hilt.android.AndroidEntryPoint;
import io.reactivex.rxjava3.disposables.CompositeDisposable;

@AndroidEntryPoint
public class MainActivity extends AppCompatActivity implements AdapterListener{

    ActivityMainBinding binding;
    MainViewModel viewModel;
    final CompositeDisposable compositeDisposable = new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        viewModel =  new ViewModelProvider(this).get(MainViewModel.class);

        NotesAdapter adapter = new NotesAdapter(this, new ArrayList<>());

        binding.addNote.setOnClickListener(v -> startActivity(new Intent(this, AddNoteActivity.class)));
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        binding.recyclerView.setAdapter(adapter);
        compositeDisposable.add(viewModel.getNotes().subscribe(
                adapter::update,
                error -> Log.i("pergjigja", error.toString())));
    }

    @Override
    public void createPDF(String title, String description) {
        PDFDocumentUtils.createPDFDocument(title, description);
    }

    @Override
    public void shareNote() {

    }

    @Override
    public void deleteNote(int id) {
        compositeDisposable.add(viewModel.deleteNote(id).subscribe(
                () -> Toast.makeText(this, getString(R.string.delete_note), Toast.LENGTH_SHORT).show(),
                error -> Toast.makeText(this, getString(R.string.error), Toast.LENGTH_SHORT).show()
        ));
    }

    @Override
    protected void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }
}