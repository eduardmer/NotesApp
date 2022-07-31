package com.notes.ui.main;

import android.graphics.pdf.PdfDocument;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.notes.R;
import com.notes.data.NotesRepository;
import com.notes.utils.PDFDocumentUtils;
import javax.inject.Inject;
import dagger.hilt.android.lifecycle.HiltViewModel;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

@HiltViewModel
public class MainViewModel extends ViewModel {

    private final NotesRepository notesRepository;
    private PdfDocument pdfDocument;
    private final MutableLiveData<NotesUIState> state;
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();

    @Inject
    public MainViewModel(NotesRepository notesRepository){
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
                () -> state.setValue(new NotesUIState(false, R.string.delete_note, null)),
                error -> state.setValue(new NotesUIState(false, R.string.error, null))
        ));
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
