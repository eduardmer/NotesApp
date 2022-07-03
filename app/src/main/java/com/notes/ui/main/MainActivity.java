package com.notes.ui.main;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.content.FileProvider;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import com.notes.BuildConfig;
import com.notes.R;
import com.notes.databinding.ActivityMainBinding;
import com.notes.ui.adapter.NotesAdapter;
import com.notes.ui.add_notes.AddNoteActivity;
import com.notes.utils.PDFDocumentUtils;

import java.io.File;
import java.io.FileOutputStream;
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
        PdfDocument pdfDocument = PDFDocumentUtils.createPDFDocument(title, description);
        File dir = new File(getFilesDir(), "Document.pdf");
        try{
            pdfDocument.writeTo(new FileOutputStream(dir));
            Log.i("pergjigja", "sakte");
        } catch (Exception e) {
            Log.i("pergjigja", e.toString());
        }
        /*PDFDocumentUtils.createPDFDocument(title, description);
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("default", "Channel name", NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription("Channel description");
            notificationManager.createNotificationChannel(channel);
        }
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "default")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(getResources().getString(R.string.app_name))
                .setDefaults(Notification.DEFAULT_ALL)
                .setPriority(Notification.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setContentText("Your text.....");
        builder.setSound(Settings.System.DEFAULT_NOTIFICATION_URI);

        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(0, builder.build());*/
    }

    @Override
    public void shareNote() {
        PdfDocument pdfDocument = PDFDocumentUtils.createPDFDocument("title", "description");
        File dir = new File(getExternalFilesDir(null), "Document.pdf");
        try{
            pdfDocument.writeTo(new FileOutputStream(dir));
            Log.i("pergjigja", "sakte");
        } catch (Exception e) {
            Log.i("pergjigja", e.toString());
        }
        pdfDocument.close();
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        Log.i("pergjigja",dir.toURI().toString());
        shareIntent.putExtra(Intent.EXTRA_STREAM, FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".provider", dir));
        shareIntent.setType("application/pdf");
        startActivity(Intent.createChooser(shareIntent, null));
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