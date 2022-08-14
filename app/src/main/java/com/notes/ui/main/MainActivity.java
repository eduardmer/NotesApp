package com.notes.ui.main;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.content.Intent;
import android.graphics.pdf.PdfDocument;
import android.os.Bundle;
import android.widget.Toast;
import com.notes.R;
import com.notes.databinding.ActivityMainBinding;
import com.notes.ui.adapter.NotesAdapter;
import com.notes.ui.add_notes.AddNoteActivity;
import com.notes.utils.Constants;
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
    private PdfDocument pdfDocument;
    final SimpleDateFormat dateFormat = new SimpleDateFormat(Constants.DATE_TIME_FORMAT, Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityMainBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        viewModel = new ViewModelProvider(this).get(MainViewModel.class);
        binding.setViewModel(viewModel);
        binding.setLifecycleOwner(this);
        NotesAdapter adapter = new NotesAdapter(this, new ArrayList<>());
        binding.addNote.setOnClickListener(v -> startActivity(new Intent(this, AddNoteActivity.class)));
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        binding.recyclerView.setAdapter(adapter);

        viewModel.getState().observe(this, uiState -> {
            if (uiState.getMessage() != -1)
                Toast.makeText(this, uiState.getMessage(), Toast.LENGTH_SHORT).show();
            if (uiState.getNotes() != null)
                adapter.update(uiState.getNotes());
        });
    }

    @Override
    public void createPDF(String title, String description) {
        compositeDisposable.add(viewModel.createPdfDocument(title, description).subscribe(pdfDocument -> {
            this.pdfDocument = pdfDocument;
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            try {
                OutputStream outputStream = getContentResolver().openOutputStream(data.getData());
                pdfDocument.writeTo(outputStream);
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, R.string.save_document_error, Toast.LENGTH_SHORT).show();
            } finally {
                pdfDocument.close();
            }
        }
    }

    @Override
    protected void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }
}