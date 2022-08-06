package com.notes.ui.main;

import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.pdf.PdfDocument;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import com.notes.AlarmReceiver;
import com.notes.R;
import com.notes.data.database.NotesRepository;
import com.notes.utils.PDFDocumentUtils;
import javax.inject.Inject;
import dagger.hilt.android.lifecycle.HiltViewModel;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

@HiltViewModel
public class MainViewModel extends AndroidViewModel {

    private final NotesRepository notesRepository;
    private PdfDocument pdfDocument;
    private final MutableLiveData<NotesUIState> state;
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();

    @Inject
    public MainViewModel(Application application, NotesRepository notesRepository) {
        super(application);
        this.notesRepository = notesRepository;
        this.state = new MutableLiveData<>(new NotesUIState(true, -1, null));
        getNotes();
    }

    public MutableLiveData<NotesUIState> getState() {
        return state;
    }

    private void getNotes() {
        compositeDisposable.add(notesRepository.getNotes().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(
                notes -> getState().setValue(new NotesUIState(false, -1, notes)),
                error -> {
                    getState().setValue(new NotesUIState(false, R.string.error, null));
                    error.printStackTrace();
                }
        ));
    }

    public void deleteNote(int id) {
        compositeDisposable.add(notesRepository.deleteNote(id).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(
                () -> {
                    state.setValue(new NotesUIState(false, R.string.delete_note, null));
                    cancelAlarm(id);
                },
                error -> state.setValue(new NotesUIState(false, R.string.error, null))
        ));
    }

    private void cancelAlarm(int noteId) {
        AlarmManager alarmManager = (AlarmManager) getApplication().getSystemService(Context.ALARM_SERVICE);
        Intent myIntent = new Intent(getApplication(), AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                getApplication(), noteId, myIntent,
                0);
        alarmManager.cancel(pendingIntent);
    }

    public Single<PdfDocument> createPdfDocument(String title, String description) {
        return Single.fromCallable(() -> {
            pdfDocument = PDFDocumentUtils.createPDFDocument(title, description);
            return pdfDocument;
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    public PdfDocument getPdfDocument() {
        return pdfDocument;
    }

    @Override
    protected void onCleared() {
        compositeDisposable.clear();
        super.onCleared();
    }
}
