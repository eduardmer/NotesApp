package com.notes.ui.main;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
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
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import dagger.hilt.android.AndroidEntryPoint;
import io.reactivex.rxjava3.disposables.CompositeDisposable;

@AndroidEntryPoint
public class MainActivity extends AppCompatActivity implements AdapterListener{

    ActivityMainBinding binding;
    MainViewModel viewModel;
    final CompositeDisposable compositeDisposable = new CompositeDisposable();
    final SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale.getDefault());

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
        compositeDisposable.add(viewModel.createPdfDocument(title, description).subscribe(pdfDocument -> {
            Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("application/pdf");
            intent.putExtra(Intent.EXTRA_TITLE, "note_" + dateFormat.format(new Date()));
            startActivityForResult(intent,1);
        }, error -> {
            error.printStackTrace();
            Toast.makeText(this, getString(R.string.save_document_error), Toast.LENGTH_SHORT).show();
        }));
    }

    @Override
    public void shareNote(String title, String description) {
        compositeDisposable.add(viewModel.createPdfDocument(title, description).subscribe(pdfDocument -> {
            File dir = new File(getExternalFilesDir(null), "Document.pdf");
            pdfDocument.writeTo(new FileOutputStream(dir));
            pdfDocument.close();
            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.putExtra(Intent.EXTRA_STREAM, FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".provider", dir));
            shareIntent.setType("application/pdf");
            startActivity(Intent.createChooser(shareIntent, null));
        }, error -> Toast.makeText(this, R.string.share_document_error, Toast.LENGTH_SHORT).show()));
    }

    @Override
    public void deleteNote(int id) {
        compositeDisposable.add(viewModel.deleteNote(id).subscribe(
                () -> Toast.makeText(this, R.string.delete_note, Toast.LENGTH_SHORT).show(),
                error -> Toast.makeText(this, R.string.error, Toast.LENGTH_SHORT).show()
        ));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            try {
                viewModel.getPdfDocument().writeTo(new FileOutputStream(data.getData().toString()));
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, R.string.save_document_error, Toast.LENGTH_SHORT).show();
            } finally {
                viewModel.getPdfDocument().close();
            }
        }
    }

    @Override
    protected void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }
}