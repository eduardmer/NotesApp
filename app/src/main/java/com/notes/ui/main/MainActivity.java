package com.notes.ui.main;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import com.notes.R;
import com.notes.databinding.ActivityMainBinding;
import com.notes.ui.adapter.NotesAdapter;
import com.notes.ui.add_notes.AddNoteActivity;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import dagger.hilt.android.AndroidEntryPoint;
import io.reactivex.rxjava3.disposables.CompositeDisposable;

@AndroidEntryPoint
public class MainActivity extends AppCompatActivity implements AdapterListener{

    MainViewModel viewModel;
    final CompositeDisposable compositeDisposable = new CompositeDisposable();
    final SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityMainBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        viewModel =  new ViewModelProvider(this).get(MainViewModel.class);
        NotesAdapter adapter = new NotesAdapter(this, new ArrayList<>());
        binding.addNote.setOnClickListener(v -> startActivity(new Intent(this, AddNoteActivity.class)));
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        binding.recyclerView.setAdapter(adapter);

        viewModel.getState().observe(this, resources -> {
            switch (resources.getStatus()) {
                case LOADING:
                    binding.progressBar.setVisibility(View.VISIBLE);
                    break;
                case SUCCESS:
                    binding.progressBar.setVisibility(View.GONE);
                    if (resources.getData() != null)
                        adapter.update(resources.getData());
                    if (resources.getMessage() != -1)
                        Toast.makeText(this, resources.getMessage(), Toast.LENGTH_SHORT).show();
                    break;
                case ERROR:
                    binding.progressBar.setVisibility(View.GONE);
                    Toast.makeText(this, resources.getMessage(), Toast.LENGTH_SHORT).show();
                    break;
            }
        });
    }

    @Override
    public void createPDF(String title, String description) {
        compositeDisposable.add(viewModel.createPdfDocument(title, description).subscribe(pdfDocument -> {
            Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT)
                    .addCategory(Intent.CATEGORY_OPENABLE)
                    .setType("application/pdf")
                    .putExtra(Intent.EXTRA_TITLE, "note_" + dateFormat.format(new Date()));
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
            Intent shareIntent = new Intent()
                    .setAction(Intent.ACTION_SEND)
                    .putExtra(Intent.EXTRA_STREAM, FileProvider.getUriForFile(this, getPackageName() + ".provider", dir))
                    .setType("application/pdf");
            startActivity(Intent.createChooser(shareIntent, null));
        }, error -> Toast.makeText(this, R.string.share_document_error, Toast.LENGTH_SHORT).show()));
    }

    @Override
    public void deleteNote(int id) {
        viewModel.deleteNote(id);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            try {
                OutputStream outputStream = getContentResolver().openOutputStream(data.getData());
                viewModel.getPdfDocument().writeTo(outputStream);
                outputStream.close();
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